package com.edmi.site.dianping.crawl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import fun.jerry.browser.WebDriverSupport;
import fun.jerry.browser.entity.WebDriverConfig;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.core.HttpClientSupport;
import fun.jerry.proxy.entity.Proxy;

@Component
public class Test2 {

	private static Logger log = LogSupport.getYhdlog();

	public static void test() {
		WebDriverConfig webDriverConfig = new WebDriverConfig();
		webDriverConfig.setUserDataDir("/temp/" + UUID.randomUUID());
//		WebDriver driver = WebDriverSupport.getChromeDriverInstance(webDriverConfig);
		WebDriver driver = WebDriverSupport.getPhantomJSDriverInstance(webDriverConfig);
		driver.manage().window().setSize(new Dimension(1366, 768));
		driver.get("http://182.106.129.54:8181/sbhc/login.html?tdsourcetag=s_pcqq_aiomsg");
		WebElement username = driver.findElement(By.id("username"));
		username.clear();
		username.sendKeys("abc");

//		WebElement password = driver.findElement(By.xpath(".//*[@id='password']"));
		WebElement password = driver.findElement(By.id("password"));
		password.clear();
		password.sendKeys("def");

		System.out.println("123");
	}

	public static void test2() {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("https://item.m.jd.com/product/18313204967.html");
		header.setProject(Project.OTHER);
		header.setSite(Site.JD);
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setHost("item.m.jd.com");
		System.out.println(HttpClientSupport.get(header).getContent());
	}

	public static void test3() throws Exception {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(
				"http://www.dianping.com/shop/93077944/review_all?queryType=sortType&&queryVal=latest");
		get.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		get.addHeader("Accept-Encoding", "gzip, deflate");
		get.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
		get.addHeader("Cache-Control", "no-cache");
		get.addHeader("Connection", "keep-alive");
		get.addHeader("Host", "www.dianping.com");
		get.addHeader("Pragma", "no-cache");
		get.addHeader("Referer", "http://www.dianping.com/shop/93077944/review_all?queryType=sortType&&queryVal=latest");
		get.addHeader("Upgrade-Insecure-Requests", "1");
		get.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

//		get.addHeader("Cookie",
//				"cy=1; cye=shanghai; _lxsdk_cuid=165c1c92e82c8-07abfaa3dc61ec-37664109-144000-165c1c92e82c8; _lxsdk=165c1c92e82c8-07abfaa3dc61ec-37664109-144000-165c1c92e82c8; _hc.v=c1517d5f-86fe-ecd6-09b7-2e6b8c802c97.1536554512; dper=60e25c1799bd2229ee0e398e9e50f5d02393b7039e565b0ab506a7cb183eeb9c173a6c850755b1a1ccf3e5aa75f552a02202ebce2ef2b2ca2d8581718723dee7a56820a37ba27e5ef8f953ebe38aefe856376593d818f02f592fe7e9a86bc1ed; ll=7fd06e815b796be3df069dec7836c3df; ua=17151837694; ctu=f5539fc230d3b0f5512266208879744a666c989f40c212a8832ffedc1a3251e5; s_ViewType=10; _lxsdk_s=165c1c92e92-b01-7d4-510%7C%7C837");
		get.addHeader("Cookie",
				"cy=1; cye=shanghai; _lxsdk_cuid=165c2903ac133-0249a99527ad89-37664109-144000-165c2903ac2c8; _lxsdk=165c2903ac133-0249a99527ad89-37664109-144000-165c2903ac2c8; _hc.v=b5e9edec-c5b5-3a39-1559-986fa734ff90.1536567557; ua=17151837694; ctu=f5539fc230d3b0f5512266208879744a5252d5d2dcc20bf59b843c188410726d; dper=60e25c1799bd2229ee0e398e9e50f5d06921dd1ed9252d002e0bc49002a4cf056dd9d6c6c31b106d577ad28fe6cce22d964aa333c2d21a53972dccdcbadd5af8b94607c72e989786beab08e2ad8f19610b5a1ece30c4a5acef56bc6df4d1e6c7; ll=7fd06e815b796be3df069dec7836c3df; s_ViewType=10; _lxsdk_s=165c69713bc-2b4-a89-47%7C%7C606");
		// 设置登陆时要求的信息，用户名和密码
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
		get.setConfig(globalConfig);

		CloseableHttpResponse response = null;
		response = (CloseableHttpResponse) httpClient.execute(get);
//			List<Cookie> cookies = cookieStore.getCookies();
//			for (int i = 0; i < cookies.size(); i++) {
//				log.info(cookies.get(i).getName() + "    " + cookies.get(i).getValue());
//            }
		// 如果响应不为null
		if (null != response) {

			// 如果请求成功
			if (null != response.getStatusLine() && (response.getStatusLine().getStatusCode() == 200)) {
				System.out.println(EntityUtils.toString(response.getEntity(), "UTF-8"));
			}
		} else {
			// 如果出现429，该请求暂停5分钟
			if (response.getStatusLine().getStatusCode() == 429) {
			}

		}
	}
	
