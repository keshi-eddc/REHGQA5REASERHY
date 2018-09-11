package com.edmi.site.dianping.crawl;

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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendPage;
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

public class DianPingShopRecommendPageCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private DianpingShopInfo shop;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingShopRecommendPageCrawl(DianpingShopInfo shop) {
		super();
		this.shop = shop;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/shop/" + shop.getShopId() +"/dishlist");
		header.setReferer("http://www.dianping.com/shop/" + shop.getShopId());
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
		header.setMaxTryTimes(10);
		String html = DianPingCommonRequest.getShopRecommend(header);
		if(StringUtils.isNotEmpty(html)) {
			Document doc = Jsoup.parse(html);
			if (html.contains("list-desc")) {
				int totalPage = DianpingParser.parseShopRecommendTotalPage(doc);
//				totalPage = (totalPage > 1 ? 1 : totalPage);
//				List<SqlEntity> sqlEntityList = new ArrayList<>();
				if (totalPage == 0) {
//					DianpingShopRecommendPage recommendPage = new DianpingShopRecommendPage();
//					recommendPage.setShopId(shop.getShopId());
//					recommendPage.setTotalPage(totalPage);
//					recommendPage.setPage(0);
//					recommendPage.setStatus(200);
//					recommendPage.setUpdateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					
					DianpingShopRecommendInfo recommend = new DianpingShopRecommendInfo();
					recommend.setShopId(shop.getShopId());
					recommend.setDishId("0");
					recommend.setPage(0);
					
					FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(recommend,
							DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
					
//					sqlEntityList.add(new SqlEntity(recommendPage, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
				} else {
					for (int page = 1; page <= totalPage; page++) {
						// 解析成功，如果是第一页，先将这一页的推荐菜解析，并且第一个页的状态置为200
						DianpingShopRecommendPage recommendPage = new DianpingShopRecommendPage();
						recommendPage.setShopId(shop.getShopId());
						recommendPage.setUpdateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
						recommendPage.setTotalPage(totalPage);
						recommendPage.setPage(page);
						if (page == 1) {
							recommendPage.setStatus(200);
							
							List<DianpingShopRecommendInfo> recommendList = DianpingParser.parseShopRecommend(doc, shop, page);
							for (DianpingShopRecommendInfo recommend : recommendList) {
								FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(recommend,
										DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
							}
							
						} else {
							recommendPage.setStatus(-1);
						}
//						sqlEntityList.add(new SqlEntity(recommendPage, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
					}
				}
//				iGeneralJdbcUtils.batchExecute(sqlEntityList);
			} else {
				log.error("未返回正确的推荐菜信息");
				log.error(html);
			}
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);

//		DianPingCommonRequest.refreshShopRecommendCookie();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct shop_id, shop_url from Dianping_ShopInfo_Cargill A "
				+ "where version = '201808' "
//				+ "and category_id = 'g107' "
				+ "and shop_id not in (select distinct shop_id from dbo.Dianping_Shop_Recommend_Info where version = '201808')"
				);
		
		while (true) {
			
			List<DianpingShopInfo> urls = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingShopInfo.class);
			
			if (CollectionUtils.isNotEmpty(urls)) {
				
				log.info("剩余未抓取推荐菜的店铺数量 ： " + urls.size());
				
				ExecutorService pool = Executors.newFixedThreadPool(15);
				
				for (DianpingShopInfo shopInfo : urls) {
					pool.execute(new DianPingShopRecommendPageCrawl(shopInfo));
				}
				
				pool.shutdown();

				while (true) {
					if (pool.isTerminated()) {
						log.error("大众点评-refresh shop recommend total page 抓取完成");
						break;
					} else {
						try {
							TimeUnit.SECONDS.sleep(60);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			} else {
				break;
			}
		}
		
	}
	
}
