package com.edmi.site.cars.autohome.crawl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.edmi.site.cars.autohome.config.CarsSiteIdSupport;
import com.edmi.site.cars.autohome.entity.ModelBrand;
import com.edmi.site.cars.autohome.entity.ReputationComment;
import com.edmi.site.cars.autohome.entity.ReputationInfo;
import com.edmi.site.cars.autohome.entity.ReputationList;
import com.edmi.site.cars.autohome.http.AutohomeCommonHttp;

import fun.jerry.cache.holder.FirstCacheHolder;
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

/**
 * 按照口碑id 抓取 口碑评论
 */
public class AutoHomeMobileReputationCommentCrawl implements Runnable {

	public static Logger log = LogSupport.getAutohomelog();

	private IGeneralJdbcUtils<?> iGeneralJdbcUtils;
	private String filepath;

	public AutoHomeMobileReputationCommentCrawl(String filepath) {
		super();
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils<?>) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		this.filepath = filepath;

	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + " run :" + filepath);
		String html = "";
		File file = new File(filepath);
		try {
			html = FileUtils.readFileToString(file, Charset.forName("GBK"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseMobileReputationComment(html);
	}

	public static void parseMobileReputationComment(String detail) {

		ReputationInfo reputationInfo = new ReputationInfo();
		reputationInfo.setPlatform("autohome");
		try {
			Document doc = Jsoup.parse(detail);
			// log.info(detail);
			Elements reputionPlatformIdeles = doc.select("#content > script:nth-child(3)");
			if (reputionPlatformIdeles != null && reputionPlatformIdeles.size() > 0) {
				String reputionPlatformIdstr = reputionPlatformIdeles.toString();
				if (reputionPlatformIdstr.contains("koubeiId =")) {
					String reputionPlatformId = StringUtils.substringBetween(reputionPlatformIdstr, "koubeiId =", ",")
							.trim();
					reputationInfo.setReputationPlatformId(Long.valueOf(reputionPlatformId));
				}
			}
			// ReputationPlatformId
			reputationInfo.setReputationInfoId(
					2 * CarsSiteIdSupport.SITE_ID_BOUND_REPUTATION + reputationInfo.getReputationPlatformId());

			setCommentCountWithRequest(reputationInfo);
			// System.out.println(reputationInfo.toString());
		} catch (Exception e) {
			log.error(reputationInfo.getReputationUrl() + "解析 content出错：", e);
		}

	}

	public static void setCommentCountWithRequest(ReputationInfo reputationInfo) {
		String id = String.valueOf(reputationInfo.getReputationPlatformId());
		HttpRequestHeader hearder = new HttpRequestHeader();
		String url = "https://reply.autohome.com.cn/ShowReply/ReplyJsonredis.ashx?count=5&page=1&" + "id=" + id
				+ "&datatype=jsonp&appid=5&callback=jsonp1";
		hearder.setUrl(url);
		int commentcountallint = 0;
		String html = AutohomeCommonHttp.getReputationComment(hearder);
		if (html.contains("commentcountall")) {
			String commentcountall = StringUtils.substringBetween(html, "commentcountall\":", ",");
			commentcountallint = Integer.valueOf(commentcountall);
		} else {
			log.info("请求-汽车之家-口碑评论-接口error：" + url);
		}
		reputationInfo.setCommentCount(commentcountallint);
		if (commentcountallint == 0) {
			// 评论没有
		} else {
			// 第一页
			extraCommentJson(html, reputationInfo);
			// 翻页
			controlTurnCommentPage(reputationInfo);
		}
	}

	// 口碑评论 翻页
	public static void controlTurnCommentPage(ReputationInfo reputationInfo) {
		String id = String.valueOf(reputationInfo.getReputationPlatformId());
		for (int i = 1;; i++) {
			HttpRequestHeader hearder = new HttpRequestHeader();
			String url = "https://reply.autohome.com.cn/ShowReply/ReplyJsonredis.ashx?count=5&" + "page="
					+ String.valueOf(i) + "&" + "id=" + id + "&datatype=jsonp&appid=5&callback=jsonp1";
			hearder.setUrl(url);
			String html = AutohomeCommonHttp.getReputationComment(hearder);
			if (html.contains("commentlist")) {
				if (html.contains("replydate")) {
					extraCommentJson(html, reputationInfo);
				} else {
					System.out.println("口碑评论 翻页 最后 一页的 下一页 :" + url);
					break;
				}

			} else {
				log.info("请求-汽车之家-口碑评论-翻页接口error：" + url);
				break;
			}
		}
	}

	// 口碑评论
	public static void extraCommentJson(String json, ReputationInfo reputationInfo) {

		json = StringUtils.substringAfter(json, "jsonp1(");
		json = StringUtils.substringBeforeLast(json, ")");
		// System.out.println("json:" + json);

		JSONObject jodata = JSONObject.parseObject(json);
		JSONArray joarr = jodata.getJSONArray("commentlist");
		// System.out.println("评论列表大小：" + joarr.size());
		for (int i = 0; i < joarr.size(); i++) {
			JSONObject joline = joarr.getJSONObject(i);
			ReputationComment reputationComment = new ReputationComment();
			// 评论 id
			String ReplyId = joline.getString("ReplyId");
			Long reputationCommentId = Long.valueOf(ReplyId);
			reputationComment.setReputationCommentId(
					2 * CarsSiteIdSupport.SITE_ID_BOUND_REPUTATION_COMMENT + reputationCommentId);
			// 口碑ID
			reputationComment.setReputationInfoId(reputationInfo.getReputationInfoId());
			// 口碑评论平台ID
			reputationComment.setCommentPlatformId(reputationCommentId);
			// 口碑评论顺序（楼层）
			String RFloor = joline.getString("RFloor");
			int commentSequence = Integer.valueOf(RFloor);
			reputationComment.setCommentSequence(commentSequence);
			// 口碑评论作者ID
			String RMemberId = joline.getString("RMemberId");
			Long commentAuthorId = Long.valueOf(RMemberId);
			reputationComment.setCommentAuthorId(2 * CarsSiteIdSupport.SITE_ID_BOUND_USER + commentAuthorId);
			// 口碑评论作者昵称
			String commentAuthorName = joline.getString("RMemberName");
			reputationComment.setCommentAuthorName(commentAuthorName);
			// 口碑评论内容
			String commentContent = joline.getString("RContent");
			reputationComment.setCommentContent(commentContent);
			// 口碑来源平台
			String commentSource = joline.getString("SpType");
			reputationComment.setCommentSource(commentSource);
			// 发表日期
			String RReplyDate = joline.getString("RReplyDate");
			String timestamp = StringUtils.substringBetween(RReplyDate, "Date(", "+");
			timestamp = timestamp.substring(0, 10);
			String publishTime = TimeStamp2Date(timestamp);
			reputationComment.setPublishTime(publishTime);
			// 赞人数
			// 踩人数
			reputationComment.setPlatform("autohome");

			FirstCacheHolder.getInstance().submitFirstCache(
					new SqlEntity(reputationComment, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));

			 System.out.println(reputationComment.toString());
			 System.out.println("=========" + i);
		}
	}

	/**
	 * Java将Unix时间戳转换成指定格式日期字符串
	 * 
	 * @param timestampString
	 *            时间戳 如："1473048265";
	 * @param formats
	 *            要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
	 *
	 * @return 返回结果 如："2016-09-05 16:06:42";
	 */
	public static String TimeStamp2Date(String timestampString) {
		String formats = "yyyy-MM-dd HH:mm:ss";
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
		return date;
	}

	public static void main(String[] args) {
//		String file = "E:\\data\\program\\autohome\\autohome_html_test\\01bvnerh4v64vkacsr6wv00000.html";
		String file = "C:\\Users\\EDDC\\Desktop\\autohome-html\\01bvn84c6q64vkacsr6crg0000.html";
		File dir = new File(file);
		String html = "";
		try {
			html = FileUtils.readFileToString(dir, Charset.forName("GBK"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseMobileReputationComment(html);
	}
}
