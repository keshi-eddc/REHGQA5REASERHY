package com.edmi.site.cars.autohome.entity;

import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;
@TableMapping("F_ReputationComment_P02")
public class ReputationComment {
	
	@LogicalPrimaryKey
	private Long ReputationCommentId;//		bigint	not null,	-- 口碑评论ID-PK
	
	private Long ReputationInfoId;//		bigint	not null,	-- 口碑ID
	
	private Long CommentPlatformId;//		bigint	not null,	-- 口碑评论平台ID
	
	private Integer CommentSequence;//			int,				-- 口碑评论顺序（楼层）
	
	private Long CommentAuthorId;//			bigint,				-- 口碑评论作者ID（对应UserInfoId）
	
	private String CommentAuthorName;//		varchar(200),		-- 口碑评论作者昵称
	
	private String CommentContent;//		varchar(max),		-- 口碑评论内容
	
	private String CommentSource;//			varchar(100),		-- 口碑来源平台
	
	private String PublishTime;//			datetime,			-- 发表日期
	
	private Integer LikeCount;//			int,				-- 赞人数
	
	private Integer UnlikeCount;//			int,				-- 踩人数
	
	private String Platform;//				nvarchar(20) null,-- 平台代码
	
	private String InsertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	private String UpdateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	public ReputationComment() {
//		super();
	}

	@Override
	public String toString() {
		return "ReputationComment [ReputationCommentId=" + ReputationCommentId + ", ReputationInfoId="
				+ ReputationInfoId + ", CommentPlatformId=" + CommentPlatformId + ", CommentSequence=" + CommentSequence
				+ ", CommentAuthorId=" + CommentAuthorId + ", CommentAuthorName=" + CommentAuthorName
				+ ", CommentContent=" + CommentContent + ", CommentSource=" + CommentSource + ", PublishTime="
				+ PublishTime + ", LikeCount=" + LikeCount + ", UnlikeCount=" + UnlikeCount + ", Platform=" + Platform
				+ ", InsertTime=" + InsertTime + ", UpdateTime=" + UpdateTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ReputationCommentId == null) ? 0 : ReputationCommentId.hashCode());
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
		ReputationComment other = (ReputationComment) obj;
		if (ReputationCommentId == null) {
			if (other.ReputationCommentId != null)
				return false;
		} else if (!ReputationCommentId.equals(other.ReputationCommentId))
			return false;
		return true;
	}

	public Long getReputationCommentId() {
		return ReputationCommentId;
	}

	public void setReputationCommentId(Long reputationCommentId) {
		ReputationCommentId = reputationCommentId;
	}

	public Long getReputationInfoId() {
		return ReputationInfoId;
	}

	public void setReputationInfoId(Long reputationInfoId) {
		ReputationInfoId = reputationInfoId;
	}

	public Long getCommentPlatformId() {
		return CommentPlatformId;
	}

	public void setCommentPlatformId(Long commentPlatformId) {
		CommentPlatformId = commentPlatformId;
	}

	public Integer getCommentSequence() {
		return CommentSequence;
	}

	public void setCommentSequence(Integer commentSequence) {
		CommentSequence = commentSequence;
	}

	public Long getCommentAuthorId() {
		return CommentAuthorId;
	}

	public void setCommentAuthorId(Long commentAuthorId) {
		CommentAuthorId = commentAuthorId;
	}

	public String getCommentAuthorName() {
		return CommentAuthorName;
	}

	public void setCommentAuthorName(String commentAuthorName) {
		CommentAuthorName = commentAuthorName;
	}

	public String getCommentContent() {
		return CommentContent;
	}

	public void setCommentContent(String commentContent) {
		CommentContent = commentContent;
	}

	public String getCommentSource() {
		return CommentSource;
	}

	public void setCommentSource(String commentSource) {
		CommentSource = commentSource;
	}

	public String getPublishTime() {
		return PublishTime;
	}

	public void setPublishTime(String publishTime) {
		PublishTime = publishTime;
	}

	public Integer getLikeCount() {
		return LikeCount;
	}

	public void setLikeCount(Integer likeCount) {
		LikeCount = likeCount;
	}

	public Integer getUnlikeCount() {
		return UnlikeCount;
	}

	public void setUnlikeCount(Integer unlikeCount) {
		UnlikeCount = unlikeCount;
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
