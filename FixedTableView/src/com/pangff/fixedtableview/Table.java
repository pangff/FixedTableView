package com.pangff.fixedtableview;

import android.graphics.RectF;

public class Table {

	private RectF titleRect= new RectF();
	private RectF leftTitleRect= new RectF();
	private RectF centerRect= new RectF();
	private RectF leftTopConerRect= new RectF();
	private RectF headLoadingRect= new RectF();
	private RectF footLoadingRect = new RectF();
	
	public RectF getTitleRect() {
		return titleRect;
	}
	public void setTitleRect(RectF titleRect) {
		this.titleRect = titleRect;
	}
	public RectF getLeftTitleRect() {
		return leftTitleRect;
	}
	public void setLeftTitleRect(RectF leftTitleRect) {
		this.leftTitleRect = leftTitleRect;
	}
	public RectF getCenterRect() {
		return centerRect;
	}
	public void setCenterRect(RectF centerRect) {
		this.centerRect = centerRect;
	}
	public RectF getLeftTopConerRect() {
		return leftTopConerRect;
	}
	public void setLeftTopConerRect(RectF leftTopConerRect) {
		this.leftTopConerRect = leftTopConerRect;
	}
	public RectF getHeadLoadingRect() {
		return headLoadingRect;
	}
	public void setHeadLoadingRect(RectF headLoadingRect) {
		this.headLoadingRect = headLoadingRect;
	}
	public RectF getFootLoadingRect() {
		return footLoadingRect;
	}
	public void setFootLoadingRect(RectF footLoadingRect) {
		this.footLoadingRect = footLoadingRect;
	}

}
