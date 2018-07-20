package com.edmi.site.dianping.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;
import com.edmi.site.dianping.entity.DianpingShopComment;
import com.edmi.site.dianping.entity.DianpingShopInfo;
import com.edmi.site.dianping.entity.DianpingShopRecommendInfo;
import com.edmi.site.dianping.entity.DianpingUserInfo;
import com.edmi.site.dianping.http.DianPingCommonRequest;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

public class DianpingParser {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	public static int parseShopListPage (Document doc) {
		int totalPage = 0;
		Element shopTag = doc.select("#shop-all-list").first();
		if (null != shopTag) {
			Elements shopElements = doc.select("#shop-all-list ul li");
			// 如果发现有店铺列表，找出有多少页
			if (CollectionUtils.isNotEmpty(shopElements)) {
				Element totalPageEle = doc.select(".page .PageLink").last();
				totalPage = null == totalPageEle ? 1 : Integer.parseInt(totalPageEle.text().trim());
			}
		}
		return totalPage;
	}
	
	public static List<DianpingShopInfo> parseShopList(Document doc, int page) {
		List<DianpingShopInfo> list = new ArrayList<>();
		Elements shopElements = doc.select("#shop-all-list ul li");
		if (CollectionUtils.isNotEmpty(shopElements)) {
			for (Element shop : shopElements) {
				DianpingShopInfo shopInfo = new DianpingShopInfo();
				Element tit = shop.select(".tit").first();
				if (null != tit) {
					Element title = tit.select("a[data-hippo-type*=shop]").first();
					if (null != title) {
						shopInfo.setShopName(title.attr("title").trim());
						shopInfo.setShopUrl(title.attr("href").trim());
						shopInfo.setShopId(shopInfo.getShopUrl().substring(shopInfo.getShopUrl().lastIndexOf("/") + 1));
					}
					// 是否包含团购
					Element tuan = tit.select("a.igroup").first();
					shopInfo.setTuanSupport(null != tuan ? 1 : 0);
					// 是否包含预定
					Element book = tit.select("a.ibook").first();
					shopInfo.setBookSupport(null != book ? 1 : 0);
					// 是否包含外卖
					Element out = tit.select("a.iout").first();
					shopInfo.setOutSupport(null != out ? 1 : 0);
					// 是否包含促销
					Element promotion = tit.select("a.ipromote").first();
					shopInfo.setPromotionSupport(null != promotion ? 1 : 0);
					// 是否包含分店
					Element branch = tit.select("a.shop-branch").first();
					if (null != branch) {
						shopInfo.setHasBranch(1);
						shopInfo.setBrandUrl(branch.attr("href"));
					} else {
						shopInfo.setHasBranch(0);
						shopInfo.setBrandUrl("");
					}
					
				}
				Element comment = shop.select(".comment").first();
				if (null != comment) {
					// 星级
					Element level = comment.select("span.sml-rank-stars").first();
					shopInfo.setStarLevel(null != level ? level.attr("title") : "");
					// 评论数
					Element reviewNum = comment.select("a.review-num").first();
					shopInfo.setReviewNum(null != reviewNum ? (null != reviewNum.select("b").first()
							? Integer.parseInt(reviewNum.select("b").first().text()) : 0) : 0);
					// 人均
					Element avgPrice = comment.select("a.mean-price").first();
					shopInfo.setAvgPrice(avgPrice.text().replace("人均", "").replace("￥", "").trim());
				}
				
				// 品类
				Element category = shop.select(".tag-addr a[data-click-name*=shop_tag_cate_click]").first();
				shopInfo.setSelfCategory(null != category ? category.text().trim() : "");
				shopInfo.setSelfCategoryId(null != category ? category.attr("href").substring(category.attr("href").lastIndexOf("/") + 1) : "");
				
				// 区域
				Element region = shop.select(".tag-addr a[data-click-name*=shop_tag_region_click]").first();
				shopInfo.setSelfSubRegion(null != region ? region.text().trim() : "");
				shopInfo.setSelfSubRegionId(null != region ? region.attr("href").substring(region.attr("href").lastIndexOf("/") + 1) : "");
				
				// 地址
				Element address = shop.select(".tag-addr .addr").first();
				shopInfo.setAddress(null != address ? address.text().trim() : "");
				// 评分
				Elements scores = shop.select(".comment-list span");
				if (CollectionUtils.isNotEmpty(scores)) {
					for (Element score : scores) {
						String text = score.text();
						String scoreText = score.select("b").first().text();
						if (text.contains("口味")) {
							shopInfo.setTasteScore(scoreText);
						} else if (text.contains("环境")) {
							shopInfo.setEnvironmentScore(scoreText);
						} else if (text.contains("服务")) {
							shopInfo.setServiceScore(scoreText);
						}
					}
				}
				list.add(shopInfo);
			}
		}
		return list;
	}
	
