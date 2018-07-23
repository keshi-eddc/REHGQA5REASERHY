package com.edmi.site.cars.autohome.parse;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edmi.site.cars.autohome.entity.ReputationComment;
import com.edmi.site.cars.autohome.entity.ReputationCrawled;
import com.edmi.site.cars.autohome.entity.ReputationList;
import com.edmi.site.cars.autohome.http.AutohomeCommonHttp;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.entity.annotation.TableMapping;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

public class Test {
	public static void main(String[] args) {
		//加载spring配置
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		//建立连接
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder
				.getBean(GeneralJdbcUtils.class);
		String showId = "01bvnerh4v64vkacsr6wv00000";
		String sql = "select * from dbo.F_ReputationList_P02 where ReputationUrl like '%" + showId + "%'";
		//从数据库查
		Map<String, Object> list = iGeneralJdbcUtils
				.queryOne(new SqlEntity(sql, DataSource.DATASOURCE_SGM, SqlType.PARSE_NO));

		for (String key : list.keySet()) {
			// System.out.println(key + " " + list.get(key));
		}
		Object SeriesBrandId = list.get("SeriesBrandId");
		String SeriesBrandIdstr = String.valueOf(SeriesBrandId);
		Integer SeriesBrandIdint = Integer.valueOf(SeriesBrandIdstr);

		Object ModelBrandId = list.get("ModelBrandId");
		String ModelBrandIdstr = String.valueOf(ModelBrandId);
		Long ModelBrandIdlon = Long.valueOf(ModelBrandIdstr);

		System.out.println("SeriesBrandIdint:" + SeriesBrandIdint);
		System.out.println("ModelBrandIdlon:" + ModelBrandIdlon);
		
		//想数据库里写  表名在ReputationComment.java @TableMapping("F_ReputationComment_P02")
		ReputationComment reputationComment = new ReputationComment();
		FirstCacheHolder.getInstance().submitFirstCache(
				new SqlEntity(reputationComment, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));

	}
}
