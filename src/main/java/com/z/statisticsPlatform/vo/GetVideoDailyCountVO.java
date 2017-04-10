package com.z.statisticsPlatform.vo;

import com.z.statisticsPlatform.dto.VideoDailyCountDTO;

import java.util.List;

/**
 * 获取视频日播放量返回结果
 *
 */
public class GetVideoDailyCountVO {
	
	private List<VideoDailyCountDTO> dailyCount;	// 视频日播放量
	
	private String title;

	public List<VideoDailyCountDTO> getDailyCount() {
		return dailyCount;
	}

	public void setDailyCount(List<VideoDailyCountDTO> dailyCount) {
		this.dailyCount = dailyCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


}
