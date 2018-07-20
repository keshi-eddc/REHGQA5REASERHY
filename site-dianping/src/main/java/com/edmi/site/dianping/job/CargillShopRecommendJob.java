package com.edmi.site.dianping.job;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.crawl.DianPingShopRecommendPageCrawl;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopInfo_Cargill;
import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.cache.utils.ClassUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;

/**
 * 项目-嘉吉
 * 店铺推荐菜
 * @author conner
 *
 */
public class CargillShopRecommendJob {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		
//		DianPingCommonRequest.refreshShopRecommendCookie();
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct shop_id as shop_id from " + ClassUtils.getTableName(DianpingShopInfo_Cargill.class) + " A "
				+ "where version = '201806' "
				+ "and not exists (select 1 from Dianping_Shop_Recommend_Info B where B.version = '201806'"
				+ "and A.shop_id = B.shop_id "
//				+ "and B.page = 1 and B.status = 200) "
				+ ") "
//				+ "and shop_id in ('72351070')"
				+ "order by shop_id"
				);
		
		List<DianpingShopInfo> shopList = iGeneralJdbcUtils.queryForListObject(
				new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
				DianpingShopInfo.class);
		
		ExecutorService pool = Executors.newFixedThreadPool(20);
		
		for (DianpingShopInfo shop : shopList) {
			pool.execute(new DianPingShopRecommendPageCrawl(shop));
		}
		
		pool.shutdown();

		while (true) {
			if (pool.isTerminated()) {
				log.error("嘉吉-点评-店铺推荐菜-抓取完成");
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
