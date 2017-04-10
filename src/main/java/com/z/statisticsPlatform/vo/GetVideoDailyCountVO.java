package com.z.statisticsPlatform.vo;

import com.z.statisticsPlatform.dto.VideoDailyCountDTO;

import java.util.List;

/**
 * 获取视频日播放量返回结果
 *
 */
public class GetVideoDailyCountVO {
	
	private List<VideoDailyCountSubVO> dailyCount;	// 视频日播放量
	
	private String title;

	public List<VideoDailyCountSubVO> getDailyCount() {
		return dailyCount;
	}

	public void setDailyCount(List<VideoDailyCountSubVO> dailyCount) {
		this.dailyCount = dailyCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public class VideoDailyCountSubVO {
		private String date;	// 统计日期，精确到天，比如2017-04-01
		private long playCount;	// videoId对应的视频在date这一天累计的播放量
		private Float growthRate;

		public Float getGrowthRate() {
			return growthRate;
		}

		public void setGrowthRate(Float growthRate) {
			this.growthRate = growthRate;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public long getPlayCount() {
			return playCount;
		}

		public void setPlayCount(long playCount) {
			this.playCount = playCount;
		}
		
	}

}
