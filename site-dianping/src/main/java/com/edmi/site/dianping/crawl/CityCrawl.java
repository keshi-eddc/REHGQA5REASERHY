package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.edmi.site.dianping.entity.DianpingCityInfo;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;
import fun.jerry.httpclient.core.HttpClientSupport;

@Component
public class CityCrawl {
	
	private static Logger log = LogSupport.getJdlog();
	
	public void crawl () {
		String url = "http://www.dianping.com/ajax/citylist/getAllDomesticCity";
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl(url);
		header.setProxyType(ProxyType.NONE);
		header.setProject(Project.BUDWEISER);
		header.setSite(Site.DIANPING);
		
		String html = HttpClientSupport.get(header).getContent();
		log.info(html);
		
		parse(html);
	}
	
	@SuppressWarnings("rawtypes")
	private void parse (String html) {
		Map<String, String> provinceMap = new HashMap<String, String>();
		JSONObject json = JSONObject.parseObject(html);
		if (json.containsKey("provinceList")) {
			JSONArray array = json.getJSONArray("provinceList");
			for (Object temp : array) {
				JSONObject province = JSONObject.parseObject(temp.toString());
				provinceMap.put(province.getString("provinceId"), province.getString("provinceName"));
			}
		}
		if (json.containsKey("cityMap")) {
			List<SqlEntity> list = new ArrayList<>();
			JSONObject temp = json.getJSONObject("cityMap");
			for (String key : temp.keySet()) {
				JSONArray array = temp.getJSONArray(key);
				for (Object object : array) {
					DianpingCityInfo city = JSONObject.parseObject(object.toString()).toJavaObject(DianpingCityInfo.class);
					city.setProvinceName(provinceMap.get(city.getProvinceId()));
					list.add(new SqlEntity(city, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
				}
			}
			IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
			
			iGeneralJdbcUtils.batchExecute(list);
		}
	}
	
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		new CityCrawl().crawl();
	}
}
