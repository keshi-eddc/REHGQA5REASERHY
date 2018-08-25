package com.edmi.site.dianping.crawl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.edmi.site.dianping.entity.IpTest;

import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.bean.HttpResponse;
import fun.jerry.httpclient.core.HttpClientSupport;
import fun.jerry.proxy.entity.Proxy;

@Component
public class Test {
	
	private static Logger log = LogSupport.getYhdlog();

	public void dly () {
		Thread t1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				test1("192.168.6.201");
			}
		});
		t1.start();
		
		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				test2("192.168.6.202");
			}
		});
		t2.start();
		
		Thread t3 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				test3("192.168.6.203");
			}
		});
		t3.start();
	}
	
	private void test1 (String source) {
		int count = 0;
		while (count < 10000) {
			count ++;
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log3.info("开始第 " + count + " 次测试");
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl("http://2018.ip138.com/ic.asp");
			header.setProxyType(ProxyType.NONE);
			header.setProxy(new Proxy(source, 8888));
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
			header.setMaxTryTimes(3);
			HttpResponse rsp = HttpClientSupport.get(header);
			IpTest ip = new IpTest();
			if (null != rsp) {
				String html = rsp.getContent();
				if (StringUtils.isNotEmpty(html)) {
					Document doc = Jsoup.parse(html);
					
					String info = doc.select("center").text();
					
					ip.setIp(info.substring(info.indexOf("[") + 1, info.lastIndexOf("]")));
					ip.setLocation(info.substring(info.indexOf("来自：") + 3));
				}
				
				if (StringUtils.isNotEmpty(ip.getIp())) {
					log3.info("第 " + count + " 次测试成功    " + "source = " + source + "    " +  ip.getIp() + "    " + ip.getLocation());
				} else {
					log3.info("第 " + count + " 次测试失败    " + "source = " + source + "    " +  ip.getIp() + "    " + ip.getLocation());
				}
			}
		}
	}
	
	private void test2 (String source) {
		int count = 0;
		while (count < 10000) {
			count ++;
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log4.info("开始第 " + count + " 次测试");
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl("http://2018.ip138.com/ic.asp");
			header.setProxyType(ProxyType.NONE);
			header.setProxy(new Proxy(source, 8888));
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
			header.setMaxTryTimes(3);
			HttpResponse rsp = HttpClientSupport.get(header);
			IpTest ip = new IpTest();
			if (null != rsp) {
				String html = rsp.getContent();
				if (StringUtils.isNotEmpty(html)) {
					Document doc = Jsoup.parse(html);
					
					String info = doc.select("center").text();
					
					ip.setIp(info.substring(info.indexOf("[") + 1, info.lastIndexOf("]")));
					ip.setLocation(info.substring(info.indexOf("来自：") + 3));
				}
				
				if (StringUtils.isNotEmpty(ip.getIp())) {
					log4.info("第 " + count + " 次测试成功    " + "source = " + source + "    " +  ip.getIp() + "    " + ip.getLocation());
				} else {
					log4.info("第 " + count + " 次测试失败    " + "source = " + source + "    " +  ip.getIp() + "    " + ip.getLocation());
				}
			}
		}
	}
	
	private void test3 (String source) {
		int count = 0;
		while (count < 10000) {
			count ++;
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("开始第 " + count + " 次测试");
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl("http://2018.ip138.com/ic.asp");
			header.setProxyType(ProxyType.NONE);
			header.setProxy(new Proxy(source, 8888));
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
			header.setMaxTryTimes(3);
			HttpResponse rsp = HttpClientSupport.get(header);
			IpTest ip = new IpTest();
			if (null != rsp) {
				String html = rsp.getContent();
				if (StringUtils.isNotEmpty(html)) {
					Document doc = Jsoup.parse(html);
					
					String info = doc.select("center").text();
					
					ip.setIp(info.substring(info.indexOf("[") + 1, info.lastIndexOf("]")));
					ip.setLocation(info.substring(info.indexOf("来自：") + 3));
				}
				
				if (StringUtils.isNotEmpty(ip.getIp())) {
					log.info("第 " + count + " 次测试成功    " + "source = " + source + "    " +  ip.getIp() + "    " + ip.getLocation());
				} else {
					log.info("第 " + count + " 次测试失败    " + "source = " + source + "    " +  ip.getIp() + "    " + ip.getLocation());
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new Test().dly();
//		HttpRequestHeader header = new HttpRequestHeader();
////		header.setUrl("http://www.useragentstring.com/pages/useragentstring.php?typ=Mobile%20Browser");
//		header.setUrl("http://www.useragentstring.com/pages/useragentstring.php?typ=Browser");
//		String html = HttpClientSupport.get(header).getContent();
//		Document doc = Jsoup.parse(html);
//		Elements list = doc.select("ul li a");
//		for (org.jsoup.nodes.Element ele : list) {
//			if (ele.html().length() > 100 && !ele.html().contains("QQ") && !ele.html().contains("MSIE 6.0")) {
//				log.info("PC_UAList.add(\"" + ele.html() + "\");");
//			}
//		}
		
	}
}
