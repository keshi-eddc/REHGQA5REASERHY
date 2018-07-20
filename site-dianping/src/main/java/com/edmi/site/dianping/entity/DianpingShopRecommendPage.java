package com.edmi.site.dianping.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_Shop_Recommend_Page")
public class DianpingShopRecommendPage extends Model {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@LogicalPrimaryKey
	@ColumnMapping("id")
	private String id;
	
	@ColumnMapping("shop_id")
	private String shopId;
	
	@ColumnMapping("status")
	private Integer status = -1;
	
	@ColumnMapping("page")
	private Integer page;
	
	@ColumnMapping("total_page")
	private Integer totalPage;
	
	@ColumnMapping("version")
	private String version = Version.version_last_month;
	
	@ColumnMapping("update_time")
	@FieldInsertExclude
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@ColumnMapping("insert_time")
	@FieldUpdateExclude
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	public String getId() {
		return shopId + "-edmi-" + page;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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
