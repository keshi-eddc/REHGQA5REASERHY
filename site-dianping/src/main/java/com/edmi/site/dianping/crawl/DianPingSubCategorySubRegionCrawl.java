package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.entity.DianpingCitySubCategory;
import com.edmi.site.dianping.entity.DianpingCitySubRegion;
import com.edmi.site.dianping.entity.DianpingSubCategorySubRegion;
import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.cache.utils.ClassUtils;
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
public class DianPingSubCategorySubRegionCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private String cityId;
	
	private String cityEnName;
	
	private String cityCnName;
	
	private String primaryCategory;
	
	private String primaryCategoryId;
	
	private List<String[]> cityCategoryList = new ArrayList<>();
	
	private List<String[]> cityRegionList = new ArrayList<>();
	
	private List<DianpingCitySubCategory> citySubCategoryList = new ArrayList<>();
	
	private List<DianpingCitySubRegion> citySubRegionList = new ArrayList<>();
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingSubCategorySubRegionCrawl(String cityId, String cityEnName, String cityCnName,
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
		getCategoryAndRegion();
		getSubCategory();
		getSubRegion();
		insertNotExist();
	}
	
	@SuppressWarnings("unchecked")
	private void insertNotExist() {
		List<DianpingCitySubCategory> subCategoryList = iGeneralJdbcUtils.queryForListObject(
				new SqlEntity(
						"select * from " + ClassUtils.getTableName(DianpingCitySubCategory.class) + " where city_id = '"
								+ cityId + "' and primary_category_id = '" + primaryCategoryId + "'",
						DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
				DianpingCitySubCategory.class);
		
		List<DianpingCitySubRegion> subRegionList = iGeneralJdbcUtils.queryForListObject(
				new SqlEntity("select * from " + ClassUtils.getTableName(DianpingCitySubRegion.class)
						+ " where city_id = '" + cityId + "'", DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
				DianpingCitySubRegion.class);
		
		List<SqlEntity> sqlEntityList = new ArrayList<>();

		for (DianpingCitySubCategory subCategory : subCategoryList) {
			for (DianpingCitySubRegion subRegion : subRegionList) {
				String url = "http://www.dianping.com/" + cityEnName + "/" + primaryCategoryId + "/" + subCategory.getSubCategoryId() + subRegion.getSubRegionId();
				DianpingSubCategorySubRegion sub = new DianpingSubCategorySubRegion();
				sub.setUrl(url);
				sub.setSubCategoryId(subCategory.getSubCategoryId());
				sub.setSubCategory(subCategory.getSubCategory());
				sub.setCategoryId(subCategory.getCategoryId());
				sub.setCategory(subCategory.getCategory());
				sub.setPrimaryCategoryId(subCategory.getPrimaryCategoryId());
				sub.setPrimaryCategory(subCategory.getPrimaryCategory());
				sub.setSubRegionId(subRegion.getSubRegionId());
				sub.setSubRegion(subRegion.getSubRegion());
				sub.setRegionId(subRegion.getRegionId());
				sub.setRegion(subRegion.getRegion());
				sub.setCityId(subRegion.getCityId());
				sub.setCityCnname(subRegion.getCityCnname());
				sub.setCityEnname(subRegion.getCityEnname());
				
				sqlEntityList.add(new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
			}
		}
		
		FirstCacheHolder.getInstance().submitFirstCache(sqlEntityList);
	}
	
	private void getCategoryAndRegion () {
		log.info("开始抓取Category和Region");
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/" + cityEnName + "/" + primaryCategoryId);
		String html = DianPingCommonRequest.getSubCategorySubRegion(header);
		Document doc = Jsoup.parse(html);
		Elements categories = doc.select("#classfy a");
		if (CollectionUtils.isNotEmpty(categories)) {
			for (Element category : categories) {
				cityCategoryList.add(new String[] {"g" + category.attr("data-cat-id"), category.text().trim()});
			}
		}
		
		Elements regions = doc.select("#region-nav a");
		if (CollectionUtils.isNotEmpty(regions)) {
			for (Element region : regions) {
				cityRegionList.add(new String[] {"r" + region.attr("data-cat-id"), region.text()});
			}
		}
	}
	
	/** 
	 * 获取SubCategory列表
	 * @param categoryId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void getSubCategory() {
		HttpRequestHeader header = new HttpRequestHeader();
		for (String[] category : cityCategoryList) {
			List<DianpingCitySubCategory> subCategoryList = new ArrayList<>();
			header.setUrl("http://www.dianping.com/" + cityEnName + "/" + primaryCategoryId + "/" + category[0]);
			String html = DianPingCommonRequest.getSubCategorySubRegion(header);
			Document doc = Jsoup.parse(html);
			Elements subCategoryElements = doc.select("#classfy-sub a");
			if (CollectionUtils.isNotEmpty(subCategoryElements)) {
				for (Element subCategory : subCategoryElements) {
					if (subCategory.hasAttr("data-cat-id")) {
						DianpingCitySubCategory sub = new DianpingCitySubCategory();
						sub.setSubCategory(subCategory.text().trim());
						sub.setSubCategoryId("g" + subCategory.attr("data-cat-id"));
						sub.setCategory(category[1]);
						sub.setCategoryId(category[0]);
						sub.setPrimaryCategory(primaryCategory);
						sub.setPrimaryCategoryId(primaryCategoryId);
						sub.setCityId(cityId);
						sub.setCityCnname(cityCnName);
						sub.setCityEnname(cityEnName);
						subCategoryList.add(sub);
					}
				}
			} else {
				// 如果该Category下面没有SubCategory，将Category作为SubCategory
				DianpingCitySubCategory sub = new DianpingCitySubCategory();
				sub.setSubCategory(category[1]);
				sub.setSubCategoryId(category[0]);
				sub.setCategory(category[1]);
				sub.setCategoryId(category[0]);
				sub.setPrimaryCategory(primaryCategory);
				sub.setPrimaryCategoryId(primaryCategoryId);
				sub.setCityId(cityId);
				sub.setCityCnname(cityCnName);
				sub.setCityEnname(cityEnName);
				subCategoryList.add(sub);
			}
			citySubCategoryList.addAll(subCategoryList);
		}
		List<SqlEntity> sqlEntityList = new ArrayList<>();
		for (DianpingCitySubCategory sub : citySubCategoryList) {
			SqlEntity sqlEntity = new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS);
			sqlEntityList.add(sqlEntity);
		}
		iGeneralJdbcUtils.batchExecute(sqlEntityList);
	}
	
	@SuppressWarnings("unchecked")
	private void getSubRegion() {
		HttpRequestHeader header = new HttpRequestHeader();
		for (String[] region : cityRegionList) {
			List<DianpingCitySubRegion> subRegionList = new ArrayList<>();
			header.setUrl("http://www.dianping.com/" + cityEnName + "/" + primaryCategoryId + "/" + region[0]);
			String html = DianPingCommonRequest.getSubCategorySubRegion(header);
			Document doc = Jsoup.parse(html);
			Elements subRegionElements = doc.select("#region-nav-sub a");
			if (CollectionUtils.isNotEmpty(subRegionElements)) {
				for (Element subRegion : subRegionElements) {
					if (subRegion.hasAttr("data-cat-id")) {
						DianpingCitySubRegion sub = new DianpingCitySubRegion();
						sub.setSubRegion(subRegion.text().trim());
						sub.setSubRegionId("r" + subRegion.attr("data-cat-id"));
						sub.setRegion(region[1]);
						sub.setRegionId(region[0]);
						sub.setCityId(cityId);
						sub.setCityCnname(cityCnName);
						sub.setCityEnname(cityEnName);
						
//						log.info(sub);
						subRegionList.add(sub);
					}
				}
			} else {
				// 如果该Category下面没有SubCategory，将Category作为SubCategory
				DianpingCitySubRegion sub = new DianpingCitySubRegion();
				sub.setSubRegion(region[1]);
				sub.setSubRegionId(region[0]);
				sub.setRegion(region[1]);
				sub.setRegionId(region[0]);
				sub.setCityId(cityId);
				sub.setCityCnname(cityCnName);
				sub.setCityEnname(cityEnName);
				subRegionList.add(sub);
			}
			citySubRegionList.addAll(subRegionList);
		}
		
		List<SqlEntity> sqlEntityList = new ArrayList<>();
		for (DianpingCitySubRegion sub : citySubRegionList) {
			SqlEntity sqlEntity = new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS);
			sqlEntityList.add(sqlEntity);
		}
		iGeneralJdbcUtils.batchExecute(sqlEntityList);
	}
	
	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println(ApplicationContextHolder.getBean(GeneralJdbcUtils.class));
		IGeneralJdbcUtils<?> iGeneralJdbcUtils = (IGeneralJdbcUtils<?>) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		
		List<Map<String, Object>> mapList = iGeneralJdbcUtils.queryForListMap(new SqlEntity(
				"with sub_category as ( "
						+ "	select city_cnname, count(distinct sub_category_id) as sub_cate_num from dbo.Dianping_City_SubCategory "
						+ "	GROUP by city_cnname "
						+ "), sub_region as ( "
						+ "	select city_cnname, count(distinct sub_region_id) as sub_region_num from dbo.Dianping_City_SubRegion "
						+ "	GROUP by city_cnname "
						+ "), subcategory_subregion as ( "
						+ "	select city_cnname, count(1) as num from dbo.Dianping_SubCategory_SubRegion "
						+ "	group by city_cnname "
						+ "), temp as ( "
						+ "	select A.city_cnname as city_cnname, A.sub_cate_num * B.sub_region_num as num "
						+ "	from sub_category A left join sub_region B on A.city_cnname = B.city_cnname "
						+ ") select * from dbo.City_DianPing where cityName not in ( "
						+ "	select A.city_cnname from temp A left join subcategory_subregion B " 
						+ "	on A.city_cnname = B.city_cnname  "
						+ "	where A.num = B.num "
						+ ") ",
				DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO));
		
		ExecutorService pool = Executors.newFixedThreadPool(1);
		
		for (Map<String, Object> map : mapList) {
			pool.submit(new DianPingSubCategorySubRegionCrawl(map.get("cityId").toString(),
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
