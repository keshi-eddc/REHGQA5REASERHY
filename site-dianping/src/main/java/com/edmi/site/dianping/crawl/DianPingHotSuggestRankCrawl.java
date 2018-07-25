package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.edmi.site.dianping.entity.DianpingCityInfo;
import com.edmi.site.dianping.entity.DianpingHotSearchRank;
import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.cache.utils.ClassUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 抓取点评APP端的实时榜，菜品榜
 * @author conner
 *
 */
@Component
public class DianPingHotSuggestRankCrawl {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;
	
	private static List<DianpingCityInfo> cityList = new ArrayList<>();
	
	public DianPingHotSuggestRankCrawl() {
		super();
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		cityList = iGeneralJdbcUtils.queryForListObject(new SqlEntity(
				"select * from " + ClassUtils.getTableName(DianpingCityInfo.class)
						+ " where cityName in ('北京', '上海', '广州', '深圳')",
				DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO), DianpingCityInfo.class);
	}
	
	/**
	 * 每周三，周六 7点，12点，15点，19点
	 */
	@Scheduled(cron="0 0 7,12,15,19 ? * Wed,Sat")
	public void realTimeRank() {
		for (DianpingCityInfo city : cityList) {
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl("https://mapi.dianping.com/mapi/hotsuggestranklist.json?categoryid=0&cityid=" + city.getCityId() + "&type=1&mylng=&mylat=0&_=" + System.currentTimeMillis());
			String html = DianPingCommonRequest.getRealTimeRank(header);
			JSONObject json = JSONObject.parseObject(html);
			
			if (json.containsKey("list")) {
				
				Date current = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(current);
				String updateTime = "";
				String batchTime = DateFormatUtils.format(current, "yyyy-MM-dd HH") + ":00";
				
				if (json.containsKey("updateInfo")) {
					updateTime = json.getString("updateInfo").replace("数据更新时间: ", "");
				} else {
					updateTime = DateFormatUtils.format(current, "yyyy-MM-dd HH:mm");
				}
				
				JSONArray array = json.getJSONArray("list");
				if (CollectionUtils.isNotEmpty(array)) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject obj = array.getJSONObject(i);
						String keyword = JSONObject.parseObject(JSONArray.parseArray(obj.getString("keyword")).get(0).toString()).getString("text");
						
						DianpingHotSearchRank realTimeRank = new DianpingHotSearchRank();
						realTimeRank.setDataType("real_time");
						realTimeRank.setCityCnname(city.getCityName());
						realTimeRank.setUpdateTime(updateTime);
						realTimeRank.setBatchTime(batchTime);
						realTimeRank.setBatchWeekDay(c.get(Calendar.DAY_OF_WEEK) - 1);
						realTimeRank.setBatchWeek(c.get(Calendar.WEEK_OF_MONTH));
						realTimeRank.setBatchMonth(c.get(Calendar.MONTH) + 1);
						realTimeRank.setRank(i + 1);
						realTimeRank.setKeyword(keyword);
						realTimeRank.setSearchCount(obj.getInteger("suggestCount"));
						
						FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(realTimeRank, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
					}
				}
			}
		}
	}
	
	/**
	 * 每周三的12点
	 */
	@Scheduled(cron="0 0 12 ? * Sat,Sun")
	public void dishRank() {
		System.out.println(123);
		for (DianpingCityInfo city : cityList) {
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl("https://mapi.dianping.com/mapi/hotsuggestranklist.json?categoryid=0&cityid=" + city.getCityId() + "&type=2&mylng=&mylat=0&_=" + System.currentTimeMillis());
			String html = DianPingCommonRequest.getRealTimeRank(header);
			JSONObject json = JSONObject.parseObject(html);
			
			if (json.containsKey("list")) {
				
				Date current = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(current);
				String updateTime = "";
				String batchTime = DateFormatUtils.format(current, "yyyy-MM-dd HH") + ":00";
				
				if (json.containsKey("updateInfo")) {
					updateTime = json.getString("updateInfo").replace("数据更新时间: ", "");
				} else {
					updateTime = DateFormatUtils.format(current, "yyyy-MM-dd HH:mm");
				}
				
				JSONArray array = json.getJSONArray("list");
				if (CollectionUtils.isNotEmpty(array)) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject obj = array.getJSONObject(i);
						String keyword = JSONObject.parseObject(JSONArray.parseArray(obj.getString("keyword")).get(0).toString()).getString("text");
						
						DianpingHotSearchRank realTimeRank = new DianpingHotSearchRank();
						realTimeRank.setDataType("dish");
						realTimeRank.setCityCnname(city.getCityName());
						realTimeRank.setUpdateTime(updateTime);
						realTimeRank.setBatchTime(batchTime);
						realTimeRank.setBatchWeekDay(c.get(Calendar.DAY_OF_WEEK) - 1);
						realTimeRank.setBatchWeek(c.get(Calendar.WEEK_OF_MONTH));
						realTimeRank.setBatchMonth(c.get(Calendar.MONTH) + 1);
						realTimeRank.setRank(i + 1);
						realTimeRank.setKeyword(keyword);
						realTimeRank.setSearchCount(obj.getInteger("suggestCount"));
						
						FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(realTimeRank, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
//		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
//		
		new DianPingHotSuggestRankCrawl().realTimeRank();
//		new DianPingHotSuggestRandCrawl().dishRank();
//		
//		((AbstractApplicationContext) context).registerShutdownHook();
	}
	
}
