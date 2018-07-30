package com.edmi.site.cars.autohome.crawl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import com.edmi.site.cars.autohome.config.CarsSiteIdSupport;
import com.edmi.site.cars.autohome.entity.ModelBrand;
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
 * 按照车型抓取口碑
 * 每个车型的口碑可能有多页
 * @author conner
 */
public class AutoHomeMobileReputationCrawl implements Runnable {

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
	
	private int totalPage = 20;
	
	public AutoHomeMobileReputationCrawl(ModelBrand modelBrand) {
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
			HttpRequestHeader header = new HttpRequestHeader();
			for (int page = 1; page <= 5; page ++) {
				header.setUrl("https://k.m.autohome.com.cn/M_Evaluation/AutoSpecKouBeiListControl?SpaceFeeling=&PowerFeeling=&ManeuverabilityFeeling="
						+ "&OilConsumptionFeeling=&SeriesQueryOrder=&boughtCity=&purposeTerms=&GradeEnum=&GradeValue=&cityname=&medalType="
						+ "&serialId=" + (modelBrand.getSeriesBrandId() - 2 * CarsSiteIdSupport.SITE_ID_BOUND_SERIES_BRAND)
						+ "&pageIndex=" + page + "&specStateEnum=&PageCount=&isSeries=false&isSending=true"
						+ "&Id=" + (modelBrand.getModelBrandId() - 2 * CarsSiteIdSupport.SITE_ID_BOUND_MODEL_BRAND) + "&yearId=&SemanticKey=&IsSemantic=false");
				String html = AutohomeCommonHttp.getMobileReputationList(header);
				if (html.contains("暂无口碑，去发表第一篇口碑吧")) {
					break;
				} else {
					Document doc = Jsoup.parse(html);
					Elements eles = doc.select(".comments .comment");
					if (CollectionUtils.isEmpty(eles)) {
//						log.info("请求失败 " + html);
//						continue;
//					} else if (CollectionUtils.isNotEmpty(eles) && eles.size() < 20) {
//						log.info("请求失败 " + eles);
//						continue;
					} else {
						for (Element ele : eles) {
							ReputationList r = new ReputationList();
							r.setReputationPlatformId(0L);
							//SeriesBrandId
							r.setSeriesBrandId(modelBrand.getSeriesBrandId());
							//ModelBrandId
							r.setModelBrandId(modelBrand.getModelBrandId());
							//Platform
							r.setPlatform(CarsSiteIdSupport.SITE_AUTOHOME);
							
							r.setReputationUrl(ele.attr("onclick").replace("javascript:location.href='", "").replace("'", ""));
							r.setReputationUrl(r.getReputationUrl().replace("//k.autohome.com.cn", "http://k.autohome.com.cn"));
							
							Element title = ele.select(".comment-title").first();
							r.setReputationTitle(null != title ? title.text().trim() : "");
							
							Element user = ele.select(".comment-user .comment-user-name").first();
							if (null != user) {
								r.setReputationAuthorId(2 * CarsSiteIdSupport.SITE_ID_BOUND_USER + NumberUtils.toLong(user.attr("id").replace("userAuthLevel_", "")));
								r.setReputationAuthorName(user.text().trim());
							}
							
							Element publishTime = ele.select(".comment-user .comment-user-time").first();
							r.setPublishTime(publishTime.text().replace("发表于", "").trim());
							
							FirstCacheHolder.getInstance().submitFirstCache(new SqlEntity(r, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));
						}
					}
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int getTotalPage () {
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl("https://k.m.autohome.com.cn/spec/28323/");
		String html = AutohomeCommonHttp.getMobileReputationList(header);
		Document doc = Jsoup.parse(html);
		
		Elements eles = doc.select(".comments .comment");
		if (CollectionUtils.isEmpty(eles)) {
			log.info("请求失败 " + html);
			return getTotalPage();
		} else if (CollectionUtils.isNotEmpty(eles) && eles.size() < 10) {
			log.info("请求失败 " + eles);
			return getTotalPage();
		} else {
			log.info("请求成功 " + eles);
			
			Element ele = doc.select("#js-pageNumber").first();
			if (null != ele) {
				
			}
		}
		
		
		return totalPage;
	}
	
	private String crawlByPage(int page, HttpRequestHeader header) {
//		String html = AutohomeCommonHttp.getReputationList(header);
		String html = AutohomeCommonHttp.getReputationListByHttp(header);
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
		for (int i = 0; i < 100; i++) {
			HttpRequestHeader header = new HttpRequestHeader();
			header.setUrl("https://k.m.autohome.com.cn/spec/28323/");
			header.setProxyType(ProxyType.NONE);
			header.setProject(Project.OTHER);
			header.setSite(Site.OTHER);
			
			header.setAccept("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
			header.setAcceptEncoding("gzip, deflate, br");
			header.setAcceptLanguage("zh-CN,zh;q=0.9,en;q=0.8");
			header.setCacheControl("no-cache");
			header.setConnection("keep-alive");
			header.setCookie("sessionip=140.206.67.58; sessionid=AD390436-D4CB-4DBE-ACFE-AF33FE78F864%7C%7C2018-05-25+15%3A02%3A59.072%7C%7C0; area=310199; __ah_uuid=389BB5BB-68CF-418B-8012-2C79F80887FE; fvlid=1527231772240Z5Gxhfrdk7; ahpau=1; UM_distinctid=1639691f1ff30-031ec2db9d2b93-3c3c520d-100200-1639691f200170; sessionuid=AD390436-D4CB-4DBE-ACFE-AF33FE78F864%7C%7C2018-05-25+15%3A02%3A59.072%7C%7C0; __utma=1.1478855483.1527303163.1527303163.1527303163.1; __utmz=1.1527303163.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); ahsids=614_526; searchhistory=%7B%22a%22%3A%7B%22t%22%3A%5B%22%22%5D%2C%22k%22%3A%22%22%7D%2C%22f%22%3A%7B%22t%22%3A%5B%22%22%5D%2C%22k%22%3A%22%22%7D%7D; ASP.NET_SessionId=rj5y2fu3firlafti53y4ru4u; sessionvid=3E57CA4A-CE41-4BB7-ABBD-2F8B70E8E155; historybbsName4=c-2098%7CAC%20Schnitzer%2Cc-145%7CPolo; papopclub=3A9D2AD369B0016BE117B0954BD4B994; pepopclub=6AF5B1912982E1EC2C4CD67E92D6675A; isFromBaiDuSearch=; isFromQQSearch=; pvidchain=2147101,2147101,2147101,2147101,2147101,2147101,2147101,2147101,2147101,2147101; ahpvno=246; ref=www.baidu.com%7C0%7C0%7C0%7C2018-05-26+12%3A35%3A22.595%7C2018-05-26+12%3A29%3A04.736");
			header.setHost("k.m.autohome.com.cn");
			header.setPragma("no-cache");
			header.setUpgradeInsecureRequests("1");
			header.setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
			header.setRequestSleepTime(1000);
			header.setMaxTryTimes(1);
			
			String html = HttpClientSupport.get(header).getContent();
			Document doc = Jsoup.parse(html);
			Elements eles = doc.select(".comments .comment");
			if (CollectionUtils.isEmpty(eles)) {
				log.info("请求失败 " + html);
			} else if (CollectionUtils.isNotEmpty(eles) && eles.size() != 20) {
				log.info("请求失败 " + eles);
			} else {
				log.info("请求成功 " + eles);
			}
		}
	}
}
