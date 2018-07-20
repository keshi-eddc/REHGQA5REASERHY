package com.edmi.site.dianping.entity;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.LogicalPrimaryKey;

public class DianpingShopInfo_Common extends DianpingShopInfo {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@ColumnMapping("page")
	private Integer page;
	
	@ColumnMapping("total_page")
	private Integer totalPage;
	
	@LogicalPrimaryKey
	@ColumnMapping("sub_category_id")
	private String subCategoryId;
	
	@LogicalPrimaryKey
	@ColumnMapping("category_id")
	private String categoryId;
	
	@ColumnMapping("primary_category_id")
	private String primaryCategoryId;
	
	@LogicalPrimaryKey
	@ColumnMapping("sub_region_id")
	private String subRegionId;
	
	@ColumnMapping("region_id")
	private String regionId;
	
	@ColumnMapping("city_id")
	private String cityId;

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

	public String getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(String subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getPrimaryCategoryId() {
		return primaryCategoryId;
	}

	public void setPrimaryCategoryId(String primaryCategoryId) {
		this.primaryCategoryId = primaryCategoryId;
	}

	public String getSubRegionId() {
		return subRegionId;
	}

	public void setSubRegionId(String subRegionId) {
		this.subRegionId = subRegionId;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	
}
