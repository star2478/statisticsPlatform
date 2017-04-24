package com.z.statisticsPlatform.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.z.statisticsPlatform.vo.GetVideoDailyCountVO;
import com.z.statisticsPlatform.vo.GetVideoDailyCountVO.VideoDailyCountSubVO;

import org.apache.catalina.filters.AddDefaultCharsetFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.z.statisticsPlatform.dao.VideoDailyCountDAO;
import com.z.statisticsPlatform.dao.VideoInfoDAO;
import com.z.statisticsPlatform.dto.VideoDailyCountDTO;
import com.z.statisticsPlatform.dto.VideoInfoDTO;
import com.z.statisticsPlatform.util.ResponseUtil;
import com.z.statisticsPlatform.util.ResultInfo;
import com.z.statisticsPlatform.util.Utils;
import com.z.statisticsPlatform.vo.GetVideoInfoByPageVO;

import ch.qos.logback.core.joran.conditional.IfAction;

@RestController
public class VideoDailyCountController {
	private final Logger logger = Logger.getLogger(getClass());
	
	private final static long ONE_DATE_MILL_SECONDS = 24*3600*1000;	//一天的ms数
	
	@Autowired
	private VideoInfoDAO videoInfoDAO;
	
	@Autowired
	private VideoDailyCountDAO videoDailyCountDAO;
        
