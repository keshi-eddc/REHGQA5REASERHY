package com.edmi.site.cars.autohome.entity;

import fun.jerry.entity.annotation.TableMapping;

@TableMapping("D_ModelBrand")
public class ModelBrand {
	private Long ModelBrandId; // bigint NOT NULL,
	private Integer SeriesBrandId; // int NOT NULL,
	private String ModelBrandName; // nvarchar(100) NOT NULL,
	private String ModelBrandUrl; // nvarchar(200),
	private Integer ModelBrandStatus; // int,
	private String SeriesModelBrandName; // nvarchar(200) NOT NULL,
	private String Platform; // nvarchar(20),
	private String InsertTime; // datetime NOT NULL,
	private String UpdateTime; // datetime NOT NULL,
	
	public ModelBrand() {
		super();
	}

	@Override
	public String toString() {
		return "ModelBrand [ModelBrandId=" + ModelBrandId + ", SeriesBrandId=" + SeriesBrandId + ", ModelBrandName="
				+ ModelBrandName + ", ModelBrandUrl=" + ModelBrandUrl + ", ModelBrandStatus=" + ModelBrandStatus
				+ ", SeriesModelBrandName=" + SeriesModelBrandName + ", Platform=" + Platform + ", InsertTime="
				+ InsertTime + ", UpdateTime=" + UpdateTime + "]";
	}

	public Long getModelBrandId() {
		return ModelBrandId;
	}

	public void setModelBrandId(Long modelBrandId) {
		ModelBrandId = modelBrandId;
	}

	public Integer getSeriesBrandId() {
		return SeriesBrandId;
	}

	public void setSeriesBrandId(Integer seriesBrandId) {
		SeriesBrandId = seriesBrandId;
	}

	public String getModelBrandName() {
		return ModelBrandName;
	}

	public void setModelBrandName(String modelBrandName) {
		ModelBrandName = modelBrandName;
	}

	public String getModelBrandUrl() {
		return ModelBrandUrl;
	}

	public void setModelBrandUrl(String modelBrandUrl) {
		ModelBrandUrl = modelBrandUrl;
	}

	public Integer getModelBrandStatus() {
		return ModelBrandStatus;
	}

	public void setModelBrandStatus(Integer modelBrandStatus) {
		ModelBrandStatus = modelBrandStatus;
	}

	public String getSeriesModelBrandName() {
		return SeriesModelBrandName;
	}

	public void setSeriesModelBrandName(String seriesModelBrandName) {
		SeriesModelBrandName = seriesModelBrandName;
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
