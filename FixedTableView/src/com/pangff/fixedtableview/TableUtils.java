package com.pangff.fixedtableview;

import android.graphics.Paint;
import android.graphics.Rect;

public class TableUtils {

	public static Rect textBounds = null;
	public static int getTextWidth(String str, Paint paint) {
		int width = 0;
		if (str != null && str.length() != 0) {
			if(textBounds==null){
				textBounds = new Rect();
			}
			paint.getTextBounds(str, 0, str.length(), textBounds);
			width = textBounds.width();
		}
		return width;
	}

	public static int getTextHeight(String str, Paint paint) {
		int height = 0;
		if (str != null && str.length() != 0) {
			if(textBounds==null){
				textBounds = new Rect();
			}
			paint.getTextBounds(str, 0, str.length(), textBounds);
			height = textBounds.bottom - textBounds.top;
		}
		return height;
	}
}
