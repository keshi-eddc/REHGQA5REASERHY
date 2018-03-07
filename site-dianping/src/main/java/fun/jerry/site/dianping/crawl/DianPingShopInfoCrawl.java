package fun.jerry.site.dianping.crawl;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import fun.jerry.common.LogSupport;
import fun.jerry.httpclient.bean.HttpRequestHeader;

public class DianPingShopInfoCrawl implements Runnable {
	
	private static Logger log = LogSupport.getDianpinglog();
	
	/**
	 * 分页请求的url中有aid这个参数
	 */
	private String aid = "";

	@Override
	public void run() {
		crawl();
	}
	
	private void crawl() {
		HttpRequestHeader header = new HttpRequestHeader();
		String url = "";
		header.setUrl("");
		String html = "";
		Document doc = Jsoup.parse(html);
		int totalPage = getTotalPage(doc);
		if (totalPage > 1) {
			parseShopList(doc, 1);
		} else {
			for (int page = 2; page <= totalPage; page ++) {
				header.setUrl("" + page + "?" + aid);
				html = "";
				parseShopList(doc, page);
			}
		}
	}
	
	private void parseShopList(Document doc, int page) {
		
	}
	
	private int getTotalPage(Document doc) {
		int totalPage = 50;
		Element page = doc.select(".page .PageLink").last();
		if (null != page) {
			totalPage = Integer.valueOf(page.text().trim());
			String href = page.attr("href").trim();
			aid = href.substring(href.indexOf("aid="));
		}
		return totalPage;
	}
	
	public static void main(String[] args) {
		String temp = "http://www.dianping.com/search/category/219/10/p9?aid=95946315%2C91920452%2C1993825%2C93663916%2C93914711%2C68067766";
		System.out.println(temp.substring(temp.indexOf("aid=")));
	}
	
}
