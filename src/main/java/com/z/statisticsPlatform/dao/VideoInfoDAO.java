package com.z.statisticsPlatform.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.z.statisticsPlatform.dto.VideoInfoDTO;

public interface VideoInfoDAO  extends MongoRepository<VideoInfoDTO, Long> {

//	VideoInfoDTO getConfigByKey(String key);
//	public KnowledgeStrategyConfigDTO getConfigByKey(String key);
	

	/**
	 * 获取视频总数
	 * @return
	 */
	public long getCount();
	
	/**
	 * 获取视频分页数据
	 * @param skip
	 * @param limit
	 * @param title
	 * @param channel
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public List<VideoInfoDTO> getVideoInfoByPage(int skip, int limit, String title, String channel, String beginTime, String endTime);
	
	/**
	 * 获取视频数据
	 * @param title
	 * @param channel
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public List<VideoInfoDTO> getVideoInfos(String title, String channel, String beginTime, String endTime);
	
	/**
	 * 获取特定视频
	 * @param title
	 * @param channel
	 * @return
	 */
	public VideoInfoDTO getVideoInfo(String link, String channel);
	
	
}