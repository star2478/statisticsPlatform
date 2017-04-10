package com.z.statisticsPlatform.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import com.z.statisticsPlatform.dto.VideoDailyCountDTO;
import com.z.statisticsPlatform.dto.VideoInfoDTO;

/**
 * 获取视频列表返回结果
 *
 */
public class GetVideoInfoByPageVO extends PageVO {
	
	private List<VideoInfoDTO> videos;	// 视频列表

	public List<VideoInfoDTO> getVideos() {
		return videos;
	}

	public void setVideos(List<VideoInfoDTO> videos) {
		this.videos = videos;
	}
}
