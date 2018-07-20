package com.edmi.site.dianping.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.Model;
import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.FieldInsertExclude;
import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("Dianping_Shop_Comment")
public class DianpingShopComment extends Model {

	private static final long serialVersionUID = 4217070017854978866L;
	
	@LogicalPrimaryKey
	@ColumnMapping("comment_id")
	private String commentId;
	
	@ColumnMapping("shop_id")
	private String shopId;
	
	@ColumnMapping("user_id")
	private String userId;
	
	@ColumnMapping("user_name")
	private String userName;
	
	@ColumnMapping("user_level")
	private String userLevel;
	
	@ColumnMapping("is_vip")
	private Integer isVip = 0;
	
	@ColumnMapping("comment_star")
	private String commentStar;
	
	@ColumnMapping("taste_comment")
	private String tasteComment;
	
	@ColumnMapping("environment_comment")
	private String environmentComment;
	
	@ColumnMapping("service_comment")
	private String serviceComment;
	
	@ColumnMapping("avg_price")
	private String avgPrice;
	
	@ColumnMapping("comment")
	private String comment;
	
	@ColumnMapping("recommend_dish")
	private String recommendDish;
	
	@ColumnMapping("comment_time")
	private String commentTime;
	
	@ColumnMapping("favorite_num")
	private Integer favoriteNum;
	
	@ColumnMapping("reply_num")
	private Integer replyNum;
	
	@ColumnMapping("collect_num")
	private Integer collectNum;
	
	@ColumnMapping("page")
	private Integer page;
	
	@ColumnMapping("first_comment_time")
	private String firstCommentTime;
	
	@ColumnMapping("update_time")
	@FieldInsertExclude
	private String updateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	@ColumnMapping("insert_time")
	@FieldUpdateExclude
	private String insertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

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

	public String getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}

	public Integer getIsVip() {
		return isVip;
	}

	public void setIsVip(Integer isVip) {
		this.isVip = isVip;
	}

	public String getCommentStar() {
		return commentStar;
	}

	public void setCommentStar(String commentStar) {
		this.commentStar = commentStar;
	}

	public String getTasteComment() {
		return tasteComment;
	}

	public void setTasteComment(String tasteComment) {
		this.tasteComment = tasteComment;
	}

	public String getEnvironmentComment() {
		return environmentComment;
	}

	public void setEnvironmentComment(String environmentComment) {
		this.environmentComment = environmentComment;
	}

	public String getServiceComment() {
		return serviceComment;
	}

	public void setServiceComment(String serviceComment) {
		this.serviceComment = serviceComment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(String commentTime) {
		this.commentTime = commentTime;
	}

	public Integer getFavoriteNum() {
		return favoriteNum;
	}

	public void setFavoriteNum(Integer favoriteNum) {
		this.favoriteNum = favoriteNum;
	}

	public Integer getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(Integer replyNum) {
		this.replyNum = replyNum;
	}

	public Integer getCollectNum() {
		return collectNum;
	}

	public void setCollectNum(Integer collectNum) {
		this.collectNum = collectNum;
	}

	public String getFirstCommentTime() {
		return firstCommentTime;
	}

	public void setFirstCommentTime(String firstCommentTime) {
		this.firstCommentTime = firstCommentTime;
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

	public String getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(String avgPrice) {
		this.avgPrice = avgPrice;
	}

	public String getRecommendDish() {
		return recommendDish;
	}

	public void setRecommendDish(String recommendDish) {
		this.recommendDish = recommendDish;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
	
}
