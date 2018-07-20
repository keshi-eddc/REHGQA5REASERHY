package com.edmi.site.dianping.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_Shop_Recommend_Info")
public class DianpingShopRecommendInfo extends Model {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@LogicalPrimaryKey
	@ColumnMapping("id")
	private String id;
	
	@ColumnMapping("dish_id")
	private String dishId;
	
	@ColumnMapping("dish")
	private String dish;
	
	@ColumnMapping("dish_url")
	private String dishUrl;
	
	@ColumnMapping("dish_image_url")
	private String dishImageUrl;
	
	@ColumnMapping("recommend_tag")
	private String recommendTag;
	
	@ColumnMapping("shop_id")
	private String shopId;
	
	@ColumnMapping("recommend_count")
	private Integer recommendCount;
	
	@ColumnMapping("price")
	private String price;
	
	@ColumnMapping("page")
	private Integer page;
	
	@ColumnMapping("version")
	private String version = Version.version_last_month;
	
	@ColumnMapping("update_time")
	@FieldInsertExclude
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@ColumnMapping("insert_time")
	@FieldUpdateExclude
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	public String getId() {
		return dishId + "--" + shopId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDishId() {
		return dishId;
	}

	public void setDishId(String dishId) {
		this.dishId = dishId;
	}

	public String getDish() {
		return dish;
	}

	public void setDish(String dish) {
		this.dish = dish;
	}

	public String getDishUrl() {
		return dishUrl;
	}

	public void setDishUrl(String dishUrl) {
		this.dishUrl = dishUrl;
	}

	public String getDishImageUrl() {
		return dishImageUrl;
	}

	public void setDishImageUrl(String dishImageUrl) {
		this.dishImageUrl = dishImageUrl;
	}

	public String getRecommendTag() {
		return recommendTag;
	}

	public void setRecommendTag(String recommendTag) {
		this.recommendTag = recommendTag;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public Integer getRecommendCount() {
		return recommendCount;
	}

	public void setRecommendCount(Integer recommendCount) {
		this.recommendCount = recommendCount;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
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
