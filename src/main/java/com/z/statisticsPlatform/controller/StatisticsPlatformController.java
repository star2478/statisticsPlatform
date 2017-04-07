package com.z.statisticsPlatform.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    	// 参数检查
    	if(pageNo == null || limit == null) {
			logger.error("param fail, pageNo=" + pageNo + ", limit=" + limit);
			return new ResultInfo(ResponseUtil.param_error_code);
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
     * @param title
     * @param channel
     * @param updateTime
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "/getVideoDailyCount")
    public ResultInfo getVideoDailyCount(String title, String channel, String uploadTime, String beginTime, String endTime) {
    	// 参数检查
    	if(title == null || channel == null || uploadTime == null || beginTime == null || endTime == null) {
			logger.error("param fail, title=" + title + ", channel=" + channel + ", uploadTime=" + uploadTime 
					+ ", beginTime=" + beginTime + ", endTime=" + endTime);
			return new ResultInfo(ResponseUtil.param_error_code);
		}

		VideoInfoDTO videoInfoDTO = videoInfoDAO.getVideoInfo(title, channel, uploadTime);
		if(videoInfoDTO == null) {
			return new ResultInfo(ResponseUtil.success_code, null);
		}
		String videoId = videoInfoDTO.get_id();
		List<String> videoIds = new ArrayList<String>();
		videoIds.add(videoId);
		List<VideoDailyCountDTO> videoInfoDTOs = videoDailyCountDAO.getVideoDailyCount(videoIds, beginTime, endTime);
    	
		return new ResultInfo(ResponseUtil.success_code, videoInfoDTOs);
    }
}
