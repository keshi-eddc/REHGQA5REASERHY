package com.edmi.site.cars.autohome.http;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import fun.jerry.browser.WebDriverSupport;
import fun.jerry.browser.entity.WebDriverConfig;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.core.HttpClientSupport;

public class AutohomeCommonHttp extends HttpClientSupport {
	
	private static Logger log = LogSupport.getAutohomelog();
	
	public static String getMobileReputationList (HttpRequestHeader header) {
		String html = "";
//		header.setProxyType(ProxyType.NONE);
		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setProject(Project.OTHER);
		header.setSite(Site.OTHER);
		
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate, br");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setCookie("sessionip=140.206.67.58; sessionid=AD390436-D4CB-4DBE-ACFE-AF33FE78F864%7C%7C2018-05-25+15%3A02%3A59.072%7C%7C0; area=310199; __ah_uuid=389BB5BB-68CF-418B-8012-2C79F80887FE; fvlid=1527231772240Z5Gxhfrdk7; ahpau=1; UM_distinctid=1639691f1ff30-031ec2db9d2b93-3c3c520d-100200-1639691f200170; sessionuid=AD390436-D4CB-4DBE-ACFE-AF33FE78F864%7C%7C2018-05-25+15%3A02%3A59.072%7C%7C0; __utma=1.1478855483.1527303163.1527303163.1527303163.1; __utmz=1.1527303163.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); ahsids=614_526; searchhistory=%7B%22a%22%3A%7B%22t%22%3A%5B%22%22%5D%2C%22k%22%3A%22%22%7D%2C%22f%22%3A%7B%22t%22%3A%5B%22%22%5D%2C%22k%22%3A%22%22%7D%7D; ASP.NET_SessionId=rj5y2fu3firlafti53y4ru4u; sessionvid=3E57CA4A-CE41-4BB7-ABBD-2F8B70E8E155; historybbsName4=c-2098%7CAC%20Schnitzer%2Cc-145%7CPolo; papopclub=3A9D2AD369B0016BE117B0954BD4B994; pepopclub=6AF5B1912982E1EC2C4CD67E92D6675A; isFromBaiDuSearch=; isFromQQSearch=; pvidchain=2147101,2147101,2147101,2147101,2147101,2147101,2147101,2147101,2147101,2147101; ahpvno=246; ref=www.baidu.com%7C0%7C0%7C0%7C2018-05-26+12%3A35%3A22.595%7C2018-05-26+12%3A29%3A04.736");
		header.setHost("k.m.autohome.com.cn");
		header.setPragma("no-cache");
		header.setUpgradeInsecureRequests("1");
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
		header.setRequestSleepTime(1000);
		header.setMaxTryTimes(1);
		html = get(header).getContent();
		return html;
	}
	
	public static String getMobileClubDetail (HttpRequestHeader header)  {
		String html = "";
//		header.setProxyType(ProxyType.NONE);
//		header.setProxyType(ProxyType.PROXY_STATIC_DLY);
		header.setProxyType(ProxyType.PROXY_CLOUD_ABUYUN);
		header.setProject(Project.OTHER);
		header.setSite(Site.OTHER);
		header.setEncode("UTF-8");
		
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate, br");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
//		header.setCookie("sessionip=140.206.67.58; sessionid=AD390436-D4CB-4DBE-ACFE-AF33FE78F864%7C%7C2018-05-25+15%3A02%3A59.072%7C%7C0; area=310199; __ah_uuid=389BB5BB-68CF-418B-8012-2C79F80887FE; fvlid=1527231772240Z5Gxhfrdk7; ahpau=1; UM_distinctid=1639691f1ff30-031ec2db9d2b93-3c3c520d-100200-1639691f200170; sessionuid=AD390436-D4CB-4DBE-ACFE-AF33FE78F864%7C%7C2018-05-25+15%3A02%3A59.072%7C%7C0; __utma=1.1478855483.1527303163.1527303163.1527303163.1; __utmz=1.1527303163.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); ahsids=614_526; searchhistory=%7B%22a%22%3A%7B%22t%22%3A%5B%22%22%5D%2C%22k%22%3A%22%22%7D%2C%22f%22%3A%7B%22t%22%3A%5B%22%22%5D%2C%22k%22%3A%22%22%7D%7D; sessionvid=3E57CA4A-CE41-4BB7-ABBD-2F8B70E8E155; historybbsName4=c-2098%7CAC%20Schnitzer%2Cc-145%7CPolo; isFromBaiDuSearch=; isFromQQSearch=; historyClub=588; isFromBD=; autoac=0EF926FA611926CE017DB35862B44DCE; autotc=3ADB8FCBCA08F1973E549E5B4A760003; ahpvno=1707; ref=www.baidu.com%7C0%7C0%7C0%7C2018-05-26+16%3A52%3A08.224%7C2018-05-26+12%3A29%3A04.736");
//		header.setHost("club.m.autohome.com.cn");
		header.setHost("club.autohome.com.cn");
		header.setPragma("no-cache");
		header.setUpgradeInsecureRequests("1");
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
		header.setRequestSleepTime(1000);
		header.setMaxTryTimes(3);
		html = get(header).getContent();
		return html;
		
	}
	
	public static String getReputationList(HttpRequestHeader header) {
		String html = "";
		WebDriver driver = null;
		try {
			WebDriverConfig config = new WebDriverConfig();
			config.setTimeOut(10);
			config.setProxyType(ProxyType.PROXY_STATIC_DLY);
//			config.setProxyType(ProxyType.PROXY_CLOUD_ABUYUN);
			config.setSite(Site.OTHER);
			config.setProject(Project.OTHER);
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
//		return get(header);
	}
	
	public static String getReputationListByHttp(HttpRequestHeader header) {
		String html = "";
		header.setEncode("GBK");
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate, br");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("k.autohome.com.cn");
		header.setUpgradeInsecureRequests("1");
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		header.setCookie("__ah_uuid=835AF3BD-E0FE-4C10-8D1F-080CEF4AF6A7; fvlid=1526025212417niz6Gp6k2w; sessionid=38BBFF60-805D-4830-B9AF-2F26D3905FE1%7C%7C2018-05-11+15%3A53%3A35.620%7C%7C0; area=310199; ahpau=1; sessionuid=38BBFF60-805D-4830-B9AF-2F26D3905FE1%7C%7C2018-05-11+15%3A53%3A35.620%7C%7C0; ahsids=4080_4221; sessionvid=4CCD1EA1-6B16-404C-B06F-08D2CC59C9F5; autoac=E3AC996AF21B1EC38488272A947DFA37; autotc=2EBF56F82B14173712FE21557B1C23D3; ASP.NET_SessionId=k0qbiw1hi4kkrddduexbh4za; ahpvno=3; pvidchain=3311277,104136; ref=0%7C0%7C0%7C0%7C2018-05-14+12%3A03%3A59.536%7C2018-05-11+15%3A53%3A35.620; ahrlid=1526270636250XF8KsedzCj-1526270647772");
		header.setTimeOut(1000);
//		WebDriver driver = null;
//		try {
//			WebDriverConfig config = new WebDriverConfig();
//			driver = WebDriverSupport.getChromeDriverInstance(config);
//			html = WebDriverSupport.load(driver, header.getUrl());
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != driver) {
//				driver.quit();
//			}
//		}
//		
//		return html;
		return get(header).getContent();
	}
	
	public static String getReputation(HttpRequestHeader header) {
		String html = "";
		header.setEncode("GBK");
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate, br");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("k.autohome.com.cn");
		header.setUpgradeInsecureRequests("1");
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
		header.setCookie("__ah_uuid=835AF3BD-E0FE-4C10-8D1F-080CEF4AF6A7; fvlid=1526025212417niz6Gp6k2w; sessionid=38BBFF60-805D-4830-B9AF-2F26D3905FE1%7C%7C2018-05-11+15%3A53%3A35.620%7C%7C0; area=310199; ahpau=1; sessionuid=38BBFF60-805D-4830-B9AF-2F26D3905FE1%7C%7C2018-05-11+15%3A53%3A35.620%7C%7C0; ahsids=4080_4221; sessionvid=4CCD1EA1-6B16-404C-B06F-08D2CC59C9F5; autoac=E3AC996AF21B1EC38488272A947DFA37; autotc=2EBF56F82B14173712FE21557B1C23D3; ASP.NET_SessionId=k0qbiw1hi4kkrddduexbh4za; ahpvno=3; pvidchain=3311277,104136; ref=0%7C0%7C0%7C0%7C2018-05-14+12%3A03%3A59.536%7C2018-05-11+15%3A53%3A35.620; ahrlid=1526270636250XF8KsedzCj-1526270647772");
		header.setTimeOut(1000);
		WebDriver driver = null;
		try {
			WebDriverConfig config = new WebDriverConfig();
			driver = WebDriverSupport.getChromeDriverInstance(config);
			html = WebDriverSupport.load(driver, header.getUrl());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != driver) {
				driver.quit();
			}
		}
		
		return html;
//		return get(header);
	}
	
	/**
	 * 获取口碑评论
	 */
	public static String getReputationComment(HttpRequestHeader header) {
		header.setEncode("GBK");
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate, sdch");
		header.setAcceptLanguage("zh-CN,zh;q=0.8,en;q=0.6");
		header.setCacheControl("max-age=0");
		header.setConnection("keep-alive");
		header.setHost("reply.autohome.com.cn");
		header.setUpgradeInsecureRequests("1");
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
//		header.setTimeOut(500);
		return get(header).getContent();
	}
	
	/**
	 * 获取经销商信息
	 */
	public static String getReputationDealer(HttpRequestHeader header) {
		header.setUrl(header.getUrl().replace("|", "&brvbar;"));
		header.setEncode("GBK");
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate, sdch");
		header.setAcceptLanguage("zh-CN,zh;q=0.8,en;q=0.6");
		header.setCacheControl("max-age=0");
		header.setConnection("keep-alive");
		header.setHost("k.autohome.com.cn");
		header.setUpgradeInsecureRequests("1");
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
//		header.setTimeOut(500);
		return get(header).getContent();
	}
	
}