	/**
	 * 解析店铺推荐菜
	 * @param doc
	 * @param shop
	 * @param page
	 * @return
	 */
	public static List<DianpingShopRecommendInfo> parseShopRecommend(Document doc, DianpingShopInfo shop, int page) {
		List<DianpingShopRecommendInfo> list = new ArrayList<>();
		Elements eles = doc.select(".list-desc ul a");
		for (Element ele : eles) {
			DianpingShopRecommendInfo recommend = new DianpingShopRecommendInfo();
			recommend.setShopId(shop.getShopId());
			recommend.setPage(page);
			
			try {
				String dishUrl = ele.attr("href");
				recommend.setDishUrl(dishUrl);
				recommend.setDishId(dishUrl.substring(dishUrl.lastIndexOf("/") + 1));
				
				Element img = ele.select(".shop-food-img img").first();
				recommend.setDishImageUrl(null != img ? (img.hasAttr("src") ? img.attr("src") : "") : "");
				
				Element dishEle = ele.select(".shop-food-name").first();
				recommend.setDish(null != dishEle ? dishEle.text().trim() : "");
				
				Element recommendCountEle = ele.select(".recommend-count").first();
				recommend.setRecommendCount(
						null != recommendCountEle ? Integer.parseInt(recommendCountEle.text().replace("人推荐", "")) : 0);
				
				StringBuilder recommendTag = new StringBuilder();
				Elements recommendTagEles = ele.select(".recommend-reson .recommend-reson-item");
				if (CollectionUtils.isNotEmpty(recommendTagEles)) {
					for (Element tag : recommendTagEles) {
						String tagText = tag.text().trim();
						if (StringUtils.isNotEmpty(tagText)) {
							recommendTag.append(tag.text().trim()).append("|");
						}
					}
					recommend.setRecommendTag(recommendTag.toString());
				} else {
					recommend.setRecommendTag("");
				}
				
				Element priceEle = ele.select(".shop-food-money").first();
				recommend.setPrice(null != priceEle ? priceEle.text().replace("￥", "").trim() : "");
			} catch (Exception e) {
				log.error("dianping shop recommend parse error", e);
			}
			
			list.add(recommend);
		}
		return list;
	}
	
	/**
	 * 解析店铺推荐菜的页数
	 * @param doc
	 * @return
	 */
	public static int parseShopRecommendTotalPage(Document doc) {
		int totalPage = -1;
		try {
			if (null != doc.select(".list-desc")) {
				// 发现有推荐菜列表的，看是否包含推荐菜
				if (CollectionUtils.isNotEmpty(doc.select(".list-desc ul a"))) {
					Element pageEle = doc.select(".shop-food-list-page .next").last();
					Element totalPageEle = null != pageEle ? pageEle.previousElementSibling() : null;
					totalPage = null != totalPageEle ? Integer.parseInt(totalPageEle.text().trim()) : 1;
				} else {
					totalPage = 0;
				}
			} else {
				// 未发现评论列表的，没有评论，总页数为0
				totalPage = 0;
			}
		} catch (Exception e) {
			log.error("dianping shop recommend parse total page error", e);
		}
		return totalPage;
	}
	
