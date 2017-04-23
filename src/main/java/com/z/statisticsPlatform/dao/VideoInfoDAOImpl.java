package com.z.statisticsPlatform.dao;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.z.statisticsPlatform.dto.VideoInfoDTO;

//public class VideoInfoDAOImpl implements VideoInfoDAO {
public class VideoInfoDAOImpl {
	private final static String VIDEO_INFO_COL = "videoInfo";	//mongodb表名
	
	@Autowired
	protected MongoTemplate mongoTemplate;

//	@Override
	public long getCount() {
		return mongoTemplate.count(null, VIDEO_INFO_COL);
	}
	
	// db.videoInfo.insert({title:"这是标题", link:"www.baidu.com",uploadTime:"2017-04-01 16:00:01",channel:"Youtube",playCount:12});
//	@Override
	public List<VideoInfoDTO> getVideoInfoByPage(int skip, int limit, String title, String channel, Integer sortType, String beginTime, String endTime) {
		Criteria criteria = getCriteria(title, channel, beginTime, endTime);
		Direction sortDirection = getSortDirection(sortType);
		String sortField = getSortField(sortType);
		Aggregation aggreResult = Aggregation.newAggregation(
				Aggregation.match(criteria),
        		Aggregation.sort(sortDirection, sortField),
        		Aggregation.skip(skip),
        		Aggregation.limit(limit)
        );
          
        AggregationResults<VideoInfoDTO> results = mongoTemplate.aggregate(aggreResult, 
        		VIDEO_INFO_COL,
        		VideoInfoDTO.class);
		return results.getMappedResults();
	}
	
	public List<VideoInfoDTO> getVideoInfos(String title, String channel, Integer sortType, String beginTime, String endTime) {
		Criteria criteria = getCriteria(title, channel, beginTime, endTime);
		Direction sortDirection = getSortDirection(sortType);
		String sortField = getSortField(sortType);
		Aggregation aggreResult = Aggregation.newAggregation(
				Aggregation.match(criteria),
        		Aggregation.sort(sortDirection, sortField)
        );
          
        AggregationResults<VideoInfoDTO> results = mongoTemplate.aggregate(aggreResult, 
        		VIDEO_INFO_COL,
        		VideoInfoDTO.class);
		return results.getMappedResults();
	}
	
	public VideoInfoDTO getVideoInfo(String link, String channel) {
		Criteria criteria = new Criteria().where("link").is(link).and("channel").is(channel);
		Query query = new Query(criteria);
		return mongoTemplate.findOne(query, VideoInfoDTO.class);
	}
	
	public long getVideoPlayCountTotal(String title, String channel, String beginTime, String endTime) {
		Criteria criteria = getCriteria(title, channel, beginTime, endTime);
		Query query = new Query(criteria);
		List<VideoInfoDTO> videoInfoDTOs = mongoTemplate.find(query, VideoInfoDTO.class);
		long result = 0l;
		for (VideoInfoDTO videoInfoDTO : videoInfoDTOs) {
			result += videoInfoDTO.getPlayCount();
		}
		return result;
	}
	
	private Direction getSortDirection(Integer sortType) {
		if ( sortType == 0 || sortType == 2) {
			return Sort.Direction.DESC;
		}
		if ( sortType == 1 || sortType == 3) {
			return Sort.Direction.ASC;
		}
		
		return Sort.Direction.DESC;
	}
	
	private String getSortField(Integer sortType) {
		if ( sortType == 0 || sortType == 1) {
			return "uploadTime";
		}
		if ( sortType == 2 || sortType == 3) {
			return "playCount";
		}
		
		return "uploadTime";
	}
	
	private Criteria getCriteria(String title, String channel, String beginTime, String endTime) {
		Criteria criteria = new Criteria();
		if (StringUtils.isNotEmpty(title)) {
			String [] arrTitle = title.trim().split("\\s+");
			Pattern pattern = Pattern.compile("^.*("+String.join("|", arrTitle)+").*$", Pattern.CASE_INSENSITIVE);
			criteria.and("title").regex(pattern);
		}
		if (StringUtils.isNotEmpty(channel)) {
			criteria.and("channel").is(channel);
		}
		if (StringUtils.isNotEmpty(beginTime) && StringUtils.isNotEmpty(endTime)) {
			criteria.and("uploadTime").gte(beginTime).lte(endTime);
		}
		return criteria;
	}
}


