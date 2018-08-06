package com.edmi.site.dianping.http;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.dianping.cookie.DianpingShopDetailCookie;
import com.edmi.site.dianping.entity.IpTest;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.RequestType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.bean.HttpResponse;
import fun.jerry.httpclient.core.HttpClientSupport;
import fun.jerry.proxy.entity.Proxy;

public class DianPingCommonRequest extends HttpClientSupport {

	private static Logger log = LogSupport.getDianpinglog();
	
	private static Logger log_test = LogSupport.getJdlog();

	public static String getSubCategorySubRegion(HttpRequestHeader header) {
		header.setRequestType(RequestType.HTTP_GET);
		header.setProxyType(ProxyType.PROXY_STATIC_AUTO);
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setAutoPcUa(true);
		header.setCookie("");
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(2);
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
		} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
			return getSubCategorySubRegion(header);
		} else {
			return "";
		}
	}

	public static String getShopList(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		header.setProxyType(ProxyType.PROXY_CLOUD_ABUYUN);
		header.setCookie("cy=1; cye=shanghai; _lxsdk_cuid=163af0776adc8-0d9507b7989639-3c3c520d-100200-163af0776adc8; _lxsdk=163af0776adc8-0d9507b7989639-3c3c520d-100200-163af0776adc8; _hc.v=dc2c85cc-a2f8-2f67-41c0-85d1e7959bcf.1527649892; _dp.ac.v=7068a142-bca3-47f9-ad44-d115c0d0ace3; s_ViewType=10; ua=%E9%AD%94%E4%BA%BA%40%E6%99%AE%E4%B9%8C; ctu=946223b20ade88cd1373a6270d8145bf62877d2bae3b09c54d78e4c29b716109; _lxsdk_s=164f43ec641-3e6-b3d-214%7C%7C17");
//		header.setUserAgent(
//				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
//		header.setUserAgent(UserAgentSupport.getPCUserAgent());
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
//		header.setAutoPcUa(true);
//		header.setAutoMobileUa(true);
		header.setRequestSleepTime(3000);
		header.setMaxTryTimes(1);
		if (header.getProject() == Project.CARGILL) {
			header.setMaxTryTimes(10);
		}
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
		} else {
			return getShopList(header);
		}
	}

	public static String getShopDetail(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setPragma("no-cache");
//		header.setReferer("http://www.dianping.com/shanghai/ch10/g110");
		header.setUpgradeInsecureRequests("1");
//		header.setCookie(
//				"s_ViewType=10; _lxsdk_cuid=16380a356c9c8-0cc4004fa439b6-3c3c520d-100200-16380a356cac8; _lxsdk=16380a356c9c8-0cc4004fa439b6-3c3c520d-100200-16380a356cac8; _hc.v=c274d1e0-4c7d-f92a-dc57-a68f092822e7.1526871579; _lxsdk_s=16380a356d6-ad6-9ab-aa7%7C%7C5");
//		String random = test(new int[] {13,14,8,6,13});
//		header.setCookie(
//				"s_ViewType=10; _lxsdk_cuid=" + random.substring(0, random.length() - 1) + "; _lxsdk=" + random.substring(0, random.length() - 1) + "; cy=1; cye=shanghai; _hc.v=c274d1e0-4c7d-f92a-dc57-a68f092822e7.1526871579; _lxsdk_s=16380a356d6-ad6-9ab-aa7%7C%7C5");
//		header.setCookie(
//				"_hc.v=\"\\\"ecf7cc6e-e3ac-4e4b-a454-a8817f963380.1526881397\\\"\"; _lxsdk_cuid=163813ba56b2d-0b00460b78a70c-3b7c015b-100200-163813ba56cc8; _lxsdk=163813ba56b2d-0b00460b78a70c-3b7c015b-100200-163813ba56cc8; _lxsdk_s=163813ba56e-7b1-ecd-dae%7C%7C43");
		
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_DIANPING.poll();
		if (null != map && map.containsKey("cookie")) {
			header.setCookie(map.get("cookie").toString());
			header.setUserAgent(map.get("user_agent").toString());
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_DIANPING.add(map);
			
		}
		
//		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1");
		
//		header.setAutoPcUa(true);
//		header.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(1);
		HttpResponse response = get(header);
//		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
//		} else {
//			return getShopList(header);
//		}
	}

	public static String getShopComment(HttpRequestHeader header) {
		
//		try {
//			Document doc = Jsoup.connect("http://2018.ip138.com/ic.asp").get();
//			log_test.info(doc.select("center").text());
//			
//			String info = doc.select("center").text();
//			
//			IpTest ip = new IpTest();
//			ip.setIp(info.substring(info.indexOf("[") + 1, info.lastIndexOf("]")));
//			ip.setLocation(info.substring(info.indexOf("来自：") + 3));
//			
//			IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
//			iGeneralJdbcUtils.execute(new SqlEntity(ip, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9");
		header.setCacheControl("max-age=0");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		header.setProxyType(ProxyType.PROXY_CLOUD_ABUYUN);
//		header.setProxyType(ProxyType.NONE);
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
//		header.setProxy(new Proxy("125.118.242.90", 6666));
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_DIANPING.poll();
		if (null != map && map.containsKey(DianpingShopDetailCookie.COOKIE_COMMENT)) {
			header.setCookie(map.get(DianpingShopDetailCookie.COOKIE_COMMENT).toString());
//			header.setUserAgent(map.get("user_agent").toString());
			header.setAutoPcUa(true);
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_DIANPING.add(map);
			
		}
		
//		header.setCookie("cy=1; cye=shanghai; _lxsdk_cuid=164ff3759fcc8-0ecb77c2df43f1-3c3c520d-1fa400-164ff3759fdc8; _lxsdk=164ff3759fcc8-0ecb77c2df43f1-3c3c520d-1fa400-164ff3759fdc8; _hc.v=de179e2c-3e70-c5f3-5af4-7b7b4f2d309e.1533290175; _dp.ac.v=308386d9-f1b7-41ab-94b4-cfdf610c4b33; dper=60e25c1799bd2229ee0e398e9e50f5d05df543700781f5e9ed5f3e6870b11b16a07b7933a14543665a530a622b192adb19cd755547127f4803460f7476fa9a48d529e1eff44affc7ef2cd9889fa7be3c4fe22e86c7d06277f72216da490648f4; ll=7fd06e815b796be3df069dec7836c3df; ua=17151837694; ctu=f5539fc230d3b0f5512266208879744a3bda46f23d8f6626b3cd626c281a04a3; s_ViewType=10; _lxsdk_s=1650d4b9879-6b4-544-057%7C%7C315");
		
//		header.setAutoPcUa(true);
//		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
//		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
//		header.setCookie();
		header.setRequestSleepTime(10000);
		header.setMaxTryTimes(1);
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
		} else {
//			return getShopComment(header);
			return "";
		}
	}
	
	public static String getShopRecommend(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setPragma("no-cache");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		header.setProxyType(ProxyType.NONE);
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_DIANPING.poll();
		if (null != map && map.containsKey(DianpingShopDetailCookie.COOKIE_RECOMMEND)) {
			header.setCookie(map.get(DianpingShopDetailCookie.COOKIE_RECOMMEND).toString());
			header.setUserAgent(map.get("user_agent").toString());
//			header.setAutoPcUa(true);
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_DIANPING.add(map);
			
		}
//		header.setAutoPcUa(true);
//		header.setCookie("cy=1; cye=shanghai; _lxsdk_cuid=163af0776adc8-0d9507b7989639-3c3c520d-100200-163af0776adc8; _lxsdk=163af0776adc8-0d9507b7989639-3c3c520d-100200-163af0776adc8; _hc.v=dc2c85cc-a2f8-2f67-41c0-85d1e7959bcf.1527649892; _dp.ac.v=7068a142-bca3-47f9-ad44-d115c0d0ace3; s_ViewType=10; ua=%E9%AD%94%E4%BA%BA%40%E6%99%AE%E4%B9%8C; ctu=946223b20ade88cd1373a6270d8145bf62877d2bae3b09c54d78e4c29b716109; ctu=57f4fba19c4400d8ada2e815a0bacf8f56ccad2e0f9bc373588beeb8f5714ecc7694423828faf1dc4fe77b9617252c16; _lxsdk_s=164cb446b5c-9ac-fe2-10c%7C%7C112");
		header.setRequestSleepTime(10000);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
//				removeInvalideCookie(COOKIES_SHOPRECOMMEND, header.getCookie());
//				header.setCookie(COOKIES_SHOPRECOMMEND.element());
				html = getShopRecommend(header);
			} else {
				html = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	//header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
	/*
	public static String getUserInfo(HttpRequestHeader header) {
		header.setAcceptEncoding("gzip, deflate, br");
		header.setAcceptLanguage("zh-CN,zh;q=0.9");
		header.setCacheControl("max-age=0");
		header.setConnection("keep-alive");
		header.setHost("m.dianping.com");
		header.setUpgradeInsecureRequests("1");
		WebDriver driver = null;
		String html = "";
		try {
			WebDriverConfig config = new WebDriverConfig();
			config.setTimeOut(10);
			config.setProxyType(ProxyType.PROXY_STATIC_DLY);
			driver = WebDriverSupport.getPhantomJSDriverInstance(config);
//			driver = WebDriverSupport.getChromeDriverInstance(config);
			html = WebDriverSupport.load(driver, header.getUrl());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != driver) {
				driver.close();
				driver.quit();
			}
		}
		
		return html;
	}
	*/
	
	public static String getUserInfo(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate, br");
		header.setAcceptLanguage("zh-CN,zh;q=0.9");
		header.setCacheControl("max-age=0");
		header.setConnection("keep-alive");
		header.setHost("m.dianping.com");
		header.setUpgradeInsecureRequests("1");
		
//		Map<String, Object> map = COOKIES_USER_DETAIL.poll();
//		if (null != map && map.containsKey("cookie")) {
//			header.setCookie(map.get("cookie").toString());
//			header.setUserAgent(map.get("user_agent").toString());
//			log.info("本批次使用的电话号码 " + map.get("phone").toString());
//			
//			COOKIES_USER_DETAIL.add(map);
//			
//		}
		
//		Map<String, Object> map = DianpingUserInfoCookie.COOKIES_USER_INFO.poll();
//		if (null != map && map.containsKey("cookie")) {
//			header.setCookie(map.get("cookie").toString());
//			header.setUserAgent(map.get("user_agent").toString());
//			log.info("本批次使用的电话号码 " + map.get("phone").toString());
//			
//			DianpingShopDetailCookie.COOKIES_SHOP_DETAIL.add(map);
//			
//		}
		
//		header.setCookie("m_flash2=1; cityid=1; default_ab=index%3AA%3A1; cy=1; cye=shanghai; _lxsdk_cuid=1638ae2d38bc8-088125da2d0cbf-3c3c520d-100200-1638ae2d38bc8; _lxsdk=1638ae2d38bc8-088125da2d0cbf-3c3c520d-100200-1638ae2d38bc8; _hc.v=4582347e-2065-f8c6-141d-0b3297f319a1.1527043511; s_ViewType=10");
//		header.setAutoMobileUa(true);
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1");
		header.setRequestSleepTime(500);
		header.setMaxTryTimes(1);
		HttpResponse response = get(header);
		return response.getContent();
	}
	
	public static String getUserCheckInfo(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setPragma("no-cache");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
//		header.setProxyType(ProxyType.NONE);
//		header.setAutoPcUa(true);
//		header.setCookie(COOKIES_USERINFO.element());
//		header.setCookie("_hc.v=\"\"1c28735c-9efb-4f85-8805-eebb74bd311d.1521009797\"\"; _lxsdk_cuid=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; _lxsdk=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; cy=1; cye=shanghai; s_ViewType=10; m_flash2=1; pvhistory=6L+U5ZuePjo8L2Vycm9yL2Vycm9yX3BhZ2U+OjwxNTIxNTA3OTI5MTk2XV9b; _lxsdk_s=16240ebf651-c5a-e75-b4d%7C%7C319");
//		header.setCookie("_hc.v=\"\"1c28735c-9efb-4f85-8805-eebb74bd311d." + ((System.currentTimeMillis() / 1000) - 6666) + "\"\"; _lxsdk_cuid=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; _lxsdk=162233ff0b061-0bcb8b147ad2f-5e183017-100200-162233ff0b2c8; cy=1; cye=shanghai; s_ViewType=10; m_flash2=1; pvhistory=6L+U5ZuePjo8L2Vycm9yL2Vycm9yX3BhZ2U+OjwxNTIxNTA3OTI5MTk2XV9b; _lxsdk_s=16240ebf651-c5a-e75-b4d%7C%7C319");
		
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_DIANPING.poll();
		if (null != map && map.containsKey("cookie")) {
			header.setCookie(map.get("cookie").toString());
			header.setUserAgent(map.get("user_agent").toString());
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_DIANPING.add(map);
			
		}
		
//		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1");
		header.setRequestSleepTime(10000);
		header.setMaxTryTimes(1);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else if (response.getCode() == HttpStatus.SC_FORBIDDEN) {
				html = getUserCheckInfo(header);
			} else {
				html = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	/**
	 * 实时榜
	 * @param header
	 * @return
	 */
	public static String getRealTimeRank(HttpRequestHeader header) {
//		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setProxyType(ProxyType.NONE);
		header.setProject(Project.CARGILL);
		header.setSite(Site.DIANPING);
		header.setAutoMobileUa(true);
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(5);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else {
				html = getRealTimeRank(header);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	/**
	 * 菜品榜
	 * @param header
	 * @return
	 */
	public static String getDishRank(HttpRequestHeader header) {
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setAutoPcUa(true);
		header.setRequestSleepTime(5000);
		header.setMaxTryTimes(5);
		HttpResponse response = get(header);
		String html = "";
		try {
			if (response.getCode() == HttpStatus.SC_OK) {
				html = response.getContent();
			} else {
				html = getDishRank(header);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	public static void main(String[] args) {
//		HttpRequestHeader header = new HttpRequestHeader();
//		header.setUrl("http://www.dianping.com/shanghai/ch10/g110r2");
//		header.setProject(Project.CARGILL);
//		header.setSite(Site.DIANPING);
//		getShopList(header);
		
//		test(new int[] {13,14,8,6,13});
		
//		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		try {
			Document doc = Jsoup.connect("http://2018.ip138.com/ic.asp").get();
			log_test.info(doc.select("center").text());
			
			String info = doc.select("center").text();
			
			IpTest ip = new IpTest();
			ip.setIp(info.substring(info.indexOf("[") + 1, info.lastIndexOf("]")));
			ip.setLocation(info.substring(info.indexOf("来自：") + 3));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String test(int[] list) {
		StringBuilder sb = new StringBuilder();
		int maxNum = 36;
		int i;
		int count = 0;
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'w', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		
		for (int num : list) {
			while (count < num) {
				i = Math.abs(r.nextInt(maxNum));
				if (i >= 0 && i < str.length) {
					pwd.append(str[i]);
					count++;
				}
			}
			sb.append(pwd).append("-");
			pwd = new StringBuffer();
			count = 0;
		}
		sb.append(pwd);
		System.out.println(sb.toString());
		return sb.toString();
	}
	
}