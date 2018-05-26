package com.edmi.site.autohome.crawl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.edmi.site.autohome.config.CarsSiteIdSupport;
import com.edmi.site.autohome.config.HtmlDataUtil;
import com.edmi.site.autohome.config.StringHelper;
import com.edmi.site.autohome.entity.ModelBrand;
import com.edmi.site.autohome.entity.ReputationComment;
import com.edmi.site.autohome.entity.ReputationInfo;
import com.edmi.site.autohome.entity.ReputationList;
import com.edmi.site.autohome.entity.UserInfo;
import com.edmi.site.autohome.http.AutohomeCommonHttp;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.cache.jdbc.GeneralJdbcUtils;
import fun.jerry.cache.jdbc.IGeneralJdbcUtils;
import fun.jerry.common.ApplicationContextHolder;
import fun.jerry.common.DateFormatSupport;
import fun.jerry.common.LogSupport;
import fun.jerry.common.enumeration.Project;
import fun.jerry.common.enumeration.ProxyType;
import fun.jerry.common.enumeration.Site;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;
import fun.jerry.httpclient.bean.HttpRequestHeader;

/**
 * 按照车型抓取口碑
 * 每个车型的口碑可能有多页
 * @author conner
 */
public class AutoHomeClubReputationCrawl implements Runnable {

	public static Logger log = LogSupport.getAutohomelog();
	
	public static final String Reputation_Pre_Url = "http://k.autohome.com.cn/spec/";
	
	public static final String Reputation_Append_Url = "/ge0/0-0-2/";
	
	private ModelBrand modelBrand;
	
	private WebDriver driver;
	
	private boolean exist = false;
	
	private IGeneralJdbcUtils<?> iGeneralJdbcUtils;
	
	private String ReputationUrl;
	
	private String PublishTime;
	
	private String ReputationAuthorName;
	
	private int totalPage = 1;
	
//	/** 口碑缓存 */
//	private List<Long> reputationInfoCache = new ArrayList<Long>();
//	
//	/** 口碑评论缓存 */
//	private List<Long> reputationCommentCache = new ArrayList<Long>();
	
	public AutoHomeClubReputationCrawl(ModelBrand modelBrand) {
		super();
		this.iGeneralJdbcUtils = (IGeneralJdbcUtils<?>) ApplicationContextHolder.getBean(GeneralJdbcUtils.class);
		this.modelBrand = modelBrand;
		Map<String, Object> map = iGeneralJdbcUtils
				.queryOne(
						new SqlEntity(
								"select top 1 ReputationUrl, convert(varchar(20), PublishTime, 23) as PublishTime, ReputationAuthorName from dbo.F_ReputationInfo_P02 where ModelBrandId = "
										+ modelBrand.getModelBrandId() + " order by PublishTime desc",
								DataSource.DATASOURCE_SGM, SqlType.PARSE_NO));
		if (null != map) {
			ReputationUrl = null != map.get("ReputationUrl") ? map.get("ReputationUrl").toString() : "";
			PublishTime = null != map.get("PublishTime") ? map.get("PublishTime").toString() : "";
			ReputationAuthorName = null != map.get("ReputationAuthorName") ? map.get("ReputationAuthorName").toString() : "";
		}
	}

