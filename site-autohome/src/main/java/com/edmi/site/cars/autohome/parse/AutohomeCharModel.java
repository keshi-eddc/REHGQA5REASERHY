package com.edmi.site.cars.autohome.parse;

public class AutohomeCharModel {

	public String getAutohomeChar(int x, int y) {
		String autohomeChar = "";

		if (x == 14 && 94 <= y && y <= 110) {
			// 1 地
			autohomeChar = "地";
			return autohomeChar;
		} else if (x == 24 && 753 <= y && y <= 766) {
			// 2 公
			autohomeChar = "公";
			return autohomeChar;
		} else if (x <= 24 && x >= 45 && 277 <= y && y <= 299) {
			// 3 和
			autohomeChar = "和";
			return autohomeChar;
		} else if (x <= 24 && x >= 81 && 1331 <= y && y <= 1352) {
			// 4 排
			autohomeChar = "排";
			return autohomeChar;
		} else if (x == 26 && 437 <= y && y <= 460) {
			// 5 机
			autohomeChar = "机";
			return autohomeChar;
		} else if (x == 28 && -118 <= y && y <= -91) {
			// 6 大
			autohomeChar = "大";
			return autohomeChar;
		} else if (x <= 28 && x >= 49 && 45 <= y && y <= 78) {
			// 7 耗
			autohomeChar = "耗";
			return autohomeChar;
		} else if (x == 32 && 140 <= y && y <= 157) {
			// 8 级
			autohomeChar = "级";
			return autohomeChar;
		} else if (x <= 32 && x >= 54 && 849 <= y && y <= 875) {
			// 9 一
			autohomeChar = "一";
			return autohomeChar;
		} else if (x <= 30 && x >= 50 && 200 <= y && y <= 300) {
			// 10 泥
			autohomeChar = "泥";
			return autohomeChar;
		} else if (x <= 44 && x >= 72 && 526 <= y && y <= 555) {
			// 11 手
			autohomeChar = "手";
			return autohomeChar;
		} else if (x >= 50 && x <= 70 && 958 <= y && y <= 970) {
			// 12 十
			autohomeChar = "十";
			return autohomeChar;
		} else if (x == 52 && 900 <= y && y <= 950) {
			// 13七
			autohomeChar = "七";
			return autohomeChar;
		} else if (x >= 50 && x <= 80 && 0 <= y && y <= 50) {
			// 14上
			autohomeChar = "上";
			return autohomeChar;
		} else if (x >= 50 && x <= 80 && 1500 <= y && y <= 1600) {
			// 15下
			autohomeChar = "下";
			return autohomeChar;
		} else if (x >= 60 && x <= 100 && 130 <= y && y <= 150) {
			// 16二
			autohomeChar = "二";
			return autohomeChar;
		} else if (x >= 90 && x <= 150 && 1200 <= y && y <= 1300) {
			// 17性
			autohomeChar = "性";
			return autohomeChar;
		} else if (x >= 100 && x <= 130 && 1200 <= y && y <= 1300) {
			// 18九
			autohomeChar = "九";
			return autohomeChar;
		}

		return autohomeChar;
	}
}
