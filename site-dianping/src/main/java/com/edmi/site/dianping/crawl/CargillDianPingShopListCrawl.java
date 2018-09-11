package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;

import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopInfo_Cargill;
import com.edmi.site.dianping.http.DianPingCommonRequest;
import com.edmi.site.dianping.parse.DianpingParser;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

public class CargillDianPingShopListCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	/**
	 * 1:按照品类; 2:按照关键词
	 */
	private int type;
	
	private String keyword;
	
	private String cityId;
	
	private String cityEnName;
	
	private String cityCnName;
	
	private String region;
	
	private String regionId;
	
	private String subRegion;
	
	private String subRegionId;
	
	private String primaryCategory;
	
	private String primaryCategoryId;
	
	private String category;
	
	private String categoryId;
	
	private String subCategory;
	
	private String subCategoryId;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;
	
	private List<DianpingShopInfo_Cargill> totalList = new ArrayList<>();

	public CargillDianPingShopListCrawl(int type, String keyword, String cityId, String cityEnName, String cityCnName,
			String primaryCategory, String primaryCategoryId, String category, String categoryId) {
		super();
		this.type = type;
		this.keyword = keyword;
		this.cityId = cityId;
		this.cityEnName = cityEnName;
		this.cityCnName = cityCnName;
		this.primaryCategory = primaryCategory;
		this.primaryCategoryId = primaryCategoryId;
		this.category = category;
		this.categoryId = categoryId;
		iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		crawl();
		log.info("################## " + totalList.size());
		if (totalList.size() != 150) {
			if (type == 1) {
				log.info("没有数据的URL " + "http://www.dianping.com/" + cityEnName + "/" + primaryCategoryId + "/" + categoryId + "o2");
			} else {
				log.info("没有数据的URL " + "http://www.dianping.com/search/keyword/" + cityId + "/" + primaryCategoryId.replace("ch", "") + "_" + keyword + "/o2");
			}
		}
		for (DianpingShopInfo_Cargill shopCargill : totalList) {
			SqlEntity sqlEntity = new SqlEntity(shopCargill, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT);
			FirstCacheHolder.getInstance().submitFirstCache(sqlEntity);
		}
		
	}
	
	private void crawl() {
		int totalPage = 1;
		totalList.clear();
		for (int page = 1; page <= totalPage; page ++) {
			
			HttpRequestHeader header = new HttpRequestHeader();
			if (type == 1) {
				header.setUrl("http://www.dianping.com/" + cityEnName + "/" + primaryCategoryId + "/" + categoryId + "o2p" + page);
//				if (page == 1) {
					header.setReferer("http://www.dianping.com/" + cityEnName + "/" + primaryCategoryId + "/" + categoryId + "o2");
//				} else {
//					header.setReferer("http://www.dianping.com/" + cityEnName + "/" + primaryCategoryId + "/" + categoryId + "o2p" + (page - 1));
//				}
			} else {
				header.setUrl("http://www.dianping.com/search/keyword/" + cityId + "/" + primaryCategoryId.replace("ch", "") + "_" + keyword + "/o2p" + page);
//				if (page == 1) {
					header.setReferer("http://www.dianping.com/search/keyword/" + cityId + "/" + primaryCategoryId.replace("ch", "") + "_" + keyword + "/o2");
//				} else {
//					header.setReferer("http://www.dianping.com/search/keyword/" + cityId + "/" + primaryCategoryId.replace("ch", "") + "_" + keyword + "/o2p" + (page - 1));
//				}
			}
			header.setProxyType(ProxyType.PROXY_STATIC_DLY);
			header.setProject(Project.CARGILL);
			header.setSite(Site.DIANPING);
			header.setMaxTryTimes(20);
			
			while (true) {
				
				String pageHtml = DianPingCommonRequest.getShopList(header);
				
				Document pageDoc = Jsoup.parse(pageHtml);
				
				// 如果没有发现店铺列表，说明没有抓取成功，继续抓取
				if (pageHtml.contains("没有找到符合条件的商户")) {
					log.info(header.getUrl() + " 请求成功，没有找到符合条件的商户,停止抓取");
					break;
				} else {
					Elements shopElements = pageDoc.select("#shop-all-list ul li");
					if (CollectionUtils.isEmpty(shopElements)) {
						log.info(header.getUrl() + " 请求成功，未发现店铺列表,继续抓取");
						continue;
//						crawl();
//						break;
					}
				}
				
				if (page == 1) {
					totalPage = DianpingParser.parseShopListPage(pageDoc);
					if (totalPage > 10) {
						totalPage = 10;
					}
					log.info("当前页数 " + page + " 总页数 " + totalPage + " " + header.getUrl());
				}
				
				List<DianpingShopInfo> list = DianpingParser.parseShopList(pageDoc, page);
				if (list.size() == 15) {
					totalList.addAll(save(list, page, totalPage));
					break;
				} else {
					log.info(header.getUrl() + " 第 " + page + " 页 抓取到的店铺数量不足15条 " + list.size());
					continue;
//					page = 1;
//					totalList.clear();
//					break;
//					crawl();
//					break;
				}
			}
		}
	}

	private List<DianpingShopInfo_Cargill> save(List<DianpingShopInfo> list, int page, int totalPage) {
		List<DianpingShopInfo_Cargill> temp = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (DianpingShopInfo shop : list) {
				DianpingShopInfo_Cargill shopCargill = new DianpingShopInfo_Cargill();
				BeanUtils.copyProperties(shop, shopCargill);
				
				shopCargill.setSource(type == 1 ? "category" : "keyword");
				shopCargill.setPage(page);
				shopCargill.setTotalPage(totalPage);
				shopCargill.setSubCategoryId("");
				shopCargill.setCategoryId(StringUtils.isNotEmpty(categoryId) ? categoryId : "");
				shopCargill.setPrimaryCategoryId(StringUtils.isNotEmpty(primaryCategoryId) ? primaryCategoryId : "");
				shopCargill.setSubRegionId(StringUtils.isNotEmpty(subRegionId) ? subRegionId : "");
				shopCargill.setRegionId(StringUtils.isNotEmpty(regionId) ? regionId : "");
				shopCargill.setCityId(StringUtils.isNotEmpty(cityId) ? cityId : "");
				shopCargill.setKeyword(StringUtils.isNotEmpty(keyword) ? keyword : "");
				
				temp.add(shopCargill);
				
//				SqlEntity sqlEntity = new SqlEntity(shopCargill, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT);
//				FirstCacheHolder.getInstance().submitFirstCache(sqlEntity);
			}
		}
		return temp;
	}
	
}
