package com.edmi.site.dianping.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_SubCategory_SubRegion")
public class DianpingSubCategorySubRegion extends Model {
	
	private static final long serialVersionUID = -2778523730542207946L;
	
	@LogicalPrimaryKey
	@ColumnMapping("url")
	private String url;
	
	@ColumnMapping("sub_category_id")
	private String subCategoryId;
	
	@ColumnMapping("sub_category")
	private String subCategory;
	
	@ColumnMapping("category_id")
	private String categoryId;
	
	@ColumnMapping("category")
	private String category;
	
	@ColumnMapping("primary_category_id")
	private String primaryCategoryId;
	
	@ColumnMapping("primary_category")
	private String primaryCategory;
	
	@ColumnMapping("sub_region_id")
	private String subRegionId;

	@ColumnMapping("sub_region")
	private String subRegion;
	
	@ColumnMapping("region_id")
	private String regionId;
	
	@ColumnMapping("region")
	private String region;
	
	@ColumnMapping("city_id")
	private String cityId;
	
	@ColumnMapping("city_cnname")
	private String cityCnname;
	
	@ColumnMapping("city_enname")
	private String cityEnname;
	
	@ColumnMapping("shop_total_page")
	private Integer shopTotalPage = -1;
	
	@FieldInsertExclude
	@ColumnMapping("update_time")
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@ColumnMapping("insert_time")
	@FieldUpdateExclude
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(String subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPrimaryCategoryId() {
		return primaryCategoryId;
	}

	public void setPrimaryCategoryId(String primaryCategoryId) {
		this.primaryCategoryId = primaryCategoryId;
	}

	public String getPrimaryCategory() {
		return primaryCategory;
	}

	public void setPrimaryCategory(String primaryCategory) {
		this.primaryCategory = primaryCategory;
	}

	public String getSubRegionId() {
		return subRegionId;
	}

	public void setSubRegionId(String subRegionId) {
		this.subRegionId = subRegionId;
	}

	public String getSubRegion() {
		return subRegion;
	}

	public void setSubRegion(String subRegion) {
		this.subRegion = subRegion;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
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

	public Integer getShopTotalPage() {
		return shopTotalPage;
	}

	public void setShopTotalPage(Integer shopTotalPage) {
		this.shopTotalPage = shopTotalPage;
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
