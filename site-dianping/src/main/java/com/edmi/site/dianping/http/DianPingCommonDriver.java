package com.edmi.site.dianping.http;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fun.jerry.browser.WebDriverSupport;
import fun.jerry.browser.entity.WebDriverConfig;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.proxy.entity.Proxy;

public class DianPingCommonDriver {

	private static Logger log = LogSupport.getDianpinglog();

	public static int count = 0;
	
	/**
	 * 校验Driver使用的代理是否有效
	 * @param driver
	 * @return
	 */
	public boolean valideProxy(WebDriver driver) {
		boolean flag = true;
		try {
			driver.get("http://2018.ip138.com/ic.asp");
			String html = driver.getPageSource();
			if (html.contains("代理服务器出现问题，或者地址有误")) {
				flag = false;
			}
		} catch (Exception e) {
			log.error("校验webdriver使用的IP是否有效时出错, ", e);
			flag = false;
		}
		return flag;
	}

	/**
	 * 拷贝人工登录后的Cookie，使用Cookie保持登录状态,登录成功后，使用新Cookie替换老Cookie
	 * @param header
	 * @return
	 */
	public static Map<String, Object> getShopComment(HttpRequestHeader header, WebDriver driver) {
		try {
			if (null == driver) {
				driver = getDriver(header);
			}
			driver.get("http://2018.ip138.com/ic.asp");
			driver.get("http://www.dianping.com");
//			driver.get("https://account.dianping.com/login?redir=http%3A%2F%2Fwww.dianping.com%2F");

			String cookies = "cy=1; cye=shanghai; _lxsdk_cuid=164ff3af98bc8-0f7d0d8fc8c96-3c3c520d-1fa400-164ff3af98b88; _lxsdk=164ff3af98bc8-0f7d0d8fc8c96-3c3c520d-1fa400-164ff3af98b88; _hc.v=e77102e0-4d2b-6b88-2809-e4de480e8065.1533290413; lgtoken=050ab8613-e4f9-4175-8541-62ea534f21c4; dper=2498a4bc85ecc3a1ed4a429771e0c082da64d4fb372ba039c9bf9c2d26cadbb439fb0855769281c4f94790f95d5362f66ec689e731a52d4fee3134347bee1b86de8293e28302a38cf03c5301c9d2c733dfe23f131b13a54382c6aa5d3e28fd6f; ll=7fd06e815b796be3df069dec7836c3df; ua=18397495453; ctu=259c123275264ff11be743799e5be38f9c94024b66705064475f64b3a9fa4505; s_ViewType=10; _lxsdk_s=164ff3af98d-2cd-61-c91%7C%7C265";
			String[] cookieArray = cookies.split(";");
			for (String temp : cookieArray) {
				String[] c = temp.split("=");
				Cookie ck = new Cookie(c[0].trim(), c[1].trim());
				driver.manage().addCookie(ck);
			}

			// driver.get("https://account.dianping.com/login?redir=http%3A%2F%2Fwww.dianping.com%2F");
			driver.get("http://www.dianping.com");
			driver.get("http://www.dianping.com/shop/4013473/review_all?queryType=sortType&&queryVal=latest");
			Set<Cookie> cookieSet = driver.manage().getCookies();
			for (Cookie temp : cookieSet) {
				System.out.println(temp.getName() + "=" + temp.getValue());
			}
			driver.get("http://www.dianping.com/shop/4013473/review_all/p2?queryType=sortType&queryVal=latest");
			log.info("#############");
			cookieSet = driver.manage().getCookies();
			for (Cookie temp : cookieSet) {
				System.out.println(temp.getName() + "=" + temp.getValue());
			}
		} catch (Exception e) {
			if (null != driver) {
				driver.close();
				driver.quit();
			}
			log.error(e);
			getShopComment(header, null);
		}
		
		return null;
	}
	