	public static void test4() throws Exception {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/shop/110269910/review_all/p4?queryType=sortType&queryVal=latest");
		header.setCookie("cy=1; cye=shanghai; _lxsdk_cuid=165c2903ac133-0249a99527ad89-37664109-144000-165c2903ac2c8; _lxsdk=165c2903ac133-0249a99527ad89-37664109-144000-165c2903ac2c8; _hc.v=b5e9edec-c5b5-3a39-1559-986fa734ff90.1536567557; ua=17151837694; ctu=f5539fc230d3b0f5512266208879744a5252d5d2dcc20bf59b843c188410726d; dper=60e25c1799bd2229ee0e398e9e50f5d06921dd1ed9252d002e0bc49002a4cf056dd9d6c6c31b106d577ad28fe6cce22d964aa333c2d21a53972dccdcbadd5af8b94607c72e989786beab08e2ad8f19610b5a1ece30c4a5acef56bc6df4d1e6c7; ll=7fd06e815b796be3df069dec7836c3df; s_ViewType=10; _lxsdk_s=165c83382a6-cba-303-513%7C%7C1886");
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9");
		header.setCacheControl("max-age=0");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_CLOUD_ABUYUN);
//		header.setProxyType(ProxyType.NONE);
//		header.setProxy(new Proxy("1.197.59.158", 46668));
//		header.setProxyType(ProxyType.NONE);
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
		
		System.out.println(HttpClientSupport.get(header).getContent());
	}
	
	public static void test5() throws Exception {
		Document doc = Jsoup.connect("http://www.dianping.com/shop/110269910/review_all/p4?queryType=sortType&queryVal=latest")
//		.proxy("123.180.69.119", 57112)
//		.proxy("221.2.175.238", 8060)
		.validateTLSCertificates(false)//忽略证书认证,每种语言客户端都有类似的API
		.header("Cookie", "dper=60e25c1799bd2229ee0e398e9e50f5d01b1bf1f4521c695c0f7d2c37d87a8e556201716871c37c8fdb2d33644a8d36ba60d0d7f6d89167c43ef21edc6e7883a6; ll=7fd06e815b796be3df069dec7836c3df; ua=17151837694; ctu=f5539fc230d3b0f5512266208879744ae361b2dfbb48c1dd356cafd19b8bfe9a; _lxsdk_cuid=165c22f5f23c8-0a05754dc2d6c6-37664109-144000-165c22f5f23c8; _lxsdk=165c22f5f23c8-0a05754dc2d6c6-37664109-144000-165c22f5f23c8; _hc.v=b560d026-e573-67bd-214e-d6294e5e1cf7.1536561211; s_ViewType=10; cy=2; cye=beijing; _lxsdk_s=165c22f5f24-8dc-3e6-471%7C%7C1009")
		.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
		.timeout(10000)
		.get();
		System.out.println(doc);
	}
	
	public static void test6() throws Exception {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://2018.ip138.com/ic.asp");
		header.setProxyType(ProxyType.PROXY_CLOUD_ABUYUN);
//		header.setProxy(new Proxy(source, port));
		header.setAutoPcUa(true);
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setEncode("GB2312");
		header.setHost("2018.ip138.com");
		header.setUpgradeInsecureRequests("1");
		header.setCookie("pgv_pvi=2987542528; ASPSESSIONIDQQDDQATB=OONPBKLCKDFFLEIICKHOMPJP; pgv_si=s6775525376; ASPSESSIONIDCSQRBCRC=ANMDEPCDMOKHMIGHLCCFNJBF; ASPSESSIONIDSSABQBTB=DLNIKPLCEAMPAELEELHDCLFH");
		System.out.println(HttpClientSupport.get(header).getContent());
	}

	public static void main(String[] args) {
		SimpleDateFormat sdf_ym = new SimpleDateFormat("yyyyMM");
		
		try {
//			test3();
			System.out.println(sdf_ym.parse("201805").getTime());
			test4();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
