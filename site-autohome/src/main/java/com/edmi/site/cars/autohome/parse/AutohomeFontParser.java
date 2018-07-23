package com.edmi.site.cars.autohome.parse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.edmi.site.cars.autohome.config.HtmlDataUtil;
import com.edmi.site.cars.autohome.config.StringHelper;
import com.edmi.site.cars.autohome.entity.AutohomeFont;
import com.google.common.collect.Lists;

import fun.jerry.cache.holder.FirstCacheHolder;
import fun.jerry.entity.system.DataSource;
import fun.jerry.entity.system.SqlEntity;
import fun.jerry.entity.system.SqlType;

public class AutohomeFontParser {

	private static Logger log = LoggerFactory.getLogger(AutohomeFontParser.class);

	public static void parseMobileFont(String detail, String htmlFileName) {
		TTFParser ttf = new TTFParser();
		// String fontData = HtmlDataUtil.getPathDatas(new
		// File("D:/autohome_font/autohomefontmap.txt"));
		// JSONObject fontJson = JSON.parseObject(fontData);
		int tatal = 0;
		int countOneFile = 0;
		try {
			String fontUrl = StringHelper.getResultByReg(detail, "url\\('(//k[1-9]{1}.autoimg.cn[^\\.]+..ttf)");
			String path = HtmlDataUtil.saveFontData(fontUrl, "E:\\data\\program\\autohome\\autohome_font\\font_test");
			TrueTypeFont font = ttf.parse(path);
			// log.info(font.getTableMap().values());

			// String detail = WebDriverSupport.load(driver,
			// reputationInfo.getReputationUrl());
			// detail = org.apache.commons.lang.StringEscapeUtils.escapeHtml(detail);
			HtmlDataUtil.saveData("E:/escapeautodata1.txt", detail);
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
					encodes.add(string.replace("&#x", "").replace(";", "").toUpperCase());
				}
			}
			Scanner sc = new Scanner(System.in);
			HashSet<String> codeset = new HashSet<>();
			System.out.println("encodes.size:" + encodes.size());

			for (String codeu : encodes) {
				AutohomeFont af = new AutohomeFont();
				tatal++;
				// System.out.println(encodes.get(i));
				// String num = StringHelper.getResultByReg(encodes.get(i), "([\\d]+)");
				// System.out.println(num);
				// String codeu = Integer.toHexString(Integer.parseInt(num));
				// String codeu = num + "";
				System.out.println(font.getPath("uni" + codeu).getCurrentPoint());
				// String data =
				// fontJson.getString(String.valueOf(font.getPath("uni"+codeu).getCurrentPoint().getY()));
				// String data =
				// fontJson.getString(String.valueOf(font.getPath("uni"+codeu).getCurrentPoint().getY()));
				// String data =
				// fontJson.getString(String.valueOf(font.getPath("uni"+codeu).getCurrentPoint()));
				try {
					String x = String.valueOf(font.getPath("uni" + codeu).getCurrentPoint().getX());
					String y = String.valueOf(font.getPath("uni" + codeu).getCurrentPoint().getY());
					String width = String.valueOf(font.getPath("uni" + codeu).getBounds().width);
					String height = String.valueOf(font.getPath("uni" + codeu).getBounds().height);
					String autohomeChar = "";
					if (codeset.contains(codeu)) {
						System.out.println(codeu + "已经存在");
						continue;
					}
					codeset.add(codeu);
					countOneFile++;
					System.out.println("codeset:" + codeset.size());
					System.out.println("=====第:" + countOneFile + " ,请输入>>> " + codeu + "<<<对应的汉字：");
					autohomeChar = sc.nextLine();
					System.out.println("autohomeChar:" + autohomeChar);

					af.setHexValue(codeu);
					af.setXValue(x);
					af.setYValue(y);
					af.setWidth(width);
					af.setHeight(height);
					af.setFontName(fontUrl);
					af.setAutohomeChar(autohomeChar);
					af.setAutohomeHtml(htmlFileName);

					FirstCacheHolder.getInstance()
							.submitFirstCache(new SqlEntity(af, DataSource.DATASOURCE_SGM, SqlType.PARSE_INSERT));

					// JSONObject fontJson1 = JSON.parseObject(data);
					// String key =
					// "width="+font.getPath("uni"+codeu).getBounds().width+",height="+font.getPath("uni"+codeu).getBounds().height;
					// System.out.println(key);
					// data = fontJson1.getString(key);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// System.out.println(data);
				// detail = detail.replaceAll(codeu, data);
			}
			System.out.println("tatal:" + tatal);
			System.out.println("countOneFile:" + countOneFile);
			// detail = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(detail);
			// if (detail.contains("您的网络异常，请根据下面步骤提示进行安全验证")) {
			// driver.findElement(By.className("yidun_intelli-text")).click();
			// }

		} catch (Exception e) {
		} finally {
		}
	}

	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

		File dir = new File("E:\\data\\program\\autohome\\autohome_html_test");
		if (dir.isDirectory()) {
			File[] list = dir.listFiles();
			Set<String> fontUrlSet = new HashSet<>();
			int count = 0;
			for (File file : list) {
				String htmlFileName = file.getName();
				count++;
				System.out.println("=======" + count + "========读到的文件名：" + htmlFileName);
				String html = "";
				try {
					html = FileUtils.readFileToString(file, Charset.forName("GBK"));
					String fontUrl = StringHelper.getResultByReg(html, "url\\('(//k[1-9]{1}.autoimg.cn[^\\.]+..ttf)");
					if (StringUtils.isNotEmpty(fontUrl)) {
						log.info(fontUrl);
						fontUrlSet.add(fontUrl);
					} else {
						log.error("fontUrl is null");
					}
					parseMobileFont(html, htmlFileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (count == 100) {
					break;
				}
			}
			log.info("fontUrlSet size : {}", fontUrlSet.size());

		}
	}
}
