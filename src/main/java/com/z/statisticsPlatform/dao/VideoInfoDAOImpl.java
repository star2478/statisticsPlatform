package com.z.statisticsPlatform.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
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
	public List<VideoInfoDTO> getVideoInfoByPage(int skip, int limit) {
//		System.out.println("======" + skip);
//		@SuppressWarnings("deprecation")
		Aggregation aggreResult = Aggregation.newAggregation(
        		Aggregation.sort(Sort.Direction.DESC, "uploadTime"),
        		Aggregation.skip(skip),
        		Aggregation.limit(limit)
        );
          
        AggregationResults<VideoInfoDTO> results = mongoTemplate.aggregate(aggreResult, 
        		VIDEO_INFO_COL,
        		VideoInfoDTO.class);
		return results.getMappedResults();
	}
	
//	public VideoInfoDTO getConfigByName(String name) {
//		Criteria criteria = new Criteria().where("key").is(name);
//		Query query = new Query(criteria);
//		return mongoTemplate.findOne(query, VideoInfoDTO.class);
////		return this._get(Criteria.where("name").is(name));
//	}
	
//	@Override
//	public void insertKnowledgeStrategy(KnowledgeStrategyConfigDTO KnowledgeStrategyConfigDTO){
//		this._add(KnowledgeStrategyConfigDTO);
//	}
//	
//	@Override
//	public List<KnowledgeStrategyConfigDTO> getKnowledgeStrategyByPage(int skip ,int limit) {
//		Sort sort = new Sort(Sort.Direction.DESC, "opTime");
//		return this._list(null, skip, limit, sort);
//	}
//	
//	@Override
//	public boolean deleteKnowledgeStrategyByName(String key) {
//		int returnCode =  this._remove(Criteria.where("key").is(key));
//		if(returnCode == 1){
//			return true;
//		}else {
//			return false;
//		}
//	}
//	
//	@Override
//	public boolean deleteKnowledgeStrategyByOpTime(String opTime) {
//		int returnCode =  this._remove(Criteria.where("opTime").is(opTime));
//		if(returnCode == 1){
//			return true;
//		}else {
//			return false;
//		}
//	}
//	
//	@Override
//	public boolean modifyKnowledgeStrategyByName(KnowledgeStrategyConfigDTO knowledgeStrategyConfigDTO){
//		String key = knowledgeStrategyConfigDTO.getKey();
//		Update update = new Update();
//		update.set("description", knowledgeStrategyConfigDTO.getDescription());
//		update.set("status", knowledgeStrategyConfigDTO.getStatus());
//		update.set("expire", knowledgeStrategyConfigDTO.getExpire());
//		update.set("opTime", knowledgeStrategyConfigDTO.getOpTime());
//		update.set("appStrategyTriggers", knowledgeStrategyConfigDTO.getAppStrategyTriggers());
//		return this._update(Criteria.where("key").is(key), update);
//	}
//	
//	@Override
//	public long getKnowledgeStrategyNumber() {
//		return this._count(null);
//	}
//	
//	@Override
//	public List<KnowledgeStrategyConfigDTO> getAllPersistentedStrategy(int status, int persistent, int expire) {
//		Criteria criteria = Criteria.where("status").is(status).and("persistent").is(persistent).and("expire").gte(expire);
//		return this._list(criteria);
//	}
}


