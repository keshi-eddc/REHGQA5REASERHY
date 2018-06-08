package com.edmi.site.cars.autohome.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.annotation.FieldUpdateExclude;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("F_ReputationInfo_P02")
public class ReputationInfo {
	
	@LogicalPrimaryKey
	private Long ReputationInfoId;// 		bigint not null,	-- 口碑ID-主键
	
	private Long ReputationPlatformId = 0L;// 	bigint not null,	-- 口碑平台ID	
	
	private String ReputationTitle;// 		varchar(200),		-- 口碑标题	
	
	private String ReputationUrl;// 		varchar(255),		-- 口碑URL	
	
	private Integer SeriesBrandId;// 		int,				-- 车系
	
	private Long ModelBrandId;//			bigint,			-- 车型
	
	private String ReputationCategory;//		varchar(200),		-- 口碑分类
	
	private String ReputationType;//			varchar(200),		-- 口碑类型
	
	private String BuyPrice;//				varchar(100),		-- 裸车价格	
	
	private String BuyDate;//				varchar(200),		-- 购买时间	
	
	private String BuyPlace;//				varchar(200),		-- 购车地点	
	
	private String Vendor;//				varchar(200),		-- 经销商	
	
	private String FuelEconomy;//			varchar(200),		-- 油耗	
	
	private String DriveDistance;//			varchar(200),		-- 行驶距离	
	
	private String Purpose;//				nvarchar(max),	-- 购车目的	
	
	private String SatisfyingPoint;//		nvarchar(max),	-- 最满意的一点	
	
	private String UnsatisfyingPoint;//		nvarchar(max),	-- 最不满意的一点	
	
	private String OthersDesc;//			nvarchar(max),	-- 其他描述	
	
	private String ChooseReason;//			nvarchar(max),	-- 选择该车原因		
	
	private String Advantage;//				nvarchar(max),	-- 优点
	
	private String Shortcomings;//			nvarchar(max),	-- 缺点
	
	private String CostPerformanceComment;//		nvarchar(max),	-- 性价比点评	
	
	private Double CostPerformanceValue;//	decimal(9,2),		-- 性价比评分	
	
	private String ConfigComment;//			nvarchar(max),	-- 配置点评
	
	private Double ConfigValue;//			decimal(9,2),		-- 配置评分
	
	private String SpaceComment;//			nvarchar(max),	-- 空间点评
	
	private Double SpaceValue;//			decimal(9,2),		-- 空间评分	
	
	private String PowerComment;//			nvarchar(max),	-- 动力点评
	
	private Double PowerValue;//			decimal(9,2),		-- 动力评分	

	private String ControlComment;//		nvarchar(max),	-- 操控点评

	private Double ControlValue;//			decimal(9,2),		-- 操控评分	

	private String FuelComment;//			nvarchar(max),	-- 油耗点评	

	private Double FuelValue;//				decimal(9,2),		-- 油耗评分	

	private String ComfortComment;//		nvarchar(max),	-- 舒适性点评

	private Double ComfortValue;//			decimal(9,2),		-- 舒适性评分	

	private String ExteriorComment;//		nvarchar(max),	-- 外观点评	

	private Double ExteriorValue;//			decimal(9,2),		-- 外观评分	

	private String InteriorComment;//		nvarchar(max),	-- 内饰点评	

	private Double InteriorValue;//			decimal(9,2),		-- 内饰评分	

	private String MaintainComment;//		nvarchar(max),	-- 养护点评

	private Double MaintainValue;//			decimal(9,2),		-- 养护评分

	private String ServiceComment;//		nvarchar(max),	-- 服务点评

	private Double ServiceValue;//			decimal(9,2),		-- 服务评分

	private String FixComment;//			nvarchar(max),	-- 维修点评

	private Double FixValue;//				decimal(9,2),		-- 维修评分

	private String InsuranceComment;//		nvarchar(max),	-- 保险点评

	private Double InsuranceValue;//		decimal(9,2),		-- 保险评分

	private String ModifyComment;//			nvarchar(max),	-- 改装点评

	private Double ModifyValue;//			decimal(9,2),		-- 改装评分

	private String PartsComment;//			nvarchar(max),	-- 配件点评

	private Double PartsValue;//			decimal(9,2),		-- 配件评分

	private String ComprehensiveComment;//	nvarchar(max),	-- 综合点评

	private Double ComprehensiveValue;//	decimal(9,2),		-- 综合评分

	private Long ReputationAuthorId;//			[bigint],				-- 口碑评论作者ID（对应UserInfoId）

	private String ReputationAuthorName;//		[varchar](200),		-- 口碑评论作者昵称

	private String PublishTime;//			datetime			--发布日期

