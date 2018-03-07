package com.edmi.site.dianping.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_City_SubRegion")
public class DianpingCitySubRegion extends Model {

	private static final long serialVersionUID = -9122466825053508208L;
	
	@ColumnMapping("sub_region")
	private String subRegion;
	
	@LogicalPrimaryKey
	@ColumnMapping("sub_region_id")
	private String subRegionId;
	
	@ColumnMapping("region")
	private String region;
	
	@LogicalPrimaryKey
	@ColumnMapping("region_id")
	private String regionId;
	
	@LogicalPrimaryKey
	@ColumnMapping("city_id")
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

	public String getSubRegion() {
		return subRegion;
	}

	public void setSubRegion(String subRegion) {
		this.subRegion = subRegion;
	}

	public String getSubRegionId() {
		return subRegionId;
	}

	public void setSubRegionId(String subRegionId) {
		this.subRegionId = subRegionId;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
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