    /**
     * 获取指定视频的日播放量
     * @param link
     * @param channel
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "/getVideoDailyCount")
    public ResultInfo getVideoDailyCount(String link, String channel, String beginTime, String endTime) {
		logger.info(getClass().getName() + ".getVideoDailyCount begin");

		return getVideoDailyCountInfo(link, channel, beginTime, endTime);
    }
    
    /**
     * 获取指定视频的日播放量和增长率，以csv格式输出
     * @param link
     * @param channel
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "/getVideoDailyCountForExport")
    public ResultInfo getVideoDailyCountForExport(String link, String channel, String beginTime, String endTime) {
		logger.info(getClass().getName() + ".getVideoDailyCountForExport begin");
		try {
			ResultInfo resultInfo = getVideoDailyCountInfo(link, channel, beginTime, endTime);
			if(!resultInfo.getCode().equals(ResponseUtil.success_code)) {
				return resultInfo;
			}
			GetVideoDailyCountVO videoDailyCount = (GetVideoDailyCountVO) resultInfo.getBody();
			List<VideoDailyCountSubVO> dailyCount = videoDailyCount.getDailyCount();
	    	String result = "时间,播放量,增长率";
	    	for (VideoDailyCountSubVO videoDailyCountSubVO : dailyCount) {
	    		String growthRate = (videoDailyCountSubVO.getGrowthRate() != null)?videoDailyCountSubVO.getGrowthRate() + "%":"";
				String info = "\n" + videoDailyCountSubVO.getDate() + "," +
						videoDailyCountSubVO.getPlayCount() + "," +
						growthRate;
				result += info;
			}
			return new ResultInfo(ResponseUtil.success_code, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(ResponseUtil.faile_code);
		}
    }
    
    /**
     * 获取指定视频的日播放量和增长率详情
     * @param link
     * @param channel
     * @param beginTime
     * @param endTime
     * @return
     */
    private ResultInfo getVideoDailyCountInfo(String link, String channel, String beginTime, String endTime) {
    	try {
	    	// 参数检查
			if (link == null || channel == null || beginTime == null || endTime == null) {
				logger.error("param fail, link=" + link + ", channel=" + channel + ", beginTime=" + beginTime + ", endTime="
						+ endTime);
				return new ResultInfo(ResponseUtil.param_error_code);
			}
	    	ResultInfo checkTimeResult = Utils.checkTime(beginTime, endTime);
	    	if(!checkTimeResult.getCode().equals(ResponseUtil.success_code)) {
	    		logger.error(checkTimeResult.getMessage() + ", beginTime=" + beginTime + ", endTime=" + endTime);
				return checkTimeResult;
	    	}
	
			GetVideoDailyCountVO result = new GetVideoDailyCountVO();
			// 初始化返回数据
			List<VideoDailyCountDTO> initCountList = initVideoDailyCount(beginTime, endTime);
	
			VideoInfoDTO videoInfoDTO = videoInfoDAO.getVideoInfo(link, channel);
			if (videoInfoDTO == null) {
				return new ResultInfo(ResponseUtil.param_error_code, "找不到对应的视频:link=" + link + ", channel=" + channel,
						null);
			}
			String videoId = videoInfoDTO.get_id();
	
			List<String> videoIds = new ArrayList<String>();
			videoIds.add(videoId);
			List<VideoDailyCountDTO> videoDailyCountDTOs = videoDailyCountDAO.getVideoDailyCount(videoIds, beginTime,
					endTime);
	
			// 赋值playcount
			for (VideoDailyCountDTO videoDailyCountDTO : videoDailyCountDTOs) {
				String date = videoDailyCountDTO.getDate();
				for (VideoDailyCountDTO dateVideo : initCountList) {
					if (dateVideo.getDate().equals(date)) {
						dateVideo.setPlayCount(videoDailyCountDTO.getPlayCount());
						break;
					}
				}
			}
	
			// 计算增长率
			result.setDailyCount(calGrowthRate(videoIds, initCountList));
			result.setTitle(videoInfoDTO.getTitle());
	
			return new ResultInfo(ResponseUtil.success_code, result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(ResponseUtil.faile_code);
		}
    }
    
    /**
     * 初始化每日数据列表，得到从beginTime到endTime间所有天的数据，每天数据为0
     * @param beginTime
     * @param endTime
     * @return
     */
	private List<VideoDailyCountDTO> initVideoDailyCount(String beginTime, String endTime) {
		List<VideoDailyCountDTO> videoInfoDTOs = new ArrayList<VideoDailyCountDTO>();
		try {
			Calendar calendar = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date beginDate = dateFormat.parse(beginTime);	// 开始时间
			Date endDate = new Date(dateFormat.parse(endTime).getTime() + ONE_DATE_MILL_SECONDS);	// 结束时间
			Date date = beginDate;
			while (!date.equals(endDate)) {
				VideoDailyCountDTO videoInfoDTO = new VideoDailyCountDTO();
				videoInfoDTO.setDate(dateFormat.format(date));
				videoInfoDTO.setPlayCount(0l);
				videoInfoDTOs.add(videoInfoDTO);
				calendar.setTime(date);
				calendar.add(Calendar.DATE, 1); // 日期加1天
				date = calendar.getTime();
			}
//			// 加上最后一天
//			VideoDailyCountDTO videoInfoDTO = new VideoDailyCountDTO();
//			videoInfoDTO.setDate(dateFormat.format(date));
//			videoInfoDTO.setPlayCount(0l);
//			videoInfoDTOs.add(videoInfoDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return videoInfoDTOs;
	}
	
	/**
	 * 计算增长率
	 * @param videoIds
	 * @param initCountList
	 * @throws ParseException 
	 */
	private List<VideoDailyCountSubVO> calGrowthRate(List<String> videoIds, List<VideoDailyCountDTO> initCountList) throws ParseException {
		List<VideoDailyCountSubVO> result = new ArrayList<VideoDailyCountSubVO>();
		if (initCountList == null || initCountList.size() <= 0) {
			return result;
		}
		GetVideoDailyCountVO vo = new GetVideoDailyCountVO();
		float curPlayCount = 0, prePlayCount = 0;
		DecimalFormat decimalFormat = new DecimalFormat(".00");
		
		// 获取整个数据更早一天的数据，用以计算第一天数据的增长率
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String firstTime = initCountList.get(0).getDate();
        String preDate = dateFormat.format(new Date(dateFormat.parse(firstTime).getTime() - ONE_DATE_MILL_SECONDS));
        List<VideoDailyCountDTO> preDateVideoDailyCount = videoDailyCountDAO.getVideoDailyCount(videoIds, preDate, preDate);
        if(preDateVideoDailyCount != null && preDateVideoDailyCount.size() > 0) {
        	prePlayCount = preDateVideoDailyCount.get(0).getPlayCount();
        }
        
		for (VideoDailyCountDTO videoDailyCountDTO : initCountList) {
			VideoDailyCountSubVO videoDailyCountSubVO = vo.new VideoDailyCountSubVO();
			videoDailyCountSubVO.setDate(videoDailyCountDTO.getDate());
			videoDailyCountSubVO.setPlayCount(videoDailyCountDTO.getPlayCount());
			curPlayCount = videoDailyCountSubVO.getPlayCount();
			if(curPlayCount == prePlayCount) {
				videoDailyCountSubVO.setGrowthRate(0f);	// 如果相邻两天播放量一样，则增长率为0
			} else if (prePlayCount == 0) {
				videoDailyCountSubVO.setGrowthRate(100f);	// 如果从0增长到正数，则增长率为100l
			} else {
				String growthRateStr = decimalFormat.format((curPlayCount - prePlayCount)*100/prePlayCount);
				videoDailyCountSubVO.setGrowthRate(Float.valueOf(growthRateStr));
			}
			
			prePlayCount = curPlayCount;
			result.add(videoDailyCountSubVO);
		}
		return result;
	}
}
