package com.edmi.site.cars.autohome.crawl;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.cars.autohome.config.HtmlDataUtil;
import com.edmi.site.cars.autohome.entity.ReputationCrawled;
import com.edmi.site.cars.autohome.entity.TopicCrawled;
import com.edmi.site.cars.autohome.http.AutohomeCommonHttp;
import com.edmi.site.cars.autohome.http.AutohomeTaskRequest;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 按照车型抓取口碑
 * 每个车型的口碑可能有多页
 * @author conner
 */
public class AutoHomeMobileReputationDetailHtmlCrawl implements Runnable {

	public static Logger log = LogSupport.getAutohomelog();
	
	private String id;
	
	private String url;
	
	public AutoHomeMobileReputationDetailHtmlCrawl(String url) {
		this.url = url;
		this.id = url.substring(url.indexOf("view_") + 5, url.indexOf(".html"));
	}

	@Override
	public void run() {

		url = url.contains("https:") ? url.replace("http:", "https:") : ("https:" + url.replace("http:", "https:"));
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl(url);
		String html = AutohomeCommonHttp.getMobileReputationDetail(header);
		if (null != html && !html.contains("verify-box") && !html.contains("您的访问出现异常")
				&& !html.contains("No such file or directory") && !html.contains("File or directory not found")
				&& !html.contains("由于您的网络存在安全问题")
				&& html.contains(id)) {
			try {
				HtmlDataUtil.saveData("D:/data/autohome/" + id + ".html", html);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ReputationCrawled tc = new ReputationCrawled();
			tc.setReputationUrl(url);
			tc.setId(id);;
			
			FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(tc, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
		} else {
			log.error("有误不能保存");
		}
	
	}
	
	public static void main(String[] args) {
//		String sql = "select top 500 ReputationUrl from dbo.F_ReputationList_P02 A "
//				+ "where PublishTime > '2018-07-27 00:00:00' "
//				+ "and not EXISTS (select 1 from dbo.F_Reputation_Crawled B where SUBSTRING(A.ReputationUrl, charindex('view_', A.ReputationUrl) + 5, 26) = B.Id)";
		String sql = "select top 500 ReputationUrl from dbo.F_ReputationList_P02 A "
				+ "where convert(varchar(20), PublishTime, 23) BETWEEN '2018-07-01' and '2018-07-31' "
				+ "and convert(varchar(20), InsertTime, 23) = '2018-08-01' "
				+ "and not EXISTS (select 1 from dbo.F_Reputation_Crawled B where SUBSTRING(A.ReputationUrl, charindex('view_', A.ReputationUrl) + 5, 26) = B.Id)";

		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		int count = 0;
		try {
			while (true) {
				count ++;
				log.info("##################" + count);
				List<ReputationCrawled> list = iGeneralJdbcUtils.queryForListObject(new SqlEntity(sql, DataSource.DATASOURCE_SGM, SqlType.PARSE_NO), ReputationCrawled.class);
				log.info("获取未抓取个数：" + list.size());
				if (CollectionUtils.isNotEmpty(list)) {
					ExecutorService pool = Executors.newFixedThreadPool(20);
					for (ReputationCrawled ss : list) {
						pool.submit(new AutoHomeMobileReputationDetailHtmlCrawl(ss.getReputationUrl()));
					}
					
					pool.shutdown();

					while (true) {
						if (pool.isTerminated()) {
							log.error("AutoHomeMobileReputationDetailHtmlCrawl 抓取完成");
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
