package com.edmi.site.dianping.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.edmi.site.dianping.config.DianpingConfig;
import com.edmi.site.dianping.cookie.DianpingShopDetailCookie;
import com.edmi.site.dianping.cookie.DianpingUserInfoCookie;

import fun.jerry.browser.WebDriverSupport;
import fun.jerry.browser.entity.WebDriverConfig;
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

public class DianPingCommonRequest extends HttpClientSupport {

	private static Logger log = LogSupport.getDianpinglog();

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
		header.setCookie("");
//		header.setUserAgent(
//				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
//		header.setUserAgent(UserAgentSupport.getPCUserAgent());
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
//		header.setAutoPcUa(true);
//		header.setAutoMobileUa(true);
		header.setRequestSleepTime(1000);
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
		
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_SHOP_DETAIL.poll();
		if (null != map && map.containsKey("cookie")) {
			header.setCookie(map.get("cookie").toString());
			header.setUserAgent(map.get("user_agent").toString());
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_SHOP_DETAIL.add(map);
			
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getShopComment(HttpRequestHeader header) {
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
//		int temp = new Random().nextInt(2000) + 100;
//		log.info("##############################  " + temp);
//		header.setCookie("cy=1; cye=shanghai; _lxsdk_cuid=1635e81d5a075-09058d3f43fb66-3f3c5501-100200-1635e81d5a1c8; "
//				+ "_lxsdk=1635e81d5a075-09058d3f43fb66-3f3c5501-100200-1635e81d5a1c8; "
//				+ "_hc.v=33d000db-e502-ea26-0b9f-5b9d922c26cf.;" + System.currentTimeMillis() / 1000 + " lgtoken=0e29cde03-9f96-471f-a65d-65064f1a090b; dper=2b77b9d675a89e5d46ca3857f188dee39b157643607744d23557342b23c3684a6afac73e3e33e885bb710a2c1daadb3c6f8a0cf30c6309ed05a0efe3f170e3a65c6d25f460fb7975ece2875a9c80766c8a7e54207a04af79186b784a1c5aa8f6; ll=7fd06e815b796be3df069dec7836c3df; ua=17080236415; ctu=8547636063072e202fea44548b5b3241fe96563d09252696b65f932541e8aeac; _lxsdk_s=1635e81d5a2-07f-870-5b0%7C%7C"
//				+ temp);
//		header.setCookie("cy=1; cye=shanghai; _lxsdk_cuid=16363a4d9d5c8-081941b76cdc5-3c3c5905-100200-16363a4d9d581; _lxsdk=16363a4d9d5c8-081941b76cdc5-3c3c5905-100200-16363a4d9d581; _hc.v=7d65222a-e429-4eef-0af2-fb043377894b.1526385138; dper=d53c28ee19e0ffaa8a3d3393127536d0c6ac92de9efd6c62493afc0a30bbebb46793b9834c958069742a1986a372819a4622755ed61fbd57a77a8e7fa9861b682d267627bfa9fbd952089820514b74489a9296d4dc9516c860007b176cdeaabc; ll=7fd06e815b796be3df069dec7836c3df; ua=15046321964; ctu=877054e387b412665d57e710e1bf3f01c861a2dc561249d58a2132e548b5f7dd; s_ViewType=10; _lxsdk_s=16363a4d9d7-2b2-718-d54%7C%7C328");
//		header.setCookie("cy=2; cye=beijing; _lxsdk_cuid=1635e4a04ecc8-04865857687ba9-3f3c5501-100200-1635e4a04ecc8; _lxsdk=1635e4a04ecc8-04865857687ba9-3f3c5501-100200-1635e4a04ecc8; _hc.v=2725c736-98fd-868d-44f2-bd11965864c0.1526295303; dper=2b77b9d675a89e5d46ca3857f188dee355599a967b78fe239baf27592a1b54c1cc173547b9de59cc4dbc45d3c7c9ecf06d37d94c9b6d9f51aa16d97feeff9f9481148fca223b824626709c47a30cc9d38f6323ef89bac9b72af5355462655b86; ll=7fd06e815b796be3df069dec7836c3df; ua=17080236415; ctu=8547636063072e202fea44548b5b3241caba2053d5b42a9557a4610f9ad4ca92; _lxsdk_s=1635e4a04ed-a-c7c-498%7C%7C"
//				+ new Random().nextInt(2000) + 100);
//		header.setAutoPcUa(true);
//		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
//		Map<String, Object> map = iGeneralJdbcUtils
//				.queryOne(new SqlEntity("select top 1 * from dbo.Dianping_Cookie where phone = '17092688735'",
//						DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO));
//		if (map.containsKey("cookie")) {
//			header.setCookie(map.get("cookie").toString());
//			log.info("本批次使用的电话号码 " + map.get("phone").toString());
//		}
		
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_SHOP_DETAIL.poll();
		if (null != map && map.containsKey("cookie")) {
			header.setCookie(map.get("cookie").toString());
//			header.setUserAgent(map.get("user_agent").toString());
			header.setAutoPcUa(true);
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_SHOP_DETAIL.add(map);
			
		}
		header.setAutoPcUa(true);
//		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
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
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_SHOP_DETAIL.poll();
		if (null != map && map.containsKey("cookie")) {
			header.setCookie(map.get("cookie").toString());
			header.setUserAgent(map.get("user_agent").toString());
//			header.setAutoPcUa(true);
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_SHOP_DETAIL.add(map);
			
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
		
		Map<String, Object> map = DianpingShopDetailCookie.COOKIES_SHOP_DETAIL.poll();
		if (null != map && map.containsKey("cookie")) {
			header.setCookie(map.get("cookie").toString());
			header.setUserAgent(map.get("user_agent").toString());
			log.info("本批次使用的电话号码 " + map.get("phone").toString());
			
			DianpingShopDetailCookie.COOKIES_SHOP_DETAIL.add(map);
			
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
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
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