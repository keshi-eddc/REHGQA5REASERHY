package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 传入需要抓取的城市和分类，如果，上海，美食
 * @author conner
 *
 */
public class DianPingSubCategorySubRegionRefresh implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private String cityCnname;
	
	private String region;
	
	private String subRegion;
	
	private String primaryCategory;
	
	private String category;
	
	private String subCategory;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingSubCategorySubRegionRefresh(String cityCnname, String region, String subRegion,
			String primaryCategory, String category, String subCategory) {
		super();
		this.cityCnname = cityCnname;
		this.region = region;
		this.subRegion = subRegion;
		this.primaryCategory = primaryCategory;
		this.category = category;
		this.subCategory = subCategory;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		log.info(cityCnname + " 开始抓取");
		crawl();
	}
	
	@SuppressWarnings("unchecked")
	private void crawl() {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from Dianping_SubCategory_SubRegion where 1 = 1 ")
			.append("and sub_category_id in (select sub_category_id from dbo.Dianping_City_SubCategory ")
				.append("where primary_category = '" + primaryCategory + "' ")
				.append(StringUtils.isNotEmpty(category) ? "and category = '" + category + "' " : " ")
				.append(StringUtils.isNotEmpty(subCategory) ? "and sub_category = '" + subCategory + "' " : " ")
				.append(")")
			.append("and sub_region_id in (select sub_region_id from dbo.Dianping_City_SubRegion ")
				.append(" where city_cnname = '" + cityCnname + "' ")
				.append(StringUtils.isNotEmpty(region) ? "and region = '" + region + "' " : " ")
				.append(StringUtils.isNotEmpty(subRegion) ? "and sub_region = '" + subRegion + "' " : " ")
				.append(")")
			;
		
		while (true) {
			
			List<DianpingSubCategorySubRegion> urls = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingSubCategorySubRegion.class);
			
			List<SqlEntity> updateList = new ArrayList<>();
			
			ExecutorService pool = Executors.newFixedThreadPool(30); 
			
			for (final DianpingSubCategorySubRegion sub : urls) {
				sub.setUpdateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				
				pool.submit(new Runnable() {
					
					@Override
					public void run() {
						HttpRequestHeader header = new HttpRequestHeader();
						header.setUrl(sub.getUrl() + "o2"); // o2 按人气排序
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
//						updateList.add(new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
//						iGeneralJdbcUtils.execute(new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
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
		}
		
//		iGeneralJdbcUtils.batchExecute(updateList);
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println(ApplicationContextHolder.getBean(GeneralJdbcUtils.class));
		IGeneralJdbcUtils<?> iGeneralJdbcUtils = (IGeneralJdbcUtils<?>) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		// '北京', '上海', '广州', '深圳', '南昌', '太原', '沈阳', '西安', '南宁', '成都', '杭州', '泉州', '潍坊', '吉林', '洛阳', '贵阳', '兰州'
		
		ExecutorService pool = Executors.newFixedThreadPool(1); 
		
		for (String city : new String[] {"上海", "北京", "广州", "深圳", "南昌", "太原", "沈阳", "西安", "南宁", "成都", "杭州", "泉州", "潍坊", "吉林", "洛阳", "贵阳", "兰州"}) {
			pool.execute(new DianPingSubCategorySubRegionRefresh(city, "", "", "美食", "", ""));
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
