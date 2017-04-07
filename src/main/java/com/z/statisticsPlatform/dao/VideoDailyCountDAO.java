package com.z.statisticsPlatform.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.z.statisticsPlatform.dto.VideoDailyCountDTO;
import com.z.statisticsPlatform.dto.VideoInfoDTO;

public interface VideoDailyCountDAO extends MongoRepository<VideoInfoDTO, Long> {
	
	/**
	 * 获取指定视频一段时间内的数据
	 * @param videoId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public List<VideoDailyCountDTO> getVideoDailyCount(List<String> videoIds, String beginTime, String endTime);

}