package com.edmi.site.autohome.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.edmi.site.autohome.entity.TopicCrawled;

import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.core.HttpClientSupport;

public class AutohomeTaskRequest extends HttpClientSupport {
	
	public static List<TopicCrawled> getClubDetailTask() {
		List<TopicCrawled> list = new ArrayList<>();
		try {
			HttpRequestHeader header = new HttpRequestHeader();
//			header.setUrl("http://101.231.74.144:9092/task/autohome/club/detail/get");
			header.setUrl("http://192.168.0.49:9092/task/autohome/club/detail/get");
			header.setProxyType(ProxyType.NONE);
			header.setProject(Project.BUDWEISER);
			header.setSite(Site.DIANPING);
			String html = get(header).getContent();
			list = JSONArray.parseArray(html, TopicCrawled.class);
		} catch (Exception e) {
			list = new ArrayList<>();
		} finally {
			if (CollectionUtils.isEmpty(list)) {
				return new ArrayList<>();
			}
		}
		return list;
	}
}