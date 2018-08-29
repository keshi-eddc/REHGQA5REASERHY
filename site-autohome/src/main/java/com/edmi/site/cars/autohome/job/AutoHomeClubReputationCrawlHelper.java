package com.edmi.site.cars.autohome.job;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.edmi.site.cars.autohome.crawl.AutoHomeMobileReputationCrawl;
import com.edmi.site.cars.autohome.entity.ModelBrand;

import fun.jerry.cache.constant.Constant;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;

/**
 * 汽车之家-论坛-口碑
 * @author conner
 *
 */
/**
 * 抓取汽车之家口碑列表
 */
@Component
public class AutoHomeClubReputationCrawlHelper {
	
	public static Logger log = LogSupport.getAutohomelog();
	
	public final static BlockingQueue<String> existBbsId = new ArrayBlockingQueue<String>(Constant.firstCacheSize);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		log.info("到点了，汽车之家-论坛-口碑 数据开始抓取！");
		
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		//select * from dbo.D_ModelBrand where Platform = 'autohome'
		List<ModelBrand> modelBrandList = iGeneralJdbcUtils
				.queryForListObject(new SqlEntity("select * from dbo.D_ModelBrand where platform = 'autohome' "
//						+ "and ModelBrandId <= 2000011047 "
//						+ "and ModelBrandId <> 2000028323 "
//						+ "and ModelBrandId > 2000011047 and ModelBrandId <= 2000022424 "
//						+ "and ModelBrandId = 2000025701 "
						+ "order by ModelBrandId desc",
						DataSource.DATASOURCE_SGM, SqlType.PARSE_NO), ModelBrand.class);
		
		log.info("汽车之家-论坛-口碑 需要抓取的车系共 " + modelBrandList.size());

		//		ExecutorService pool = Executors.newFixedThreadPool(config.getAutohomeReputationThreadNum());
		ExecutorService pool = Executors.newFixedThreadPool(10);
		
		for (ModelBrand task : modelBrandList) {
//			for (int i = 0; i < 200; i++) {
//				pool.submit(new AutoHomeClubReputationCrawl(task));
				pool.submit(new AutoHomeMobileReputationCrawl(task));
//			}
		}
		
		pool.shutdown();

		while (true) {
			if (pool.isTerminated()) {
				log.error("汽车之家-论坛-口碑-抓取完成");
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
