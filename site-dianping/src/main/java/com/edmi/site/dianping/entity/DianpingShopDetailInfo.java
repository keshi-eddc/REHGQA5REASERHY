package com.edmi.site.dianping.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_Shop_Detail_Info")
public class DianpingShopDetailInfo extends Model {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@LogicalPrimaryKey
	@ColumnMapping("shop_id")
	private String shopId;
	
	@ColumnMapping("latitude")
	private String latitude;
	
	@ColumnMapping("longtitude")
	private String longtitude;
	
	@ColumnMapping("address")
	private String address;
	
	@ColumnMapping("review_num")
	private Integer reviewNum;
	
	@ColumnMapping("avg_price")
	private String avgPrice;
	
	@ColumnMapping("taste_score")
	private String tasteScore;
	
	@ColumnMapping("environment_score")
	private String environmentScore;
	
	@ColumnMapping("service_score")
	private String serviceScore;
	
	@ColumnMapping("phone")
	private String phone;
	
	@ColumnMapping("open_time")
	private String openTime;
	
	@ColumnMapping("update_time")
	@FieldInsertExclude
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@ColumnMapping("insert_time")
	@FieldUpdateExclude
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getReviewNum() {
		return reviewNum;
	}

	public void setReviewNum(Integer reviewNum) {
		this.reviewNum = reviewNum;
	}

	public String getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(String avgPrice) {
		this.avgPrice = avgPrice;
	}

	public String getTasteScore() {
		return tasteScore;
	}

	public void setTasteScore(String tasteScore) {
		this.tasteScore = tasteScore;
	}

	public String getEnvironmentScore() {
		return environmentScore;
	}

	public void setEnvironmentScore(String environmentScore) {
		this.environmentScore = environmentScore;
	}

	public String getServiceScore() {
		return serviceScore;
	}

	public void setServiceScore(String serviceScore) {
		this.serviceScore = serviceScore;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
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
