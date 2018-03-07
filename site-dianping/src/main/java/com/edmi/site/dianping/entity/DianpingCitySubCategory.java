package com.edmi.site.dianping.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_City_SubCategory")
public class DianpingCitySubCategory extends Model {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@ColumnMapping("sub_category")
	private String subCategory;
	
	@ColumnMapping("sub_category_id")
	@LogicalPrimaryKey
	private String subCategoryId;
	
	@ColumnMapping("category")
	private String category;
	
	@ColumnMapping("category_id")
	@LogicalPrimaryKey
	private String categoryId;
	
	@ColumnMapping("primary_category")
	private String primaryCategory;
	
	@ColumnMapping("primary_category_id")
	private String primaryCategoryId;
	
	@ColumnMapping("city_id")
	@LogicalPrimaryKey
	private String cityId;
	
	@ColumnMapping("city_cnname")
	private String cityCnname;
	
	@ColumnMapping("city_enname")
	private String cityEnname;

	@ColumnMapping("update_time")
	@FieldInsertExclude
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@ColumnMapping("insert_time")
	@FieldUpdateExclude
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(String subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getPrimaryCategory() {
		return primaryCategory;
	}

	public void setPrimaryCategory(String primaryCategory) {
		this.primaryCategory = primaryCategory;
	}

	public String getPrimaryCategoryId() {
		return primaryCategoryId;
	}

	public void setPrimaryCategoryId(String primaryCategoryId) {
		this.primaryCategoryId = primaryCategoryId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityCnname() {
		return cityCnname;
	}

	public void setCityCnname(String cityCnname) {
		this.cityCnname = cityCnname;
	}

	public String getCityEnname() {
		return cityEnname;
	}

	public void setCityEnname(String cityEnname) {
		this.cityEnname = cityEnname;
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
