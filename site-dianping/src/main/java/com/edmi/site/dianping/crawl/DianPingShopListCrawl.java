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

import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingSubCategorySubRegion;
import com.edmi.site.dianping.entity.DianpingSubCategorySubRegionPage;
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

public class DianPingShopListCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private DianpingSubCategorySubRegion subCategorySubRegion;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingShopListCrawl(DianpingSubCategorySubRegion subCategorySubRegion) {
		super();
		this.subCategorySubRegion = subCategorySubRegion;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
	}

	@Override
	public void run() {
		crawl();
	}
	
	private void crawl() {

		StringBuilder sql = new StringBuilder();
		sql.append("select top 5000 * from Dianping_SubCategory_SubRegion_Page where status is null or status <> 200")
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
			
			List<DianpingSubCategorySubRegionPage> urls = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingSubCategorySubRegionPage.class);
			
			if (CollectionUtils.isNotEmpty(urls)) {
			} else {
				log.info("店铺列表抓取完成");
				break;
			}
			
			ExecutorService pool = Executors.newFixedThreadPool(20);
			
			for (final DianpingSubCategorySubRegionPage sub : urls) {
				sub.setUpdateTime(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				
				pool.submit(new Runnable() {
					
					@Override
					public void run() {
						parseShopList(sub);
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
	
	private void parseShopList(DianpingSubCategorySubRegionPage sub) {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl(sub.getUrl() + "p" + sub.getPage());
		String html = DianPingCommonRequest.getShopList(header);
		Document doc = Jsoup.parse(html);
		Elements shopElements = doc.select("#shop-all-list ul li");
		if (CollectionUtils.isNotEmpty(shopElements)) {
			sub.setStatus(200);
			for (Element shop : shopElements) {
				DianpingShopInfo shopInfo = new DianpingShopInfo();
//				shopInfo.setSubCategoryId(sub.getSubCategoryId());
//				shopInfo.setSubRegionId(sub.getSubRegionId());
				
				Element tit = shop.select(".tit").first();
				if (null != tit) {
					Element title = tit.select("a[data-hippo-type*=shop]").first();
					if (null != title) {
						shopInfo.setShopName(title.attr("title").trim());
						shopInfo.setShopUrl(title.attr("href").trim());
						shopInfo.setShopId(shopInfo.getShopUrl().substring(shopInfo.getShopUrl().lastIndexOf("/") + 1));
					}
					// 是否包含团购
					Element tuan = tit.select("a.igroup").first();
					shopInfo.setTuanSupport(null != tuan ? 1 : 0);
					// 是否包含预定
					Element book = tit.select("a.ibook").first();
					shopInfo.setBookSupport(null != book ? 1 : 0);
					// 是否包含外卖
					Element out = tit.select("a.iout").first();
					shopInfo.setOutSupport(null != out ? 1 : 0);
					// 是否包含促销
					Element promotion = tit.select("a.ipromote").first();
					shopInfo.setPromotionSupport(null != promotion ? 1 : 0);
					// 是否包含分店
					Element branch = tit.select("a.shop-branch").first();
					if (null != branch) {
						shopInfo.setHasBranch(1);
						shopInfo.setBrandUrl(branch.attr("href"));
					} else {
						shopInfo.setHasBranch(0);
						shopInfo.setBrandUrl("");
					}
					
				}
				Element comment = shop.select(".comment").first();
				if (null != comment) {
					// 星级
					Element level = comment.select("span.sml-rank-stars").first();
					shopInfo.setStarLevel(null != level ? level.attr("title") : "");
					// 评论数
					Element reviewNum = comment.select("a.review-num").first();
					shopInfo.setReviewNum(null != reviewNum ? (null != reviewNum.select("b").first()
							? Integer.parseInt(reviewNum.select("b").first().text()) : 0) : 0);
					// 人均
					Element avgPrice = comment.select("a.mean-price").first();
					shopInfo.setAvgPrice(avgPrice.text().replace("人均", "").replace("￥", "").trim());
				}
				
				// 地址
				Element address = shop.select(".tag-addr .addr").first();
				shopInfo.setAddress(null != address ? address.text().trim() : "");
				// 评分
				Elements scores = shop.select(".comment-list span");
				if (CollectionUtils.isNotEmpty(scores)) {
					for (Element score : scores) {
						String text = score.text();
						String scoreText = score.select("b").first().text();
						if (text.contains("口味")) {
							shopInfo.setTasteScore(scoreText);
						} else if (text.contains("环境")) {
							shopInfo.setEnvironmentScore(scoreText);
						} else if (text.contains("服务")) {
							shopInfo.setServiceScore(scoreText);
						}
					}
				}
				FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(shopInfo, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
			}
		} else if (html.contains("没有找到符合条件的商户") || html.contains("建议您：更改筛选条件重新查找")) {
			sub.setStatus(200);
		} else {
			sub.setStatus(0);
		}
		
		iGeneralJdbcUtils.execute(new SqlEntity(sub, DataSource.DATASOURCE_DianPing, SqlType.PARSE_UPDATE));
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
		
		new DianPingShopListCrawl(null).run();
	}
	
}
