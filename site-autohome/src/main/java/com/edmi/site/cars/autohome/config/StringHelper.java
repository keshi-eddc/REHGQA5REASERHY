package com.edmi.site.cars.autohome.config;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
@Component
public class StringHelper {

	public static String getResultByReg(String content, String reg) {
		List<String> list = new ArrayList<String>();
		Pattern pa = Pattern.compile(reg, Pattern.DOTALL);
		Matcher ma1 = pa.matcher(content);
		if (ma1.find()) {
			list.add(ma1.group(1));
			return list.get(0);
		} else { 
			return null;
		}
	}

	public static List<String> getResultListByReg(String content, String reg) {
		List<String> list = new ArrayList<String>();
		Pattern pa = Pattern.compile(reg, Pattern.DOTALL);
		Matcher ma1 = pa.matcher(content);
		while (ma1.find()) {
			list.add(ma1.group(1));
		}
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list;
		}
	}


	public static String convertNumToString(String s) {
		String result = "";
		String[] aa = s.replace("&nbsp;", "").split(";");
		for (int i = 0; i < aa.length; i++) {
			if (aa[i].indexOf("&#") >= 0) {
				String[] bb = aa[i].split("&#");
				for (int j = 0; j < bb.length; j++) {
					if (bb[j].matches("[0-9]+") && aa[i].indexOf("&#" + bb[j]) >= 0 && aa[i].indexOf(bb[j] + "&#" + bb[j]) < 0) {
						result = result + ((char) Integer.valueOf(bb[j].replace(";", "")).intValue());
					} else {
						result = result + bb[j];
					}
				}
			} else {
				result = result + aa[i];
			}
		}
		return result;
	}

	public static String nullToString(Object s) {
//		return (!Validation.isEmpty(s)) ? (String.valueOf(s)) : ("");
		return null != s ? (String.valueOf(s)) : ("");
	}
	/** 数据库插入null值 */
	public static String nullToString2(Object s) {
		return null != s ? (String.valueOf(s)) : ("null");
	}



	public static String[][] convertResult(ResultSet res) {
		String[][] result = null;
		int column = 0;
		Vector vector = new Vector();
		try {
			while (res.next()) {
				column = res.getMetaData().getColumnCount();
				String[] str = new String[column];
				for (int i = 1; i < column + 1; i++) {
					Object ob = res.getObject(i);
					if (ob == null)
						str[i - 1] = "";
					else
						str[i - 1] = ob.toString();
				}
				vector.addElement(str.clone());
			}
			result = new String[vector.size()][column];
			vector.copyInto(result);
			vector.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getLastResultByReg(String content, String reg) {
		Pattern p = Pattern.compile(reg, Pattern.DOTALL);
		Matcher m = p.matcher(content);
		Stack s = new Stack();
		while (m.find()) {
			s.push(m.group(1));
		}
		return s.isEmpty() ? null : (String) s.pop();
	}

	/** 去除字符串前后的全角空格 并替换掉字符串中的全角空格 */
	public static String trimCHN(String para) {
		while (para.startsWith(" ")) {
			para = para.substring(1, para.length()).trim();
		}
		while (para.endsWith(" ")) {
			para = para.substring(0, para.length() - 1).trim();
		}
		para = para.replaceAll(" ", " ");
		return para;
	}

	/** 将html语言中的unicode转换成中文 */
	public static String escapeHtml(String unicodeStr) {
		if (unicodeStr == null) {
			return null;
		}
		StringBuffer retBuf = new StringBuffer();
		int maxLoop = unicodeStr.length();
		for (int i = 0; i < maxLoop; i++) {
			if (unicodeStr.charAt(i) == '\\') {
				if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
					try {
						retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
						i += 5;
					} catch (NumberFormatException localNumberFormatException) {
						retBuf.append(unicodeStr.charAt(i));
					}
				else
					retBuf.append(unicodeStr.charAt(i));
			} else {
				retBuf.append(unicodeStr.charAt(i));
			}
		}
		return retBuf.toString();
	}

	public static String formatTimes(String dateformat, Date date) {
		SimpleDateFormat dft = new SimpleDateFormat(dateformat);
		return dft.format(date);
	}

	public static String formatTimes() {
		SimpleDateFormat dft = new SimpleDateFormat("yyyyMMddHHmmss");
		return dft.format(new Date());
	}

	public static double getNumberFromStr(String str){
		String regEx="[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return Double.parseDouble(m.replaceAll("").trim());
	}

	/**
	 * 获取CrawlerTaskInfo的时间节点
	 * @param startTime
	 * @param interval
	 * @return
	 * @throws ParseException
	 */
	public static String getCrawlerDate(String startTime, int interval) throws ParseException {
		// 获取当前时间与起始时间的差距天数
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		long now = c.getTime().getTime();
		long start = sdf.parse(startTime).getTime();
		int days = (int) ((now - start) / (24 * 3600 * 1000));
		// 时间差每7天向后推移一次
		int num = days / interval;
		c.setTime(sdf.parse(startTime));
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + num * interval);
		return sdf.format(c.getTime());
	}
	
	/**
	 * 获取CrawlerTaskInfo的时间节点
	 * @param startTime
	 * @param interval
	 * @return
	 * @throws ParseException
	 */
	public static String getCrawlerDate(String startTime) throws ParseException {
		int interva = 7;
		// 获取当前时间与起始时间的差距天数
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		long now = c.getTime().getTime();
		long start = sdf.parse(startTime).getTime();
		int days = (int) ((now - start) / (24 * 3600 * 1000));
		// 时间差每7天向后推移一次
		int num = days / interva;
		c.setTime(sdf.parse(startTime));
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + num * interva);
		return sdf.format(c.getTime());
	}
	
	public static void main(String[] args) {
	   try {
		System.out.println(getCrawlerDate("2018-03-21", 7));
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

}
