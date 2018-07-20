package com.edmi.site.dianping.crawl.budweiser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.edmi.site.dianping.entity.DianpingShopDetailInfo;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.http.DianPingCommonRequest;
import com.edmi.site.dianping.http.DianPingTaskRequest;

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

public class BudweiserDianPingShopDetailCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private DianpingShopInfo shopInfo;
	
	public BudweiserDianPingShopDetailCrawl(DianpingShopInfo shopInfo) {
		super();
		this.shopInfo = shopInfo;
	}

	@Override
	public void run() {
		
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl(shopInfo.getShopUrl());
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		header.setProxyType(ProxyType.PROXY_CLOUD_ABUYUN);
//		header.setProxyType(ProxyType.NONE);
		header.setProject(Project.BUDWEISER);
		header.setSite(Site.DIANPING);
		
		String pageHtml = DianPingCommonRequest.getShopDetail(header);
//		WebDriverConfig config = new WebDriverConfig();
//		config.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		String pageHtml = WebDriverSupport.load(WebDriverSupport.getPhantomJSDriverInstance(config), header.getUrl());
//		log.info(pageHtml);
		
		if (pageHtml.contains("shop/" + shopInfo.getShopId())) {
			parse(pageHtml);
		} else if (pageHtml.contains("抱歉！页面无法访问")) {
			DianpingShopDetailInfo detail = new DianpingShopDetailInfo();
			detail.setShopId(shopInfo.getShopId());
			detail.setAddress("404");
			
			FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(detail, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
		}
 	}
	
	private void parse (String html) {
		log.info("开始解析 " + shopInfo.getShopUrl());
		Document pageDoc = Jsoup.parse(html);
//		log.info(html);
		try {
			DianpingShopDetailInfo detail = new DianpingShopDetailInfo();
			detail.setShopId(shopInfo.getShopId());
			
			String json = "";
			Pattern p = Pattern.compile("window.shop_config=(.*?)</script>");  
			Matcher m = p.matcher(html);  
			while(m.find()){  
				json = m.group(1);
			}
			
			JSONObject obj = JSONObject.parseObject(json);
			if (null != obj && obj.containsKey("shopGlat")) {
				detail.setLatitude(obj.getString("shopGlat"));
			}
			
			if (null != obj && obj.containsKey("shopGlng")) {
				detail.setLongtitude(obj.getString("shopGlng"));
			}
			
			Element address = pageDoc.select("span[itemprop=street-address]").first();
			detail.setAddress(address.text().trim());
			
			Elements scores = pageDoc.select("#comment_score span");
			for (Element s : scores) {
				String text = s.text();
				if (text.contains("口味:")) {
					detail.setTasteScore(text.replace("口味:", ""));
				} else if (text.contains("环境:")) {
					detail.setEnvironmentScore(text.replace("环境:", ""));
				} else if (text.contains("服务:")) {
					detail.setServiceScore(text.replace("服务:", ""));
				}
			}
			
			Element phone = pageDoc.select("span[itemprop=tel]").first();
			if (null != phone) {
				detail.setPhone(phone.text().trim());
			}
			
			Element openTime = pageDoc.select("span:contains(营业时间)").first();
			if (null != openTime) {
				Element aa = openTime.nextElementSibling();
				detail.setOpenTime(null != aa ? aa.text().trim() : "");
			}
			
			Element price = pageDoc.select("#avgPriceTitle").first();
			detail.setAvgPrice(null != price ? price.text().trim() : "");
			
			Element reviewNum = pageDoc.select("#reviewCount").first();
			
			detail.setReviewNum(null != reviewNum ? NumberUtils.toInt(reviewNum.text().replace("条评论", "")) : 0);
			
			FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(detail, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
		} catch (Exception e) {
			log.error("parse error {}", e);
		}
	}
	
//	public static void main(String[] args) {
//		DianpingShopInfo shopInfo = new DianpingShopInfo();
//		shopInfo.setShopUrl("http://www.dianping.com/shop/72351070");
//		shopInfo.setShopId("72351070");
//		
////		DianPingCommonRequest.refreshShopDetailCookie();
//		
//		new BudweiserDianPingShopDetailCrawl(shopInfo).run();
//	}
	
	@SuppressWarnings({ "unused", "rawtypes", "unchecked", "resource" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);

		int count = 0;
		try {
			while (true) {
				count ++;
				log.info("##################" + count);
				List<DianpingShopInfo> shopInfoList = DianPingTaskRequest.getShopDetailTask();
				if (CollectionUtils.isNotEmpty(shopInfoList)) {
					log.info("获取未抓取用户个数：" + shopInfoList.size());
					ExecutorService pool = Executors.newFixedThreadPool(19);
					for (DianpingShopInfo ss : shopInfoList) {
						pool.submit(new BudweiserDianPingShopDetailCrawl(ss));
						TimeUnit.MILLISECONDS.sleep(50);
					}
					
					pool.shutdown();

					while (true) {
						if (pool.isTerminated()) {
							log.error("大众点评-refresh DianpingSubCategorySubRegion 抓取完成");
							break;
						} else {
							try {
								TimeUnit.SECONDS.sleep(60);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
				} else {
					System.out.println("$$$$$$$$$$$$$$$" + count);
					try {
						TimeUnit.MINUTES.sleep(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("##################" + count);
		}
		
	}
	
}
