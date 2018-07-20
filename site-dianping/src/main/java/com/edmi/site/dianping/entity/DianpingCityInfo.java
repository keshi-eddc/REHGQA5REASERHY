package com.edmi.site.dianping.entity;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_CityInfo")
public class DianpingCityInfo implements Serializable {

	private static final long serialVersionUID = 4217070017854978866L;
	
	private Integer activeCity;// int,
	private Integer appHotLevel;// int,
	private String cityAbbrCode;// nvarchar(50),
	private String cityAreaCode;// nvarchar(50),
	private String cityEnName;// nvarchar(255),
	private String cityId;// nvarchar(50) not null,
	private String cityLevel;// nvarchar(50),
	private String cityName;// nvarchar(50),
	private String cityOrderId;// nvarchar(50),
	private String cityPyName;// nvarchar(50),
	private String directURL;// nvarchar(255),
	private String gLat;// nvarchar(50),
	private String gLng;// nvarchar(50),
	private Integer overseasCity;// int,
	private String parentCityId;// nvarchar(50),
	private String provinceId;// nvarchar(50),
	private String provinceName;// nvarchar(50),
	private Integer scenery;// int,
	private String standardEnName;// nvarchar(50),
	private Integer tuanGouFlag;// int,
	
	@FieldUpdateExclude
	@ColumnMapping("insert_time")
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	public Integer getActiveCity() {
		return activeCity;
	}

	public void setActiveCity(Integer activeCity) {
		this.activeCity = activeCity;
	}

	public Integer getAppHotLevel() {
		return appHotLevel;
	}

	public void setAppHotLevel(Integer appHotLevel) {
		this.appHotLevel = appHotLevel;
	}

	public String getCityAbbrCode() {
		return cityAbbrCode;
	}

	public void setCityAbbrCode(String cityAbbrCode) {
		this.cityAbbrCode = cityAbbrCode;
	}

	public String getCityAreaCode() {
		return cityAreaCode;
	}

	public void setCityAreaCode(String cityAreaCode) {
		this.cityAreaCode = cityAreaCode;
	}

	public String getCityEnName() {
		return cityEnName;
	}

	public void setCityEnName(String cityEnName) {
		this.cityEnName = cityEnName;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityLevel() {
		return cityLevel;
	}

	public void setCityLevel(String cityLevel) {
		this.cityLevel = cityLevel;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityOrderId() {
		return cityOrderId;
	}

	public void setCityOrderId(String cityOrderId) {
		this.cityOrderId = cityOrderId;
	}

	public String getCityPyName() {
		return cityPyName;
	}

	public void setCityPyName(String cityPyName) {
		this.cityPyName = cityPyName;
	}

	public String getDirectURL() {
		return directURL;
	}

	public void setDirectURL(String directURL) {
		this.directURL = directURL;
	}

	public String getgLat() {
		return gLat;
	}

	public void setgLat(String gLat) {
		this.gLat = gLat;
	}

	public String getgLng() {
		return gLng;
	}

	public void setgLng(String gLng) {
		this.gLng = gLng;
	}

	public Integer getOverseasCity() {
		return overseasCity;
	}

	public void setOverseasCity(Integer overseasCity) {
		this.overseasCity = overseasCity;
	}

	public String getParentCityId() {
		return parentCityId;
	}

	public void setParentCityId(String parentCityId) {
		this.parentCityId = parentCityId;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public Integer getScenery() {
		return scenery;
	}

	public void setScenery(Integer scenery) {
		this.scenery = scenery;
	}

	public String getStandardEnName() {
		return standardEnName;
	}

	public void setStandardEnName(String standardEnName) {
		this.standardEnName = standardEnName;
	}

	public Integer getTuanGouFlag() {
		return tuanGouFlag;
	}

	public void setTuanGouFlag(Integer tuanGouFlag) {
		this.tuanGouFlag = tuanGouFlag;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

}
