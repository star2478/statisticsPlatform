package com.z.statisticsPlatform.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.z.statisticsPlatform.dto.VideoDailyCountDTO;
import com.z.statisticsPlatform.dto.VideoInfoDTO;

import org.springframework.data.mongodb.repository.MongoRepository;

//@Component("KnowledgeStrategyConfigDAO")
public class VideoDailyCountDAOImpl{
	private final static String VIDEO_DAILY_COUNT_COL = "videoDailyCount";	//mongodb表名
	
	@Autowired
	protected MongoTemplate mongoTemplate;

	// {"videoId" : "58e27416343e55c71cc8d213", "date" : "2017-04-08", "playCount" : 5 }
	public List<VideoDailyCountDTO> getVideoDailyCount(List<String> videoIds, String beginTime, String endTime) {
		Criteria criteria = new Criteria().where("videoId").in(videoIds);
		if (StringUtils.isNotEmpty(beginTime) && StringUtils.isNotEmpty(endTime)) {
			criteria.and("date").gte(beginTime).lte(endTime);
		}
		Aggregation aggreResult = Aggregation.newAggregation(
				Aggregation.match(criteria),
        		Aggregation.sort(Sort.Direction.DESC, "date")
        );
          
        AggregationResults<VideoDailyCountDTO> results = mongoTemplate.aggregate(aggreResult, 
        		VIDEO_DAILY_COUNT_COL,
        		VideoDailyCountDTO.class);
		return results.getMappedResults();
	}
}


