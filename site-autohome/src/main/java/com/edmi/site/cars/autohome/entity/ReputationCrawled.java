package com.edmi.site.cars.autohome.entity;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("F_Reputation_Crawled")
public class ReputationCrawled {
	
	@ColumnMapping("ReputationUrl")
	private String ReputationUrl;
	
	@ColumnMapping("Id")
	private String Id;

	public String getReputationUrl() {
		return ReputationUrl;
	}

	public void setReputationUrl(String reputationUrl) {
		ReputationUrl = reputationUrl;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}
	
}
