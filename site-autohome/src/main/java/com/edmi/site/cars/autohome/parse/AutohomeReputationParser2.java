package com.edmi.site.cars.autohome.parse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.edmi.site.cars.autohome.config.CarsSiteIdSupport;
import com.edmi.site.cars.autohome.config.HtmlDataUtil;
import com.edmi.site.cars.autohome.config.StringHelper;
import com.edmi.site.cars.autohome.entity.ReputationComment;
import com.edmi.site.cars.autohome.entity.ReputationInfo;
import com.edmi.site.cars.autohome.entity.UserInfo;
import com.edmi.site.cars.autohome.http.AutohomeCommonHttp;
import com.google.common.collect.Lists;
import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

public class AutohomeReputationParser2 {

	private static Logger log = LoggerFactory.getLogger(AutohomeReputationParser2.class);

	public static void parsePcReputation(String detail) {
		ReputationInfo reputationInfo = new ReputationInfo();
		reputationInfo.setPlatform("autohome");
		TTFParser ttf = new TTFParser();
		String fontData = HtmlDataUtil.getPathDatas(new File("D:/autohome_font/autohomefontmap.txt"));
		JSONObject fontJson = JSON.parseObject(fontData);
		try {
			String fontUrl = StringHelper.getResultByReg(detail, "url\\('(//k2.autoimg.cn[^\\.]+..ttf)");
			String path = HtmlDataUtil.saveFontData(fontUrl, "d:/autohome_font/font");
			TrueTypeFont font = ttf.parse(path);
			// String detail = WebDriverSupport.load(driver,
			// reputationInfo.getReputationUrl());
			// detail = org.apache.commons.lang.StringEscapeUtils.escapeHtml(detail);
			HtmlDataUtil.saveData("d:/escapeautodata1.txt", detail);
			// List<String> encodes = StringHelper.getResultListByReg(detail,
			// "font-family:myfont;&quot;&gt;(&#[\\d]+;)&lt");
			// List<String> encodes = StringHelper.getResultListByReg(detail,
			// "font-family:myfont'>(&#[\\d]+;)<");

			String regex = "&#x[A-Fa-f0-9]{4};";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(detail);
			List<String> encodes = Lists.newArrayList();
			while (matcher.find()) {
				// 匹配后的结果
				String string = matcher.group(0);
				// 如果编码列表不包含则添加该编码。
				if (!encodes.contains(string)) {
					encodes.add(string.replace("&#x", "").replace(";", ""));
				}
			}

			for (String codeu : encodes) {
				// System.out.println(encodes.get(i));
				// String num = StringHelper.getResultByReg(encodes.get(i), "([\\d]+)");
				// System.out.println(num);
				// String codeu = Integer.toHexString(Integer.parseInt(num));
				// String codeu = num + "";
				System.out.println(font.getPath("uni" + codeu).getCurrentPoint());
				// String data =
				// fontJson.getString(String.valueOf(font.getPath("uni"+codeu).getCurrentPoint().getY()));
				String data = fontJson
						.getString(String.valueOf(font.getPath("uni" + codeu).getCurrentPoint().getY() - 2));
				// String data =
				// fontJson.getString(String.valueOf(font.getPath("uni"+codeu).getCurrentPoint()));
				try {
					JSONObject fontJson1 = JSON.parseObject(data);
					String key = "width=" + font.getPath("uni" + codeu).getBounds().width + ",height="
							+ font.getPath("uni" + codeu).getBounds().height;
					// System.out.println(key);
					data = fontJson1.getString(key);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(data);
				detail = detail.replaceAll(codeu, data);
			}
			detail = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(detail);
			// if (detail.contains("您的网络异常，请根据下面步骤提示进行安全验证")) {
			// driver.findElement(By.className("yidun_intelli-text")).click();
			// }

			Document doc = Jsoup.parse(detail);
			log.info(detail);
			// Elements mouthconList = doc.getElementsByClass("mouthcon");
			Elements mouthconList = doc.select(".mouth");

			if (CollectionUtils.isNotEmpty(mouthconList)) {
				log.info("汽车之家-论坛-口碑 开始解析详细信息 " + reputationInfo.getReputationUrl());
				for (Element ele : mouthconList) {
					if (!ele.tagName().equals("div")) {
						continue;
					}
					UserInfo userInfo = new UserInfo();
					userInfo.setPlatform(CarsSiteIdSupport.SITE_AUTOHOME);

					Element right = ele.select(".mouthcon-cont-right").first();
					Element left = ele.select(".mouthcon-cont-left").first();

					Element user_cont = doc.select(".user-cont").first();
					if (null != user_cont) {
						Element user = user_cont.select("a").first();
						Element user_id = doc.select("#hidAuthorId").first();
						if (null != user_id) {
							userInfo.setUserPlatformId(NumberUtils.isNumber(user_id.attr("value"))
									? NumberUtils.toLong(user_id.attr("value"))
									: 0);
							userInfo.setUserInfoId(
									2 * CarsSiteIdSupport.SITE_ID_BOUND_USER + userInfo.getUserPlatformId());

							reputationInfo.setReputationAuthorId(userInfo.getUserInfoId());
						}
						Element userName = user.select("img").first();
						if (null != userName) {
							userInfo.setUserName(userName.hasAttr("title") ? userName.attr("title").trim() : "");
							reputationInfo.setReputationAuthorName(userInfo.getUserName());
						}
					}

					if (null != left) {

						Elements choose_dl_divs = left.getElementsByClass("choose-dl");
						if (CollectionUtils.isNotEmpty(choose_dl_divs)) {
							for (Element dl : choose_dl_divs) {
								String text = dl.getElementsByTag("dt").toString();
								if (text.contains("购买车型")) {
									/*
									 * Elements as = dl.getElementsByTag("a");
									 * reputationInfo.setBuy_car_series(as.get(0).attr("href").trim().replace("/",
									 * "")); reputationInfo.setBuy_car_series_name(as.get(0).text());
									 * 
									 * reputationInfo.setBuy_car_model(as.get(1).attr("href").trim());
									 * reputationInfo.setBuy_car_model_name(as.get(1).text());
									 */
								} else if (text.contains("购买地点")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setBuyPlace(
											StringEscapeUtils.escapeHtml4(dd.text()).replace("&nbsp;", " ").trim());
								} else if (text.contains("购车经销商")) {
									Element dd = dl.select("dd a").first();
									if (null != dd && dd.hasAttr("data-val")) {
										HttpRequestHeader dealerHeader = new HttpRequestHeader();
										dealerHeader.setUrl(
												"http://k.autohome.com.cn/frontapi/GetDealerInfor?dearerandspecIdlist="
														+ dd.attr("data-val") + "|");
										String dealerInfo = AutohomeCommonHttp.getReputationDealer(dealerHeader);
										if (StringUtils.isNotEmpty(dealerInfo)) {
											JSONObject json = JSONObject.parseObject(dealerInfo);
											if (json.containsKey("result")) {
												JSONObject dealer = json.getJSONObject("result");
												if (dealer.containsKey("List")) {
													JSONArray array = dealer.getJSONArray("List");
													if (CollectionUtils.isNotEmpty(array)) {
														JSONObject vendor = array.getJSONObject(0);
														reputationInfo.setVendor(vendor.getString("CompanySimple"));
													}
												}
											}
										}
									}
								} else if (text.contains("购买时间")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setBuyDate(
											StringEscapeUtils.escapeHtml4(dd.text()).replace("&nbsp;", " ").trim());
								} else if (text.contains("裸车购买价")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setBuyPrice(
											StringEscapeUtils.escapeHtml4(dd.text()).replace("&nbsp;", " ").trim());
								} else if (text.contains("目前行驶")) {
									Element dd = dl.getElementsByTag("dd").first();
									if (null != dd) {
										Elements ps = dd.getElementsByTag("p");
										if (CollectionUtils.isNotEmpty(ps)) {
											for (Element p : ps) {
												if (p.text().contains("千瓦时/百公里") || p.text().contains("升/百公里")) {
													reputationInfo.setFuelEconomy(StringEscapeUtils
															.escapeHtml4(p.text()).replace("&nbsp;", " ").trim());
												} else {
													reputationInfo.setDriveDistance(StringEscapeUtils
															.escapeHtml4(p.text()).replace("&nbsp;", " ").trim());
												}
											}
										} else {
											log.error("汽车之家-论坛-口碑 " + reputationInfo.getReputationUrl()
													+ " 油耗 目前行驶 解析错误");
										}
									}
								} else if (text.contains("空间")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setSpaceValue(NumberUtils.toDouble(dd.text().trim()));
								} else if (text.contains("动力")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setPowerValue(NumberUtils.toDouble(dd.text().trim()));
								} else if (text.contains("操控")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setControlValue(NumberUtils.toDouble(dd.text().trim()));
								} else if ((text.contains("油耗") || text.contains("耗电量")) && !text.contains("目前行驶")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setFuelValue(NumberUtils.toDouble(dd.text().trim()));
								} else if (text.contains("舒适性")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setComfortValue(NumberUtils.toDouble(dd.text().trim()));
								} else if (text.contains("外观")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setExteriorValue(NumberUtils.toDouble(dd.text().trim()));
								} else if (text.contains("内饰")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setInteriorValue(NumberUtils.toDouble(dd.text().trim()));
								} else if (text.contains("性价比")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setCostPerformanceValue(NumberUtils.toDouble(dd.text().trim()));
								} else if (text.contains("购车目的")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setPurpose(dd.text().trim());
								}
							}
						}
					}

					if (null != right) {
						// log.error(right.html());
						// 口碑反爬虫屏蔽了某些字，词 begin
						// String js = "var result = [];"
						// + " var spans = document.querySelectorAll('.text-con
						// span[class^=\"hs_kw\"]');"
						// + " for (var i = 0; i < spans.length; i++) {"
						// + " var style; "
						// + " if (window.hs_fuckyou_dd) {style = window.hs_fuckyou_dd(spans[i],
						// ':before'); console.log('hs_fuckyou_dd');} "
						// + " else if (window.hs_fuckyou) {style = window.hs_fuckyou(spans[i],
						// ':before'); console.log('hs_fuckyou');}"
						// + " result.push({'class' : spans[i].getAttribute('class'), 'content' :
						// style.content.replace(/\"/g, '')});"
						// + " }"
						// + " return result;";
						//
						// List<Map<String, String>> result = (List<Map<String, String>>)
						// ((JavascriptExecutor)driver).executeScript(js);
						//
						// Map<String, String> classContentMap = new HashMap<String, String>();
						// if (CollectionUtils.isNotEmpty(result)) {
						// for (Map<String, String> temp : result) {
						// classContentMap.put(temp.get("class").toString(),
						// temp.get("content").toString());
						// }
						// }
						// 口碑反爬虫屏蔽了某些字，词 begin

						// Elements spans = right.select(".text-con span[class^='hs_kw']");
						// if (CollectionUtils.isNotEmpty(spans)) {
						// for (Element span : spans) {
						// String className = span.hasAttr("class") ? span.attr("class") : "";
						// span.removeClass(className);
						// span.text(classContentMap.get(className));
						// }
						// }

						Elements text_cons = right.select(".text-con");
						if (CollectionUtils.isNotEmpty(text_cons)) {
							for (Element text_con : text_cons) {
								String[] commentPoints = text_con.text().trim().split("【");
								for (String point : commentPoints) {
									point = point.replace("\\r", "").replace("\\n", "");
									if (point.contains("最满意的一点】")) {
										reputationInfo.setSatisfyingPoint((new String(point.getBytes("utf-8"), "utf-8")
												.replace("<br/>", "").replace("最满意的一点】", "")
												+ " "
												+ (StringUtils.isNotEmpty(reputationInfo.getSatisfyingPoint())
														? reputationInfo.getSatisfyingPoint()
														: "")).trim());
									} else if (point.contains("最不满意的一点】")) {
										reputationInfo
												.setUnsatisfyingPoint((new String(point.getBytes("utf-8"), "utf-8")
														.replace("<br/>", "").replace("最不满意的一点】", "")
														+ " "
														+ (StringUtils.isNotEmpty(reputationInfo.getUnsatisfyingPoint())
																? reputationInfo.getUnsatisfyingPoint()
																: "")).trim());
									} else if (point.contains("空间】")) {
										reputationInfo.setSpaceComment((new String(point.getBytes("utf-8"), "utf-8")
												.replace("<br/>", "").replace("空间】", "")
												+ " "
												+ (StringUtils.isNotEmpty(reputationInfo.getSpaceComment())
														? reputationInfo.getSpaceComment()
														: "")).trim());
									} else if (point.contains("动力】")) {
										reputationInfo.setPowerComment((new String(point.getBytes("utf-8"), "utf-8")
												.replace("<br/>", "").replace("动力】", "")
												+ " "
												+ (StringUtils.isNotEmpty(reputationInfo.getPowerComment())
														? reputationInfo.getPowerComment()
														: "")).trim());
									} else if (point.contains("操控】")) {
										reputationInfo.setControlComment((new String(point.getBytes("utf-8"), "utf-8")
												.replace("<br/>", "").replace("操控】", "")
												+ " "
												+ (StringUtils.isNotEmpty(reputationInfo.getControlComment())
														? reputationInfo.getControlComment()
														: "")).trim());
									} else if (point.contains("油耗】")) {
										reputationInfo.setFuelComment((new String(point.getBytes("utf-8"), "utf-8")
												.replace("<br/>", "").replace("油耗】", "")
												+ " "
												+ (StringUtils.isNotEmpty(reputationInfo.getFuelComment())
														? reputationInfo.getFuelComment()
														: "")).trim());
									} else if (point.contains("舒适性】")) {
										reputationInfo.setComfortComment((new String(point.getBytes("utf-8"), "utf-8")
												.replace("<br/>", "").replace("舒适性】", "")
												+ " "
												+ (StringUtils.isNotEmpty(reputationInfo.getComfortComment())
														? reputationInfo.getComfortComment()
														: "")).trim());
									} else if (point.contains("外观】")) {
										reputationInfo.setExteriorComment((new String(point.getBytes("utf-8"), "utf-8")
												.replace("<br/>", "").replace("外观】", "")
												+ " "
												+ (StringUtils.isNotEmpty(reputationInfo.getExteriorComment())
														? reputationInfo.getExteriorComment()
														: "")).trim());
									} else if (point.contains("内饰】")) {
										reputationInfo.setInteriorComment((new String(point.getBytes("utf-8"), "utf-8")
												.replace("<br/>", "").replace("内饰】", "")
												+ " "
												+ (StringUtils.isNotEmpty(reputationInfo.getInteriorComment())
														? reputationInfo.getInteriorComment()
														: "")).trim());
									} else if (point.contains("性价比】")) {
										reputationInfo
												.setCostPerformanceComment((new String(point.getBytes("utf-8"), "utf-8")
														.replace("<br/>", "").replace("性价比】", "")
														+ " "
														+ (StringUtils
																.isNotEmpty(reputationInfo.getCostPerformanceComment())
																		? reputationInfo.getCostPerformanceComment()
																		: "")).trim());
									} else if (point.contains("其它描述】")) {
										reputationInfo.setOthersDesc((new String(point.getBytes("utf-8"), "utf-8")
												.replace("<br/>", "").replace("其它描述】", "")
												+ " "
												+ (StringUtils.isNotEmpty(reputationInfo.getOthersDesc())
														? reputationInfo.getOthersDesc()
														: "")).trim());
									} else if (point.contains("为什么最终选择这款车】")) {
										reputationInfo.setChooseReason((new String(point.getBytes("utf-8"), "utf-8")
												.replace("<br/>", "").replace("为什么最终选择这款车】", "")
												+ " "
												+ (StringUtils.isNotEmpty(reputationInfo.getChooseReason())
														? reputationInfo.getChooseReason()
														: "")).trim());
									}
								}
							}
						}
					}

					Element help = doc.select(".help").first();
					if (null != help) {
						Element view = help.select(".orange").first();
						if (null != view) {
							reputationInfo.setViewCount(
									NumberUtils.isNumber(view.text().trim()) ? NumberUtils.toInt(view.text().trim())
											: 0);
						}
						Element support = help.select(".supportNumber").first();
						if (null != support) {
							reputationInfo.setLikeCount(NumberUtils.isNumber(support.text().trim())
									? NumberUtils.toInt(support.text().trim())
									: 0);
						}
						Element comment = help.select(".CommentNumber").first();
						if (null != comment) {
							reputationInfo.setCommentCount(NumberUtils.isNumber(comment.text().trim())
									? NumberUtils.toInt(comment.text().trim())
									: 0);
						}
					}

					// FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(userInfo,
					// DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));

				}
			}

			FirstCacheHolder.getInstance()
					.submitFirstCache(new SqlEntity(reputationInfo, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));

		} catch (Exception e) {
			log.error(reputationInfo.getReputationUrl() + "解析 content出错：", e);
		} finally {
			// if (null != driver) {
			// driver.close();
			// driver.quit();
			// }
		}
		if (null != reputationInfo.getCommentCount() && reputationInfo.getCommentCount() > 0) {
			// parseComment(reputationInfo);
		}

	}

	public static void parseMobileReputation(String detail) {
		IGeneralJdbcUtils iGeneralJdbcUtils = (IGeneralJdbcUtils) ApplicationContextHolder
				.getBean(GeneralJdbcUtils.class);

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
						Elements eles = ele.select("div.item > span");
						String satisfyingPoint = eles.first().text();
						reputationInfo.setSatisfyingPoint(satisfyingPoint);
					} else if (titlestr.contains("最不满意")) {
						// UnsatisfyingPoint
						Elements eles = ele.select("div.item > span");
						String unsatisfyingPoint = eles.first().text();
						reputationInfo.setUnsatisfyingPoint(unsatisfyingPoint);
					} else if (titlestr.contains("为什么选择这款车")) {
						// OthersDesc
						// Advantage
						// Advantage
						// ChooseReason
						Elements eles = ele.select("div.item > span");
						String chooseReason = eles.first().text();
						reputationInfo.setChooseReason(chooseReason);

					} else if (titlestr.contains("性价比")) {
						// CostPerformanceComment
						// CostPerformanceValue
						Elements eles = ele.select("div.item > span");
						String costPerformanceComment = eles.text();
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
						Elements eles = ele.select("div.item > span");
						String spaceComment = eles.text();
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double spaceValue = NumberUtils.toDouble(starstr);
						reputationInfo.setSpaceComment(spaceComment);
						reputationInfo.setSpaceValue(spaceValue);
					} else if (titlestr.contains("动力")) {
						// PowerComment
						// PowerValue
						Elements eles = ele.select("div.item > span");
						String powerComment = eles.text();
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double powerValue = NumberUtils.toDouble(starstr);
						reputationInfo.setPowerComment(powerComment);
						reputationInfo.setPowerValue(powerValue);
					} else if (titlestr.contains("操控")) {
						// ControlComment
						// ControlValue
						Elements eles = ele.select("div.item > span");
						String controlComment = eles.text();
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double controlValue = NumberUtils.toDouble(starstr);
						reputationInfo.setControlComment(controlComment);
						reputationInfo.setControlValue(controlValue);
					} else if (titlestr.contains("油耗") || titlestr.contains("能耗")) {
						// FuelComment
						// FuelValue
						Elements eles = ele.select("div.item > span");
						String fuelComment = eles.text();
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double fuelValue = NumberUtils.toDouble(starstr);
						reputationInfo.setFuelComment(fuelComment);
						reputationInfo.setFuelValue(fuelValue);
					} else if (titlestr.contains("舒适性")) {
						// ComfortComment
						// ComfortValue
						Elements eles = ele.select("div.item > span");
						String comfortComment = eles.text();
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double comfortValue = NumberUtils.toDouble(starstr);
						reputationInfo.setComfortComment(comfortComment);
						reputationInfo.setComfortValue(comfortValue);
					} else if (titlestr.contains("外观")) {
						// ExteriorComment
						// ExteriorValue
						Elements eles = ele.select("div.item > span");
						String exteriorComment = eles.text();
						Elements stareles = ele.select("h4 > span.unit-star > span.light");
						String starWidth = stareles.attr("style");
						String starstr = getStarValue(starWidth);
						double exteriorValue = NumberUtils.toDouble(starstr);
						reputationInfo.setExteriorComment(exteriorComment);
						reputationInfo.setExteriorValue(exteriorValue);
					} else if (titlestr.contains("内饰")) {
						// InteriorComment
						// InteriorValue
						Elements eles = ele.select("div.item > span");
						String interiorComment = eles.text();
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

			System.out.println(reputationInfo.toString());
			// 存入数据库
			// FirstCacheHolder.getInstance()
			// .submitFirstCache(new SqlEntity(reputationInfo, DataSource.DATASOURCE_SGM,
			// SqlType.PARSE_INSERT));
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
			// extraCommentJson(html, reputationInfo);
			// 翻页
			// controlTurnCommentPage(reputationInfo);
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
		System.out.println("json:" + json);

		JSONObject jodata = JSONObject.parseObject(json);
		JSONArray joarr = jodata.getJSONArray("commentlist");
		System.out.println("评论列表大小：" + joarr.size());
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
			String publishTime = joline.getString("replydate");
			reputationComment.setPublishTime(publishTime);
			// 赞人数
			// 踩人数
			System.out.println(reputationComment.toString());
			System.out.println("=========" + i);
		}
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

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

		File dir = new File("E:\\data\\program\\autohome\\autohome_html_test\\01bvnerh4v64vkacsr6wv00000.html");
		// File dir = new
		// File("E:\\data\\program\\autohome\\autohome_html_test\\01bvy92hqe64vkadhh68r00000.html");

		// File dir = new
		// File("E:\\data\\program\\autohome\\autohome_html_test\\01bw9je8wj64vkccsg6rsg0000.html");

		// if (dir.isDirectory()) {
		// File[] list = dir.listFiles();
		// for (File file : list) {
		String html = "";
		try {
			html = FileUtils.readFileToString(dir, Charset.forName("GBK"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseMobileReputation(html);
		// }
		// }

	}
}
