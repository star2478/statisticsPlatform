package com.z.statisticsPlatform.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.z.statisticsPlatform.dao.VideoInfoDAO;
import com.z.statisticsPlatform.dto.VideoInfoDTO;
import com.z.statisticsPlatform.util.ResponseUtil;
import com.z.statisticsPlatform.util.ResultInfo;

@RestController
public class StatisticsPlatformController {
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
    	// 参数检查
    	if(pageNo == null || limit == null) {
			logger.error("param fail, pageNo=" + pageNo + ", limit=" + limit);
			return new ResultInfo(ResponseUtil.param_error_code);
		}
    	
    	channel = (channel != null && !channel.equals("所有渠道")) ? channel : null;
    	
		long count = videoInfoDAO.getCount();
		Long totalPageNumber = (count % limit) == 0 ? count/limit :(count/limit) + 1;
		// 如果请求的页码大于总页数，则返回错误
		if(totalPageNumber > 0 && pageNo > totalPageNumber) {
			logger.error("param fail, pageNo=" + pageNo + ", limit=" + limit);
			return new ResultInfo(ResponseUtil.param_error_code);
		}
		
		Integer skip = (pageNo - 1) * limit;
    	List<VideoInfoDTO> videoInfoDTOs = videoInfoDAO.getVideoInfoByPage(skip, limit + 1, title, channel, beginTime, endTime);
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("videos", videoInfoDTOs);
    	result.put("totalPageNumber", ((videoInfoDTOs != null && videoInfoDTOs.size() > 0) ? totalPageNumber : 0));	// 此处totalPageNumber表示所有数据总页数，而非
    	result.put("currentPageNumber", pageNo);
    	result.put("hasPrePage", ((pageNo > 1) ? 1 : 0));
    	result.put("hasNextPage", ((videoInfoDTOs != null && videoInfoDTOs.size() > limit) ? 1 : 0));
		return new ResultInfo(ResponseUtil.success_code, result);
    }
}
