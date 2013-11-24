package com.pangff.fixedtableview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pangff.fixedtableview.TableLayout.OnItemClickListener;
import com.pangff.fixedtableview.TableLayout.OnPullToRefreshListener;
import com.pangff.fixedtableview.TableLayout.OnSortListener;

public class MainActivity extends Activity {

	TableLayout table ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		table = (TableLayout) this.findViewById(R.id.table);
		table.setHeader(getHeader());//设置表头
		table.setData(getDataList());//设置数据
		table.setLoadingDrawable(R.anim.rotate);//设置刷新的loading图片（必须是RotateDrawble）
		table.setLoadingText("加载中..");//设置刷新loading的文字内容
		
		/**
		 * 刷新
		 */
		table.setOnPullToRefreshListener(new OnPullToRefreshListener() {
			@Override
			public void loadPre(View view) {//下拉加载上面的数据
				table.postDelayed(new Runnable() {
					@Override
					public void run() {
						table.refreshComplete();
					}
				},3000);
			}
			
			@Override
			public void loadNext(View view) {//下拉加载后面的数据
				loadData();
			}
		});
		
		/**
		 * item点击
		 */
		table.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(TableCell item) {
				Log.e("TableLayout", "选中股票Code:"+item.getStockCode());
			}
		});
		
		/**
		 * 排序
		 */
		table.setOnSortListener(new OnSortListener() {
			@Override
			public void onSort(final TableCell header) {
				table.showTitleLoading(header);//显示表头的loading
				
				//模拟排序请求
				table.postDelayed(new Runnable() {
					@Override
					public void run() {
						table.endTitleLoading(header);//排序结束隐藏表头loading
						//排序结束后设置当前排序方式
						header.setSortDrawble(getResources().getDrawable(R.drawable.stock_list_title_button_up_bg));
					}
				}, 4000);
			}
		});
		
	}
	
	/**
	 * 模拟网络请求
	 */
	public void loadData(){
		table.postDelayed(new Runnable() {
			@Override
			public void run() {
				table.setData(getDataList2());
				//数据返回后要调用refreshComplete隐藏loading
				table.refreshComplete();
			}
		},3000);
	}
	
	public List<TableCell> getHeader() {
		List<TableCell> headerList = new ArrayList<TableCell>();
		for (int i = 0; i < 15; i++) {
			TableCell header = new TableCell();
			header.setText("表头第[" + i + "]");//设置内容
			header.setSortLoadingDrawable(getResources().getDrawable(R.anim.rotate));//设置表头的loading drawble
			header.setSortDrawble(getResources().getDrawable(R.drawable.stock_list_title_sortable));//设置当前的排序drawble
			headerList.add(header);
		}
		return headerList;
	}
	
	/**
	 * 模拟下拉后表格数据
	 * @return
	 */
	public List<List<TableCell>> getDataList2(){
		List<List<TableCell>> dataList = new ArrayList<List<TableCell>>();
		for (int i = 0; i < 40; i++) {
			List<TableCell> rowList = new ArrayList<TableCell>();
			for (int j = 0; j < 15; j++) {
				TableCell data = new TableCell();
				if(j==0){//第一列表头
					data.setText("上证指数");
					data.setStockCode("000001");
					data.setStockCodeColor(Color.parseColor("#D7710A"));//设置股票code颜色
				}else{
					data.setDataCellDrawable(getResources().getDrawable(R.drawable.market_back_down));//设置当前单元格的背景
					data.setText("[" + i + "行" + j + "列]");//设置当前单元格的内容
					data.setTextColor(Color.BLACK);//设置当前单元格文字颜色
				}
				rowList.add(data);
			}
			dataList.add(rowList);
		}
		return dataList;
	}
	
	/**
	 * 模拟初始化数据
	 * @return
	 */
	public List<List<TableCell>> getDataList(){
		List<List<TableCell>> dataList = new ArrayList<List<TableCell>>();
		for (int i = 0; i < 20; i++) {
			List<TableCell> rowList = new ArrayList<TableCell>();
			for (int j = 0; j < 15; j++) {
				TableCell data = new TableCell();
				if(j==0){//第一列表头
					data.setText("上证指数");
					data.setStockCode("000001");
					data.setStockCodeColor(Color.parseColor("#D7710A"));//设置股票code颜色
				}else{
					data.setDataCellDrawable(getResources().getDrawable(R.drawable.market_back_down));//设置当前单元格的背景
					data.setText("[" + i + "行" + j + "列]");//设置当前单元格的内容
					data.setTextColor(Color.BLACK);//设置当前单元格文字颜色
				}
				rowList.add(data);
			}
			dataList.add(rowList);
		}
		return dataList;
	}

}
