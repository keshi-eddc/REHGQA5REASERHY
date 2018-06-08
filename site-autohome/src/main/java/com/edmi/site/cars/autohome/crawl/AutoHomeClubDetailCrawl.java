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
import com.edmi.site.cars.autohome.entity.TopicCrawled;
import com.edmi.site.cars.autohome.http.AutohomeCommonHttp;
import com.edmi.site.cars.autohome.http.AutohomeTaskRequest;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 按照车型抓取口碑 每个车型的口碑可能有多页
 * 
 * @author conner
 */
public class AutoHomeClubDetailCrawl implements Runnable {
	
	public static Logger log = LogSupport.getJdlog();
	
	private String id;
	
	private String url;
	
	public AutoHomeClubDetailCrawl(String id, String url) {
		super();
		this.id = id;
		this.url = url;
	}

	@Override
	public void run() {
		url = url
//				.replace("club.autohome.com.cn", "club.m.autohome.com.cn")
				.replace("http:", "https:");
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl(url);
		String html = AutohomeCommonHttp.getMobileClubDetail(header);
		if (null != html && !html.contains("verify-box") && !html.contains("您的访问出现异常")
				&& !html.contains("No such file or directory") && !html.contains("File or directory not found")
				&& !html.contains("由于您的网络存在安全问题")) {
			try {
				HtmlDataUtil.saveData("/home/conner/autohome/" + id + ".html", html);
			} catch (IOException e) {
				e.printStackTrace();
			}
			TopicCrawled tc = new TopicCrawled();
			tc.setTopicInfoId(id);
			tc.setTopicUrl(url);
			
			FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(tc, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
		} else {
//			run();
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		int count = 0;
		try {
			while (true) {
				count ++;
				log.info("##################" + count);
				List<TopicCrawled> list = AutohomeTaskRequest.getClubDetailTask();
				log.info("获取未抓取个数：" + list.size());
				if (CollectionUtils.isNotEmpty(list)) {
					ExecutorService pool = Executors.newFixedThreadPool(10);
					for (TopicCrawled ss : list) {
						pool.submit(new AutoHomeClubDetailCrawl(ss.getTopicInfoId(), ss.getTopicUrl()));
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
