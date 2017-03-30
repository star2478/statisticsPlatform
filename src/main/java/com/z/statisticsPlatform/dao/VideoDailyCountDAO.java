package com.z.statisticsPlatform.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.z.statisticsPlatform.dto.VideoInfoDTO;

public interface VideoDailyCountDAO extends MongoRepository<VideoInfoDTO, Long> {
//public interface KnowledgeStrategyConfigDAO {

//	VideoInfoDTO getConfigByKey(String key);
////	public KnowledgeStrategyConfigDTO getConfigByKey(String key);
//	
//	public VideoInfoDTO getConfigByName(String name);
	
//	public void insertKnowledgeStrategy(KnowledgeStrategyConfigDTO KnowledgeStrategyConfigDTO);
//	
//	public List<KnowledgeStrategyConfigDTO> getKnowledgeStrategyByPage(int skip ,int limit);
//	
//	public boolean deleteKnowledgeStrategyByName(String name);
//	
//	public boolean deleteKnowledgeStrategyByOpTime(String opTime);
//	
//	public boolean modifyKnowledgeStrategyByName(KnowledgeStrategyConfigDTO knowledgeStrategyConfigDTO);
//	
//	public long getKnowledgeStrategyNumber();
//	
//	public List<KnowledgeStrategyConfigDTO> getAllPersistentedStrategy(int status, int persistent, int expire);
	
//	public void dropCol();
}