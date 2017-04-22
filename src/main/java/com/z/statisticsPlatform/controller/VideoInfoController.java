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
public class VideoInfoController {
	private final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private VideoInfoDAO videoInfoDAO;

    /**
     * 获取视频统计数据分页数据
     * @param pageNo
     * @param limit
     * @return
     */
    @RequestMapping(value = "/getVideoInfoByPage")
    public ResultInfo getVideoInfoByPage(Integer pageNo, Integer limit, String title, String channel, String beginTime, String endTime) {
		logger.info(getClass().getName() + ".getVideoInfoByPage  begin");
		try {
	    	// 参数检查
	    	if(pageNo == null || limit == null) {
				logger.error("param fail, pageNo=" + pageNo + ", limit=" + limit);
				return new ResultInfo(ResponseUtil.param_error_code);
			}
			if(!Utils.checkTime(beginTime, endTime)) {
				logger.error("between days > " + Utils.MAX_BETWEEN_DAYS + ", beginTime=" + beginTime + ", endTime=" + endTime);
				return new ResultInfo(ResponseUtil.param_error_code, "最多允许查询" + Utils.MAX_BETWEEN_DAYS + "天内数据", null);
			}
	    	
	    	if (channel != null && channel.equals("所有渠道")) {
	    		channel = null;
	    	}
			
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
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(ResponseUtil.faile_code);
		}
    }
    
    /**
     * 获取视频统计数据，以csv格式输出
     * @param title
     * @param channel
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "/getVideoInfoForExport")
    public ResultInfo getVideoInfoForExport(String title, String channel, String beginTime, String endTime) {
		logger.info(getClass().getName() + ".getVideoInfoForExport  begin");
		try {
	    	// 参数检查
			if(!Utils.checkTime(beginTime, endTime)) {
				logger.error("between days > " + Utils.MAX_BETWEEN_DAYS + ", beginTime=" + beginTime + ", endTime=" + endTime);
				return new ResultInfo(ResponseUtil.param_error_code, "最多允许查询" + Utils.MAX_BETWEEN_DAYS + "天内数据", null);
			}
	    	
	    	if (channel != null && channel.equals("所有渠道")) {
	    		channel = null;
	    	}
	
			List<VideoInfoDTO> videoInfoDTOs = videoInfoDAO.getVideoInfos(title, channel, beginTime, endTime);
	    	String result = "标题,视频链接,渠道,上传时间,累计播放量";
	    	for (VideoInfoDTO videoInfoDTO : videoInfoDTOs) {
				String info = "\n" + videoInfoDTO.getTitle() + "," +
						videoInfoDTO.getLink() + "," +
						videoInfoDTO.getChannel() + "," +
						videoInfoDTO.getUploadTime() + "," +
						videoInfoDTO.getPlayCount();
				result += info;
			}
			return new ResultInfo(ResponseUtil.success_code, result);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResultInfo(ResponseUtil.faile_code);
		}
    }
}
