package com.edmi.site.dianping.crawl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.edmi.site.dianping.entity.DianpingCity;

import fun.jerry.common.LogSupport;
import fun.jerry.httpclient.bean.HttpRequestHeader;

@Component
public class CityCrawl {
	
	private static Logger log = LogSupport.getJdlog();
	
	public void crawl () {
		String url = "http://www.dianping.com/citylist";
		HttpRequestHeader header = new HttpRequestHeader();
		header.setUrl(url);
		
		String html = "";
		Document doc = Jsoup.parse(html);
		
		Elements lis = doc.select(".main-citylist ul li");
		if (CollectionUtils.isNotEmpty(lis)) {
			for (Element sec : lis) {
				Element region_e = sec.select(".vocabulary").first();
				String region_name = null != region_e ? region_e.text().trim() : "";
				// 直辖市和港澳台解析
				if (region_name.contains("直辖市")) {
					Elements as = sec.select("a");
					for (Element a : as) {
						DianpingCity city = new DianpingCity();
						city.setCityCnName(a.text().trim());
						city.setCityUrl(a.attr("href"));
						city.setProvinceName(a.text().trim());
						city.setRegion(region_name);
						city.setCountry("china");
					}
				} else if (region_name.contains("港澳台")) {
					Elements as = sec.select("a");
					for (Element a : as) {
						DianpingCity city = new DianpingCity();
						city.setCityCnName(a.text().trim());
						city.setCityUrl(a.attr("href"));
						city.setRegion(region_name);
						city.setCountry("china");
					}
				}
			}
		}
	}
}