	/**
	 * 使用代理IP + 自动登录，但是容易出现验证码，很难维护
	 * @param header
	 * @return
	 */
	public static String getShopCommentByLogin(HttpRequestHeader header) {
		try {
			WebDriver driver = getDriver(header);
			driver.get("http://2018.ip138.com/ic.asp");
			driver.get("http://www.dianping.com");
			driver.get("https://account.dianping.com/login?redir=http%3A%2F%2Fwww.dianping.com%2Fshanghai%2Fch10%2Fg110");
//			driver.get("https://account.dianping.com/account/iframeLogin?callback=EasyLogin_frame_callback0&wide=false&protocol=https:&redir=http%3A%2F%2Fwww.dianping.com%2Fshanghai%2Fch10%2Fg110");
			
			login(driver);

//			String cookies = "cy=1; cye=shanghai; _lxsdk_cuid=164ff3af98bc8-0f7d0d8fc8c96-3c3c520d-1fa400-164ff3af98b88; _lxsdk=164ff3af98bc8-0f7d0d8fc8c96-3c3c520d-1fa400-164ff3af98b88; _hc.v=e77102e0-4d2b-6b88-2809-e4de480e8065.1533290413; lgtoken=050ab8613-e4f9-4175-8541-62ea534f21c4; dper=2498a4bc85ecc3a1ed4a429771e0c082da64d4fb372ba039c9bf9c2d26cadbb439fb0855769281c4f94790f95d5362f66ec689e731a52d4fee3134347bee1b86de8293e28302a38cf03c5301c9d2c733dfe23f131b13a54382c6aa5d3e28fd6f; ll=7fd06e815b796be3df069dec7836c3df; ua=18397495453; ctu=259c123275264ff11be743799e5be38f9c94024b66705064475f64b3a9fa4505; s_ViewType=10; _lxsdk_s=164ff3af98d-2cd-61-c91%7C%7C265";
//			String[] cookieArray = cookies.split(";");
//			for (String temp : cookieArray) {
//				String[] c = temp.split("=");
//				Cookie ck = new Cookie(c[0].trim(), c[1].trim());
//				driver.manage().addCookie(ck);
//			}

			// driver.get("https://account.dianping.com/login?redir=http%3A%2F%2Fwww.dianping.com%2F");
			driver.get("http://www.dianping.com");
			driver.get("http://www.dianping.com/shop/97435241/review_all/p11?queryType=sortType&queryVal=default");
			log.info(driver.getPageSource());
			Set<Cookie> cookieSet = driver.manage().getCookies();
			for (Cookie temp : cookieSet) {
				System.out.println(temp.getName() + "=" + temp.getValue());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}
	
	private static void login(WebDriver driver) {
		WebElement iframe = driver.findElement(By.tagName("iframe"));
		driver.switchTo().frame(iframe);
		WebElement a = driver.findElement(By.className("bottom-password-login"));
		a.click();
		
		WebElement b = driver.findElement(By.id("tab-account"));
		b.click();
		
		WebElement phone = driver.findElement(By.id("account-textbox"));
		phone.clear();
		phone.sendKeys("17151837694");
		
		WebElement password = driver.findElement(By.id("password-textbox"));
		password.clear();
		password.sendKeys("123abc123");
		
		WebElement login = driver.findElement(By.id("login-button-account"));
		login.click();
	}

	@SuppressWarnings("rawtypes")
	private static WebDriver getDriver(HttpRequestHeader header) {
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder
				.getBean(GeneralJdbcUtils.class);
		long date = System.currentTimeMillis() / 1000;
//		String sql = "with temp as ("
//				+ " select ipAddress, dly_expiretime from ProxyIP1.dbo.proxyIp where status = 1 and dly_expiretime - 500 > "
//				+ date + " UNION ALL"
//				+ " select ipAddress, dly_expiretime from ProxyIP2.dbo.proxyIp where status = 1 and dly_expiretime - 500 > "
//				+ date + " UNION ALL"
//				+ " select ipAddress, dly_expiretime from ProxyIP3.dbo.proxyIp where status = 1 and dly_expiretime - 500 > "
//				+ date + " UNION ALL"
//				+ " select ipAddress, dly_expiretime from ProxyIP4.dbo.proxyIp where status = 1 and dly_expiretime - 500 > "
//				+ date + " UNION ALL"
//				+ " select ipAddress, dly_expiretime from ProxyIP5.dbo.proxyIp where status = 1 and dly_expiretime - 500 > "
//				+ date + " UNION ALL"
//				+ " select ipAddress, dly_expiretime from ProxyIP6.dbo.proxyIp where status = 1 and dly_expiretime - 500 > "
//				+ date + " UNION ALL"
//				+ " select ipAddress, dly_expiretime from ProxyIP7.dbo.proxyIp where status = 1 and dly_expiretime - 500 > "
//				+ date + " UNION ALL"
//				+ " select ipAddress, dly_expiretime from ProxyIP8.dbo.proxyIp where status = 1 and dly_expiretime - 500 > "
//				+ date + ") select top 1 * from temp order by newid()";
//		Map<String, Object> map = iGeneralJdbcUtils
//				.queryOne(new SqlEntity(sql, DataSource.DATASOURCE_ProxyIP, SqlType.PARSE_NO));
//
//		String ipAddress = map.get("ipAddress").toString();
//		ipAddress = "1.195.25.33:57112";
//		String[] ips = ipAddress.split(":");
//		Proxy proxy = new Proxy(ips[0], NumberUtils.toInt(ips[1]));

		WebDriverConfig config = new WebDriverConfig();
//		config.setProxy(proxy);
		config.setUserDataDir("/temp/" + UUID.randomUUID());
		WebDriver driver = WebDriverSupport.getChromeDriverInstance(config);
		return driver;
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		getShopComment(null, null);
//		getShopCommentByLogin(null);
	}

}