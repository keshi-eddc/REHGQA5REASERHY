package com.edmi.site.dianping.entity;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_City")
public class DianpingCity implements Serializable {

	private static final long serialVersionUID = 4217070017854978866L;
	
	private String cityId;
	
	@LogicalPrimaryKey
	private String cityCnName;
	
	private String cityEnName;
	
	private String cityUrl;
	
	private String provinceName;
	
	private String region;
	
	private String country;

	@FieldInsertExclude
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@FieldUpdateExclude
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	@Override
	public String toString() {
		return "DianpingCity [cityId=" + cityId + ", cityCnName=" + cityCnName + ", cityEnName=" + cityEnName
				+ ", provinceName=" + provinceName + ", region=" + region + ", country=" + country + ", updateTime="
				+ updateTime + ", insertTime=" + insertTime + "]";
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityCnName() {
		return cityCnName;
	}

	public void setCityCnName(String cityCnName) {
		this.cityCnName = cityCnName;
	}

	public String getCityEnName() {
		return cityEnName;
	}

	public void setCityEnName(String cityEnName) {
		this.cityEnName = cityEnName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getCityUrl() {
		return cityUrl;
	}

	public void setCityUrl(String cityUrl) {
		this.cityUrl = cityUrl;
	}

}
