package com.edmi.site.cars.autohome.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("T_Topic_Crawled")
public class TopicCrawled {
	
	@ColumnMapping("topic_info_id")
	private String topicInfoId;
	
	@ColumnMapping("topic_url")
	private String topicUrl;
	
	@ColumnMapping("load_status")
	private Integer loadStatus = 1;
	
	@ColumnMapping("update_time")
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@ColumnMapping("insert_time")
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	public String getTopicInfoId() {
		return topicInfoId;
	}

	public void setTopicInfoId(String topicInfoId) {
		this.topicInfoId = topicInfoId;
	}

	public String getTopicUrl() {
		return topicUrl;
	}

	public void setTopicUrl(String topicUrl) {
		this.topicUrl = topicUrl;
	}

	public Integer getLoadStatus() {
		return loadStatus;
	}

	public void setLoadStatus(Integer loadStatus) {
		this.loadStatus = loadStatus;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}
	
}
