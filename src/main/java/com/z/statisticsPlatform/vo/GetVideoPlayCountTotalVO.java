package com.z.statisticsPlatform.vo;

import com.z.statisticsPlatform.dto.VideoDailyCountDTO;

import java.util.List;

/**
 * 获取视频播放量总数返回结果
 *
 */
public class GetVideoPlayCountTotalVO {
	
	private long playCountTotal;
	private int crawlingTimes;
	public long getPlayCountTotal() {
		return playCountTotal;
	}
	public void setPlayCountTotal(long playCountTotal) {
		this.playCountTotal = playCountTotal;
	}
	public int getCrawlingTimes() {
		return crawlingTimes;
	}
	public void setCrawlingTimes(int crawlingTimes) {
		this.crawlingTimes = crawlingTimes;
	}

}
