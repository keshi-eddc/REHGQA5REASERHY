package com.edmi.site.dianping.entity;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.LogicalPrimaryKey;
import fun.jerry.entity.annotation.TableMapping;

/**
 * 按照关键词搜索的搜索的时候一个店铺可能出现在多个关键词下面，保存多条记录
 * @author conner
 *
 */
@TableMapping("Dianping_ShopInfo_Cargill")
public class DianpingShopInfo_Cargill extends DianpingShopInfo_Common {

	private static final long serialVersionUID = 4217070017854978866L;
	
	/**
	 * 来源：keyword， category
	 */
	@LogicalPrimaryKey
	@ColumnMapping("source")
	private String source;
	
	@LogicalPrimaryKey
	@ColumnMapping("keyword")
	private String keyword;
	
	@ColumnMapping("version")
	private String version = Version.version_last_month;
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
