package com.edmi.site.autohome.crawl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edmi.site.autohome.config.HtmlDataUtil;
import com.edmi.site.autohome.http.AutohomeCommonHttp;

import fun.jerry.common.DateFormatSupport;
import fun.jerry.common.LogSupport;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 按照车型抓取口碑 每个车型的口碑可能有多页
 * 
 * @author conner
 */
public class AutoHomeClubListCrawl implements Runnable {

	public static Logger log = LogSupport.getYhdlog();
	
	public static Logger fileLog = LogSupport.getOfftakelog();

	private static final String sort = "?orderby=dateline&qaType=-1";
	
	private static final Date endDate = DateFormatSupport.dateFormat(DateFormatSupport.YYYY_MM, "2017-10-01");
	
	private String id;

	private long seriesId;

	public AutoHomeClubListCrawl(String id) {
		super();
		this.id = id;
		this.seriesId = Long.parseLong(id.trim()) - 200000000;
	}

	@Override
	public void run() {
		int totalPage = 1;
		for (int page = 1; page <= totalPage ; page ++) {
			String url = "https://club.autohome.com.cn/bbs/forum-c-" + seriesId + "-" + page + ".html" + sort;
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl(url);
			int count = 0;
			String html = "";
			// 如果没有超过最大请求次数，并且不合法
			while (count < 20 && (StringUtils.isEmpty(html) || html.contains("verify-box") || html.contains("您的访问出现异常")
					|| html.contains("No such file or directory") || html.contains("File or directory not found")
					|| html.contains("由于您的网络存在安全问题"))) {
				
				html = AutohomeCommonHttp.getMobileClubDetail(header);
				count ++;
			}
//			log.info(html);
			// 有的车系的论坛会跳到其他车系，正常跳转，终止抓取
			if (page == 1 && !html.contains("bbs/forum-c-" + seriesId + "-1.html")) {
				log.info(id + " 当前车系论坛会跳转到其他论坛中，正常跳转，终止");
				saveByPage(page, html);
				break;
			} else if (StringUtils.isNotEmpty(html) && !html.contains("verify-box") && !html.contains("您的访问出现异常")
					&& !html.contains("No such file or directory") && !html.contains("File or directory not found")
					&& !html.contains("由于您的网络存在安全问题")) {
				// 不包含非法内容的后续处理
				
				Document doc = Jsoup.parse(html);
				
				if (page == 1) {
					Element tp = doc.select(".pagearea .fr").first();
					totalPage = null != tp ? NumberUtils.toInt(tp.text().replace("共", "").replace("页", "")) : 1;
					log.info(id + " 总页数 " + totalPage);
				}
				
				// 找到列表中第一个发布日期，如果第一个发布日期都比 endDate 早，保存文件，并结束
				Elements publishDateEles = doc.select("#subcontent .list_dl .tdate");
				
				if (CollectionUtils.isNotEmpty(publishDateEles)) {
					
					boolean flag = false;
					
					for (Element publishDate : publishDateEles) {
						Date date = DateFormatSupport.dateFormat(DateFormatSupport.YYYY_MM, publishDate.text().trim());
						if (date.after(endDate) || date.compareTo(endDate) == 0) {
							flag = true;
							break;
						}
					}
					
					if (flag) {
						saveByPage(page, html);
					} else {
						log.info(id + " 发现所有日子都比截止日期早，终止, 当前页码 " + page);
						saveByPage(page, html);
						break;
					}
					
				} else {
					log.info(id + " 未发现发帖日期，终止");
					saveByPage(page, html);
					break;
				}
				
			} else if (count >= 20) {
				// 超过最大次数保存并结束
				log.info(id + " 超过最大重试次数，终止");
				saveByPage(page, html);
				break;
			}
		}
	}
	
	private void saveByPage(int page, String html) {
		try {
			HtmlDataUtil.saveData("E:/autohome/clublist/" + id + "-" + page + ".html", html);
		} catch (Exception e) {
			fileLog.error("文件保存出错 : ", e);
		}
	}

