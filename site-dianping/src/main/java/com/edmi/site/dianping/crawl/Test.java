package com.edmi.site.dianping.crawl;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.core.HttpClientSupport;

@Component
public class Test {
	
	private static Logger log = LogSupport.getJdlog();
	
	public void dly() {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://dly.134t.com/query.txt?key=NP71F79F3F&word=&count=1000&detail=true");
		header.setProxyType(ProxyType.NONE);
		header.setAutoPcUa(true);
		System.out.println(HttpClientSupport.get(header).getContent());
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
