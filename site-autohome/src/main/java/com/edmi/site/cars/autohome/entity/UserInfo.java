package com.edmi.site.cars.autohome.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.annotation.LogicalPrimaryKey;

public class UserInfo {

	@LogicalPrimaryKey
	private Long UserInfoId; // bigint NOT NULL,
	
	private Long UserPlatformId; // int NOT NULL,
	
	private String UserName = ""; // nvarchar(100) NOT NULL,
	
	private String UserArea; // nvarchar(100),
	
	private String UserBirthday; // datetime,
	
	private Integer UserSex; // int,
	
	private String UserCars; // nvarchar(500),
	
	private String RegisterTime; // datetime,
	
	private String UserTitle; // nvarchar(100),
	
	private Integer UserAuth; // int,
	
	private Integer UserLevel; // int,
	
	private Integer TopicCount; // int,
	
	private Integer ReplyCount; // int,
	
	private Integer PickCount; // int,
	
	private Integer FocusCount; // int,
	
	private Integer FanCount; // int,
	
	private String urls;
	
	private String Platform; // nvarchar(20),
	
	private String InsertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"); // datetime NOT NULL,
	
	private String UpdateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"); // datetime NOT NULL,
	
	public UserInfo() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((UserInfoId == null) ? 0 : UserInfoId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInfo other = (UserInfo) obj;
		if (UserInfoId == null) {
			if (other.UserInfoId != null)
				return false;
		} else if (!UserInfoId.equals(other.UserInfoId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserInfo [UserInfoId=" + UserInfoId + ", UserPlatformId=" + UserPlatformId + ", UserName=" + UserName
				+ ", UserArea=" + UserArea + ", UserBirthday=" + UserBirthday + ", UserSex=" + UserSex + ", UserCars="
				+ UserCars + ", RegisterTime=" + RegisterTime + ", UserTitle=" + UserTitle + ", UserAuth=" + UserAuth
				+ ", UserLevel=" + UserLevel + ", TopicCount=" + TopicCount + ", ReplyCount=" + ReplyCount
				+ ", PickCount=" + PickCount + ", FocusCount=" + FocusCount + ", FanCount=" + FanCount + ", Platform="
				+ Platform + ", InsertTime=" + InsertTime + ", UpdateTime=" + UpdateTime + "]";
	}

	public Long getUserInfoId() {
		return UserInfoId;
	}

	public void setUserInfoId(Long userInfoId) {
		UserInfoId = userInfoId;
	}

	public Long getUserPlatformId() {
		return UserPlatformId;
	}

	public void setUserPlatformId(Long userPlatformId) {
		UserPlatformId = userPlatformId;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getUserArea() {
		return UserArea;
	}

	public void setUserArea(String userArea) {
		UserArea = userArea;
	}

	public String getUserBirthday() {
		return UserBirthday;
	}

	public void setUserBirthday(String userBirthday) {
		UserBirthday = userBirthday;
	}

	public Integer getUserSex() {
		return UserSex;
	}

	public void setUserSex(Integer userSex) {
		UserSex = userSex;
	}

	public String getUserCars() {
		return UserCars;
	}

	public void setUserCars(String userCars) {
		UserCars = userCars;
	}

	public String getRegisterTime() {
		return RegisterTime;
	}

	public void setRegisterTime(String registerTime) {
		RegisterTime = registerTime;
	}

	public String getUserTitle() {
		return UserTitle;
	}

	public void setUserTitle(String userTitle) {
		UserTitle = userTitle;
	}

	public Integer getUserAuth() {
		return UserAuth;
	}

	public void setUserAuth(Integer userAuth) {
		UserAuth = userAuth;
	}

	public Integer getUserLevel() {
		return UserLevel;
	}

	public void setUserLevel(Integer userLevel) {
		UserLevel = userLevel;
	}

	public Integer getTopicCount() {
		return TopicCount;
	}

	public void setTopicCount(Integer topicCount) {
		TopicCount = topicCount;
	}

	public Integer getReplyCount() {
		return ReplyCount;
	}

	public void setReplyCount(Integer replyCount) {
		ReplyCount = replyCount;
	}

	public Integer getPickCount() {
		return PickCount;
	}

	public void setPickCount(Integer pickCount) {
		PickCount = pickCount;
	}

	public Integer getFocusCount() {
		return FocusCount;
	}

	public void setFocusCount(Integer focusCount) {
		FocusCount = focusCount;
	}

	public Integer getFanCount() {
		return FanCount;
	}

	public void setFanCount(Integer fanCount) {
		FanCount = fanCount;
	}

	public String getPlatform() {
		return Platform;
	}

	public void setPlatform(String platform) {
		Platform = platform;
	}

	public String getInsertTime() {
		return InsertTime;
	}

	public void setInsertTime(String insertTime) {
		InsertTime = insertTime;
	}

	public String getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(String updateTime) {
		UpdateTime = updateTime;
	}
	
}
