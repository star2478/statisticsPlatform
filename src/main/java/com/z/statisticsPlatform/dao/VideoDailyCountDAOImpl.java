package com.z.statisticsPlatform.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.z.statisticsPlatform.dto.VideoInfoDTO;

import org.springframework.data.mongodb.repository.MongoRepository;

//@Component("KnowledgeStrategyConfigDAO")
//public class KnowledgeStrategyConfigDAOImpl extends BaseMongoDAO<KnowledgeStrategyConfigDTO> implements KnowledgeStrategyConfigDAO {
//public class KnowledgeStrategyConfigDAOImpl implements KnowledgeStrategyConfigDAO,MongoRepository<KnowledgeStrategyConfigDTO, Long> {
public class VideoDailyCountDAOImpl{// implements KnowledgeStrategyConfigDAO {
	private final static String VIDEO_DAILY_COUNT_COL = "videoDailyCount";	//mongodb表名
	
	@Autowired
	protected MongoTemplate mongoTemplate;

}