	public static void main(String[] args) {
		
		List<String> list = new ArrayList<>();
		list.add("200000006");
		list.add("200000013");
		list.add("200000015");
		list.add("200000016");
		list.add("200000018");
		list.add("200000019");
		list.add("200000022");
		list.add("200000023");
		list.add("200000024");
		list.add("200000025");
		list.add("200000038");
		list.add("200000045");
		list.add("200000046");
		list.add("200000047");
		list.add("200000049");
		list.add("200000050");
		list.add("200000051");
		list.add("200000052");
		list.add("200000053");
		list.add("200000056");
		list.add("200000057");
		list.add("200000059");
		list.add("200000060");
		list.add("200000063");
		list.add("200000064");
		list.add("200000065");
		list.add("200000066");
		list.add("200000067");
		list.add("200000068");
		list.add("200000069");
		list.add("200000075");
		list.add("200000076");
		list.add("200000077");
		list.add("200000078");
		list.add("200000081");
		list.add("200000082");
		list.add("200000083");
		list.add("200000084");
		list.add("200000085");
		list.add("200000086");
		list.add("200000087");
		list.add("200000089");
		list.add("200000090");
		list.add("200000091");
		list.add("200000094");
		list.add("200000095");
		list.add("200000097");
		list.add("200000098");
		list.add("200000099");
		list.add("200000101");
		list.add("200000102");
		list.add("200000103");
		list.add("200000104");
		list.add("200000106");
		list.add("200000107");
		list.add("200000109");
		list.add("200000110");
		list.add("200000111");
		list.add("200000112");
		list.add("200000117");
		list.add("200000121");
		list.add("200000122");
		list.add("200000126");
		list.add("200000127");
		list.add("200000128");
		list.add("200000130");
		list.add("200000131");
		list.add("200000132");
		list.add("200000133");
		list.add("200000135");
		list.add("200000138");
		list.add("200000139");
		list.add("200000141");
		list.add("200000142");
		list.add("200000144");
		list.add("200000145");
		list.add("200000146");
		list.add("200000148");
		list.add("200000149");
		list.add("200000153");
		list.add("200000155");
		list.add("200000159");
		list.add("200000161");
		list.add("200000162");
		list.add("200000163");
		list.add("200000164");
		list.add("200000166");
		list.add("200000168");
		list.add("200000170");
		list.add("200000172");
		list.add("200000174");
		list.add("200000175");
		list.add("200000177");
		list.add("200000178");
		list.add("200000179");
		list.add("200000182");
		list.add("200000184");
		list.add("200000185");
		list.add("200000186");
		list.add("200000188");
		list.add("200000191");
		list.add("200000192");
		list.add("200000194");
		list.add("200000196");
		list.add("200000197");
		list.add("200000199");
		list.add("200000201");
		list.add("200000202");
		list.add("200000204");
		list.add("200000205");
		list.add("200000206");
		list.add("200000207");
		list.add("200000208");
		list.add("200000209");
		list.add("200000210");
		list.add("200000211");
		list.add("200000212");
		list.add("200000222");
		list.add("200000224");
		list.add("200000227");
		list.add("200000230");
		list.add("200000231");
		list.add("200000232");
		list.add("200000233");
		list.add("200000235");
		list.add("200000237");
		list.add("200000238");
		list.add("200000252");
		list.add("200000255");
		list.add("200000256");
		list.add("200000257");
		list.add("200000258");
		list.add("200000261");
		list.add("200000263");
		list.add("200000264");
		list.add("200000265");
		list.add("200000266");
		list.add("200000267");
		list.add("200000270");
		list.add("200000271");
		list.add("200000272");
		list.add("200000275");
		list.add("200000277");
		list.add("200000281");
		list.add("200000283");
		list.add("200000284");
		list.add("200000285");
		list.add("200000286");
		list.add("200000287");
		list.add("200000289");
		list.add("200000290");
		list.add("200000291");
		list.add("200000293");
		list.add("200000295");
		list.add("200000298");
		list.add("200000300");
		list.add("200000304");
		list.add("200000305");
		list.add("200000306");
		list.add("200000308");
		list.add("200000311");
		list.add("200000314");
		list.add("200000316");
		list.add("200000317");
		list.add("200000322");
		list.add("200000325");
		list.add("200000328");
		list.add("200000329");
		list.add("200000330");
		list.add("200000332");
		list.add("200000333");
		list.add("200000334");
		list.add("200000341");
		list.add("200000342");
		list.add("200000343");
		list.add("200000344");
		list.add("200000345");
		list.add("200000348");
		list.add("200000351");
		list.add("200000352");
		list.add("200000354");
		list.add("200000355");
		list.add("200000356");
		list.add("200000357");
		list.add("200000358");
		list.add("200000359");
		list.add("200000360");
		list.add("200000361");
		list.add("200000362");
		list.add("200000363");
		list.add("200000364");
		list.add("200000365");
		list.add("200000366");
		list.add("200000367");
		list.add("200000368");
		list.add("200000369");
		list.add("200000370");
		list.add("200000371");
		list.add("200000372");
		list.add("200000373");
		
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (String id : list) {
			pool.submit(new AutoHomeClubListCrawl(id));
		}
		pool.shutdown();

		while (true) {
			if (pool.isTerminated()) {
				log.error("############################################### 抓取完成");
				break;
			} else {
				try {
					TimeUnit.SECONDS.sleep(60);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
//		try {
			// 6 page ,2 end
//			new Thread(new AutoHomeClubListCrawl("200000170")).start();
			// 跳转
//			new Thread(new AutoHomeClubListCrawl("200000640")).start();
			// 14页发现全部早于截止日期
//			new Thread(new AutoHomeClubListCrawl("200000777")).start();
			// 共 1 页
//			new Thread(new AutoHomeClubListCrawl("200002307")).start();
			// 跳转
//			new Thread(new AutoHomeClubListCrawl("200003098")).start();
			// 共 1 页
//			new Thread(new AutoHomeClubListCrawl("200003329")).start();
			// 共 1 页
//			new Thread(new AutoHomeClubListCrawl("200003366")).start();
//			new Thread(new AutoHomeClubListCrawl("200003857")).start();
			
			
			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}
}
