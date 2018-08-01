package com.edmi.site.dianping.crawl;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.edmi.site.dianping.entity.IpTest;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.core.HttpClientSupport;
import fun.jerry.proxy.entity.Proxy;

@Component
public class Test {
	
	private static Logger log = LogSupport.getJdlog();
	
	public void dly() {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://2018.ip138.com/ic.asp");
		header.setProxyType(ProxyType.NONE);
		header.setProxy(new Proxy("192.168.6.201", 8888));
		header.setAutoPcUa(true);
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setEncode("GB2312");
		header.setHost("2018.ip138.com");
		header.setUpgradeInsecureRequests("1");
		header.setCookie("pgv_pvi=2987542528; ASPSESSIONIDQQDDQATB=OONPBKLCKDFFLEIICKHOMPJP; pgv_si=s6775525376; ASPSESSIONIDCSQRBCRC=ANMDEPCDMOKHMIGHLCCFNJBF; ASPSESSIONIDSSABQBTB=DLNIKPLCEAMPAELEELHDCLFH");
		log.info(HttpClientSupport.get(header).getContent());
		
		Document doc = Jsoup.parse(HttpClientSupport.get(header).getContent());
		
		String info = doc.select("center").text();
		
		IpTest ip = new IpTest();
		ip.setIp(info.substring(info.indexOf("[") + 1, info.lastIndexOf("]")));
		ip.setLocation(info.substring(info.indexOf("来自：") + 3));
		
//		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
//		iGeneralJdbcUtils.execute(new SqlEntity(ip, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
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
