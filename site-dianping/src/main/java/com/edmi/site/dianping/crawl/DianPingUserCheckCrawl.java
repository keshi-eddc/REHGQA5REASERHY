package com.edmi.site.dianping.crawl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendPage;
import com.edmi.site.dianping.entity.DianpingUserCheckInfo;
import com.edmi.site.dianping.entity.DianpingUserInfo;
import com.edmi.site.dianping.http.DianPingCommonRequest;
import com.edmi.site.dianping.parse.DianpingParser;

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
 * 点评用户签到信息
 * @author conner
 *
 */
public class DianPingUserCheckCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	/**
	 * 2016-12-31 23:59:59
	 */
	private static final long time = 1483199999000L;
	
	private long maxCheckTime;
	
	private DianpingShopComment comment;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;

	public DianPingUserCheckCrawl(DianpingShopComment comment) {
		super();
		this.comment = comment;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		this.maxCheckTime = time;
	}

	@Override
	public void run() {
		HttpRequestHeader header = new HttpRequestHeader();
		for (int page = 1; ; page ++) {
			header.setUrl("http://www.dianping.com/ajax/member/checkin/checkinList?memberId=" + comment.getUserId() + "&page=" + page);
			String html = DianPingCommonRequest.getUserCheckInfo(header);
			if(StringUtils.isNotEmpty(html)) {
				try {
					JSONObject jsonObj = JSONObject.parseObject(html);
					if (jsonObj.containsKey("code") && jsonObj.getInteger("code") == 200) {
						if (jsonObj.containsKey("msg")) {
							JSONObject msg = jsonObj.getJSONObject("msg");
							if (msg.containsKey("checkinList")) {
								JSONArray array = msg.getJSONArray("checkinList");
								if (CollectionUtils.isNotEmpty(array)) {
									boolean stopFlag = false;
									for (Object obj : array) {
										JSONObject temp = JSONObject.parseObject(obj.toString());
										DianpingUserCheckInfo check = new DianpingUserCheckInfo();
										check.setId(temp.getString("checkInId"));
										check.setUserId(comment.getUserId());
										check.setUserName(comment.getUserName());
										check.setShopId(temp.getString("shopId"));
										check.setShopName(temp.getString("shopName"));
										check.setCheckTime(temp.getLong("addTime"));
										check.setCheckTimeStr(DateFormatUtils.format(check.getCheckTime(), "yyyy-MM-dd HH:mm"));
										if (check.getCheckTime() < maxCheckTime) {
											stopFlag = true;
											break;
										}
//										iGeneralJdbcUtils.execute(new SqlEntity(check, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT_NOT_EXISTS));
										FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(check, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
									}
									if (stopFlag) {
										log.info(comment.getUserId() + " 发现 2017，该用户签到信息抓取结束");
										break;
									}
								} else {
									log.info(comment.getUserId() + " 未发现签到列表，该用户签到信息抓取结束");
									DianpingUserCheckInfo check = new DianpingUserCheckInfo();
									check.setId("-1");
									check.setUserId(comment.getUserId());
									FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(check, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
									break;
								}
							}
						}
					}
				} catch (Exception e) {
					log.error(comment.getUserId() + " " + comment.getUserName() + " 解析报错：", e);
				}
				
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);

//		DianPingCommonRequest.refreshUserInfoCookie();
//		DianPingCommonRequest.refreshShopRecommendCookie();
		
		StringBuilder sql = new StringBuilder();
		sql.append("select top 10000 * from dbo.Dianping_User_Info A "
					+ "where not exists (select 1 from dbo.Dianping_User_Check_Info B where A.user_id = B.user_id)"
				);
		
		while (true) {
			List<DianpingShopComment> urls = iGeneralJdbcUtils.queryForListObject(
					new SqlEntity(sql.toString(), DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO),
					DianpingShopComment.class);
			
			if (CollectionUtils.isEmpty(urls)) {
				break;
			}
			
			ExecutorService pool = Executors.newFixedThreadPool(30);
			for (DianpingShopComment comment : urls) {
				pool.execute(new DianPingUserCheckCrawl(comment));
			}
			
			pool.shutdown();

			while (true) {
				if (pool.isTerminated()) {
					log.error("大众点评-refresh user check 抓取完成");
					break;
				} else {
					try {
						TimeUnit.SECONDS.sleep(60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
}
