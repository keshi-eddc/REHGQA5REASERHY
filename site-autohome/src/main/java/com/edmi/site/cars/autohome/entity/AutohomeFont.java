package com.edmi.site.cars.autohome.entity;

import fun.jerry.entity.annotation.ColumnMapping;
import fun.jerry.entity.annotation.TableMapping;

@TableMapping("autohome_font")
public class AutohomeFont {
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
