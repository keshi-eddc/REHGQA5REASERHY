package com.edmi.site.dianping.entity;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_HotSearch_Rank")
public class DianpingHotSearchRank implements Serializable {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@ColumnMapping("city_cnname")
	private String cityCnname;
	
	@ColumnMapping("batch_time")
	private String batchTime;
	
	@ColumnMapping("batch_week_day")
	private Integer batchWeekDay;
	
	@ColumnMapping("batch_week")
	private Integer batchWeek;
	
	@ColumnMapping("batch_month")
	private Integer batchMonth;
	
	@ColumnMapping("rank")
	private Integer rank;
	
	@ColumnMapping("keyword")
	private String keyword;
	
	@ColumnMapping("search_count")
	private Integer searchCount;
	
	@ColumnMapping("data_type")
	private String dataType;
	
	@FieldUpdateExclude
	@ColumnMapping("update_time")
	private String updateTime;
	
	@FieldUpdateExclude
	@ColumnMapping("insert_time")
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	public String getCityCnname() {
		return cityCnname;
	}

	public void setCityCnname(String cityCnname) {
		this.cityCnname = cityCnname;
	}

	public String getBatchTime() {
		return batchTime;
	}

	public void setBatchTime(String batchTime) {
		this.batchTime = batchTime;
	}

	public Integer getBatchWeekDay() {
		return batchWeekDay;
	}

	public void setBatchWeekDay(Integer batchWeekDay) {
		this.batchWeekDay = batchWeekDay;
	}

	public Integer getBatchWeek() {
		return batchWeek;
	}

	public void setBatchWeek(Integer batchWeek) {
		this.batchWeek = batchWeek;
	}

	public Integer getBatchMonth() {
		return batchMonth;
	}

	public void setBatchMonth(Integer batchMonth) {
		this.batchMonth = batchMonth;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getSearchCount() {
		return searchCount;
	}

	public void setSearchCount(Integer searchCount) {
		this.searchCount = searchCount;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
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
