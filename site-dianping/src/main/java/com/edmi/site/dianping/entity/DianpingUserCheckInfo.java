package com.edmi.site.dianping.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_User_Check_Info")
public class DianpingUserCheckInfo extends Model {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@LogicalPrimaryKey
	@ColumnMapping("id")
	private String id;
	
	@ColumnMapping("user_id")
	private String userId;
	
	@ColumnMapping("user_name")
	private String userName;
	
	@ColumnMapping("shop_id")
	private String shopId;
	
	@ColumnMapping("shop_name")
	private String shopName;
	
	@ColumnMapping("check_time_str")
	private String checkTimeStr;
	
	@ColumnMapping("check_time")
	private Long checkTime;
	
	@ColumnMapping("update_time")
	@FieldInsertExclude
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@ColumnMapping("insert_time")
	@FieldUpdateExclude
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public Long getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(Long checkTime) {
		this.checkTime = checkTime;
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

	public String getCheckTimeStr() {
		return checkTimeStr;
	}

	public void setCheckTimeStr(String checkTimeStr) {
		this.checkTimeStr = checkTimeStr;
	}
	
}
