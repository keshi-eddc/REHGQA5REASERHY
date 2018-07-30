package com.edmi.site.cars.autohome.parse;

import com.edmi.site.cars.autohome.http.AutohomeCommonHttp;

import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * @author 
 *https://www.screwfix.com/
 */
public class RequsetTest2 {

	public static void main(String[] args) {
		String url = "https://www.screwfix.com/c/tools/drills/cat830704#category=cat830704&page_size=100&page_start=0";
		String cookie = "";
		HttpRequestHeader hearder = new HttpRequestHeader();
		hearder.setUrl(url);
//		hearder.setCookie(cookie);
		String content = AutohomeCommonHttp.getReputationComment(hearder);
		System.out.println(content);
	}
}
