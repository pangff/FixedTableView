package com.pangff.fixedtableview;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;


public class TableCell {

	private String text="";//内容
	private String stockCode="";//股票代码
	private RectF rect = new RectF(0,0,0,0);//区域
	private int sortSate;//排序方式
	private boolean isRowPressed;//是否行点击
	private boolean isHeaderPressed;//是否表头点击
	
	private int textColor=-1;//字体颜色
	private int stockCodeColor=-1;
	private int textSize=-1;//字体大小
	private boolean shouldBold = false;//是否加粗
	private Drawable dataCellDrawable;//数据背景框
	private Drawable sortDrawble;//数据排序icon
	private boolean isLoading;
	private Drawable sortLoadingDrawable;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public int getSortSate() {
		return sortSate;
	}
	public void setSortSate(int sortSate) {
		this.sortSate = sortSate;
	}
	public RectF getRect() {
		return rect;
	}
	public void setRect(RectF rect) {
		this.rect = rect;
	}
	
	public boolean isHeaderPressed() {
		return isHeaderPressed;
	}
	public void setHeaderPressed(boolean isHeaderPressed) {
		this.isHeaderPressed = isHeaderPressed;
	}
	public boolean isRowPressed() {
		return isRowPressed;
	}
	public void setRowPressed(boolean isRowPressed) {
		this.isRowPressed = isRowPressed;
	}
	public int getTextColor() {
		return textColor;
	}
	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
	public int getTextSize() {
		return textSize;
	}
	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}
	public boolean isShouldBold() {
		return shouldBold;
	}
	public void setShouldBold(boolean shouldBold) {
		this.shouldBold = shouldBold;
	}
	public Drawable getDataCellDrawable() {
		return dataCellDrawable;
	}
	public void setDataCellDrawable(Drawable dataCellDrawable) {
		this.dataCellDrawable = dataCellDrawable;
	}
	public Drawable getSortDrawble() {
		return sortDrawble;
	}
	public void setSortDrawble(Drawable sortDrawble) {
		this.sortDrawble = sortDrawble;
	}
	public boolean isLoading() {
		return isLoading;
	}
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
	public Drawable getSortLoadingDrawable() {
		return sortLoadingDrawable;
	}
	public void setSortLoadingDrawable(Drawable sortLoadingDrawable) {
		this.sortLoadingDrawable = sortLoadingDrawable;
	}
	public int getStockCodeColor() {
		return stockCodeColor;
	}
	public void setStockCodeColor(int stockCodeColor) {
		this.stockCodeColor = stockCodeColor;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	
}
