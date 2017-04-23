package com.z.statisticsPlatform.controller;


import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.z.statisticsPlatform.util.ResponseUtil;
import com.z.statisticsPlatform.util.ResultInfo;

@RestController
public class ChannelController {
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * 获取所有渠道
	 * @return
	 */
    @RequestMapping(value = "/getChannelList")
    public ResultInfo getChannelList() {
    	logger.info(getClass().getName() + ".getChannelList begin");
    	List<String> groups = Arrays.asList("优酷", "腾讯视频", "搜狐视频", "爱奇艺", "乐视视频", "A站", "B站", "土豆", "美拍", "Youtube", "第一视频", "秀兜", "人人视频", "百度视频", "360影视", "今日头条", "ZAKER", "UC", "一点资讯", "天天快报", "凤凰网", "北京时间", "搜狐新闻", "秒拍");
		Comparator<Object> comp = Collator.getInstance(java.util.Locale.CHINA);
	    int size = groups.size();
	    String[] sortArray = (String[]) groups.toArray(new String[size]);
		Arrays.sort(sortArray, comp);
    	Map<String, Object> result = new HashMap<String, Object>();
    	result.put("channels", sortArray);
		return new ResultInfo(ResponseUtil.success_code, result);
    }
}
