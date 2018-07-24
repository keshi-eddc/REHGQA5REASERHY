package com.edmi.site.dianping.cookie;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;

@Component
public class DianpingShopDetailCookie implements InitializingBean {
	
	private Logger log = LogSupport.getDianpinglog();
	
	public final static BlockingQueue<Map<String, Object>> COOKIES_SHOP_DETAIL = new ArrayBlockingQueue<>(50);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Scheduled(cron="0 0/10 * * * ?")
    public void refresh() {
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder
				.getBean(GeneralJdbcUtils.class);
		
		COOKIES_SHOP_DETAIL.clear();
		
		COOKIES_SHOP_DETAIL.addAll(iGeneralJdbcUtils
				.queryForListMap(new SqlEntity("select * from dbo.Dianping_Cookie where len(user_agent) > 0",
						DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO)));
		
		log.info("本次加载Cookie数量 " + COOKIES_SHOP_DETAIL.size());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void afterPropertiesSet() throws Exception {
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder
				.getBean(GeneralJdbcUtils.class);
		COOKIES_SHOP_DETAIL.addAll(iGeneralJdbcUtils
				.queryForListMap(new SqlEntity("select * from dbo.Dianping_Cookie where len(user_agent) > 0",
						DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO)));
		
		log.info("首次加载Cookie数量 " + COOKIES_SHOP_DETAIL.size());
	}
}
