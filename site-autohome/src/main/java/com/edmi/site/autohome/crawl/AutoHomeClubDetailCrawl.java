package com.edmi.site.autohome.crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.edmi.site.autohome.config.HtmlDataUtil;
import com.edmi.site.autohome.entity.ModelBrand;
import com.edmi.site.autohome.http.AutohomeCommonHttp;

import fun.jerry.common.LogSupport;
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
		if (!html.contains("您的访问出现异常")) {
			HtmlDataUtil.saveData("E:/autohome/club/none/" + id + ".html", html);
		} else {
			HtmlDataUtil.saveData("E:/autohome/club/none/" + id + "-error.html", html);
		}
	}

	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream(new File("E:\\part-00000"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			
			ExecutorService pool = Executors.newFixedThreadPool(10);
			int count = 0;
			File file = new File("E:/autohome/club/none/");
			String[] filelist = file.list();
			List<String> idList = new ArrayList<>();
			for (String fileName : filelist) {
				File tempFile = new File("E:/autohome/club/none/" + "/" + fileName);
				if (tempFile.length() < 5000) {
					idList.add(fileName.replace(".html", ""));
				}
			}
			System.out.println(idList.size());
			
			while ((line = br.readLine()) != null) {
				String[] temp = line.split(",");
				if (idList.contains(temp[0])) {
					pool.submit(new AutoHomeClubDetailCrawl(temp[0], temp[1]));
				}
			}
			
			br.close();
			
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
