package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.bean.HttpRequestHeader.NameValue;
import fun.jerry.httpclient.core.HttpClientSupport;

@Component
public class IDFederationTest {
	
	public static void main(String[] args) {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("https://login.microsoftonline.com/57368c21-b8cf-42cf-bd0b-43ecd4bc62ae/oauth2/authorize");
		List<NameValue> list = new ArrayList<HttpRequestHeader.NameValue>();
		NameValue nv = header.new NameValue("client_id", "Sean_Xiao@cargill.com");
//		NameValue nv1 = header.new NameValue("grant_type", "client_credential");
		list.add(nv);
//		list.add(nv1);
		header.setValues(list);
		System.out.println(HttpClientSupport.post(header));
	}
}
