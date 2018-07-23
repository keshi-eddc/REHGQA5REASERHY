package com.edmi.site.cars.autohome.entity;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("autohome_font")
public class AutohomeFont {
	@ColumnMapping("autohomeHtml")
	private String autohomeHtml;
	
	@ColumnMapping("autohomeChar")
	private String autohomeChar;

	@ColumnMapping("hex_value")
	private String hexValue;

	@ColumnMapping("x_value")
	private String XValue;

	@ColumnMapping("y_value")
	private String YValue;

	@ColumnMapping("width")
	private String width;

	@ColumnMapping("height")
	private String height;

	@ColumnMapping("font_name")
	private String fontName;

	private String InsertTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	private String UpdateTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");

	
	public String getAutohomeHtml() {
		return autohomeHtml;
	}

	public void setAutohomeHtml(String autohomeHtml) {
		this.autohomeHtml = autohomeHtml;
	}

	public String getAutohomeChar() {
		return autohomeChar;
	}

	public void setAutohomeChar(String autohomeChar) {
		this.autohomeChar = autohomeChar;
	}

	public String getInsertTime() {
		return InsertTime;
	}

	public void setInsertTime(String insertTime) {
		InsertTime = insertTime;
	}

	public String getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(String updateTime) {
		UpdateTime = updateTime;
	}

	public String getHexValue() {
		return hexValue;
	}

	public void setHexValue(String hexValue) {
		this.hexValue = hexValue;
	}

	public String getXValue() {
		return XValue;
	}

	public void setXValue(String xValue) {
		XValue = xValue;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public String getYValue() {
		return YValue;
	}

	public void setYValue(String yValue) {
		YValue = yValue;
	}
}
