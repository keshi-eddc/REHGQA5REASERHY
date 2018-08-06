package com.edmi.site.dianping.job;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.crawl.CargillDianPingShopListCrawl;
import com.edmi.site.dianping.entity.DianpingCityInfo;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;

/**
 * 项目-嘉吉
 * 17个城市，按城市，品类，人气抓取点评店铺Top30页
 * @author conner
 *
 */
public class CargillShopJob {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		String[] keywords = new String[] {"汉堡", "鸡排", "鸡块", "鸡翅", "鸡柳", "鸡腿", "鸡爪", "烤鸡"};
//		String[] keywords = new String[] {"汉堡"};
		
		// 西餐、韩国料理、日本菜、面包甜点、咖啡店、粤菜、台湾菜、川菜、东南亚菜、小吃面食
		// 西餐、韩国料理、日本菜、面包甜点、咖啡店、粤菜、台湾菜、川菜、东南亚菜、小吃快餐
//		String[] categories = new String[] {"g116", "g114", "g113", "g117", "g132", "g103", "g107", "g102", "g115", "g217"};
		String[] categories = new String[] {"g116", "g114", "g113", "g117", "g132", "g103", "g107", "g102", "g115", "g112"};
//		String[] categories = new String[] {"g116"};
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils<?>) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		
		List<DianpingCityInfo> cityList = iGeneralJdbcUtils.queryForListObject(new SqlEntity(
				"select * from dbo.Dianping_CityInfo where cityName in ("
				+ "'北京', '上海', '广州', '深圳')",
//				+ "'北京')",
				DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO), DianpingCityInfo.class);
		
		ExecutorService pool = Executors.newFixedThreadPool(10);
		
		for (DianpingCityInfo cityInfo : cityList) {
			
			for (String keyword : keywords) {
				pool.submit(new CargillDianPingShopListCrawl(2, keyword, cityInfo.getCityId(), null, null, null, "ch10", "", ""));
			}
			
			for (String categoryId : categories) {
				pool.submit(new CargillDianPingShopListCrawl(1, categoryId, cityInfo.getCityId(), cityInfo.getCityEnName(), cityInfo.getCityName(), "美食", "ch10", "", categoryId));
			}
			
		}
		
		pool.shutdown();

		while (true) {
			if (pool.isTerminated()) {
				log.error("嘉吉-点评-关键词搜索店铺-抓去完成");
				break;
			} else {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
