package fun.jerry.site.dianping.http;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpStatus;
import org.openqa.selenium.Proxy.ProxyType;

import fun.jerry.common.ProxyType;
import fun.jerry.common.RequestType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.bean.HttpResponse;
import fun.jerry.httpclient.core.HttpClientSupport;
import fun.jerry.httpclient.core.UserAgentSupport;

public class DianPingCommonRequest extends HttpClientSupport {
	
	private final static BlockingQueue<String> COOKIES = new ArrayBlockingQueue<String>(200);

	private final static BlockingQueue<String> COOKIES_Search = new ArrayBlockingQueue<String>(200);
	
	static {
		COOKIES.add("showNav=#nav-tab|0|1; navCtgScroll=300; showNav=javascript:; navCtgScroll=0; _hc.v=e21bfd77-23c0-bd96-85cf-adec6aa34747.1509347333; __utma=1.780503649.1510041292.1510041292.1510041292.1; __utmz=1.1510041292.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _lxsdk_cuid=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; _lxsdk=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; aburl=1; cy=16; cye=wuhan; s_ViewType=10; _lxsdk_s=160badf2b55-705-8a3-e77%7C%7C431");
//		COOKIES_Search.add("showNav=#nav-tab|0|1; navCtgScroll=100; showNav=#nav-tab|0|1; navCtgScroll=0; _hc.v=e21bfd77-23c0-bd96-85cf-adec6aa34747.1509347333; __utma=1.780503649.1510041292.1510041292.1510041292.1; __utmz=1.1510041292.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _lxsdk_cuid=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; _lxsdk=1608bd17337c8-0c6a9a9c51456a-5e183017-100200-1608bd17337c8; aburl=1; cy=1; cye=shanghai; s_ViewType=10; _lxsdk_s=161e55c7af9-bc2-a76-b58%7C%7C29");
		COOKIES_Search.add("showNav=javascript:; navCtgScroll=200; showNav=javascript:; navCtgScroll=0; _hc.v=e21bfd77-23c0-bd96-85cf-adec6aa34747.1509347333; __utma=1.780503649.1510041292.1510041292.1510041292.1; __utmz=1.1510041292.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); JSESSIONID=26CEC1B3FCEF371EED7A05E9969BEE13; _lxsdk_cuid=161fa3115fcc8-0538dde0ed830a-5e183017-100200-161fa3115fdc8; _lxsdk=161fa3115fcc8-0538dde0ed830a-5e183017-100200-161fa3115fdc8; cy=1; cye=shanghai; s_ViewType=10; _lxsdk_s=161fa311600-d4e-fc9-1de%7C%7C103");
	}
	
	public static String getSubCategorySubRegion(HttpRequestHeader header) {
		header.setRequestType(RequestType.DRIVER_CHROME);
		header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.setAcceptEncoding("gzip, deflate");
		header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
		header.setCacheControl("no-cache");
		header.setConnection("keep-alive");
		header.setHost("www.dianping.com");
		header.setUpgradeInsecureRequests("1");
		header.setUserAgent(UserAgentSupport.getPCUserAgent());
		header.setCookie(COOKIES_Search.element());
		header.setRequestSleepTime(5000);
		if (header.getRequestType() == RequestType.DRIVER_CHROME) {
			
		}
		HttpResponse response = get(header);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
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
		header.setProxyType(ProxyType.PROXY_TYPE_STATIC);
		header.setCookie(COOKIES_Search.element());
		header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
		HttpResponse response = get(header);
		header.setRequestSleepTime(5000);
		if (response.getCode() == HttpStatus.SC_OK) {
			return response.getContent();
		} else {
			return "";
		}
	}
}