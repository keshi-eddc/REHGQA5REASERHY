package com.edmi.site.dianping.entity;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("IP_Test")
public class IpTest implements Serializable {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@ColumnMapping("source")
	private String source;

	@ColumnMapping("port")
	private Integer port;
	
	@ColumnMapping("ip")
	private String ip;
	
	@ColumnMapping("location")
	private String location;
	
	@FieldUpdateExclude
	@ColumnMapping("insert_time")
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getIp() {
		return StringUtils.isNotEmpty(ip) ? ip : "";
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLocation() {
		return StringUtils.isNotEmpty(location) ? location : "";
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

}
