package com.sneezer.parkrio;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class ChartActivity extends AbstractAsyncActivity {
	private static String TAG = "graphActivity";
	private CookieManager cookieManager;
	private String cookieString;
	
	private final String viewstateParam = "/wEPDwUKMTU5NDg5OTA1MGRkQtZJsU4tV32pG1Vj7vs7uizyBb4=";
	private final String serverCharset = "EUC-KR";
	private String base_url = "";
	private final String uri = "hwork/iframe_MonthGraph.aspx";
	private final String uri2 = "hwork/iframe_MonthGraph2.aspx";

	private static String intentKindParam;
	private static String intentDateParam;
	private TextView currentDateView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);

		CookieSyncManager.createInstance(getApplicationContext());
		cookieManager = CookieManager.getInstance();
		final String base_url = getString(R.string.base_url);
		this.cookieString = cookieManager.getCookie(base_url);
		
		Calendar calendar = Calendar.getInstance();
		
		if ( getIntent().getStringExtra("kind") != null ) {
			intentKindParam = getIntent().getStringExtra("kind");
		} else {
			intentKindParam = "elec";
		} 
		if ( getIntent().getStringExtra("date") != null ) {
			intentDateParam = getIntent().getStringExtra("date");
		} else {
			intentDateParam = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		}
		
		String[] dateArray = intentDateParam.split("-");
		
		currentDateView = (TextView) findViewById(R.id.currentDateEntry);
		currentDateView.setText(dateArray[0]+"-"+dateArray[1]);
		currentDateView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					String[] dateArray = intentDateParam.split("-");
					DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[2]));
					
					Field[] f = datePickerDialog.getClass().getDeclaredFields();
					for (Field dateField : f) {
						if(dateField.getName().equals("mDatePicker")) {
							dateField.setAccessible(true);
							DatePicker datePicker = (DatePicker)dateField.get(datePickerDialog);
							Field datePickerFields[] = dateField.getType().getDeclaredFields();
							
							for(Field datePickerField : datePickerFields) {
								if("mDayPicker".equals(datePickerField.getName())) {
									datePickerField.setAccessible(true);
									Object dayPicker = new Object();
									dayPicker = datePickerField.get(datePicker);
									((View)dayPicker).setVisibility(View.GONE);
								}
							}
						}
					}
					
					datePickerDialog.show();
					
				} catch (Exception e) {
					e.printStackTrace();
				}				
				
			}
		});

		findViewById(R.id.prevYearBtn).setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				
			}
		});

		findViewById(R.id.prevMonthBtn).setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
			}
		});
		new FetchDailyDataTask().execute(intentKindParam,intentDateParam);
		
	}
	
	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			String selDate = String.format("%4d-%2d-%2d",year,monthOfYear,dayOfMonth);  
			Log.i("ondatesetlistener",selDate);
			currentDateView.setText(String.format("%4d-%2d",year,monthOfYear));
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		CookieSyncManager.getInstance().startSync();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		CookieSyncManager.getInstance().stopSync();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outstate) {
		outstate.putString("kind",intentKindParam);
		outstate.putString("date",intentDateParam);
	}

	private class FetchDailyDataTask extends AsyncTask<String, Void, String> {
		Map <String,Object> resultMap = new HashMap<String,Object>();
			
		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		
			try { 
				URL url = new URL(getString(R.string.base_url)+uri);
				Log.i("param1",params.toString());
				String [] dateStrArray = params[1].split("-");
				String postParams = "__VIEWSTATE="+URLEncoder.encode(viewstateParam,serverCharset) + 
						"&selYear=" + dateStrArray[0] + "&selMonth=" + Integer.parseInt(dateStrArray[1]) + 
								"&sKind="+params[0].toUpperCase();
				Log.i("url",postParams);

				resultMap.put("kind",params[0]);
				resultMap.put("date",params[1]);
				if ( cookieString != null ) {
					Log.i("cookie",cookieString);
					resultMap.put("html", HttpClientForParkrio.fetch(url, cookieString, postParams));
					postParams = "__VIEWSTATE="+URLEncoder.encode(viewstateParam,serverCharset) + 
							"&selYear=" + (Integer.parseInt(dateStrArray[0])-1) + "&selMonth=" + Integer.parseInt(dateStrArray[1]) + 
									"&sKind="+params[0].toUpperCase();
					resultMap.put("lastyear", HttpClientForParkrio.fetch(url, cookieString, postParams));

				} else {
					// if no-cookie then debug mode
					resultMap.put("html", readAsset(getApplicationContext(),uri));
					resultMap.put("lastyear", readAsset(getApplicationContext(),uri2));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return "OK";
		}
		
		@Override
		protected void onPostExecute(String html) {
			dismissProgressDialog();

			try {
				List<Double> parseValue = parseMonthlyValuePage(resultMap.get("html").toString());
				
				// bar chart
				setBarChart(parseValue,resultMap.get("kind").toString(),resultMap.get("date").toString());
				
				// line chart
				//setLineChart(parseValue,resultMap.get("kind").toString(),resultMap.get("date").toString());
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
			super.onPostExecute(html);
		}
	}
		private Map<String,Object> getKindInfo (String kind) {
			Map<String,Object> result = new HashMap<String,Object>();
			if ( kind.equals("elec") ) {
				result.put("title","일별 전기 사용량");
				result.put("unit", "㎾");
				result.put("name","전기");
				result.put("barColor", Color.BLUE);
			} else if ( kind.equals("hotwater")) {
				result.put("title","일별 온수 사용량");
				result.put("unit", "㎥");
				result.put("name","온수");
				result.put("barColor", Color.BLUE);
			} else if ( kind.equals("water")) {
				result.put("title","일별 수도 사용량");
				result.put("unit", "㎥");
				result.put("name","수도");
				result.put("barColor", Color.BLUE);
			} else if ( kind.equals("heat")) {
				result.put("title","일별 난방 사용량");
				result.put("unit", "㎥");
				result.put("name","난방");
				result.put("barColor", Color.BLUE);
			} else if ( kind.equals("gas")) {
				result.put("title","일별 가스 사용량");
				result.put("unit", "㎥");
				result.put("name","가스");
				result.put("barColor", Color.BLUE);
			}
			return result;
		}
		
		private void setBarChart(List<Double> parseValue,String kind, String dateString) {
			XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
			
			Map kindInfo = getKindInfo(kind);
			String[] dateStringParse = dateString.split("-");
			
			renderer.setChartTitle(Integer.parseInt(dateStringParse[1]) + "월 " + kindInfo.get("title").toString());
			renderer.setChartTitleTextSize(20);
			
			String[] titles = new String[] {kindInfo.get("name").toString()};
			int[] colors = new int[] { (Integer) kindInfo.get("barColor") };
			
			renderer.setLegendTextSize(15);
			int length = colors.length;
			
			for ( int i = 0 ; i < length ; i++) {
				SimpleSeriesRenderer r = new SimpleSeriesRenderer();
				r.setColor(colors[i]);
				renderer.addSeriesRenderer(r);
			}
			
			renderer.setXTitle("날짜");
			renderer.setYTitle(kindInfo.get("unit").toString());
			renderer.setAxisTitleTextSize(20);
			renderer.setBarSpacing(0.1);
			
			renderer.setLabelsTextSize(12);
			renderer.setXAxisMin(0.5);
			renderer.setXAxisMax(parseValue.size()+1);
			renderer.setYAxisMin(0);

			double maxValue = 0.0;
			for (int i=0;i<parseValue.size();i++) {
				if (maxValue < parseValue.get(i)) 
					maxValue = parseValue.get(i);
			}
			renderer.setYAxisMax(maxValue*1.2);
			
			renderer.setAxesColor(Color.GRAY);
			renderer.setLabelsColor(Color.WHITE);
			
			renderer.setXLabelsAlign(Align.CENTER);
			renderer.setYLabelsAlign(Align.RIGHT);
			renderer.setShowGrid(true);
			renderer.setGridColor(Color.parseColor("#c9c9c9"));
			renderer.setPanEnabled(false,false);
			renderer.setZoomEnabled(true,true);
			
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			for (int i = 0; i < titles.length; i++) {
				CategorySeries series = new CategorySeries(titles[i]);
				XYSeries dataSeries = new XYSeries("data");
				for ( int j =0 ; j < parseValue.size() ; j++ ) {
					series.add(Integer.toString(j+1),parseValue.get(j));
					dataSeries.add(j+1,parseValue.get(j));
				}
				//dataset.addSeries(series.toXYSeries());
				dataset.addSeries(dataSeries);
			}
			SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
			
			// 챠트 value 표시 설정
			r.setDisplayChartValues(true);
			r.setChartValuesTextSize(9);
			r.setChartValuesSpacing(2);
			
			// 그라데이션 설정
			r.setGradientEnabled(true);
			r.setGradientStart(0, Color.parseColor("#ffffff"));
			r.setGradientStop(maxValue*0.8, (Integer) kindInfo.get("barColor"));
			
			// 그래프 객체 생성
			GraphicalView gv = ChartFactory.getBarChartView(getApplicationContext(), dataset, renderer, Type.STACKED);
			// 그래프를 LinearLayout에 추가
			LinearLayout llBody = (LinearLayout) findViewById(R.id.chart_area);
			llBody.addView(gv);
		}




	private List<Double> parseMonthlyValuePage(String dayValue) throws Exception {
		List<Double> resultSet = new ArrayList<Double>();
		List<Date> date = new ArrayList<Date>();
		
		Source source = new Source(dayValue);
		source.fullSequentialParse();
		
		Element outerTable = source.getAllElements(HTMLElementName.TABLE).get(1).getAllElements(HTMLElementName.TABLE).get(0);

		Element outerTd = outerTable.getAllElements(HTMLElementName.TR).get(0).getAllElements(HTMLElementName.TD).get(1);
		Element innerTable = outerTd.getAllElements(HTMLElementName.TABLE).get(0);
		List innerTd = innerTable.getAllElements(HTMLElementName.TR).get(1).getAllElements(HTMLElementName.TD);

		Iterator tdIter = innerTd.iterator();

		tdIter.next();	// skip empty td
		
		while ( tdIter.hasNext() ) {
			
			Element td = (Element) tdIter.next();
			if (td.getChildElements().size() > 0) {
				Element dataTable = td.getAllElements(HTMLElementName.TABLE).get(0);
				String temp = dataTable.getAttributeValue("onmouseover");
				Pattern p = Pattern.compile("[0-9.]+");
				Matcher mc = p.matcher(temp);
				if (mc.find()) {
					resultSet.add(Double.parseDouble(mc.group(0)));
				}
			}
		}

		source.clearCache();
		return resultSet;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compare, menu);
		return true;
	}
	
	public String readAsset (Context context, String filename) {
		AssetManager am = context.getResources().getAssets();
		InputStream is = null;
		String result = null;
		try {
			is = am.open(filename);
			int size = is.available();
			
			if ( size > 0 ) {
				byte[] data = new byte[size];
				is.read(data);
				result = new String(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (Exception e) {}
			}
		}
		am = null;
		return result;
	}
	
}
