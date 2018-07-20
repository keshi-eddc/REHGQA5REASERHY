package com.edmi.site.dianping.job;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.crawl.DianPingShopCommentCrawl;
import com.edmi.site.dianping.crawl.budweiser.BudweiserDianPingShopListCrawl;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingSubCategorySubRegion;
import com.edmi.site.dianping.http.DianPingTaskRequest;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;

/**
 * 项目-嘉吉
 * 用户信息
 * @author conner
 *
 */
public class CargillUserJob {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	@SuppressWarnings({ "rawtypes", "unused" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		
		int count = 0;
		try {
			while (true) {
				count ++;
				log.info("##################" + count);
				List<DianpingShopInfo> shopList = DianPingTaskRequest.getCommentShop();
				log.info("获取未抓取评论的店铺个数：" + shopList.size());
				if (CollectionUtils.isNotEmpty(shopList)) {
					ExecutorService pool = Executors.newFixedThreadPool(1);
//					
					for (DianpingShopInfo shop : shopList) {
						pool.execute(new DianPingShopCommentCrawl(shop, false));
					}
					
					pool.shutdown();

					while (true) {
						if (pool.isTerminated()) {
							log.error("嘉吉-点评-店铺评论-抓取完成");
							break;
						} else {
							try {
								TimeUnit.SECONDS.sleep(90);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					System.out.println("$$$$$$$$$$$$$$$" + count);
					try {
						TimeUnit.SECONDS.sleep(90);
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
