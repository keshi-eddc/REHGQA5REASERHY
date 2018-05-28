package com.edmi.site.autohome.crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.edmi.site.autohome.config.HtmlDataUtil;
import com.edmi.site.autohome.http.AutohomeCommonHttp;

import fun.jerry.common.LogSupport;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 按照车型抓取口碑 每个车型的口碑可能有多页
 * 
 * @author conner
 */
public class AutoHomeClubListCrawl implements Runnable {

	public static Logger log = LogSupport.getYhdlog();

	private String id;

	private String url;

	public AutoHomeClubListCrawl(String id, String url) {
		super();
		this.id = id;
		this.url = url;
	}

	@Override
	public void run() {
		url = url
				// .replace("club.autohome.com.cn", "club.m.autohome.com.cn")
				.replace("http:", "https:");
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl(url);
		String html = AutohomeCommonHttp.getMobileClubDetail(header);
		Document doc = Jsoup.parse(html);
		Elements eles = doc.select("#subcontent .list_dl");
		int size = CollectionUtils.isNotEmpty(eles) ? eles.size() : 0;
		if (!html.contains("您的访问出现异常") && !html.contains("No such file or directory")
				&& !html.contains("File or directory not found")) {
			HtmlDataUtil.saveData("E:/autohome/clublist/" + id + "-" + size + ".html", html);
		} else {
			// HtmlDataUtil.saveData("E:/autohome/clublist/" + id + "-" + size +
			// "-error.html", html);
			run();
		}

	}

	public static void main(String[] args) {
		try {
			
			for (int i = 0; i < 300; i++) {
				
				log.info("加载第 " + i + " 次论坛列表");
				
				File file = new File("E:/forum_url_p02");
//				File file = new File("E:/forum_url_p02-test");
				String[] haha = file.list();
				List<String> originalIdList = new ArrayList<>();
				List<String> needCrawlIdList = new ArrayList<>();
				for (String fileName : haha) {
					File tempFile = new File("E:/forum_url_p02/" + fileName);
					FileInputStream fis = new FileInputStream(tempFile);
					BufferedReader br = new BufferedReader(new InputStreamReader(fis));
					String line = null;
					while ((line = br.readLine()) != null) {
						originalIdList.add(line);
						needCrawlIdList.add(line.split(",")[0]);
					}
					br.close();
				}
				
				File crawled = new File("E:/autohome/clublist");
				String[] crawledFileList = crawled.list();
				for (String exist : crawledFileList) {
					File delete = new File("E:/autohome/clublist/" + exist);
					if (delete.exists()) {
						FileUtils.forceDelete(delete);
					}
				}
				
				while (originalIdList.size() != crawledFileList.length) {
					ExecutorService pool = Executors.newFixedThreadPool(20);
					
					int count = 0;

					for (String line : originalIdList) {
						String[] temp = line.split(",");
						if (needCrawlIdList.contains(temp[0])) {
							pool.submit(new AutoHomeClubListCrawl(temp[0], temp[1]));
							count++;
						}
					}
					log.info(count + " $$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					pool.shutdown();

					while (true) {
						if (pool.isTerminated()) {
							log.error("汽车之家-论坛-口碑-抓取完成");
							
							crawled = new File("E:/autohome/clublist");
							crawledFileList = crawled.list();
							for (String fileName : crawledFileList) {
								Iterator<String> it = needCrawlIdList.iterator();
								while (it.hasNext()) {
									if (it.next().equals(fileName.split("-")[0])) {
										it.remove();
									}
								}
							}
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

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
