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
import com.edmi.site.cars.autohome.crawl.AutoHomeMobileReputationExtract;

import fun.jerry.cache.constant.Constant;
import fun.jerry.common.LogSupport;

/**
 * 汽车之家-口碑 解析HTML文件
 */
@Component
public class AutoHomeReputationExtractHelper {
	public final static BlockingQueue<String> existBbsId = new ArrayBlockingQueue<String>(Constant.firstCacheSize);

	public static Logger log = LogSupport.getAutohomelog();

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

		log.info("到点了，汽车之家-口碑 数据开始解析");
		String path = "E:\\data\\ProjectData\\autohome\\reputation_out_20180801";
		File file = new File(path);
		File[] filelist = file.listFiles();
		System.out.println("包含：" + filelist.length + "个 文件");

		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (File f : filelist) {
			String filename = f.getName();
			// System.out.println("filename:" + filename);
			if (filename.contains("html")) {
				String filePath = f.getAbsolutePath();
				pool.submit(new AutoHomeMobileReputationExtract(filePath));
			}
		}
		pool.shutdown();

		while (true) {
			if (pool.isTerminated()) {
				log.error("汽车之家-口碑-文件读取解析完成");
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
