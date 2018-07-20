package com.edmi.site.dianping.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_User_Info")
public class DianpingUserInfo extends Model {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@LogicalPrimaryKey
	@ColumnMapping("user_id")
	private String userId;
	
	@ColumnMapping("user_name")
	private String userName;
	
	@ColumnMapping("is_vip")
	private Integer isVip;
	
	@ColumnMapping("user_level")
	private String userLevel;
	
	@ColumnMapping("sex")
	private String sex;
	
	@ColumnMapping("city")
	private String city;
	
	@ColumnMapping("focus_num")
	private Integer focusNum;
	
	@ColumnMapping("fans_num")
	private Integer fansNum;
	
	@ColumnMapping("interaction_num")
	private Integer interactionNum;
	
	@ColumnMapping("contribution")
	private String contribution;
	
	@ColumnMapping("community_level")
	private String communityLevel;
	
	@ColumnMapping("regist_time")
	private String registTime;
	
	@ColumnMapping("love_situation")
	private String loveSituation;
	
	@ColumnMapping("birthday")
	private String birthday;
	
	@ColumnMapping("star")
	private String star;
	
	@ColumnMapping("update_time")
	@FieldInsertExclude
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@ColumnMapping("insert_time")
	@FieldUpdateExclude
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

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

	public Integer getIsVip() {
		return isVip;
	}

	public void setIsVip(Integer isVip) {
		this.isVip = isVip;
	}

	public String getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getFocusNum() {
		return focusNum;
	}

	public void setFocusNum(Integer focusNum) {
		this.focusNum = focusNum;
	}

	public Integer getFansNum() {
		return fansNum;
	}

	public void setFansNum(Integer fansNum) {
		this.fansNum = fansNum;
	}

	public Integer getInteractionNum() {
		return interactionNum;
	}

	public void setInteractionNum(Integer interactionNum) {
		this.interactionNum = interactionNum;
	}

	public String getContribution() {
		return contribution;
	}

	public void setContribution(String contribution) {
		this.contribution = contribution;
	}

	public String getCommunityLevel() {
		return communityLevel;
	}

	public void setCommunityLevel(String communityLevel) {
		this.communityLevel = communityLevel;
	}

	public String getRegistTime() {
		return registTime;
	}

	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}

	public String getLoveSituation() {
		return loveSituation;
	}

	public void setLoveSituation(String loveSituation) {
		this.loveSituation = loveSituation;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
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
