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
import com.z.statisticsPlatform.vo.GetVideoInfoByPageVO;

import ch.qos.logback.core.joran.conditional.IfAction;

@RestController
public class StatisticsPlatformController {
	private final Logger logger = Logger.getLogger(getClass());

	private final static int MAX_BETWEEN_DAYS = 90;	// 最多允许查询90天内数据
	
	private final static long ONE_DATE_MILL_SECONDS = 24*3600*1000;	//一天的ms数
	
	@Autowired
	private VideoInfoDAO videoInfoDAO;
	
	@Autowired
	private VideoDailyCountDAO videoDailyCountDAO;

    /**
     * 获取视频统计数据分页数据
     * @param pageNo
     * @param limit
     * @return
     */
    @RequestMapping(value = "/getVideoInfoByPage")
    public ResultInfo getVideoInfoByPage(Integer pageNo, Integer limit, String title, String channel, String beginTime, String endTime) {
		logger.info(getClass().getName() + ".getVideoInfoByPage  begin");
    	// 参数检查
    	if(pageNo == null || limit == null) {
			logger.error("param fail, pageNo=" + pageNo + ", limit=" + limit);
			return new ResultInfo(ResponseUtil.param_error_code);
		}
		if(!checkTime(beginTime, endTime)) {
			logger.error("between days > " + MAX_BETWEEN_DAYS + ", beginTime=" + beginTime + ", endTime=" + endTime);
			return new ResultInfo(ResponseUtil.param_error_code, "最多允许查询" + MAX_BETWEEN_DAYS + "天内数据", null);
		}
    	
    	if (channel != null && channel.equals("所有渠道")) {
    		channel = null;
    	}
    	
//		long count = videoInfoDAO.getCount();
//		Long totalPageNumber = (count % limit) == 0 ? count/limit :(count/limit) + 1;
//		// 如果请求的页码大于总页数，则返回错误
//		if(totalPageNumber > 0 && pageNo > totalPageNumber) {
//			logger.error("param fail, pageNo=" + pageNo + ", limit=" + limit);
//			return new ResultInfo(ResponseUtil.param_error_code);
//		}
		
		Integer skip = (pageNo - 1) * limit;
		// limit+1多请求一条是为了计算是否有下一页
		List<VideoInfoDTO> videoInfoDTOs = videoInfoDAO.getVideoInfoByPage(skip, limit + 1, title, channel, beginTime, endTime);
    	GetVideoInfoByPageVO result = new GetVideoInfoByPageVO();
    	result.setHasPrePage(((pageNo > 1) ? 1 : 0));
    	int hasNextPage = (videoInfoDTOs != null && videoInfoDTOs.size() > limit) ? 1 : 0;
    	result.setHasNextPage(hasNextPage);
    	result.setCurPage(pageNo);
    	
    	List<VideoInfoDTO> videos = new ArrayList<VideoInfoDTO>();
		int size = videoInfoDTOs.size();
		for (int i = 0; i < size; i++) {
			// 如果有下一页，则丢弃最后一个元素
			if((i == size - 1) && hasNextPage == 1) {
				break;
			}
			videos.add(videoInfoDTOs.get(i));
		}
    	result.setVideos(videos);
    	
		return new ResultInfo(ResponseUtil.success_code, result);
    }
        
    /**
     * 获取指定视频日播放量
     * @param link
     * @param channel
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "/getVideoDailyCount")
    public ResultInfo getVideoDailyCount(String link, String channel, String beginTime, String endTime) {
		logger.info(getClass().getName() + ".getVideoDailyCount begin");
		try {
			// 参数检查
			if(link == null || channel == null || beginTime == null || endTime == null) {
				logger.error("param fail, link=" + link + ", channel=" + channel
						+ ", beginTime=" + beginTime + ", endTime=" + endTime);
				return new ResultInfo(ResponseUtil.param_error_code);
			}
			if(!checkTime(beginTime, endTime)) {
				logger.error("between days > " + MAX_BETWEEN_DAYS + ", beginTime=" + beginTime + ", endTime=" + endTime);
				return new ResultInfo(ResponseUtil.param_error_code, "最多允许查询" + MAX_BETWEEN_DAYS + "天内数据", null);
			}

			GetVideoDailyCountVO result = new GetVideoDailyCountVO();
			// 初始化返回数据
			List<VideoDailyCountDTO> initCountList = initVideoDailyCount(beginTime, endTime);

			VideoInfoDTO videoInfoDTO = videoInfoDAO.getVideoInfo(link, channel);
			if(videoInfoDTO == null) {
				return new ResultInfo(ResponseUtil.param_error_code, "找不到对应的视频:link="+link+", channel="+channel, null);
			}
			String videoId = videoInfoDTO.get_id();

			
			List<String> videoIds = new ArrayList<String>();
			videoIds.add(videoId);
			List<VideoDailyCountDTO> videoDailyCountDTOs = videoDailyCountDAO.getVideoDailyCount(videoIds, beginTime, endTime);
			
			// 赋值playcount
			for (VideoDailyCountDTO videoDailyCountDTO: videoDailyCountDTOs) {
				String date = videoDailyCountDTO.getDate();
				for (VideoDailyCountDTO dateVideo: initCountList) {
					if(dateVideo.getDate().equals(date)) {
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
	 * 检查时间间隔天数是否超过MAX_BETWEEN_DAYS
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	private boolean checkTime(String beginTime, String endTime) {
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(beginTime));
			long time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(endTime));
			long time2 = cal.getTimeInMillis();
			long betweenDays=(time2-time1)/(1000*3600*24);

			int bDays = Integer.parseInt(String.valueOf(betweenDays));
			return (bDays <= MAX_BETWEEN_DAYS) ? true : false;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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
				videoDailyCountSubVO.setGrowthRate(null);	// 如果从0增长到正数，则增长率为null
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