	public static DianpingUserInfo parseUserInfo_PC(Document doc, DianpingShopComment comment) {
		DianpingUserInfo user = new DianpingUserInfo();
		user.setUserId(comment.getUserId());
		try {
			
			Element nameEle = doc.select(".name").first();
			user.setUserName(null != nameEle ? nameEle.text().trim() : "");
			
			Element vipEle = doc.select(".vip .icon-vip").first();
			user.setIsVip(null != vipEle ? 1 : 0);
			
			Element userLevelEle = doc.select(".user-info .user-rank-rst").first();
			user.setUserLevel(null != userLevelEle ? userLevelEle.attr("class") : "");
			
			Element cityEle = doc.select(".user-groun").first();
			user.setCity(null != cityEle ? cityEle.text().trim(): "");
			
			Element sexEle = doc.select(".user-groun i").first();
			user.setSex(null != sexEle ? sexEle.attr("class") : "");
			
			Elements group1 = doc.select(".container .aside .user_atten ul li");
			for (Element ele : group1) {
				String text = ele.text().trim();
				String strongText = null != ele.select("strong").first() ? ele.select("strong").first().text() : "0";
				int num = Integer.parseInt(strongText);
				if (text.contains("关注")) {
					user.setFocusNum(num);
				} else if (text.contains("粉丝")) {
					user.setFansNum(num);
				} else if (text.contains("互动")) {
					user.setInteractionNum(num);
				}
			}
			
			Elements group2 = doc.select(".container .aside .user-time p");
			for (Element ele : group2) {
				String text = ele.text().trim();
				if (text.contains("贡献值")) {
					user.setContribution(text.replace("贡献值：", "").replace("\"", ""));
				} else if (text.contains("社区等级")) {
					user.setCommunityLevel(text.replace("社区等级：", "").replace("\"", ""));
				} else if (text.contains("注册时间")) {
					user.setRegistTime(text.replace("注册时间：", "").replace("\"", ""));
				}
			}
	
			Elements group3 = doc.select(".container .aside .user-message ul li");
			for (Element ele : group3) {
				String text = ele.text().trim();
				if (text.contains("恋爱状况")) {
					user.setLoveSituation(text.replace("恋爱状况：", "").replace("\"", ""));
				} else if (text.contains("生日")) {
					user.setBirthday(text.replace("生日：", "").replace("\"", ""));
				} else if (text.contains("星座")) {
					user.setStar(text.replace("星座：", "").replace("\"", ""));
				}
			}
			
			
		} catch (Exception e) {
			log.error("dianping user info parse error", e);
		}
		return user;
	}
	
	public static DianpingUserInfo parseUserInfo_Mobile(JSONObject jsonObj, DianpingShopComment comment) {
		DianpingUserInfo user = new DianpingUserInfo();
		user.setUserId(comment.getUserId());
		try {
			if (jsonObj.containsKey("data")) {
				JSONObject data = jsonObj.getJSONObject("data");
				user.setUserName(StringUtils.isNotEmpty(data.getString("nickName")) ? data.getString("nickName") : "");
				user.setIsVip(null != data.getBoolean("isVip") && data.getBoolean("isVip") ? 1 : 0);
				user.setUserLevel(StringUtils.isNotEmpty(data.getString("level")) ? data.getString("level") : "");
				user.setSex(StringUtils.isNotEmpty(data.getString("gender")) ? data.getString("gender") : "");
				user.setCity(StringUtils.isNotEmpty(data.getString("city")) ? data.getString("city") : "");
				user.setFocusNum(StringUtils.isNotEmpty(data.getString("followCount")) ? Integer.parseInt(data.getString("followCount")) : 0);
				user.setFansNum(StringUtils.isNotEmpty(data.getString("fansCount")) ? Integer.parseInt(data.getString("fansCount")) : 0);
//				user.setInteractionNum(StringUtils.isNotEmpty(data.getString("fansCount")) ? Integer.parseInt(data.getString("fansCount")) : 0);
				user.setStar(StringUtils.isNotEmpty(data.getString("constellation")) ? data.getString("constellation") : "");
			}
		} catch (Exception e) {
			log.error("dianping user info parse error", e);
		}
		return user;
	}
	
	public static void main(String[] args) {
	}
}
