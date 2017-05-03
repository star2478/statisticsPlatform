package com.z.statisticsPlatform.vo;

import com.z.statisticsPlatform.dto.VideoDailyCountDTO;

import java.util.List;


public class GetChannelListVO {
	
	private String channelName;	// 渠道名
	
	private String channelStatus;	// 渠道状态

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelStatus() {
		return channelStatus;
	}

	public void setChannelStatus(String channelStatus) {
		this.channelStatus = channelStatus;
	}

}
