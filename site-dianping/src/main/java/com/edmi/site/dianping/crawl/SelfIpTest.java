package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.entity.IpTest;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.bean.HttpResponse;
import fun.jerry.httpclient.core.HttpClientSupport;
import fun.jerry.proxy.entity.Proxy;

public class SelfIpTest implements Runnable {
	
	private static Logger log = LogSupport.getYhdlog();
	
	private String source;
	
	private int port;

	public SelfIpTest(String source, int port) {
		super();
		this.source = source;
		this.port = port;
	}

	@Override
	public void run() {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://2018.ip138.com/ic.asp");
		header.setProxyType(ProxyType.NONE);
		header.setProxy(new Proxy(source, port));
		header.setAutoPcUa(true);
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setEncode("GB2312");
		header.setHost("2018.ip138.com");
		header.setUpgradeInsecureRequests("1");
		header.setCookie("pgv_pvi=2987542528; ASPSESSIONIDQQDDQATB=OONPBKLCKDFFLEIICKHOMPJP; pgv_si=s6775525376; ASPSESSIONIDCSQRBCRC=ANMDEPCDMOKHMIGHLCCFNJBF; ASPSESSIONIDSSABQBTB=DLNIKPLCEAMPAELEELHDCLFH");
//			log.info(HttpClientSupport.get(header).getContent());
		header.setMaxTryTimes(1);
		HttpResponse rsp = HttpClientSupport.get(header);
		IpTest ip = new IpTest();
		ip.setSource(source);
		ip.setPort(port);
		if (null != rsp) {
			String html = rsp.getContent();
			if (StringUtils.isNotEmpty(html)) {
				Document doc = Jsoup.parse(html);
				
				String info = doc.select("center").text();
				
				ip.setIp(info.substring(info.indexOf("[") + 1, info.lastIndexOf("]")));
				ip.setLocation(info.substring(info.indexOf("来自：") + 3));
			}
		}
		
		FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(ip, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
	}
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		List<String[]> sourceList = new ArrayList<>();
		String[] source1 = new String[] {"192.168.6.201", "8888"};
		sourceList.add(source1);
		String[] source2 = new String[] {"192.168.6.202", "8888"};
		sourceList.add(source2);
		String[] source3 = new String[] {"192.168.6.203", "8888"};
		sourceList.add(source3);
		String[] source4 = new String[] {"192.168.6.204", "8888"};
		sourceList.add(source4);
		String[] source5 = new String[] {"192.168.6.205", "8888"};
		sourceList.add(source5);
		String[] source6 = new String[] {"192.168.6.206", "8888"};
		sourceList.add(source6);
		String[] source7 = new String[] {"192.168.6.207", "8888"};
		sourceList.add(source7);
		String[] source8 = new String[] {"192.168.6.208", "8888"};
		sourceList.add(source8);
		
		ExecutorService pool = Executors.newFixedThreadPool(sourceList.size());
//		
		for (int i = 0; i < 100; i++) {
			String[] source = sourceList.get(i % sourceList.size());
			pool.execute(new SelfIpTest(source[0], NumberUtils.toInt(source[1], 8888)));
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
	}
	
}
