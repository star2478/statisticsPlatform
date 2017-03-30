package com.z.statisticsPlatform.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;


@Document(collection = "videoInfo")
@Component("VideoInfoDTO")
@CompoundIndexes({
	@CompoundIndex(background = true, unique = true, name = "utc_uidx", def = "{'uploadTime': -1, 'title': -1, 'channel': -1}")
})
public class VideoInfoDTO {

    private String _id;
	
	private String title;	// 视频标题
	private String link;	// 视频链接
	private String uploadTime;	// 视频上传时间，格式精确到秒（排序用），比如2017-04-01 16:00:01，如果页面上爬取不到秒级，则以0代替
	private String channel;	// 渠道名
	private long playCount;	// 累计到目前为止该视频在该渠道内的播放量
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public long getPlayCount() {
		return playCount;
	}
	public void setPlayCount(long playCount) {
		this.playCount = playCount;
	}
	
}
