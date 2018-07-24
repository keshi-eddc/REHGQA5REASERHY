package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.entity.DianpingShopCommentPage;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingSubCategorySubRegion;
import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

public class DianPingShopCommentPageCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private DianpingSubCategorySubRegion subCategorySubRegion;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingShopCommentPageCrawl(DianpingSubCategorySubRegion subCategorySubRegion) {
		super();
		this.subCategorySubRegion = subCategorySubRegion;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		StringBuilder sql = new StringBuilder();
		sql.append("select top 1000 * from Dianping_ShopInfo A "
				+ "where not exists (select 1 from Dianping_Shop_Comment_Page B where A.shop_id = B.shop_id) "
//				+ "and shop_id in ('10004919', '10008690')"
				+ "order by shop_id"
				);
		
		while (true) {
			List<DianpingShopInfo> urls = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingShopInfo.class);
			if (CollectionUtils.isNotEmpty(urls)) {
				
				ExecutorService pool = Executors.newFixedThreadPool(5);
				
				for (final DianpingShopInfo shopInfo : urls) {
					pool.submit(new Runnable() {
						public void run() {
							List<SqlEntity> sqlEntityList = new ArrayList<>();
//							sqlEntityList.clear();
							int totalPage = getTotalPage(shopInfo);
							for (int i = 1; i <= totalPage; i++) {
								DianpingShopCommentPage commentPage = new DianpingShopCommentPage();
								commentPage.setShopId(shopInfo.getShopId());
								commentPage.setTotalPage(totalPage);
								commentPage.setPage(i);
								commentPage.setStatus(-1);
								sqlEntityList.add(new SqlEntity(commentPage, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
							}
							iGeneralJdbcUtils.batchExecute(sqlEntityList);
						}
					});
				}
				
				pool.shutdown();

				while (true) {
					if (pool.isTerminated()) {
						log.error("大众点评-refresh shop comment total page 抓取完成");
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
	
	private int getTotalPage (DianpingShopInfo shopInfo) {
		int totalPage = -1;
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/shop/" + shopInfo.getShopId() +"/review_all?queryType=sortType&queryVal=latest");
		header.setReferer("http://www.dianping.com/shop/" + shopInfo.getShopId() + "/review_all");
		String html = DianPingCommonRequest.getShopComment(header);
		Document doc = Jsoup.parse(html);
		if (null != doc.select(".reviews-items")) {
			// 发现有评论列表的，看是否包含评论
			if (CollectionUtils.isNotEmpty(doc.select(".reviews-items ul li"))) {
				Element pageEle = doc.select(".reviews-pages .PageLink").last();
				totalPage = null != pageEle ? Integer.parseInt(pageEle.text().trim()) : 1;
			} else {
				totalPage = 0;
			}
		} else {
			// 未发现评论列表的，没有评论，总页数为0
			totalPage = 0;
		}
		return totalPage;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
//		StringBuilder sql = new StringBuilder();
//		sql.append("select * from Dianping_SubCategory_SubRegion where shop_total_page > 0 and city_cnname = '上海'");
//		
//		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
//		List<DianpingSubCategorySubRegion> urls = iGeneralJdbcUtils.queryForListObject(
//				new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
//				DianpingSubCategorySubRegion.class);
//		
//		List<SqlEntity> sqlEntityList = new ArrayList<SqlEntity>();
//		for (DianpingSubCategorySubRegion sub : urls) {
//			int totalPage = sub.getShopTotalPage();
//			for (int i = 1; i <= totalPage; i++) {
//				DianpingSubCategorySubRegionPage subPage = new DianpingSubCategorySubRegionPage();
//				BeanUtils.copyProperties(sub, subPage);
//				subPage.setPage(i);
//				
//				sqlEntityList.add(new SqlEntity(subPage, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
//			}
//		}
//		iGeneralJdbcUtils.batchExecute(sqlEntityList);
		
		new DianPingShopCommentPageCrawl(null).run();
		
//		DianpingShopCommentPage commentPage = new DianpingShopCommentPage();
//		commentPage.setShopId("1");
//		commentPage.setTotalPage(2);
//		commentPage.setPage(1);
//		commentPage.setStatus(-1);
//		System.out.println(BuildSqlByBeanUtil.insertNotExists(commentPage));
	}
	
}
