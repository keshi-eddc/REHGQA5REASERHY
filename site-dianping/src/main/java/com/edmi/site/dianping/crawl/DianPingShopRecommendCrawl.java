package com.edmi.site.dianping.crawl;

import java.util.Date;
import java.util.List;
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

import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.entity.DianpingShopCommentPage;
import com.edmi.site.dianping.entity.DianpingShopInfo;
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

public class DianPingShopRecommendCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private DianpingShopInfo shop;
	
	@SuppressWarnings("rawtypes")
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	@SuppressWarnings("rawtypes")
	public DianPingShopRecommendCrawl(DianpingShopInfo shop) {
		super();
		this.shop = shop;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		crawl();
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
	
	private void crawl() {
		StringBuilder sql = new StringBuilder();
		sql.append("select top 1000 * from Dianping_Shop_Comment_Page where status <> 200 ")
//			.append("and sub_category_id in (select sub_category_id from dbo.Dianping_City_SubCategory ")
//				.append("where primary_category = '" + primaryCategory + "' ")
//				.append(StringUtils.isNotEmpty(category) ? "and category = '" + category + "' " : " ")
//				.append(StringUtils.isNotEmpty(subCategory) ? "and sub_category = '" + subCategory + "' " : " ")
//				.append(")")
//			.append("and sub_region_id in (select sub_region_id from dbo.Dianping_City_SubRegion ")
//				.append(" where city_cnname = '" + cityCnname + "' ")
//				.append(StringUtils.isNotEmpty(region) ? "and region = '" + region + "' " : " ")
//				.append(StringUtils.isNotEmpty(subRegion) ? "and sub_region = '" + subRegion + "' " : " ")
//				.append(")")
			;
		
		while (true) {
			
			List<DianpingShopCommentPage> pageList = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingShopCommentPage.class);
			
			if (CollectionUtils.isNotEmpty(pageList)) {
//				DianPingCommonRequest.refreshShopCommentTotalPageCookie();
			} else {
				log.info("店铺评论抓取完成");
				break;
			}
			
			ExecutorService pool = Executors.newFixedThreadPool(5);
			
			for (final DianpingShopCommentPage page : pageList) {
				page.setUpdateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				
				pool.submit(new Runnable() {
					
					@Override
					public void run() {
						parseComment(page);
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
						TimeUnit.SECONDS.sleep(60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void parseComment(DianpingShopCommentPage page) {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/shop/" + page.getShopId() 
				+ "/review_all/p" + page.getPage() + "?queryType=sortType&queryVal=latest");
		if (page.getPage() == 1) {
			header.setReferer("http://www.dianping.com/shop/" + page.getShopId() 
				+ "/review_all?queryType=sortType&queryVal=latest");
		} else {
			header.setReferer("http://www.dianping.com/shop/" + page.getShopId() 
				+ "/review_all/p" + (page.getPage() - 1) + "?queryType=sortType&queryVal=latest");
		}
		String html = DianPingCommonRequest.getShopComment(header);
//		log.info(html);
		Document doc = Jsoup.parse(html);
		Elements commentList = doc.select(".reviews-items ul li");
		if (CollectionUtils.isNotEmpty(commentList)) {
			page.setStatus(200);
			for (Element shop : commentList) {
				try {

					DianpingShopComment comment = new DianpingShopComment();
					comment.setShopId(page.getShopId());
					
					comment.setPage(page.getPage());
					
					Element userIdEle = shop.select("a[href*=member]").first();
					comment.setUserId(null != userIdEle ? userIdEle.attr("data-user-id") : "");
					
					Element userNameEle = shop.select(".main-review .dper-info a[href*=member]").first();
					comment.setUserName(null != userNameEle ? userNameEle.text() : "");
					
					Element userLevelEle = shop.select(".main-review .dper-info .user-rank-rst").first();
					comment.setUserLevel(null != userLevelEle ? userLevelEle.classNames().toString() : "");
					
					Element isVipEle = shop.select(".main-review .dper-info .vip").first();
					comment.setIsVip(null != isVipEle ? 1 : 0);
					
					Element commentStar = shop.select(".review-rank .star").first();
					comment.setCommentStar(null != commentStar ? commentStar.classNames().toString() : "");
					
					Elements scores = shop.select(".review-rank .score .item");
					comment.setTasteComment("");
					comment.setEnvironmentComment("");
					comment.setServiceComment("");
					if (CollectionUtils.isNotEmpty(scores)) {
						
						for (Element score : scores) {
							String text = score.text().trim();
							if (text.contains("口味")) {
								comment.setTasteComment(text.replace("口味：", "").trim());
							} else if (text.contains("环境")) {
								comment.setEnvironmentComment(text.replace("环境：", "").trim());
							} else if (text.contains("服务")) {
								comment.setServiceComment(text.replace("服务：", "").trim());
							} else if (text.contains("人均")) {
								comment.setAvgPrice(text.replace("人均：", "").trim());
							}
						}
					}
					
					Element commentEle = shop.select(".review-words").first();
					comment.setComment(null != commentEle ? commentEle.text().trim() : "");
					
					Elements recommentDishEles = shop.select(".review-recommend a");
					if (CollectionUtils.isNotEmpty(recommentDishEles)) {
						StringBuilder dish = new StringBuilder();
						for (Element dishEle : recommentDishEles) {
							dish.append(dishEle.text()).append(" ");
						}
						comment.setRecommendDish(dish.toString());
					} else {
						comment.setRecommendDish("");
					}
					
					Element commentTimeEle = shop.select(".misc-info .time").first();
					comment.setCommentTime(null != commentTimeEle ? commentTimeEle.text().trim() : "");
					
					Element commentIdEle = shop.select(".actions a[data-id]").first();
					comment.setCommentId(null != commentIdEle ? commentIdEle.attr("data-id") : "");
					
//					iGeneralJdbcUtils.execute(new SqlEntity(comment, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
					FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(comment, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (html.contains("没有找到符合条件的商户") || html.contains("建议您：更改筛选条件重新查找")) {
			page.setStatus(200);
		} else {
			page.setStatus(0);
		}
		
		iGeneralJdbcUtils.execute(new SqlEntity(page, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
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
		
		new DianPingShopRecommendCrawl(null).run();
	}
	
}
