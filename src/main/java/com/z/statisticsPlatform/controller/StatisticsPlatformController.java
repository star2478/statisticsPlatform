package com.z.statisticsPlatform.controller;


import java.util.List;

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
    public List<VideoInfoDTO> getVideoInfoByPage(Integer pageNo, Integer limit) {
		long count = videoInfoDAO.getCount();
		Long totalPageNumer = (count % limit) == 0 ? count/limit :(count/limit) + 1;
		//如果请求的页码大于总页数，则返回错误
		if(totalPageNumer > 0 && pageNo > totalPageNumer) {
			logger.error("param fail, pageNo=" + pageNo + ", limit=" + limit);
//			buildResponse(modelMap, ResponseEnum.INVALID_PARAMS);
			return null;
		}
		
		Integer skip = (pageNo - 1) * limit;
    	List<VideoInfoDTO> videoInfoDTOs = videoInfoDAO.getVideoInfoByPage(skip, limit);
    	return videoInfoDTOs;
    }
}
