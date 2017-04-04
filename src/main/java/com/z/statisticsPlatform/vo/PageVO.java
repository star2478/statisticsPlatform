package com.z.statisticsPlatform.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

public class PageVO {
	
	private int hasPrePage;	// 是否有上一页
	private int hasNextPage;	// 是否有下一页
	public int getHasPrePage() {
		return hasPrePage;
	}
	public void setHasPrePage(int hasPrePage) {
		this.hasPrePage = hasPrePage;
	}
	public int getHasNextPage() {
		return hasNextPage;
	}
	public void setHasNextPage(int hasNextPage) {
		this.hasNextPage = hasNextPage;
	}
	
}