	@Override
	public void run() {
		try {
			
			List<ReputationList> reputationList = new ArrayList<>();
			
			HttpRequestHeader header = new HttpRequestHeader();
			String url = Reputation_Pre_Url + (modelBrand.getModelBrandId() - 2 * CarsSiteIdSupport.SITE_ID_BOUND_MODEL_BRAND) + Reputation_Append_Url;
			for (int page = 1; page <= totalPage; page ++) {
				if (page > 1) {
					url = Reputation_Pre_Url + (modelBrand.getModelBrandId() - 2 * CarsSiteIdSupport.SITE_ID_BOUND_MODEL_BRAND) 
							+ Reputation_Append_Url + "index_" + page + ".html#dataList";
				}
				header.setUrl(url);
				header.setObj(ReputationInfo.class.getSimpleName());
				header.setProxyType(ProxyType.PROXY_STATIC_DLY);
				header.setProject(Project.OTHER);
				header.setSite(Site.OTHER);
				String html = crawlByPage(page, header);
				
//				if (parseByPage(page, html, reputationList)) {
//					break;
//				}
				
				parseByPage(page, html, reputationList);
				
			}
			
//			for (ReputationInfo reputation : reputationList) {
//				parseDetail(reputation);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String crawlByPage(int page, HttpRequestHeader header) {
//		String html = AutohomeCommonHttp.getReputationList(header);
		String html = AutohomeCommonHttp.getReputationListByHttp(header);
		try {
			if (!html.contains("暂无符合该列表的口碑")) {
				
				if (html.contains("由于您的网络存在安全问题") || html.contains("您的访问出现异常") || html.contains("需要您滑动验证")) {
					log.info(header.getUrl() + " 由于您的网络存在安全问题, 重试");
					return crawlByPage(page, header);
				}
				
				// 应该有口碑的，但是没有找到，重新请求
				Document doc = Jsoup.parse(html);
				Elements eles = doc.select(".row .mouth .mouthcon");
				if (CollectionUtils.isEmpty(eles)) {
					return crawlByPage(page, header);
				}
			}
			if (page == 1) {
				//查找总页数
				Document content = Jsoup.parse(html);
				Elements pages = content.select(".page-item-info");
				if (CollectionUtils.isNotEmpty(pages)) {
					Element ele = pages.first();
					totalPage = NumberUtils.toInt(ele.text().replace("共", "").replace("页", ""));
					
					if (totalPage > 10) {
						totalPage = 10;
					}
					
				}
				log.info(modelBrand.getSeriesBrandId() + " " + modelBrand.getModelBrandId() + " 总页数 " + totalPage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	
	private boolean parseByPage(int page, String html, List<ReputationList> reputationList) {
		boolean exist = false;
		Document doc = Jsoup.parse(html);
		Elements mouthconList = doc.select(".mouthcon");
		if (CollectionUtils.isNotEmpty(mouthconList)) {
			log.info("汽车之家-论坛-口碑 开始解析 " + modelBrand.getSeriesBrandId() + " " + modelBrand.getModelBrandId() + " 当前页码： " + page + ", " + mouthconList.size());
			for (Element ele : mouthconList) {
				ReputationList reputationInfo = new ReputationList();
				reputationInfo.setReputationPlatformId(0L);
				//SeriesBrandId
				reputationInfo.setSeriesBrandId(modelBrand.getSeriesBrandId());
				//ModelBrandId
				reputationInfo.setModelBrandId(modelBrand.getModelBrandId());
				//Platform
				reputationInfo.setPlatform(CarsSiteIdSupport.SITE_AUTOHOME);
				try {
					
					Element user = ele.select(".usercont .name-text p a").first();
					if (null != user) {
						reputationInfo.setReputationAuthorName(user.text().trim());
						String href = user.attr("href").trim();
						Pattern pattern = Pattern.compile("[1-9]+\\d*", Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(href);
						String userId = "";
						if(matcher.find()) {
							userId = matcher.group(0);
							//comment.setPublishTime(DateFormatSupport.dateFormat(DateFormatSupport.YYYY_MM_DD_HH_MM_SS, new Date(Long.parseLong(commentId))));
						}
						reputationInfo.setReputationAuthorId(2 * CarsSiteIdSupport.SITE_ID_BOUND_USER + Long.parseLong(userId));
					}
					
					Elements as = ele.select(".mouthcon-cont-right .mouth-item .title-name a[href*=k.autohome.com.cn/detail]");
					
					Element publishTime = null;
					Element title = null;
					if (CollectionUtils.isNotEmpty(as)) {
						if (as.size() == 1) {
							publishTime = as.first();
						} else if (as.size() == 2) {
							publishTime = as.first();
							title = as.last();
						}
						
						if (null != publishTime) {
							reputationInfo.setPublishTime(publishTime.text().trim());
							reputationInfo.setReputationUrl(publishTime.attr("href"));
							reputationInfo.setReputationUrl(reputationInfo.getReputationUrl().replace("//k.autohome.com.cn", "http://k.autohome.com.cn"));
						}
						if (null != title) {
							reputationInfo.setReputationTitle(title.text());
						}
					}
					
//					if (DateFormatSupport.before(reputationInfo.getPublishTime(), DateFormatSupport.YYYY_MM_DD,
//							DateFormatSupport.dateFormat(DateFormatSupport.YYYY_MM_DD, PublishTime))
//							&& ReputationAuthorName.equals(reputationInfo.getReputationAuthorName())) {
//						log.info("汽车之家-论坛-口碑 " + reputationInfo.getReputationInfoId() + "  已存在, 退出抓取");
//						exist = true;
//						break;
//					}
					
//					ReputationInfo criteria = new ReputationInfo();
//					criteria.setReputationAuthorName(reputationInfo.getReputationAuthorName());
//					criteria.setPublishTime(reputationInfo.getPublishTime());
//					
//					if (iGeneralJdbcUtils.exist()) {
//						log.info("汽车之家-论坛-口碑 " + reputationInfo.getReputationInfoId() + "  已存在, 退出抓取");
//						exist = true;
//						break;
//					}
					
					Element ReputationSource = ele.select(".mouthcon-cont-right .mouth-item .title-name span").first();
					if (null != ReputationSource) {
						reputationInfo.setReputationSource(ReputationSource.text().replace("来自：", "").trim());
					}
					
					reputationList.add(reputationInfo);
					
					FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(reputationInfo, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
					
				} catch (Exception e) {
					log.error("汽车之家-论坛-口碑 " + modelBrand.getSeriesBrandId() + " " + modelBrand.getModelBrandId() + " 解析错误：", e);
				}
			}
		} else {
			log.info("汽车之家-论坛-口碑 " + modelBrand.getSeriesBrandId() + " " + modelBrand.getModelBrandId() + " 没有发现口碑");
		}
		
		return exist;
	}
	
	private void parseDetail(ReputationInfo reputationInfo) {
	    TTFParser ttf = new TTFParser();
	    String fontData = HtmlDataUtil.getPathDatas(new File("D:/autohome_font/autohomefontmap.txt"));
	    JSONObject fontJson =  JSON.parseObject(fontData);
		try {
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl(reputationInfo.getReputationUrl());
//			driver.get(reputationInfo.getReputationUrl());
			String detail = AutohomeCommonHttp.getReputation(header);
//			String detail = WebDriverSupport.load(driver, reputationInfo.getReputationUrl());
			
			detail = org.apache.commons.lang.StringEscapeUtils.escapeHtml(detail);
//			detail = StringEscapeUtils.unescapeHtml4(detail);
			if (detail.contains("您的网络异常，请根据下面步骤提示进行安全验证")) {
				driver.findElement(By.className("yidun_intelli-text")).click();
			}
			
			String fontUrl = StringHelper.getResultByReg(detail, "url\\('(//k2.autoimg.cn[^\\.]+..ttf)");
			String path = HtmlDataUtil.saveFontData(fontUrl,"d:/autohome_font/ttf");
			TrueTypeFont font = ttf.parse(path);
		//	String detail = WebDriverSupport.load(driver, reputationInfo.getReputationUrl());
//			detail = org.apache.commons.lang.StringEscapeUtils.escapeHtml(detail);
//			log.info(detail);
			HtmlDataUtil.saveData("d:/escapeautodata1.txt", detail);
			List<String> encodes = StringHelper.getResultListByReg(detail, "font-family:myfont;&quot;&gt;(&#[\\d]+;)&lt");
//			List<String> encodes = StringHelper.getResultListByReg(detail, "font-family:myfont;\">(&#[\\d]+;)<");
			for (int i = 0; i < encodes.size(); i++) {
//				System.out.println(encodes.get(i));
				String num = StringHelper.getResultByReg(encodes.get(i), "([\\d]+)");
//				System.out.println(num);
				String codeu = Integer.toHexString(Integer.parseInt(num));
				//System.out.println(font.getPath("uni"+codeu).getCurrentPoint());
				String data = fontJson.getString(String.valueOf(font.getPath("uni"+codeu).getCurrentPoint().getY()));
				try {
					JSONObject fontJson1 = JSON.parseObject(data);
					String key = "width="+font.getPath("uni"+codeu).getBounds().width+",height="+font.getPath("uni"+codeu).getBounds().height;
				//	System.out.println(key);
					data = fontJson1.getString(key);
				} catch (Exception e) {
//					e.printStackTrace();
				}
//				System.out.println(data);
				detail = detail.replaceAll(encodes.get(i), data);
			}
			
			detail = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(detail);
			
			Document doc = Jsoup.parse(detail);
//			log.info(detail);
//			Elements mouthconList = doc.getElementsByClass("mouthcon");
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
	                        userInfo.setUserPlatformId(NumberUtils.isNumber(user_id.attr("value")) ? NumberUtils.toLong(user_id.attr("value")) : 0);
                            userInfo.setUserInfoId(2 * CarsSiteIdSupport.SITE_ID_BOUND_USER + userInfo.getUserPlatformId());
                            
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
									Elements as = dl.getElementsByTag("a");
									reputationInfo.setBuy_car_series(as.get(0).attr("href").trim().replace("/", ""));
									reputationInfo.setBuy_car_series_name(as.get(0).text());
									
									reputationInfo.setBuy_car_model(as.get(1).attr("href").trim());
									reputationInfo.setBuy_car_model_name(as.get(1).text());
									*/
								} else if (text.contains("购买地点")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setBuyPlace(StringEscapeUtils.escapeHtml4(dd.text()).replace("&nbsp;", " ").trim());
								} else if (text.contains("购车经销商")) {
									Element dd = dl.select("dd a").first();
									if (null != dd && dd.hasAttr("data-val")) {
										HttpRequestHeader dealerHeader = new HttpRequestHeader();
										dealerHeader.setUrl("http://k.autohome.com.cn/frontapi/GetDealerInfor?dearerandspecIdlist=" + dd.attr("data-val") + "|");
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
									reputationInfo.setBuyDate(StringEscapeUtils.escapeHtml4(dd.text()).replace("&nbsp;", " ").trim());
								} else if (text.contains("裸车购买价")) {
									Element dd = dl.getElementsByTag("dd").first();
									reputationInfo.setBuyPrice(StringEscapeUtils.escapeHtml4(dd.text()).replace("&nbsp;", " ").trim());
								} else if (text.contains("目前行驶")) {
									Element dd = dl.getElementsByTag("dd").first();
									if (null != dd) {
										Elements ps = dd.getElementsByTag("p");
										if (CollectionUtils.isNotEmpty(ps)) {
											for (Element p : ps) {
												if (p.text().contains("千瓦时/百公里") || p.text().contains("升/百公里")) {
													reputationInfo.setFuelEconomy(StringEscapeUtils.escapeHtml4(p.text()).replace("&nbsp;", " ").trim());
												} else {
													reputationInfo.setDriveDistance(StringEscapeUtils.escapeHtml4(p.text()).replace("&nbsp;", " ").trim());
												}
											}
										} else {
											log.error("汽车之家-论坛-口碑 " + reputationInfo.getReputationUrl() + " 油耗 目前行驶 解析错误");
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
//						log.error(right.html());
						// 口碑反爬虫屏蔽了某些字，词 begin
//						String js = "var result = [];"
//							+ "	var spans = document.querySelectorAll('.text-con span[class^=\"hs_kw\"]');"
//							+ "	for (var i = 0; i < spans.length; i++) {"
//							+ "		var style; "
//							+ "		if (window.hs_fuckyou_dd) {style = window.hs_fuckyou_dd(spans[i], ':before'); console.log('hs_fuckyou_dd');} "
//							+ "		else if (window.hs_fuckyou) {style = window.hs_fuckyou(spans[i], ':before');  console.log('hs_fuckyou');}"
//							+ "		result.push({'class' : spans[i].getAttribute('class'), 'content' : style.content.replace(/\"/g, '')});"
//							+ "	}"
//							+ "	return result;";
//						
//						List<Map<String, String>> result = (List<Map<String, String>>) ((JavascriptExecutor)driver).executeScript(js);
//						
//						Map<String, String> classContentMap = new HashMap<String, String>();
//						if (CollectionUtils.isNotEmpty(result)) {
//							for (Map<String, String> temp : result) {
//								classContentMap.put(temp.get("class").toString(), temp.get("content").toString());
//							}
//						}
						// 口碑反爬虫屏蔽了某些字，词 begin
						
//						Elements spans = right.select(".text-con span[class^='hs_kw']");
//						if (CollectionUtils.isNotEmpty(spans)) {
//							for (Element span : spans) {
//								String className = span.hasAttr("class") ? span.attr("class") : "";
//								span.removeClass(className);
//								span.text(classContentMap.get(className));
//							}
//						}
						
						Elements text_cons = right.select(".text-con");
						if (CollectionUtils.isNotEmpty(text_cons)) {
							for (Element text_con : text_cons) {
								String[] commentPoints = text_con.text().trim().split("【");
								for (String point : commentPoints) {
									point = point.replace("\\r", "").replace("\\n", "");
									if (point.contains("最满意的一点】") || point.contains("最满意】")) {
										reputationInfo.setSatisfyingPoint((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "").replace("最满意的一点】", "").replace("最满意】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getSatisfyingPoint()) ? reputationInfo.getSatisfyingPoint() : ""))
												.trim());
									} else if (point.contains("最不满意的一点】") || point.contains("最不满意】")) {
										reputationInfo.setUnsatisfyingPoint((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("最不满意的一点】", "").replace("最不满意】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getUnsatisfyingPoint()) ? reputationInfo.getUnsatisfyingPoint() : ""))
												.trim());
									} else if (point.contains("空间】")) {
										reputationInfo.setSpaceComment((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("空间】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getSpaceComment()) ? reputationInfo.getSpaceComment() : ""))
												.trim());
									} else if (point.contains("动力】")) {
										reputationInfo.setPowerComment((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("动力】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getPowerComment()) ? reputationInfo.getPowerComment() : ""))
												.trim());
									} else if (point.contains("操控】")) {
										reputationInfo.setControlComment((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("操控】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getControlComment()) ? reputationInfo.getControlComment() : ""))
												.trim());
									} else if (point.contains("油耗】")) {
										reputationInfo.setFuelComment((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("油耗】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getFuelComment()) ? reputationInfo.getFuelComment() : ""))
												.trim());
									} else if (point.contains("舒适性】")) {
										reputationInfo.setComfortComment((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("舒适性】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getComfortComment()) ? reputationInfo.getComfortComment() : ""))
												.trim());
									} else if (point.contains("外观】")) {
										reputationInfo.setExteriorComment((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("外观】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getExteriorComment()) ? reputationInfo.getExteriorComment() : ""))
												.trim());
									} else if (point.contains("内饰】")) {
										reputationInfo.setInteriorComment((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("内饰】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getInteriorComment()) ? reputationInfo.getInteriorComment() : ""))
												.trim());
									} else if (point.contains("性价比】")) {
										reputationInfo.setCostPerformanceComment((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("性价比】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getCostPerformanceComment()) ? reputationInfo.getCostPerformanceComment() : ""))
												.trim());
									} else if (point.contains("其它描述】")) {
										reputationInfo.setOthersDesc((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("其它描述】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getOthersDesc()) ? reputationInfo.getOthersDesc() : ""))
												.trim());
									} else if (point.contains("为什么最终选择这款车】") || point.contains("为什么选择这款车】")) {
										reputationInfo.setChooseReason((new String(point.getBytes("utf-8"),"utf-8")
												.replace("<br/>", "")
												.replace("为什么最终选择这款车】", "").replace("为什么选择这款车】", "")
												+ " " + (StringUtils.isNotEmpty(reputationInfo.getChooseReason()) ? reputationInfo.getChooseReason() : ""))
												.trim());
									}
								}
							}
						}
					}
					
					Element help = doc.select(".help").first();
					if (null != help) {
						Element view = help.select(".orange").first();
						if (null != view) {
							reputationInfo.setViewCount(NumberUtils.isNumber(view.text().trim()) ? NumberUtils.toInt(view.text().trim()) : 0);
						}
						Element support = help.select(".supportNumber").first();
						if (null != support) {
							reputationInfo.setLikeCount(NumberUtils.isNumber(support.text().trim()) ? NumberUtils.toInt(support.text().trim()) : 0);
						}
						Element comment = help.select(".CommentNumber").first();
						if (null != comment) {
							reputationInfo.setCommentCount(NumberUtils.isNumber(comment.text().trim()) ? NumberUtils.toInt(comment.text().trim()) : 0);
						}
					}
						
//					FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(userInfo, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
					
				}
			}
			log.info(reputationInfo);
//			FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(reputationInfo, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
			
		} catch (Exception e) {
//			log.error(reputationInfo.getReputationUrl() + "解析 content出错：", e);
		} finally {
			if (null != driver) {
				driver.close();
				driver.quit();
			}
		}
		if (null != reputationInfo.getCommentCount() && reputationInfo.getCommentCount() > 0) {
//			parseComment(reputationInfo);
		}
	}
	
	private void parseComment(ReputationInfo reputationInfo) {
		int page = 1;
		HttpRequestHeader header = new HttpRequestHeader();
		header.setObj(ReputationComment.class.getSimpleName());
		while (true) {
			String url = "http://reply.autohome.com.cn/ShowReply/ReplyJsonredis.ashx?count=undefined&page=" + page
					+ "&id=" + reputationInfo.getReputationPlatformId() + "&datatype=jsonp&appid=5&_="
					+ new Date().getTime();
			header.setUrl(url);
			log.info("汽车之家-论坛-口碑 " + reputationInfo.getReputationPlatformId() + " 开始抓取口碑跟帖 page = " + page);
			String html = AutohomeCommonHttp.getReputationComment(header).replace("(", "").replace(")", "");
			if (StringUtils.isNotEmpty(html)) {
				try {
					String escapeHtml = StringEscapeUtils.unescapeJava(html);
					JSONObject commentlist = JSONObject.parseObject(escapeHtml);
					if (commentlist.containsKey("commentlist")) {
						JSONArray array = commentlist.getJSONArray("commentlist");
						if (CollectionUtils.isNotEmpty(array)) {
							for (Object o : array) {
								JSONObject temp = JSONObject.parseObject(o.toString());
								ReputationComment comment = new ReputationComment();
								comment.setPlatform(CarsSiteIdSupport.SITE_AUTOHOME);
								comment.setCommentPlatformId(temp.getLong("ReplyId"));
								comment.setReputationCommentId(2 * CarsSiteIdSupport.SITE_ID_BOUND_REPUTATION_COMMENT + comment.getCommentPlatformId());
								comment.setReputationInfoId(reputationInfo.getReputationInfoId());
								comment.setCommentContent(temp.getString("RContent").trim());
								comment.setCommentSource(temp.getString("SpType"));
								comment.setCommentSequence(temp.getInteger("RFloor"));
								
								Pattern pattern = Pattern.compile("[1-9]+\\d*", Pattern.CASE_INSENSITIVE);
								Matcher matcher = pattern.matcher(temp.getString("RReplyDate"));
								String commentId = "";
								if(matcher.find()) {
									commentId = matcher.group(0);
									comment.setPublishTime(DateFormatSupport.dateFormat(DateFormatSupport.YYYY_MM_DD_HH_MM_SS, new Date(Long.parseLong(commentId))));
								}
								
								UserInfo user = new UserInfo();
								user.setPlatform(CarsSiteIdSupport.SITE_AUTOHOME);
								user.setUserPlatformId(temp.getLong("RMemberId"));
								user.setUserInfoId(2 * CarsSiteIdSupport.SITE_ID_BOUND_USER + temp.getLong("RMemberId"));
								user.setUserName(temp.getString("RMemberName"));
								
								comment.setCommentAuthorId(user.getUserInfoId());
								comment.setCommentAuthorName(user.getUserName());
									
								FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(user, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
								
								FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(comment, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
								
							}
						} else {
							break;
						}
					} else {
						break;
					}
				
				} catch (Exception e) {
					log.error(url + " 口碑评论解析错误", e);
				}
			} else {
				break;
			}
			page ++;
		}
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}
	/**
	 * 将指定信息保存到本地
	 * @param dir
	 * @param content
	 */
	public static void saveData(String dir, String content) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(dir);
			outputStream.write(content.getBytes());
			outputStream.flush();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
			}
		}

	}
	public static void main(String[] args) {
		String abc = "318967";
		try {
			System.out.println(new String(abc.getBytes("utf-8"),"utf-8").replace("<br/>", ""));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
