package com.z.statisticsPlatform.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;


@Document(collection = "videoDailyCount")
@Component("VideoDailyCountDTO")
@CompoundIndexes({
	@CompoundIndex(background = true, unique = false, name = "date_idx", def = "{'date': -1}")
})
public class VideoDailyCountDTO {

    private String _id;
	
	private String videoId;	// 对应videoInfo表一个唯一视频，即uploadTime、title、channel共同确定的一个视频，可取videoInfo表的_id字段
	private String date;	// 统计日期，精确到天，比如2017-04-01
	private long playCount;	// videoId对应的视频在date这一天累计的播放量
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
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
