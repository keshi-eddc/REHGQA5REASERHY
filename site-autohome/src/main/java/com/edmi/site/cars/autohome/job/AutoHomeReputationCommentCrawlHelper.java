package com.edmi.site.cars.autohome.job;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import com.edmi.site.cars.autohome.crawl.AutoHomeMobileReputationCommentCrawl;
import fun.jerry.cache.constant.Constant;
import fun.jerry.common.LogSupport;
/**
 * 汽车之家-口碑
 *
 */
@Component
public class AutoHomeReputationCommentCrawlHelper {

	public static Logger log = LogSupport.getAutohomelog();

	public final static BlockingQueue<String> existBbsId = new ArrayBlockingQueue<String>(Constant.firstCacheSize);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

		log.info("到点了，汽车之家-口碑评论 数据开始抓取！");

		String path = "E:\\data\\program\\autohome\\aotohome_reputation";
		File file = new File(path);
		File[] filelist = file.listFiles();
		System.out.println("包含：" + filelist.length + "个 文件");
		ExecutorService pool = Executors.newFixedThreadPool(10);
		int count = 0;
		for (File f : filelist) {
			count++;
			String filename = f.getName();
			// System.out.println("filename:" + filename);
			if (filename.contains("html")) {
				String filePath = f.getAbsolutePath();
				System.out.println("count>>" + count + "<<" + filePath);
				pool.submit(new AutoHomeMobileReputationCommentCrawl(filePath));
			}

		}
		pool.shutdown();

		while (true) {
			if (pool.isTerminated()) {
				log.error("汽车之家-口碑评论-抓取完成");
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
