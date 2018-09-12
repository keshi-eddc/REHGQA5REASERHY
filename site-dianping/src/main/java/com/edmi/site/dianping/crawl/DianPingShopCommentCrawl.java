package com.edmi.site.dianping.crawl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.DateFormatSupport;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 店铺-评论抓取
 * @author conner
 *
 */
public class DianPingShopCommentCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	private DianpingShopInfo dianpingShopInfo;
	
	private IGeneralJdbcUtils iGeneralJdbcUtils;
	
	private long maxCommentTime = 0L; 
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat sdf_ym = new SimpleDateFormat("yyyyMM");
	
	private long stopTime = 0L;

	public DianPingShopCommentCrawl(DianpingShopInfo dianpingShopInfo) {
		super();
		this.dianpingShopInfo = dianpingShopInfo;
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		Map<String, Object> map = iGeneralJdbcUtils
				.queryOne(new SqlEntity(
						"select max(comment_time) as commentTime from dbo.Dianping_Shop_Comment where shop_id = '"
								+ dianpingShopInfo.getShopId() + "'",
						DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO));
		if (null != map && map.containsKey("commentTime") && null != map.get("commentTime")) {
			try {
				maxCommentTime = sdf.parse(map.get("commentTime").toString()).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Map<String, Object> map_version = iGeneralJdbcUtils
				.queryOne(new SqlEntity(
						"select min(version) as version from dbo.Dianping_ShopInfo_Cargill where shop_id = '"
								+ dianpingShopInfo.getShopId() + "'",
						DataSource.DATASOURCE_DianPing, SqlType.PARSE_NO));
		if (null != map_version && map_version.containsKey("version") && null != map_version.get("version")) {
			try {
				stopTime = sdf_ym.parse(map_version.get("version").toString()).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		log.info("开始抓取店铺评论 ： " + dianpingShopInfo.getShopId());
		int totalPage = getTotalPage();
		List<Integer> pageList = new ArrayList<>();
		for (int page = 1; page <= totalPage; page ++) {
			pageList.add(page);
		}
		for (int page = 2; page <= totalPage; page ++) {
			if (getCommentByPage(page, totalPage)) {
				break;
			}
		}
		log.info("店铺评论抓取结束 ： " + dianpingShopInfo.getShopId());
	}
	
	private boolean parseComment(Document doc, int page, int totalPage) {
		boolean flag = false;
		Elements commentList = doc.select(".reviews-items ul li .main-review");
			
		log.info(dianpingShopInfo.getShopId() + " 总页数 " + totalPage + " 当前页数 " + page + " 该页有 " + commentList.size() + " 条记录");
		
		for (Element shop : commentList) {
			try {

				DianpingShopComment comment = new DianpingShopComment();
				comment.setShopId(dianpingShopInfo.getShopId());
				
				comment.setPage(page);
				
				Element userIdEle = shop.select("a[href*=member]").first();
				comment.setUserId(null != userIdEle ? userIdEle.attr("href").replace("/member/", "") : "");
				comment.setUserName(null != userIdEle ? userIdEle.text() : "");
				
				Element userLevelEle = shop.select(".dper-info .user-rank-rst").first();
				comment.setUserLevel(null != userLevelEle ? userLevelEle.classNames().toString() : "");
				
				Element isVipEle = shop.select(".dper-info .vip").first();
				comment.setIsVip(null != isVipEle ? 1 : 0);
				
				Element commentStar = shop.select(".review-rank .star").first();
				comment.setCommentStar(null != commentStar ? commentStar.classNames().toString() : "");
				
				Elements scores = shop.select(".review-rank .score .item");
				comment.setTasteComment("");
				comment.setEnvironmentComment("");
				comment.setServiceComment("");
				if (CollectionUtils.isNotEmpty(scores)) {
					
					for (Element score : scores) {
						String text = score.text().trim();
						if (text.contains("口味")) {
							comment.setTasteComment(text.replace("口味：", "").trim());
						} else if (text.contains("环境")) {
							comment.setEnvironmentComment(text.replace("环境：", "").trim());
						} else if (text.contains("服务")) {
							comment.setServiceComment(text.replace("服务：", "").trim());
						} else if (text.contains("人均")) {
							comment.setAvgPrice(text.replace("人均：", "").trim());
						}
					}
				}
				
				Element commentEle = shop.select(".review-words").first();
				comment.setComment(null != commentEle ? commentEle.html().replace("&nbsp;", ",").trim() : "");
				
				Elements recommentDishEles = shop.select(".review-recommend a");
				if (CollectionUtils.isNotEmpty(recommentDishEles)) {
					StringBuilder dish = new StringBuilder();
					for (Element dishEle : recommentDishEles) {
						dish.append(dishEle.text()).append(" ");
					}
					comment.setRecommendDish(dish.toString());
				} else {
					comment.setRecommendDish("");
				}
				
				Element commentTimeEle = shop.select(".misc-info .time").first();
				String commentTime = null != commentTimeEle ? commentTimeEle.text().trim() : "";
				if (commentTime.contains("更新于")) {
					comment.setFirstCommentTime(commentTime.substring(0, 10).trim());
					comment.setCommentTime(commentTime.substring(commentTime.indexOf("更新于") + 3).trim());
				} else {
					comment.setCommentTime(commentTime);
				}
				
				Element commentIdEle = shop.select(".actions a[data-id]").first();
				comment.setCommentId(null != commentIdEle ? commentIdEle.attr("data-id") : "");
				
				if (DateFormatSupport.before(comment.getCommentTime(), DateFormatSupport.YYYY_MM_DD, new java.util.Date(stopTime))
						|| maxCommentTime >= sdf.parse(comment.getCommentTime()).getTime()) {
					flag = true;
					log.info(dianpingShopInfo.getShopId() + " 页数 " + page + " 评论时间早于指定截止时间，中断抓取 " + comment.getCommentTime());
					break;
				}
				
				Element praiseEle = shop.select(".actions .praise").first().nextElementSibling();
				comment.setFavoriteNum(praiseEle.hasClass("col-exp")
						? NumberUtils.toInt(praiseEle.text().replace("(", "").replace(")", ""), 0) : 0);
				
				Element replyEle = shop.select(".actions .reply").first().nextElementSibling();
				comment.setReplyNum(replyEle.hasClass("col-exp")
						? NumberUtils.toInt(replyEle.text().replace("(", "").replace(")", ""), 0) : 0);
				
				Element collectionEle = shop.select(".actions .favor").first().nextElementSibling();
				comment.setCollectNum(collectionEle.hasClass("col-exp")
						? NumberUtils.toInt(collectionEle.text().replace("(", "").replace(")", ""), 0) : 0);
				
				FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(comment, DataSource.DATASOURCE_DianPing, SqlType.PARSE_INSERT));
			
				log.info(dianpingShopInfo.getShopId() + " " + totalPage + " 当前页数 " + page + " 向缓存中添加一条记录");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	private boolean getCommentByPage(int page, int totalPage) {
		boolean flag = false;
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId() 
				+ "/review_all/p" + page + "?queryType=sortType&queryVal=latest");
		if (page == 1) {
			header.setReferer("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId() 
				+ "/review_all?queryType=sortType&queryVal=latest");
		} else {
			header.setReferer("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId() 
				+ "/review_all/p" + (page - 1));// + "?queryType=sortType&queryVal=latest");
		}
		String html = DianPingCommonRequest.getShopComment(header);
		Document doc = Jsoup.parse(html);
		Elements commentList = doc.select(".reviews-items ul li .main-review");
		if (CollectionUtils.isNotEmpty(commentList)) {
			flag = parseComment(doc, page, totalPage);
		} else {
			log.info("总页数 " + totalPage + " 当前页数 " + page + " 该页应该有数据，但是没有发现, 重新请求");
			getCommentByPage(page, totalPage);
		}
		
		return flag;
	}
	
	private int getTotalPage () {
		int totalPage = -1;
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId() +"/review_all?queryType=sortType&&queryVal=latest");
		header.setReferer("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId() + "/review_all");
//		header.setReferer("http://www.dianping.com/shop/" + dianpingShopInfo.getShopId());
		header.setMaxTryTimes(1);
		String html = DianPingCommonRequest.getShopComment(header);
		Document doc = Jsoup.parse(html);
//		log.info(html);
		if (null != doc.select(".no-review-item").first() || html.contains("该商户暂不展示评价")) {
			totalPage = 0;
		} else if (html.contains("抱歉！页面无法访问") || html.contains("很抱歉，您要访问的页面不存在")) {
			log.info(header.getUrl() + " 应该有评论，但是返回页面无法访问或页面不存在，重新请求");
			log.info(html);
			// 未发现评论列表的，没有评论，总页数为0
			totalPage = getTotalPage();
		} else if (CollectionUtils.isNotEmpty(doc.select(".reviews-items"))) {
			// 发现有评论列表的，看是否包含评论
			if (CollectionUtils.isNotEmpty(doc.select(".reviews-items ul li"))) {
				Element pageEle = doc.select(".reviews-pages .PageLink").last();
				totalPage = null != pageEle ? Integer.parseInt(pageEle.text().trim()) : 1;
//				getCommentByPage(1, totalPage);
				parseComment(doc, 1, totalPage);
			} else {
				log.info(header.getUrl() + " 应该有评论，但是没有找到，重新请求");
				log.info(html);
				// 未发现评论列表的，没有评论，总页数为0
				totalPage = getTotalPage();
			}
		} else {
			log.info(header.getUrl() + " 应该有页数，但是没有找到，重新请求");
			log.info(html);
			// 未发现评论列表的，没有评论，总页数为0
			totalPage = getTotalPage();
		}
		return totalPage;
	}
}
