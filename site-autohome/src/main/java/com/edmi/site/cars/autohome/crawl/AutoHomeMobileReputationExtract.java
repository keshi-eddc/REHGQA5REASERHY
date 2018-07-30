package com.edmi.site.cars.autohome.crawl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edmi.site.cars.autohome.config.CarsSiteIdSupport;
import com.edmi.site.cars.autohome.entity.ReputationInfo;
import com.edmi.site.cars.autohome.http.AutohomeCommonHttp;
import com.edmi.site.cars.autohome.parse.AutohomeReputationParser2;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.LogSupport;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 解析汽车之家口碑文件
 */
public class AutoHomeMobileReputationExtract implements Runnable {

	public static Logger log = LogSupport.getAutohomelog();
	// 是否插入数据库
	static Boolean isInsert = true;
	// static Boolean isInsert = false;
	private String filepath;
	private static IGeneralJdbcUtils<?> iGeneralJdbcUtils;
	public static final AtomicInteger count = new AtomicInteger(0);

	public AutoHomeMobileReputationExtract(String filepath) {
		super();
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils<?>) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		this.filepath = filepath;
	}

	@Override
	public void run() {
		log.info(Thread.currentThread().getName() + " run :" + filepath);
		log.info(">>读了：<<" + count.getAndIncrement());
		String html = "";
		File file = new File(filepath);
		try {
			html = FileUtils.readFileToString(file, Charset.forName("GBK"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseMobileReputation(html);
	}

	public void parseMobileReputation(String detail) {

		ReputationInfo reputationInfo = new ReputationInfo();
		reputationInfo.setPlatform("autohome");
		try {
			Document doc = Jsoup.parse(detail);
			// log.info(detail);
			// 口碑 URL
			Elements reputionUrleles = doc.select(".report");
			if (reputionUrleles != null && reputionUrleles.size() > 0) {
				String reputionUrlstr = reputionUrleles.toString();
				if (reputionUrlstr.contains("url=") && reputionUrlstr.contains(".html")) {
					String reputionUrl = StringUtils.substringBetween(reputionUrlstr, "url=", ".html") + ".html";
					reputationInfo.setReputationUrl(reputionUrl);
				}
			}
			log.info("汽车之家-论坛-口碑 开始解析详细信息 " + reputationInfo.getReputationUrl());

			// showId
			String showId = "";
			Elements reputionPlatformIdeles = doc.select("#content > script:nth-child(3)");
			if (reputionPlatformIdeles != null && reputionPlatformIdeles.size() > 0) {
				String reputionPlatformIdstr = reputionPlatformIdeles.toString();
				if (reputionPlatformIdstr.contains("koubeiId =")) {
					String reputionPlatformId = StringUtils.substringBetween(reputionPlatformIdstr, "koubeiId =", ",")
							.trim();
					reputationInfo.setReputationPlatformId(Long.valueOf(reputionPlatformId));
					showId = StringUtils.substringBetween(reputionPlatformIdstr, "showId =", ";").trim();
					showId = showId.replaceAll("\"", "");
				}
			}
			// ReputationPlatformId
			reputationInfo.setReputationInfoId(
					2 * CarsSiteIdSupport.SITE_ID_BOUND_REPUTATION + reputationInfo.getReputationPlatformId());
			// ReputationTitle
			Elements ReputationTitleeles = doc.select("#content > div:nth-child(2) > section > header > h1");
			if (ReputationTitleeles != null && ReputationTitleeles.size() > 0) {
				String reputationTitle = ReputationTitleeles.text().trim();
				reputationInfo.setReputationTitle(reputationTitle);
			}

			// 从表查F_ReputationList_P02
			// System.out.println("showId:" + showId);
			String sql = "select * from dbo.F_ReputationList_P02 where ReputationUrl like '%" + showId + "%'";
			Map<String, Object> reputationListmap = iGeneralJdbcUtils
					.queryOne(new SqlEntity(sql, DataSource.DATASOURCE_SGM, SqlType.PARSE_NO));

			// SeriesBrandId
			Object seriesBrandId = reputationListmap.get("SeriesBrandId");
			String seriesBrandIdstr = String.valueOf(seriesBrandId);
			// ModelBrandId TODO
			Object modelBrandId = reputationListmap.get("ModelBrandId");
			String modelBrandIdstr = String.valueOf(modelBrandId);
			reputationInfo.setSeriesBrandId(Integer.valueOf(seriesBrandIdstr));
			reputationInfo.setModelBrandId(Long.valueOf(modelBrandIdstr));

			// ReputationCategory
			String reputationCategory = "";
			// ReputationCategory
			String ReputationType = "";

			// BuyDate
			Elements buyDateles = doc.select("section.cartype > span.date > span");
			if (buyDateles != null && buyDateles.size() > 0) {
				String buyDate = buyDateles.text().trim();
				reputationInfo.setBuyDate(buyDate);
			}
			// BuyPlace
			// Vendor
			Elements buyPlaceeles = doc.select("section > section.cartype > span.site");
			if (buyPlaceeles != null && buyPlaceeles.size() > 0) {
				String buyPlace = "";
				String vendor = "";
				String[] buyPlacetemp = buyPlaceeles.text().trim().split(" ");
				StringBuilder strbu = new StringBuilder();
				for (int i = 0; i < buyPlacetemp.length - 1; i++) {
					String k = buyPlacetemp[i];
					if (k.contains("经销商：")) {
						k = StringUtils.substringAfter(k, "经销商：");
					}
					strbu = strbu.append(k + " ");
				}
				buyPlace = strbu.toString().trim();
				vendor = buyPlacetemp[buyPlacetemp.length - 1];
				reputationInfo.setBuyPlace(buyPlace);
				reputationInfo.setVendor(vendor);
			}
			// BuyPrice
			// FuelEconomy
			// DriveDistance
			Elements fuelEconomyeles = doc.select(" section > section.edition-base > div.unit-gather > div");
			if (fuelEconomyeles != null && fuelEconomyeles.size() > 0) {
				for (Element ele : fuelEconomyeles) {
					String k = ele.text();
					if (k.contains("价")) {
						String buyPrice = StringUtils.substringAfter(k, "价").trim();
						reputationInfo.setBuyPrice(buyPrice);
					} else if (k.contains("油耗")) {
						String fuelEconomy = StringUtils.substringAfter(k, "油耗").trim();
						reputationInfo.setFuelEconomy(fuelEconomy);
					} else if (k.contains("行驶")) {
						String driveDistance = StringUtils.substringAfter(k, "行驶").trim();
						reputationInfo.setDriveDistance(driveDistance);
					}
				}
			}
			// Purpose 没有

			//
			Elements mattereles = doc.select("section > section.edition-base > div.matter > div");
			if (mattereles != null && mattereles.size() > 0) {
				for (Element ele : mattereles) {

					// System.out.println(ele.toString());
					// System.out.println("-------------------------");
					Elements titleles = ele.select("h4");
					String titlestr = titleles.text();
					// System.out.println(titlestr);
					if (titlestr.contains("最满意")) {
						// SatisfyingPoint
						String satisfyingPoint = ele.text();
						if (satisfyingPoint.contains("【最满意】")) {
							satisfyingPoint = satisfyingPoint.replace("【最满意】", "");
						}
						reputationInfo.setSatisfyingPoint(satisfyingPoint);
					} else if (titlestr.contains("最不满意")) {
						// UnsatisfyingPoint
						String unsatisfyingPoint = ele.text();
						if (unsatisfyingPoint.contains("【最不满意】")) {
							unsatisfyingPoint = unsatisfyingPoint.replace("【最不满意】", "");
						}
						reputationInfo.setUnsatisfyingPoint(unsatisfyingPoint);
					} else if (titlestr.contains("为什么选择这款车")) {
						// OthersDesc
						// Advantage
						// Advantage
						// ChooseReason
						String chooseReason = ele.text();
						if (chooseReason.contains("【为什么选择这款车】")) {
							chooseReason = chooseReason.replace("【为什么选择这款车】", "");
						}
						reputationInfo.setChooseReason(chooseReason);

					} else if (titlestr.contains("性价比")) {
						// CostPerformanceComment
						// CostPerformanceValue
						String costPerformanceComment = ele.text();
						if (costPerformanceComment.contains("【性价比】")) {
							costPerformanceComment = costPerformanceComment.replace("【性价比】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double costPerformanceValue = NumberUtils.toDouble(starstr);
						reputationInfo.setCostPerformanceComment(costPerformanceComment.trim());
						reputationInfo.setCostPerformanceValue(costPerformanceValue);
					} else if (titlestr.contains("配置")) {
						// 没有TODO
					} else if (titlestr.contains("空间")) {
						// SpaceComment
						// SpaceValue
						String spaceComment = ele.text().replace("【空间】", "");
						if (spaceComment.contains("【空间】")) {
							spaceComment = spaceComment.replace("【空间】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double spaceValue = NumberUtils.toDouble(starstr);
						reputationInfo.setSpaceComment(spaceComment);
						reputationInfo.setSpaceValue(spaceValue);
					} else if (titlestr.contains("动力")) {
						// PowerComment
						// PowerValue
						String powerComment = ele.text();
						if (powerComment.contains("【动力】")) {
							powerComment = powerComment.replace("【动力】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double powerValue = NumberUtils.toDouble(starstr);
						reputationInfo.setPowerComment(powerComment);
						reputationInfo.setPowerValue(powerValue);
					} else if (titlestr.contains("操控")) {
						// ControlComment
						// ControlValue
						String controlComment = ele.text();
						if (controlComment.contains("【操控】")) {
							controlComment = controlComment.replace("【操控】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double controlValue = NumberUtils.toDouble(starstr);
						reputationInfo.setControlComment(controlComment);
						reputationInfo.setControlValue(controlValue);
					} else if (titlestr.contains("油耗") || titlestr.contains("能耗")) {
						// FuelComment
						// FuelValue
						String fuelComment = ele.text();
						if (fuelComment.contains("【油耗】")) {
							fuelComment = fuelComment.replace("【油耗】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double fuelValue = NumberUtils.toDouble(starstr);
						reputationInfo.setFuelComment(fuelComment);
						reputationInfo.setFuelValue(fuelValue);
					} else if (titlestr.contains("舒适性")) {
						// ComfortComment
						// ComfortValue
						String comfortComment = ele.text();
						if (comfortComment.contains("【舒适性】")) {
							comfortComment = comfortComment.replace("【舒适性】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double comfortValue = NumberUtils.toDouble(starstr);
						reputationInfo.setComfortComment(comfortComment);
						reputationInfo.setComfortValue(comfortValue);
					} else if (titlestr.contains("外观")) {
						// ExteriorComment
						// ExteriorValue
						String exteriorComment = ele.text();
						if (exteriorComment.contains("【外观】")) {
							exteriorComment = exteriorComment.replace("【外观】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double exteriorValue = NumberUtils.toDouble(starstr);
						reputationInfo.setExteriorComment(exteriorComment);
						reputationInfo.setExteriorValue(exteriorValue);
					} else if (titlestr.contains("内饰")) {
						// InteriorComment
						// InteriorValue
						String interiorComment = ele.text();
						if (interiorComment.contains("【内饰】")) {
							interiorComment = interiorComment.replace("【内饰】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double interiorValue = NumberUtils.toDouble(starstr);
						reputationInfo.setInteriorComment(interiorComment);
						reputationInfo.setInteriorValue(interiorValue);
					} else if (titlestr.contains("养护")) {
						// 养护TODO
					} else if (titlestr.contains("服务")) {
						// 服务TODO
					} else if (titlestr.contains("保险")) {
						// 保险TODO
					} else if (titlestr.contains("改装")) {
						// 改装TODO
					} else if (titlestr.contains("配件")) {
						// 配件TODO
					} else if (titlestr.contains("综合")) {
						// 综合TODO
					}
				}
				// ReputationAuthorId
				// ReputationAuthorName
				// PublishTimes
				Elements reputationAuthorIdeles = doc.select("section.wom-details> header > div.info");
				if (reputationAuthorIdeles != null && reputationAuthorIdeles.size() > 0) {
					String reputationAuthorName = reputationAuthorIdeles.select("a.name").text().trim();
					String reputationAuthorId = reputationAuthorIdeles.select("a.name > span").attr("id");
					if (reputationAuthorId.contains("_")) {
						reputationAuthorId = StringUtils.substringAfterLast(reputationAuthorId, "_");
					}
					String publishTimes = reputationAuthorIdeles.select("time").text();
					if (publishTimes.contains("发表于")) {
						publishTimes = StringUtils.substringAfter(publishTimes, "发表于");
					}
					reputationInfo.setReputationAuthorName(reputationAuthorName);
					reputationInfo.setReputationAuthorId(
							2 * CarsSiteIdSupport.SITE_ID_BOUND_REPUTATION + NumberUtils.toLong(reputationAuthorId));
					reputationInfo.setPublishTime(publishTimes);
				}
				// ViewCount
				// CommentCount
				setCommentCountWithRequest(reputationInfo);
				// LikeCount
				Elements likecounteles = doc.select(" footer > div > div > span.js_userfulnumber");
				if (likecounteles != null && likecounteles.size() > 0) {
					String likeCount = likecounteles.text().trim();
					reputationInfo.setLikeCount(Integer.valueOf(likeCount));
				}
				// UnlikeCount
			}
			// 打印对象
			log.info(reputationInfo.toString());
			// 存入数据库
			if (isInsert) {
				FirstCacheHolder.getInstance().submitFirstCache(
						new SqlEntity(reputationInfo, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
			}
		} catch (Exception e) {
			log.error(reputationInfo.getReputationUrl() + "解析 content出错：", e);
		}

	}

	public void setCommentCountWithRequest(ReputationInfo reputationInfo) {
		String id = String.valueOf(reputationInfo.getReputationPlatformId());
		HttpRequestHeader hearder = new HttpRequestHeader();
		String url = "https://reply.autohome.com.cn/ShowReply/ReplyJsonredis.ashx?count=5&page=1&" + "id=" + id
				+ "&datatype=jsonp&appid=5&callback=jsonp1";
		hearder.setUrl(url);
		int commentcountallint = 0;
		String html = AutohomeCommonHttp.getReputationComment(hearder);
		if (StringUtils.isNotBlank(html)) {
			if (html.contains("commentcountall")) {
				String commentcountall = StringUtils.substringBetween(html, "commentcountall\":", ",");
				commentcountallint = Integer.valueOf(commentcountall);
			} else {
				log.info("请求-汽车之家-口碑评论-接口error：" + url);
			}
			reputationInfo.setCommentCount(commentcountallint);
		} else {
			log.error("setCommentCountWithRequest erro :" + url);
		}

		// if (commentcountallint == 0) {
		// // 评论没有
		// } else {
		// // 第一页
		// // extraCommentJson(html, reputationInfo);
		// // 翻页
		// // controlTurnCommentPage(reputationInfo);
		// }
	}

	public static String getStarValue(String starWidth) {
		double starValue = 0;
		if (starWidth.contains("width:") && starWidth.contains("%")) {
			String starstr = StringUtils.substringBetween(starWidth, "width:", "%");
			double width = Double.valueOf(starstr);
			starValue = width / 20;
		} else {
			return null;
		}
		return String.valueOf(starValue);
	}

	public static void parseMobileReputationUpdate(String detail) {

		ReputationInfo reputationInfo = new ReputationInfo();
		reputationInfo.setPlatform("autohome");
		try {
			Document doc = Jsoup.parse(detail);
			// log.info(detail);
			// 口碑 URL
			Elements reputionUrleles = doc.select(".report");
			if (reputionUrleles != null && reputionUrleles.size() > 0) {
				String reputionUrlstr = reputionUrleles.toString();
				if (reputionUrlstr.contains("url=") && reputionUrlstr.contains(".html")) {
					String reputionUrl = StringUtils.substringBetween(reputionUrlstr, "url=", ".html") + ".html";
					reputationInfo.setReputationUrl(reputionUrl);
				}
			}
			log.info("汽车之家-论坛-口碑 开始解析详细信息 " + reputationInfo.getReputationUrl());

			// showId
			String showId = "";
			Elements reputionPlatformIdeles = doc.select("#content > script:nth-child(3)");
			if (reputionPlatformIdeles != null && reputionPlatformIdeles.size() > 0) {
				String reputionPlatformIdstr = reputionPlatformIdeles.toString();
				if (reputionPlatformIdstr.contains("koubeiId =")) {
					String reputionPlatformId = StringUtils.substringBetween(reputionPlatformIdstr, "koubeiId =", ",")
							.trim();
					reputationInfo.setReputationPlatformId(Long.valueOf(reputionPlatformId));
					showId = StringUtils.substringBetween(reputionPlatformIdstr, "showId =", ";").trim();
					showId = showId.replaceAll("\"", "");
				}
			}
			// ReputationPlatformId
			reputationInfo.setReputationInfoId(
					2 * CarsSiteIdSupport.SITE_ID_BOUND_REPUTATION + reputationInfo.getReputationPlatformId());
			// ReputationTitle
			Elements ReputationTitleeles = doc.select("#content > div:nth-child(2) > section > header > h1");
			if (ReputationTitleeles != null && ReputationTitleeles.size() > 0) {
				String reputationTitle = ReputationTitleeles.text().trim();
				reputationInfo.setReputationTitle(reputationTitle);
			}

			// 从表查F_ReputationList_P02
			// System.out.println("showId:" + showId);
			// String sql = "select * from dbo.F_ReputationList_P02 where ReputationUrl like
			// '%" + showId + "%'";
			// Map<String, Object> reputationListmap = iGeneralJdbcUtils
			// .queryOne(new SqlEntity(sql, DataSource.DATASOURCE_SGM, SqlType.PARSE_NO));
			//
			// // SeriesBrandId
			// Object seriesBrandId = reputationListmap.get("SeriesBrandId");
			// String seriesBrandIdstr = String.valueOf(seriesBrandId);
			// // ModelBrandId TODO
			// Object modelBrandId = reputationListmap.get("ModelBrandId");
			// String modelBrandIdstr = String.valueOf(modelBrandId);
			// reputationInfo.setSeriesBrandId(Integer.valueOf(seriesBrandIdstr));
			// reputationInfo.setModelBrandId(Long.valueOf(modelBrandIdstr));

			// ReputationCategory
			String reputationCategory = "";
			// ReputationCategory
			String ReputationType = "";

			// BuyDate
			Elements buyDateles = doc.select("section.cartype > span.date > span");
			if (buyDateles != null && buyDateles.size() > 0) {
				String buyDate = buyDateles.text().trim();
				reputationInfo.setBuyDate(buyDate);
			}
			// BuyPlace
			// Vendor
			Elements buyPlaceeles = doc.select("section > section.cartype > span.site");
			if (buyPlaceeles != null && buyPlaceeles.size() > 0) {
				String buyPlace = "";
				String vendor = "";
				String[] buyPlacetemp = buyPlaceeles.text().trim().split(" ");
				StringBuilder strbu = new StringBuilder();
				for (int i = 0; i < buyPlacetemp.length - 1; i++) {
					String k = buyPlacetemp[i];
					if (k.contains("经销商：")) {
						k = StringUtils.substringAfter(k, "经销商：");
					}
					strbu = strbu.append(k + " ");
				}
				buyPlace = strbu.toString().trim();
				vendor = buyPlacetemp[buyPlacetemp.length - 1];
				reputationInfo.setBuyPlace(buyPlace);
				reputationInfo.setVendor(vendor);
			}
			// BuyPrice
			// FuelEconomy
			// DriveDistance
			Elements fuelEconomyeles = doc.select(" section > section.edition-base > div.unit-gather > div");
			if (fuelEconomyeles != null && fuelEconomyeles.size() > 0) {
				for (Element ele : fuelEconomyeles) {
					String k = ele.text();
					if (k.contains("价")) {
						String buyPrice = StringUtils.substringAfter(k, "价").trim();
						reputationInfo.setBuyPrice(buyPrice);
					} else if (k.contains("油耗")) {
						String fuelEconomy = StringUtils.substringAfter(k, "油耗").trim();
						reputationInfo.setFuelEconomy(fuelEconomy);
					} else if (k.contains("行驶")) {
						String driveDistance = StringUtils.substringAfter(k, "行驶").trim();
						reputationInfo.setDriveDistance(driveDistance);
					}
				}
			}
			// Purpose 没有

			//
			Elements mattereles = doc.select("section > section.edition-base > div.matter > div");
			if (mattereles != null && mattereles.size() > 0) {
				for (Element ele : mattereles) {
					// System.out.println(ele.toString());
					// System.out.println("-------------------------");
					Elements titleles = ele.select("h4");
					String titlestr = titleles.text();
					// System.out.println(titlestr);
					if (titlestr.contains("最满意")) {
						// SatisfyingPoint
						String satisfyingPoint = ele.text();
						if (satisfyingPoint.contains("【最满意】")) {
							satisfyingPoint = satisfyingPoint.replace("【最满意】", "");
						}
						reputationInfo.setSatisfyingPoint(satisfyingPoint);
					} else if (titlestr.contains("最不满意")) {
						// UnsatisfyingPoint
						String unsatisfyingPoint = ele.text();
						if (unsatisfyingPoint.contains("【最不满意】")) {
							unsatisfyingPoint = unsatisfyingPoint.replace("【最不满意】", "");
						}
						reputationInfo.setUnsatisfyingPoint(unsatisfyingPoint);
					} else if (titlestr.contains("为什么选择这款车")) {
						// OthersDesc
						// Advantage
						// Advantage
						// ChooseReason
						String chooseReason = ele.text();
						if (chooseReason.contains("【为什么选择这款车】")) {
							chooseReason = chooseReason.replace("【为什么选择这款车】", "");
						}
						reputationInfo.setChooseReason(chooseReason);

					} else if (titlestr.contains("性价比")) {
						// CostPerformanceComment
						// CostPerformanceValue
						String costPerformanceComment = ele.text();
						if (costPerformanceComment.contains("【性价比】")) {
							costPerformanceComment = costPerformanceComment.replace("【性价比】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double costPerformanceValue = NumberUtils.toDouble(starstr);
						reputationInfo.setCostPerformanceComment(costPerformanceComment.trim());
						reputationInfo.setCostPerformanceValue(costPerformanceValue);
					} else if (titlestr.contains("配置")) {
						// 没有TODO
					} else if (titlestr.contains("空间")) {
						// SpaceComment
						// SpaceValue
						String spaceComment = ele.text().replace("【空间】", "");
						if (spaceComment.contains("【空间】")) {
							spaceComment = spaceComment.replace("【空间】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double spaceValue = NumberUtils.toDouble(starstr);
						reputationInfo.setSpaceComment(spaceComment);
						reputationInfo.setSpaceValue(spaceValue);
					} else if (titlestr.contains("动力")) {
						// PowerComment
						// PowerValue
						String powerComment = ele.text();
						if (powerComment.contains("【动力】")) {
							powerComment = powerComment.replace("【动力】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double powerValue = NumberUtils.toDouble(starstr);
						reputationInfo.setPowerComment(powerComment);
						reputationInfo.setPowerValue(powerValue);
					} else if (titlestr.contains("操控")) {
						// ControlComment
						// ControlValue
						String controlComment = ele.text();
						if (controlComment.contains("【操控】")) {
							controlComment = controlComment.replace("【操控】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double controlValue = NumberUtils.toDouble(starstr);
						reputationInfo.setControlComment(controlComment);
						reputationInfo.setControlValue(controlValue);
					} else if (titlestr.contains("油耗") || titlestr.contains("能耗")) {
						// FuelComment
						// FuelValue
						String fuelComment = ele.text();
						if (fuelComment.contains("【油耗】")) {
							fuelComment = fuelComment.replace("【油耗】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double fuelValue = NumberUtils.toDouble(starstr);
						reputationInfo.setFuelComment(fuelComment);
						reputationInfo.setFuelValue(fuelValue);
					} else if (titlestr.contains("舒适性")) {
						// ComfortComment
						// ComfortValue
						String comfortComment = ele.text();
						if (comfortComment.contains("【舒适性】")) {
							comfortComment = comfortComment.replace("【舒适性】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double comfortValue = NumberUtils.toDouble(starstr);
						reputationInfo.setComfortComment(comfortComment);
						reputationInfo.setComfortValue(comfortValue);
					} else if (titlestr.contains("外观")) {
						// ExteriorComment
						// ExteriorValue
						String exteriorComment = ele.text();
						if (exteriorComment.contains("【外观】")) {
							exteriorComment = exteriorComment.replace("【外观】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double exteriorValue = NumberUtils.toDouble(starstr);
						reputationInfo.setExteriorComment(exteriorComment);
						reputationInfo.setExteriorValue(exteriorValue);
					} else if (titlestr.contains("内饰")) {
						// InteriorComment
						// InteriorValue
						String interiorComment = ele.text();
						if (interiorComment.contains("【内饰】")) {
							interiorComment = interiorComment.replace("【内饰】", "");
						}
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double interiorValue = NumberUtils.toDouble(starstr);
						reputationInfo.setInteriorComment(interiorComment);
						reputationInfo.setInteriorValue(interiorValue);
					} else if (titlestr.contains("养护")) {
						// 养护TODO
					} else if (titlestr.contains("服务")) {
						// 服务TODO
					} else if (titlestr.contains("保险")) {
						// 保险TODO
					} else if (titlestr.contains("改装")) {
						// 改装TODO
					} else if (titlestr.contains("配件")) {
						// 配件TODO
					} else if (titlestr.contains("综合")) {
						// 综合TODO
					}

				}
				// ReputationAuthorId
				// ReputationAuthorName
				// PublishTimes
				Elements reputationAuthorIdeles = doc.select("section.wom-details> header > div.info");
				if (reputationAuthorIdeles != null && reputationAuthorIdeles.size() > 0) {
					String reputationAuthorName = reputationAuthorIdeles.select("a.name").text().trim();
					String reputationAuthorId = reputationAuthorIdeles.select("a.name > span").attr("id");
					if (reputationAuthorId.contains("_")) {
						reputationAuthorId = StringUtils.substringAfterLast(reputationAuthorId, "_");
					}
					String publishTimes = reputationAuthorIdeles.select("time").text();
					if (publishTimes.contains("发表于")) {
						publishTimes = StringUtils.substringAfter(publishTimes, "发表于");
					}
					reputationInfo.setReputationAuthorName(reputationAuthorName);
					reputationInfo.setReputationAuthorId(
							2 * CarsSiteIdSupport.SITE_ID_BOUND_REPUTATION + NumberUtils.toLong(reputationAuthorId));
					reputationInfo.setPublishTime(publishTimes);
				}
				// ViewCount
				// CommentCount
				// LikeCount
				Elements likecounteles = doc.select(" footer > div > div > span.js_userfulnumber");
				if (likecounteles != null && likecounteles.size() > 0) {
					String likeCount = likecounteles.text().trim();
					reputationInfo.setLikeCount(Integer.valueOf(likeCount));
				}
				// UnlikeCount
			}

			System.out.println(reputationInfo.toString());
			// 存入数据库
			if (isInsert) {
				FirstCacheHolder.getInstance().submitFirstCache(
						new SqlEntity(reputationInfo, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
			}
		} catch (Exception e) {
			log.error(reputationInfo.getReputationUrl() + "解析 content出错：", e);
		}

	}

	public static void main(String[] args) {
		File file = new File("E:\\data\\ProjectData\\autohome\\rout\\01bw85k0en64vkcchp64w00000.html");
		String html = "";
		try {
			html = FileUtils.readFileToString(file, Charset.forName("GBK"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// parseMobileReputation(html);

	}
}