	private String ReputationSource;

	private Integer ViewCount;//			int,				-- 浏览人数

	private Integer CommentCount;//			int,				-- 评论人数

	private Integer LikeCount;//			int,				-- 赞人数

	private Integer UnlikeCount;//			int,				-- 踩人数

	private String Platform;//				nvarchar(20) null,-- 平台代码

	@FieldUpdateExclude
	private String InsertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

//	@FieldInsertExclude
	private String UpdateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	public ReputationInfo() {
		super();
	}

	@Override
	public String toString() {
		return "ReputationInfo [ReputationInfoId=" + ReputationInfoId + ", ReputationPlatformId=" + ReputationPlatformId
				+ ", ReputationTitle=" + ReputationTitle + ", ReputationUrl=" + ReputationUrl + ", SeriesBrandId="
				+ SeriesBrandId + ", ModelBrandId=" + ModelBrandId + ", ReputationCategory=" + ReputationCategory
				+ ", ReputationType=" + ReputationType + ", BuyPrice=" + BuyPrice + ", BuyDate=" + BuyDate + ", BuyPlace="
				+ BuyPlace + ", Vendor=" + Vendor + ", FuelEconomy=" + FuelEconomy + ", DriveDistance=" + DriveDistance
				+ ", Purpose=" + Purpose + ", SatisfyingPoint=" + SatisfyingPoint + ", UnsatisfyingPoint="
				+ UnsatisfyingPoint + ", OthersDesc=" + OthersDesc + ", ChooseReason=" + ChooseReason + ", Advantage=" + Advantage + ", Shortcomings=" + Shortcomings
				+ ", CostPerformanceComment=" + CostPerformanceComment + ", CostPerformanceValue="
				+ CostPerformanceValue + ", ConfigComment=" + ConfigComment + ", ConfigValue=" + ConfigValue
				+ ", SpaceComment=" + SpaceComment + ", SpaceValue=" + SpaceValue + ", PowerComment=" + PowerComment
				+ ", PowerValue=" + PowerValue + ", ControlComment=" + ControlComment + ", ControlValue=" + ControlValue
				+ ", FuelComment=" + FuelComment + ", FuelValue=" + FuelValue + ", ComfortComment=" + ComfortComment
				+ ", ComfortValue=" + ComfortValue + ", ExteriorComment=" + ExteriorComment + ", ExteriorValue="
				+ ExteriorValue + ", InteriorComment=" + InteriorComment + ", InteriorValue=" + InteriorValue
				+ ", MaintainComment=" + MaintainComment + ", MaintainValue=" + MaintainValue + ", ServiceComment="
				+ ServiceComment + ", ServiceValue=" + ServiceValue + ", FixComment=" + FixComment + ", FixValue="
				+ FixValue + ", InsuranceComment=" + InsuranceComment + ", InsuranceValue=" + InsuranceValue
				+ ", ModifyComment=" + ModifyComment + ", ModifyValue=" + ModifyValue + ", PartsComment=" + PartsComment
				+ ", PartsValue=" + PartsValue + ", ComprehensiveComment=" + ComprehensiveComment
				+ ", ComprehensiveValue=" + ComprehensiveValue + ", CommentAuthorId=" + ReputationAuthorId
				+ ", CommentAuthorName=" + ReputationAuthorName + ", PublishTime=" + PublishTime + ", ViewCount="
				+ ViewCount + ", CommentCount=" + CommentCount + ", LikeCount=" + LikeCount + ", UnlikeCount="
				+ UnlikeCount + ", Platform=" + Platform + ", InsertTime=" + InsertTime + ", UpdateTime=" + UpdateTime
				+ "]";
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((ReputationInfoId == null) ? 0 : ReputationInfoId.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		ReputationInfo other = (ReputationInfo) obj;
//		if (ReputationInfoId == null) {
//			if (other.ReputationInfoId != null)
//				return false;
//		} else if (!ReputationInfoId.equals(other.ReputationInfoId))
//			return false;
//		return true;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ReputationUrl == null) ? 0 : ReputationUrl.hashCode());
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
		ReputationInfo other = (ReputationInfo) obj;
		if (ReputationUrl == null) {
			if (other.ReputationUrl != null)
				return false;
		} else if (!ReputationUrl.equals(other.ReputationUrl))
			return false;
		return true;
	}

	public Long getReputationInfoId() {
		return ReputationInfoId;
	}

	public void setReputationInfoId(Long reputationInfoId) {
		ReputationInfoId = reputationInfoId;
	}

	public Long getReputationPlatformId() {
		return ReputationPlatformId;
	}

	public void setReputationPlatformId(Long reputationPlatformId) {
		ReputationPlatformId = reputationPlatformId;
	}

	public String getReputationTitle() {
		return ReputationTitle;
	}

	public void setReputationTitle(String reputationTitle) {
		ReputationTitle = reputationTitle;
	}

	public String getReputationUrl() {
		return ReputationUrl;
	}

	public void setReputationUrl(String reputationUrl) {
		ReputationUrl = reputationUrl;
	}

	public Integer getSeriesBrandId() {
		return SeriesBrandId;
	}

	public void setSeriesBrandId(Integer seriesBrandId) {
		SeriesBrandId = seriesBrandId;
	}

	public Long getModelBrandId() {
		return ModelBrandId;
	}

	public void setModelBrandId(Long modelBrandId) {
		ModelBrandId = modelBrandId;
	}

	public String getReputationCategory() {
		return ReputationCategory;
	}

	public void setReputationCategory(String reputationCategory) {
		ReputationCategory = reputationCategory;
	}

	public String getReputationType() {
		return ReputationType;
	}

	public void setReputationType(String reputationType) {
		ReputationType = reputationType;
	}

	public String getBuyPrice() {
		return BuyPrice;
	}

	public void setBuyPrice(String buyPrice) {
		BuyPrice = buyPrice;
	}

	public String getBuyDate() {
		return BuyDate;
	}

	public void setBuyDate(String buyDate) {
		BuyDate = buyDate;
	}

	public String getBuyPlace() {
		return BuyPlace;
	}

	public void setBuyPlace(String buyPlace) {
		BuyPlace = buyPlace;
	}

	public String getVendor() {
		return Vendor;
	}

	public void setVendor(String vendor) {
		Vendor = vendor;
	}

	public String getFuelEconomy() {
		return FuelEconomy;
	}

	public void setFuelEconomy(String fuelEconomy) {
		FuelEconomy = fuelEconomy;
	}

	public String getDriveDistance() {
		return DriveDistance;
	}

	public void setDriveDistance(String driveDistance) {
		DriveDistance = driveDistance;
	}

	public String getPurpose() {
		return Purpose;
	}

	public void setPurpose(String purpose) {
		Purpose = purpose;
	}

	public String getSatisfyingPoint() {
		return SatisfyingPoint;
	}

	public void setSatisfyingPoint(String satisfyingPoint) {
		SatisfyingPoint = satisfyingPoint;
	}

	public String getUnsatisfyingPoint() {
		return UnsatisfyingPoint;
	}

	public void setUnsatisfyingPoint(String unsatisfyingPoint) {
		UnsatisfyingPoint = unsatisfyingPoint;
	}

	public String getCostPerformanceComment() {
		return CostPerformanceComment;
	}

	public void setCostPerformanceComment(String costPerformanceComment) {
		CostPerformanceComment = costPerformanceComment;
	}

	public String getOthersDesc() {
		return OthersDesc;
	}

	public void setOthersDesc(String othersDesc) {
		OthersDesc = othersDesc;
	}

	public String getChooseReason() {
		return ChooseReason;
	}

	public void setChooseReason(String chooseReason) {
		ChooseReason = chooseReason;
	}

	public String getAdvantage() {
		return Advantage;
	}

	public void setAdvantage(String advantage) {
		Advantage = advantage;
	}

	public String getShortcomings() {
		return Shortcomings;
	}

	public void setShortcomings(String shortcomings) {
		Shortcomings = shortcomings;
	}

	public Double getCostPerformanceValue() {
		return CostPerformanceValue;
	}

	public void setCostPerformanceValue(Double costPerformanceValue) {
		CostPerformanceValue = costPerformanceValue;
	}

	public String getConfigComment() {
		return ConfigComment;
	}

	public void setConfigComment(String configComment) {
		ConfigComment = configComment;
	}

	public Double getConfigValue() {
		return ConfigValue;
	}

	public void setConfigValue(Double configValue) {
		ConfigValue = configValue;
	}

	public String getSpaceComment() {
		return SpaceComment;
	}

	public void setSpaceComment(String spaceComment) {
		SpaceComment = spaceComment;
	}

	public Double getSpaceValue() {
		return SpaceValue;
	}

	public void setSpaceValue(Double spaceValue) {
		SpaceValue = spaceValue;
	}

	public String getPowerComment() {
		return PowerComment;
	}

	public void setPowerComment(String powerComment) {
		PowerComment = powerComment;
	}

	public Double getPowerValue() {
		return PowerValue;
	}

	public void setPowerValue(Double powerValue) {
		PowerValue = powerValue;
	}

	public String getControlComment() {
		return ControlComment;
	}

	public void setControlComment(String controlComment) {
		ControlComment = controlComment;
	}

	public Double getControlValue() {
		return ControlValue;
	}

	public void setControlValue(Double controlValue) {
		ControlValue = controlValue;
	}

	public String getFuelComment() {
		return FuelComment;
	}

	public void setFuelComment(String fuelComment) {
		FuelComment = fuelComment;
	}

	public Double getFuelValue() {
		return FuelValue;
	}

	public void setFuelValue(Double fuelValue) {
		FuelValue = fuelValue;
	}

	public String getComfortComment() {
		return ComfortComment;
	}

	public void setComfortComment(String comfortComment) {
		ComfortComment = comfortComment;
	}

	public Double getComfortValue() {
		return ComfortValue;
	}

	public void setComfortValue(Double comfortValue) {
		ComfortValue = comfortValue;
	}

	public String getExteriorComment() {
		return ExteriorComment;
	}

	public void setExteriorComment(String exteriorComment) {
		ExteriorComment = exteriorComment;
	}

	public Double getExteriorValue() {
		return ExteriorValue;
	}

	public void setExteriorValue(Double exteriorValue) {
		ExteriorValue = exteriorValue;
	}

	public String getInteriorComment() {
		return InteriorComment;
	}

	public void setInteriorComment(String interiorComment) {
		InteriorComment = interiorComment;
	}

	public Double getInteriorValue() {
		return InteriorValue;
	}

	public void setInteriorValue(Double interiorValue) {
		InteriorValue = interiorValue;
	}

	public String getMaintainComment() {
		return MaintainComment;
	}

	public void setMaintainComment(String maintainComment) {
		MaintainComment = maintainComment;
	}

	public Double getMaintainValue() {
		return MaintainValue;
	}

	public void setMaintainValue(Double maintainValue) {
		MaintainValue = maintainValue;
	}

	public String getServiceComment() {
		return ServiceComment;
	}

	public void setServiceComment(String serviceComment) {
		ServiceComment = serviceComment;
	}

	public Double getServiceValue() {
		return ServiceValue;
	}

	public void setServiceValue(Double serviceValue) {
		ServiceValue = serviceValue;
	}

	public String getFixComment() {
		return FixComment;
	}

	public void setFixComment(String fixComment) {
		FixComment = fixComment;
	}

	public Double getFixValue() {
		return FixValue;
	}

	public void setFixValue(Double fixValue) {
		FixValue = fixValue;
	}

	public String getInsuranceComment() {
		return InsuranceComment;
	}

	public void setInsuranceComment(String insuranceComment) {
		InsuranceComment = insuranceComment;
	}

	public Double getInsuranceValue() {
		return InsuranceValue;
	}

	public void setInsuranceValue(Double insuranceValue) {
		InsuranceValue = insuranceValue;
	}

	public String getModifyComment() {
		return ModifyComment;
	}

	public void setModifyComment(String modifyComment) {
		ModifyComment = modifyComment;
	}

	public Double getModifyValue() {
		return ModifyValue;
	}

	public void setModifyValue(Double modifyValue) {
		ModifyValue = modifyValue;
	}

	public String getPartsComment() {
		return PartsComment;
	}

	public void setPartsComment(String partsComment) {
		PartsComment = partsComment;
	}

	public Double getPartsValue() {
		return PartsValue;
	}

	public void setPartsValue(Double partsValue) {
		PartsValue = partsValue;
	}

	public String getComprehensiveComment() {
		return ComprehensiveComment;
	}

	public void setComprehensiveComment(String comprehensiveComment) {
		ComprehensiveComment = comprehensiveComment;
	}

	public Double getComprehensiveValue() {
		return ComprehensiveValue;
	}

	public void setComprehensiveValue(Double comprehensiveValue) {
		ComprehensiveValue = comprehensiveValue;
	}

	public Long getReputationAuthorId() {
		return ReputationAuthorId;
	}

	public void setReputationAuthorId(Long reputationAuthorId) {
		ReputationAuthorId = reputationAuthorId;
	}

	public String getReputationAuthorName() {
		return ReputationAuthorName;
	}

	public void setReputationAuthorName(String reputationAuthorName) {
		ReputationAuthorName = reputationAuthorName;
	}

	public String getPublishTime() {
		return PublishTime;
	}

	public void setPublishTime(String publishTime) {
		PublishTime = publishTime;
	}

	public Integer getViewCount() {
		return ViewCount;
	}

	public void setViewCount(Integer viewCount) {
		ViewCount = viewCount;
	}

	public Integer getCommentCount() {
		return CommentCount;
	}

	public void setCommentCount(Integer commentCount) {
		CommentCount = commentCount;
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

	public String getReputationSource() {
		return ReputationSource;
	}

	public void setReputationSource(String reputationSource) {
		ReputationSource = reputationSource;
	};
	
	
}
