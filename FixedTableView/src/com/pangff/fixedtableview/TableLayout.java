package com.pangff.fixedtableview;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class TableLayout extends ViewGroup {

	private final Flinger flinger;
	Paint paint;
	Table myTable;
	List<TableCell> headerList;
	List<List<TableCell>> dataList;
	List<TableCell> leftHeaderList;
	
	private VelocityTracker velocityTracker;
	private final int minimumVelocity;
	private final int maximumVelocity;


	int loadingHeight = 0;
	int headLoadingHeight = 0;
	int footLoadingHeight = 0;

	int viewHeight = 0;
	int viewWidth = 0;
	
	int viewLeft, viewTop, viewRight, viewBottom;
	
	boolean headerLoadingVisible = false;
	boolean footerLoadingVisible = false;
	OnPullToRefreshListener onPullToRefreshListener;
	OnSortListener onSortListener;
	OnItemClickListener onItemClickListener;
	
	int titleHeight;
	int leftTitleWidth;
	int cellWidth;
	int cellHeight;
	String loadingText;
	int headerTextColor;
	int defaultTextSize;
	int defaultTextColor;
	int bgColor;
	int rowColor;
	int rowPressedColor;
	int headerBg;
	int headerPressedBg;
	int diverHeight;
	int cellVericalPadding;
	int cellHorizantalPadding;
	int loadingBgColor;
	int loadingTextColor;
	int loadingTextSize;
	Drawable headerLoadingDrawable, footerLoadingDrawable;
	ValueAnimator titleLoadingAnimator;
	ValueAnimator headerLoadingAnimator;
	ValueAnimator footerLoadingAnimator;
	Drawable headerSpan;
	int driverLineTopColor;
	int driverLineBottomColor;
	
	public interface OnPullToRefreshListener{
		public void loadPre(View view);
		public void loadNext(View view);
	}
	
	public interface OnSortListener{
		public void onSort(TableCell header);
	}
	
	public interface OnItemClickListener{
		public void onItemClick(TableCell item);
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		this.onItemClickListener = onItemClickListener;
	}
	
	public void setOnSortListener(OnSortListener onSortListener){
		this.onSortListener = onSortListener;
	}
	
	public void setOnPullToRefreshListener(OnPullToRefreshListener onPullToRefreshListener){
		this.onPullToRefreshListener = onPullToRefreshListener;
	}

	public TableLayout(Context context) {
		this(context, null, 0);
	}

	public TableLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TableLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.TableLayout);     
		titleHeight = (int) a.getDimension(R.styleable.TableLayout_titleHeight, 90);
		leftTitleWidth = (int) a.getDimension(R.styleable.TableLayout_leftTitleWidth, 200);
		cellWidth= (int) a.getDimension(R.styleable.TableLayout_cellWidth, 170);
		cellHeight = (int) a.getDimension(R.styleable.TableLayout_cellHeight, 70);
		headerTextColor = a.getColor(R.styleable.TableLayout_headerTextColor, Color.BLACK);
		defaultTextSize = (int) a.getDimension(R.styleable.TableLayout_defaultTextSize, 30);
		defaultTextColor = a.getColor(R.styleable.TableLayout_defaultTextColor, Color.BLACK);
		bgColor =  a.getColor(R.styleable.TableLayout_bgColor,Color.parseColor("#DCEFF3"));
		rowColor = a.getColor(R.styleable.TableLayout_rowColor,Color.parseColor("#F7F7F7"));
		rowPressedColor = a.getColor(R.styleable.TableLayout_rowPressedColor,Color.parseColor("#E5F4F7"));
		headerBg = a.getColor(R.styleable.TableLayout_headerBgColor,Color.parseColor("#00000000"));
		headerPressedBg = a.getColor(R.styleable.TableLayout_headerPressedBgColor,Color.parseColor("#66000000"));
		diverHeight = (int) a.getDimension(R.styleable.TableLayout_diverHeight, 2);
		cellVericalPadding = (int) a.getDimension(R.styleable.TableLayout_cellVericalPadding, 30);
		cellHorizantalPadding = (int) a.getDimension(R.styleable.TableLayout_cellHorizantalPadding, 10);
		loadingBgColor =  a.getColor(R.styleable.TableLayout_loadingBgColor,Color.GRAY);
		loadingTextColor = a.getColor(R.styleable.TableLayout_loadingBgColor,Color.BLACK);
		loadingTextSize =  (int) a.getDimension(R.styleable.TableLayout_loadingTextSize, 30);
		
		loadingText = a.getString(R.styleable.TableLayout_loadingText);
		headerLoadingDrawable = a.getDrawable(R.styleable.TableLayout_loadingDrawable);
		footerLoadingDrawable = a.getDrawable(R.styleable.TableLayout_loadingDrawable);
		headerSpan = getResources().getDrawable(R.drawable.stock_name__custom_line);
		
		driverLineTopColor = Color.parseColor("#E5E5E5");
		driverLineBottomColor = Color.parseColor("#FFFFFF");
		
		if(a!=null){
			a.recycle();
		}
		myTable = new Table();
		paint = new Paint();
		paint.setAntiAlias(true);
		this.flinger = new Flinger(context);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		this.minimumVelocity = configuration.getScaledMinimumFlingVelocity();
		this.maximumVelocity = configuration.getScaledMaximumFlingVelocity();
		
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (changed) {
			viewHeight = bottom - top;
			viewWidth = right - left;
			viewLeft = left;
			viewRight = right;
			viewTop = 0;
			viewBottom = viewHeight;
		}
	}
	
	@Override  
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	    computeTableWidthHeight();// 布局时计算table的宽高
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}  
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void computeTableWidthHeight() {

		// 测量header 0 的宽度
		myTable.getLeftTopConerRect().set(viewLeft, viewTop, viewLeft
				+ leftTitleWidth, viewTop + titleHeight);// 左上角
		myTable.getTitleRect().set(viewLeft + leftTitleWidth, viewTop,
				viewRight, viewTop + titleHeight);
		myTable.getLeftTitleRect().set(viewLeft, viewTop + titleHeight
				+ headLoadingHeight, viewLeft + leftTitleWidth, viewBottom
				- footLoadingHeight);
		myTable.getCenterRect().set(viewLeft + leftTitleWidth, viewTop
				+ titleHeight + headLoadingHeight, viewRight, viewBottom
				- footLoadingHeight);
		computCellLocation();
	}

	private void computCellLocation() {
		computHeaderCellLocation();
		computLeftTitleCellLocation();
		int footerLoadngStartY = computDataCellLocation();
		computFooterLoadingLocation(footerLoadngStartY);
		computHeaderLoadingLocation();
	}

	private void computFooterLoadingLocation(int startY) {
		myTable.getFootLoadingRect().set(viewLeft, startY, viewRight,
				startY + footLoadingHeight);
	}
	

	private void computHeaderCellLocation() {
		int width = 0;
		int startX = (int) myTable.getTitleRect().left;
		for (int i = 1; i < headerList.size(); i++) {
			TableCell header = headerList.get(i);
			header.getRect().set(startX + width+getActualScrollX(), myTable.getTitleRect().top, startX + width + cellWidth + 2 * cellHorizantalPadding + getActualScrollX(),myTable.getTitleRect().bottom);
			width += (cellWidth + 2 * cellHorizantalPadding);
		}
	}

	private void computLeftTitleCellLocation() {
		int height = 0;
		int startX = (int) myTable.getLeftTitleRect().left;
		int startY = (int) myTable.getLeftTitleRect().top;
		for (int i = 0; i < leftHeaderList.size(); i++) {
			TableCell header = leftHeaderList.get(i);
			header.getRect().set(startX, startY + height+getActualScrollY(), myTable.getLeftTitleRect().right,
					startY + height + (cellHeight + 2 * cellVericalPadding)+getActualScrollY());
			height += (cellHeight + 2 * cellVericalPadding);
		}
	}

	private int computDataCellLocation() {
		int startX = (int) myTable.getCenterRect().left;
		int startY = (int) myTable.getCenterRect().top;
		int height = 0;
		for (int j = 0; j < dataList.size(); j++) {// 行
			int width = 0;
			for (int i = 1; i < dataList.get(j).size(); i++) {
				TableCell header = dataList.get(j).get(i);// 第j行第i列
				header.getRect().set(startX + width+getActualScrollX(), startY + height+getActualScrollY(), startX + width
						+ cellWidth + 2 * cellHorizantalPadding+getActualScrollX(), startY
						+ height + (cellHeight + 2 * cellVericalPadding)+getActualScrollY());
				width += (cellWidth + 2 * cellHorizantalPadding);
			}
			height += (cellHeight + 2 * cellVericalPadding);
		}
		return (int) dataList.get(dataList.size()-1).get(0).getRect().bottom;
	}
	
	
	private void computHeaderLoadingLocation() {
		myTable.getHeadLoadingRect().set(viewLeft, viewTop + titleHeight + getActualScrollY(),
				viewRight, viewTop + titleHeight + headLoadingHeight + getActualScrollY());
	}
	

	private void overTop() {
		if (!headerLoadingVisible) {
			setHeaderLodingVisible(true);
			if(onPullToRefreshListener!=null){
				onPullToRefreshListener.loadPre(this);
			}
		}
		
	}

	private void overBottom() {
		if (!footerLoadingVisible) {
			setFooterLodingVisible(true);
			if(onPullToRefreshListener!=null){
				onPullToRefreshListener.loadNext(this);
			}
		}
	}
	
	public void refreshComplete(){
		this.setHeaderLodingVisible(false);
		this.setFooterLodingVisible(false);
		requestLayout();
	}

	private void resetOverScroll() {

		int dy = currentY + oldY;
		if (dy > 0) {
			currentY = -oldY;
			overTop();
		} else if (dy < -getMaxScrollY() && getMaxScrollY() > 0) {
			currentY = -oldY - getMaxScrollY();
			overBottom();
		}
		int dx = currentX + oldX;
		if (dx > 0) {
			currentX = -oldX;
		} else if (dx < -getMaxScrollX() && getMaxScrollX() > 0) {
			currentX = -oldX - getMaxScrollX();
		}
	}
	
	

	private void drawRect(Canvas canvas) {
		resetOverScroll();
		computCellLocation();
		drawData(canvas);
		drawHeaderLoading(canvas);
		drawLeftTitle(canvas);
		drawDiver(canvas);
		drawTopHeader(canvas);
		drawHeaderSpander(canvas);
		drawFooterLoading(canvas);
		drawLeftTopConer(canvas);
	}

	private void drawHeaderLoading(Canvas canvas) {
		if (headerLoadingVisible) {
			paint.setColor(loadingBgColor);
			canvas.drawRect(myTable.getHeadLoadingRect(), paint);
			paint.setColor(loadingTextColor);
			paint.setTextSize(loadingTextSize);
			int textHeight = TableUtils.getTextHeight(loadingText, paint);
			paint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(loadingText, myTable.getHeadLoadingRect().centerX(),
					myTable.getHeadLoadingRect().centerY() + textHeight / 2, paint);

			headerLoadingDrawable.getBounds().set(
					(int) myTable.getHeadLoadingRect().left+cellWidth/2+cellHorizantalPadding-headerLoadingDrawable.getMinimumWidth()/2,
					(int) myTable.getHeadLoadingRect().top + cellVericalPadding,
					(int) myTable.getHeadLoadingRect().left-headerLoadingDrawable.getMinimumWidth()/2+cellWidth/2+cellHorizantalPadding+ headerLoadingDrawable.getMinimumWidth(),
					(int) myTable.getHeadLoadingRect().bottom - cellVericalPadding);
			headerLoadingDrawable.draw(canvas);
		}
	}

	private void drawFooterLoading(Canvas canvas) {
		if (footerLoadingVisible) {
			paint.setColor(loadingBgColor);
			canvas.drawRect(myTable.getFootLoadingRect(), paint);
			paint.setColor(loadingTextColor);
			paint.setTextSize(loadingTextSize);
			int textHeight = TableUtils.getTextHeight(loadingText, paint);
			paint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(loadingText, myTable.getFootLoadingRect().centerX(),
					myTable.getFootLoadingRect().centerY() + textHeight / 2, paint);

			footerLoadingDrawable.getBounds().set(
					(int) myTable.getFootLoadingRect().left+cellWidth/2+cellHorizantalPadding-headerLoadingDrawable.getMinimumWidth()/2,
					(int) myTable.getFootLoadingRect().top + cellVericalPadding,
					(int) myTable.getFootLoadingRect().left-footerLoadingDrawable.getMinimumWidth()/2+cellWidth/2+cellHorizantalPadding+ footerLoadingDrawable.getMinimumWidth(),
					(int) myTable.getFootLoadingRect().bottom - cellVericalPadding);
			footerLoadingDrawable.draw(canvas);
		}
	}

	@SuppressLint("NewApi")
	private void drawTopHeader(final Canvas canvas) {
		paint.setColor(bgColor);
		canvas.drawRect(myTable.getTitleRect(), paint);
		paint.setTextAlign(Paint.Align.CENTER);
		for (int i = 1; i < headerList.size(); i++) {
			TableCell header = headerList.get(i);
			if(header.isHeaderPressed()){
				paint.setColor(headerPressedBg);
			}else{
				paint.setColor(headerBg);
			}
			canvas.drawRect(header.getRect().left,header.getRect().top, header.getRect().right, header.getRect().bottom, paint);
			paint.setColor(headerTextColor);
			
			if(header.getTextSize()==-1){
				paint.setTextSize(defaultTextSize);
			}else{
				paint.setTextSize(header.getTextSize());
			}
			int textHeight = TableUtils.getTextHeight(header.getText(),paint);
			int textWidth = TableUtils.getTextWidth(header.getText(), paint);
			if(header.getSortDrawble()==null){
				canvas.drawText(header.getText(), header.getRect().centerX(), header.getRect().centerY() + textHeight/ 2, paint);
			}else{
				canvas.drawText(header.getText(), header.getRect().centerX()-10, header.getRect().centerY() + textHeight/ 2, paint);
				final Drawable sortDrawable = header.getSortDrawble();
				final Drawable LoadingDrawable = header.getSortLoadingDrawable();
				
				if(sortDrawable!=null && !header.isLoading()){
					int sortIconLeft = (int)header.getRect().centerX() + textWidth/2;
					int sortIconTop = (int)header.getRect().centerY()-textHeight/4;
					int sortIconBottom = (int)header.getRect().centerY()+textHeight/2;
					int sortIconWidth = (sortIconBottom-sortIconTop)*sortDrawable.getMinimumWidth()/sortDrawable.getMinimumHeight();
					int sortIconRight = sortIconWidth+(int)header.getRect().centerX() + textWidth/2;
					
					sortDrawable.setBounds(sortIconLeft,sortIconTop, sortIconRight, sortIconBottom);
					sortDrawable.draw(canvas);
					
				}
				if(LoadingDrawable!=null && header.isLoading()){
					int sortIconLeft = (int)header.getRect().centerX() + textWidth/2;
					int sortIconTop = (int)header.getRect().centerY()-textHeight/4;
					int sortIconBottom = (int)header.getRect().centerY()+textHeight/2;
					int sortIconWidth = (sortIconBottom-sortIconTop)*LoadingDrawable.getMinimumWidth()/LoadingDrawable.getMinimumHeight();
					int sortIconRight = sortIconWidth+(int)header.getRect().centerX() + textWidth/2;
					
					LoadingDrawable.getBounds().set(sortIconLeft,sortIconTop, sortIconRight, sortIconBottom);
					LoadingDrawable.draw(canvas);
				}
			}
		}
	}
	

	private void drawLeftTitle(Canvas canvas) {
		
		paint.setTextAlign(Paint.Align.CENTER);
		for (int i = 0; i < leftHeaderList.size(); i++) {
			TableCell header = leftHeaderList.get(i);
			if(header.isRowPressed()){
				paint.setColor(rowPressedColor);
			}else{
				paint.setColor(rowColor);
			}
			canvas.drawRect(header.getRect().left, header.getRect().top, header.getRect().right,header.getRect().bottom, paint);
			
			if(header.getTextSize()==-1){
				paint.setTextSize(defaultTextSize);
			}else{
				paint.setTextSize(header.getTextSize());
			}
			int textHeight = TableUtils.getTextHeight(header.getText(),paint);
			if(header.getTextColor()!=-1){
				paint.setColor(header.getTextColor());
			}else{
				paint.setColor(defaultTextColor);
			}
			canvas.drawText(header.getText(), header.getRect().centerX(),header.getRect().centerY()-textHeight+textHeight*3/4, paint);
			if(!header.getStockCode().equals("")){
				if(header.getStockCodeColor()!=-1){
					paint.setColor(header.getStockCodeColor());
				}else{
					paint.setColor(defaultTextColor);
				}
				canvas.drawText(header.getStockCode(), header.getRect().centerX(),header.getRect().centerY()+textHeight, paint);
			}
		}
	}
	
	private void drawDiver(Canvas canvas){
		for (int i = 0; i < leftHeaderList.size(); i++) {
			TableCell header = leftHeaderList.get(i);
			paint.setColor(driverLineTopColor);
			canvas.drawLine(viewLeft, header.getRect().bottom-2, viewRight,header.getRect().bottom-1, paint);
			paint.setColor(driverLineBottomColor);
			canvas.drawLine(viewLeft, header.getRect().bottom-1, viewRight,header.getRect().bottom, paint);
		}
	}
	
	
	private void drawHeaderSpander(Canvas canvas){
		for(int i=1;i<headerList.size();i++){
			TableCell header = headerList.get(i);
			headerSpan.setBounds((int)header.getRect().right-2, (int)header.getRect().top, (int)header.getRect().right, (int)header.getRect().bottom);
			headerSpan.draw(canvas);
		}
	}
	

	private void drawData(Canvas canvas) {
		paint.setTextAlign(Paint.Align.CENTER);
		for (int j = 0; j < dataList.size(); j++) {
			List<TableCell> rowList = dataList.get(j);
			for (int i = 1; i < rowList.size(); i++) {
				TableCell header = rowList.get(i);
				if(header.isRowPressed()){
					paint.setColor(rowPressedColor);
				}else{
					paint.setColor(rowColor);
				}
				
				canvas.drawRect(header.getRect().left,
						header.getRect().top,
						header.getRect().right,
						header.getRect().bottom, paint);
				
				Drawable drawable = header.getDataCellDrawable();
				if(drawable!=null){
					int left = (int)header.getRect().left+cellHorizantalPadding;
					int right = (int)header.getRect().right-cellHorizantalPadding;
					int top = (int)header.getRect().top+cellVericalPadding;
					int bottom = (int)header.getRect().bottom-cellVericalPadding;
					drawable.getBounds().set(left,top,right,bottom );
					drawable.draw(canvas);
				}
				if(header.getTextSize()==-1){
					paint.setTextSize(defaultTextSize);
				}else{
					paint.setTextSize(header.getTextSize());
				}
				int textHeight = TableUtils.getTextHeight(header.getText(),paint);
				if(header.getTextColor()!=-1){
					paint.setColor(header.getTextColor());
				}else{
					paint.setColor(defaultTextColor);
				}
				canvas.drawText(header.getText(), header.getRect().centerX(), header.getRect().centerY()+ textHeight / 2 , paint);
			}
		}
	}

	private void drawLeftTopConer(Canvas canvas) {
		paint.setColor(bgColor);
		canvas.drawRect(myTable.getLeftTopConerRect(), paint);
		if (headerList.get(1).getTextColor() != -1) {
			paint.setColor(headerList.get(1).getTextColor());
		} else {
			paint.setColor(defaultTextColor);
		}
		paint.setTextSize(defaultTextSize);
		paint.setTextAlign(Paint.Align.CENTER);
		if(headerList.get(0).getTextSize()==-1){
			paint.setTextSize(defaultTextSize);
		}else{
			paint.setTextSize(headerList.get(0).getTextSize());
		}
		int textHeight = TableUtils.getTextHeight(headerList.get(0).getText(),paint);
		canvas.drawText(headerList.get(0).getText(), myTable
				.getLeftTopConerRect().centerX(), myTable.getLeftTopConerRect()
				.centerY() + textHeight / 2, paint);
		
		headerSpan.setBounds((int)myTable.getLeftTopConerRect().right-2, (int)myTable.getLeftTopConerRect().top, (int)myTable.getLeftTopConerRect().right, (int)myTable.getLeftTopConerRect().bottom);
		headerSpan.draw(canvas);
	}

	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		drawRect(canvas);
	}

	int downX, downY;
	int currentX = 0, currentY = 0;
	int oldX = 0, oldY = 0;
	
	
	private void onPressed(float x,float y){
		for(int i=1;i<headerList.size();i++){
			if(headerList.get(i).getRect().contains(x, y)){
				headerList.get(i).setHeaderPressed(true);
				int left = (int) headerList.get(i).getRect().left;
				int right = (int) headerList.get(i).getRect().right;
				int top = (int) headerList.get(i).getRect().top;
				int bottom = (int) headerList.get(i).getRect().bottom;
				invalidate(left, top, right,bottom);
				break;
			}
		}
		
		for(int i=0;i<leftHeaderList.size();i++){
			TableCell table = leftHeaderList.get(i);
			if(table.getRect().top<y&&table.getRect().bottom>y){
				table.setRowPressed(true);
				for(int j=0;j<dataList.get(i).size();j++){
					dataList.get(i).get(j).setRowPressed(true);
				}
				invalidate((int)viewLeft, (int)table.getRect().top, (int)viewRight,(int)table.getRect().bottom);
				break;
			}
		}
	}
	
	private void onUnPressed(){
		for(int i=1;i<headerList.size();i++){
			 if(headerList.get(i).isHeaderPressed()){
			 	headerList.get(i).setHeaderPressed(false);
				int left = (int) headerList.get(i).getRect().left;
				int right = (int) headerList.get(i).getRect().right;
				int top = (int) headerList.get(i).getRect().top;
				int bottom = (int) headerList.get(i).getRect().bottom;
				invalidate(left, top, right,bottom);
				if(onSortListener!=null){
					onSortListener.onSort(headerList.get(i));
				}
			 }
		}
		
		for(int i=0;i<leftHeaderList.size();i++){
			TableCell table = leftHeaderList.get(i);
			if(table.isRowPressed()){
				table.setRowPressed(false);
				for(int j=0;j<dataList.get(i).size();j++){
					dataList.get(i).get(j).setRowPressed(false);
				}
				invalidate((int)viewLeft, (int)table.getRect().top, (int)viewRight,(int)table.getRect().bottom);
				if(onItemClickListener!=null){
					onItemClickListener.onItemClick(table);
				}
				break;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (velocityTracker == null) { // If we do not have velocity tracker
			velocityTracker = VelocityTracker.obtain(); // then get one
		}
		if(myTable.getLeftTopConerRect().contains(event.getX(), event.getY())){
			return true;
		}
		velocityTracker.addMovement(event); // add this movement to it
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = (int) event.getX();
			downY = (int) event.getY();
			onPressed(downX,downY);
			if (!flinger.isFinished()) { // If scrolling, then stop now
				flinger.forceFinished();
			} else {
				oldX = oldX + currentX;
				oldY = oldY + currentY;
				
				currentX = 0;
				currentY = 0;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int dx = (int) (event.getX() - downX);
			int dy = (int) (event.getY() - downY);
			onUnPressed();
			// if (Math.abs(currentX) > Math.abs(currentY)) {
			// currentY = 0;
			// } else {
			// currentX = 0;
			// }
			scrollBy(dx, dy);
			break;
		case MotionEvent.ACTION_UP:
			onUnPressed();
			final VelocityTracker velocityTracker = this.velocityTracker;
			velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
			int velocityX = (int) velocityTracker.getXVelocity();
			int velocityY = (int) velocityTracker.getYVelocity();

			if (Math.abs(velocityX) > minimumVelocity
					|| Math.abs(velocityY) > minimumVelocity) {
				flinger.start(-getActualScrollX(), -getActualScrollY(),
						velocityX, velocityY, getMaxScrollX(), getMaxScrollY());
			} else {
				if (this.velocityTracker != null) { // If the velocity less than
													// threshold
					this.velocityTracker.recycle(); // recycle the tracker
					this.velocityTracker = null;
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	private class Flinger implements Runnable {
		private final Scroller scroller;

		private int lastX = 0;
		private int lastY = 0;

		Flinger(Context context) {
			scroller = new Scroller(context);
		}

		void start(int initX, int initY, int initialVelocityX,
				int initialVelocityY, int maxX, int maxY) {

			scroller.fling(initX, initY, initialVelocityX, initialVelocityY, 0,
					maxX, 0, maxY);

			lastX = initX;
			lastY = initY;
			post(this);
		}

		public void run() {
			if (scroller.isFinished()) {
				return;
			}

			boolean more = scroller.computeScrollOffset();
			int x = scroller.getCurrX();
			int y = scroller.getCurrY();
			int diffX = lastX - x;
			int diffY = lastY - y;
			if (diffX != 0 || diffY != 0) {
				scrollByFling(diffX, diffY);
				lastX = x;
				lastY = y;
			}

			if (more) {
				post(this);
			}
		}

		boolean isFinished() {
			return scroller.isFinished();
		}

		void forceFinished() {
			if (!scroller.isFinished()) {
				oldX = oldX + currentX;
				oldY = oldY + currentY;
				scroller.forceFinished(true);
			}
		}
	}

	public void scrollBy(int x, int y) {
		if (getMaxScrollX() > 0) {
			currentX = x;
		}
		if (getMaxScrollY() > 0) {
			currentY = y;
		}
		invalidate();
	}

	private void scrollByFling(int x, int y) {
		if (getMaxScrollX() > 0) {
			currentX -= x;
		}
		if (getMaxScrollY() > 0) {
			currentY -= y;
		}
		invalidate();
	}

	private int getActualScrollX() {
		return oldX + currentX;
	}

	private int getActualScrollY() {
		return oldY + currentY;
	}

	private int getMaxScrollX() {
		return (cellWidth + cellHorizantalPadding * 2)
				* (headerList.size() - 1) - (viewWidth - leftTitleWidth);
	}

	private int getMaxScrollY() {
		return (cellHeight + cellVericalPadding * 2)
				* dataList.size()
				- (viewHeight - titleHeight - headLoadingHeight - footLoadingHeight);
	}

	/**
	 * 隐藏显示顶部loading
	 * @param isVisible
	 */
	public void setHeaderLodingVisible(boolean isVisible) {
		if (isVisible) {
			headerLoadingVisible = true;
			headLoadingHeight = loadingHeight;
			if(headerLoadingAnimator==null){
				headerLoadingAnimator = ValueAnimator.ofInt(0,10000);
				headerLoadingAnimator.setInterpolator(new LinearInterpolator());
				headerLoadingAnimator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						if(headerLoadingVisible){
							((RotateDrawable) headerLoadingDrawable).setLevel((Integer) animation.getAnimatedValue());
							invalidate((int)myTable.getHeadLoadingRect().left,(int)myTable.getHeadLoadingRect().top,(int)myTable.getHeadLoadingRect().right,(int)myTable.getHeadLoadingRect().bottom);
						}
					}
				});
				headerLoadingAnimator.setDuration(1000);
				headerLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
			}
			headerLoadingAnimator.start();
		} else {
			headerLoadingVisible = false;
			headLoadingHeight = 0;
			if(headerLoadingAnimator!=null){
				headerLoadingAnimator.end();
				headerLoadingAnimator.cancel();
			}
		}
		computeTableWidthHeight();
		invalidate();
	}

	/**
	 * 隐藏显示底部loading
	 * @param isVisible
	 */
	public void setFooterLodingVisible(boolean isVisible) {
		if (isVisible) {
			footerLoadingVisible = true;
			footLoadingHeight = loadingHeight;
			if(footerLoadingAnimator==null){
				footerLoadingAnimator = ValueAnimator.ofInt(0,10000);
				footerLoadingAnimator.setInterpolator(new LinearInterpolator());
				footerLoadingAnimator.setDuration(1000);
				footerLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
				footerLoadingAnimator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						if(footerLoadingVisible){
							((RotateDrawable)footerLoadingDrawable).setLevel((Integer) animation.getAnimatedValue());
							invalidate((int)myTable.getFootLoadingRect().left,(int)myTable.getFootLoadingRect().top,(int)myTable.getFootLoadingRect().right,(int)myTable.getFootLoadingRect().bottom);
						}
					}
				});
			}
			footerLoadingAnimator.start();	
		} else {
			footerLoadingVisible = false;
			footLoadingHeight = 0;
			if(footerLoadingAnimator!=null){
				footerLoadingAnimator.end();
				footerLoadingAnimator.cancel();
			}
		}
		computeTableWidthHeight();
		invalidate();
	}

	
	
	/**
	 * 设置加载loading图
	 * @param id
	 */
	public void setLoadingDrawable(int id){
		headerLoadingDrawable = getResources().getDrawable(id);
		footerLoadingDrawable = getResources().getDrawable(id);
		loadingHeight = headerLoadingDrawable.getMinimumHeight() + 2* cellVericalPadding;
	}
	
	/**
	 * 设置加载loading文字
	 * @param text
	 */
	public void setLoadingText(String text){
		 this.loadingText = text;
	}

	/**
	 * 设置表头
	 * @param headerList
	 */
	public void setHeader(List<TableCell> headerList){
		this.headerList = headerList;
	}

	/**
	 * 设置数据
	 * @param dataList
	 */
	public void setData(List<List<TableCell>> dataList) {
		this.dataList = dataList;
		leftHeaderList = new ArrayList<TableCell>();
		for (int i = 0; i < dataList.size(); i++) {
			TableCell data = dataList.get(i).get(0);
			leftHeaderList.add(data);
		}
		requestLayout();
	}
	
	
	
	/**
	 * 结束表头loading
	 * @param cell
	 */
	public void endTitleLoading(final TableCell cell){
		if(titleLoadingAnimator!=null){
			titleLoadingAnimator.end();
			titleLoadingAnimator.cancel();
		}
		if(currentSort!=null){
			currentSort.setLoading(false);
			invalidate(new Rect((int)currentSort.getRect().left, (int)currentSort.getRect().top, (int)currentSort.getRect().right, (int)currentSort.getRect().bottom));
		}
	}
	
	
	/**
	 * 显示表头loading
	 */
	TableCell currentSort;
	public void showTitleLoading(final TableCell cell){
		endTitleLoading(cell);
		cell.setLoading(true);
		titleLoadingAnimator = ValueAnimator.ofInt(0,10000);
		titleLoadingAnimator.setInterpolator(new LinearInterpolator());
		titleLoadingAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				((RotateDrawable) cell.getSortLoadingDrawable()).setLevel((Integer) animation.getAnimatedValue());
				invalidate(new Rect((int)cell.getRect().left,(int)cell.getRect().top,(int)cell.getRect().right,(int)cell.getRect().bottom));
			}
		});
		titleLoadingAnimator.setDuration(1000);
		titleLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
		titleLoadingAnimator.start();
		currentSort = cell;
	}

}
