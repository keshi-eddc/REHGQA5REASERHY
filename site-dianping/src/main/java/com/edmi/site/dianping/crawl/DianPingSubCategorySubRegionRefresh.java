package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.entity.DianpingSubCategorySubRegion;
import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.DataSource;
import fun.jerry.entity.SqlEntity;
import fun.jerry.entity.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 传入需要抓取的城市和分类，如果，上海，美食
 * @author conner
 *
 */
public class DianPingSubCategorySubRegionRefresh implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private String cityId;
	
	private String cityEnName;
	
	private String cityCnName;
	
	private String primaryCategory;
	
	private String primaryCategoryId;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingSubCategorySubRegionRefresh(String cityId, String cityEnName, String cityCnName,
			String primaryCategory, String primaryCategoryId) {
		super();
		this.cityId = cityId;
		this.cityEnName = cityEnName;
		this.cityCnName = cityCnName;
		this.primaryCategory = primaryCategory;
		this.primaryCategoryId = primaryCategoryId;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils<?>) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		log.info(cityCnName + " 开始抓取");
		crawl();
	}
	
	@SuppressWarnings("unchecked")
	private void crawl() {
		List<DianpingSubCategorySubRegion> urls = iGeneralJdbcUtils.queryForListObject(
				new SqlEntity("select * from Dianping_SubCategory_SubRegion "
						+ "where 1 = 1 and shop_total_page = -1 "
						+ "and (sub_category_id in (select sub_category_id from dbo.Dianping_City_SubCategory where city_id = '" + cityId + "' and primary_category = '" + primaryCategory + "') "
						+ "or sub_region_id in (select sub_region_id from dbo.Dianping_City_SubRegion where city_id = '" + cityId + "'))", DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
				DianpingSubCategorySubRegion.class);
		
		List<SqlEntity> updateList = new ArrayList<>();
		
		ExecutorService pool = Executors.newFixedThreadPool(10); 
		
		for (DianpingSubCategorySubRegion sub : urls) {
			sub.setUpdateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
			
			pool.submit(new Runnable() {
				
				@Override
				public void run() {
					HttpRequestHeader header = new HttpRequestHeader();
					header.setUrl(sub.getUrl());
					String html = DianPingCommonRequest.getShopList(header);
					Document doc = Jsoup.parse(html);
					Element shopTag = doc.select("#shop-all-list").first();
					if (null != shopTag) {
						Elements shopElements = doc.select("#shop-all-list ul li");
						// 如果发现有店铺列表，找出有多少页
						if (CollectionUtils.isNotEmpty(shopElements)) {
							Element totalPageEle = doc.select(".page .PageLink").last();
							int totalPage = null == totalPageEle ? 1 : Integer.parseInt(totalPageEle.text().trim());
							sub.setShopTotalPage(totalPage);
						} else {
							sub.setShopTotalPage(-1);
						}
					} else {
						// 如果么有店铺列表，为0页
						sub.setShopTotalPage(0);
					}
//					updateList.add(new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
//					iGeneralJdbcUtils.execute(new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
					FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
				}
			});
		}
		
		pool.shutdown();

		while (true) {
			if (pool.isTerminated()) {
				log.error("大众点评-抓取完成");
				break;
			} else {
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
//		iGeneralJdbcUtils.batchExecute(updateList);
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println(ApplicationContextHolder.getBean(GeneralJdbcUtils.class));
		IGeneralJdbcUtils<?> iGeneralJdbcUtils = (IGeneralJdbcUtils<?>) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		
		List<Map<String, Object>> mapList = iGeneralJdbcUtils.queryForListMap(new SqlEntity(
				"select * from dbo.City_DianPing where cityName in ('北京', '上海', '广州', '深圳', '兰州', '昆明', '成都',  '长春', '沈阳', '西宁', '西安', "
				+ "'郑州', '济南', '太原', '合肥', '武汉', '长沙', '南京', '贵阳', '南宁', '杭州', '南昌', '福州','台北','海口', '银川','拉萨','澳门',"
				+ "'香港','天津','重庆','哈尔滨', '石家庄','呼和浩特','乌鲁木齐')",
				DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO));
		
		ExecutorService pool = Executors.newFixedThreadPool(5); 
		
		for (Map<String, Object> map : mapList) {
			
			pool.submit(new DianPingSubCategorySubRegionRefresh(map.get("cityId").toString(),
					map.get("cityEnName").toString(), map.get("cityName").toString(), "美食", "ch10"));
		}
		
		pool.shutdown();

		while (true) {
			if (pool.isTerminated()) {
				log.error("大众点评-抓取完成");
				break;
			} else {
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
