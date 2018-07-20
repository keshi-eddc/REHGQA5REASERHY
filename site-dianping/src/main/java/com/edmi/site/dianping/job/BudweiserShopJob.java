package com.edmi.site.dianping.job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.crawl.CargillDianPingShopListCrawl;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;

/**
 * 项目-百威
 * 所有城市的所有数据，类目顺序 西餐，酒吧，中餐，KTV
 * @author conner
 *
 */
public class BudweiserShopJob {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	public static void main(String[] args) {
		
		// 韩国料理,咖啡厅,面包甜点,日本菜,台湾菜,西餐,粤菜
		String[] categories = new String[] {"g114", "g132", "g117", "g113", "g107", "g116", "g103"};
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils<?> iGeneralJdbcUtils = (IGeneralJdbcUtils<?>) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		
		List<Map<String, Object>> mapList = iGeneralJdbcUtils.queryForListMap(new SqlEntity(
				"select * from dbo.City_DianPing where cityName in ("
				+ "'北京', '上海', '广州', '深圳', '南昌', '太原', '沈阳', '西安', '南宁', '成都', '杭州', '泉州', '潍坊', '吉林', '洛阳', '贵阳', '兰州')",
				DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO));
		
		ExecutorService pool = Executors.newFixedThreadPool(5);
		
		for (Map<String, Object> map : mapList) {
			
			for (String categoryId : categories) {
				pool.submit(new CargillDianPingShopListCrawl(1, null, map.get("cityId").toString(), map.get("cityEnName").toString(), map.get("cityName").toString(), "美食", "ch10", "", categoryId));
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
