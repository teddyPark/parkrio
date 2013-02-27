package com.sneezer.parkrio;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;


import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class ChartActivity extends AbstractAsyncActivity {
	private static String TAG = "graphActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);
		
		//String intentParam = getIntent().getStringExtra("type");
		//Log.i("type",intentParam);
		
		String todayString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		new FetchDailyDataTask().execute(todayString);
		

		
		//Log.i("chart", assetContent);
		//Map <String,Measurement> getData = new HashMap();
		/*
		try {
			getData = parseDayValuePage(dayValue);
			todayMeasument = getData.get("today");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i(TAG,todayMeasument.toString());
		SetText("today", todayMeasument);
		*/
	}

	private class FetchDailyDataTask extends AsyncTask<String, Void, String> {
		Map <String, String> htmls = new HashMap<String, String>();
		private final String viewstateParam = "/wEPDwUKMTExNDI5NDgyMmRkxn5PRekHb9BgiR+zMd0FnM0Fa5I=";
		private final String serverCharset = "EUC-KR";
			
		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String base_url = getString(R.string.base_url);
			String html = new String();
			
			CookieSyncManager.createInstance(getApplicationContext());
			CookieManager cookieManager = CookieManager.getInstance();
			try { 
				URL url = new URL(getString(R.string.base_url)+"hwork/iframe_DayValue.aspx");
				/*
				// 금일 검침 데이터 가져오기
				Date today = new java.util.Date();
				String dateString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
				String postParams = "__VIEWSTATE="+URLEncoder.encode(viewstateParam,serverCharset) + "&txtFDate=" + dateString;
				htmls.put("today", HttpClientForParkrio.fetch(url, cookieManager.getCookie(base_url), postParams));
	*/
				htmls.put("thismonth",readAsset(getApplicationContext(),"iframe_MonthGraph.aspx"));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return html.toString();
		}
		
		@Override
		protected void onPostExecute(String html) {
			dismissProgressDialog();

			try {
				Double[] parseValue = parseMonthlyValuePage(htmls.get("thismonth"));
				
				for ( int i =0 ; i < parseValue.length ; i++ ) {
					Log.i("setText",Double.toString(parseValue[i]));
				}
				
				XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
				renderer.setChartTitle("전기 사용량");
				renderer.setChartTitleTextSize(20);
				
				String[] titles = new String[] {"전기"};
				int[] colors = new int[] { Color.BLUE };
				
				renderer.setLegendTextSize(15);
				int length = colors.length;
				
				for ( int i = 0 ; i < length ; i++) {
					SimpleSeriesRenderer r = new SimpleSeriesRenderer();
					r.setColor(colors[i]);
					renderer.addSeriesRenderer(r);
				}
				
				renderer.setXTitle("날짜");
				renderer.setYTitle("KW");
				renderer.setAxisTitleTextSize(12);
				
				renderer.setLabelsTextSize(10);
				renderer.setXAxisMin(0.5);
				renderer.setXAxisMax(12.5);
				renderer.setYAxisMin(0);
				renderer.setYAxisMax(100);
				
				renderer.setAxesColor(Color.WHITE);
				renderer.setLabelsColor(Color.CYAN);
				
				renderer.setXLabelsAlign(Align.LEFT);
				renderer.setYLabelsAlign(Align.LEFT);
				
				renderer.setPanEnabled(false,false);
				renderer.setZoomEnabled(true,true);
				
				XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
				for (int i = 0; i < titles.length; i++) {
					CategorySeries series = new CategorySeries(titles[i]);
					for ( int j =0 ; j < parseValue.length ; j++ ) {
						series.add(parseValue[j]);
						Log.i("setText",Double.toString(parseValue[j]));
					}
					dataset.addSeries(series.toXYSeries());
				}
				// 그래프 객체 생성
				GraphicalView gv = ChartFactory.getBarChartView(getApplicationContext(), dataset,
						renderer, Type.STACKED);

				// 그래프를 LinearLayout에 추가
				LinearLayout llBody = (LinearLayout) findViewById(R.id.chart_area);
				llBody.addView(gv);
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
			super.onPostExecute(html);
		}
		
	}

	private Double[] parseMonthlyValuePage(String dayValue) throws Exception {
		Double[] values = new Double[40];
		
		Source source = new Source(dayValue);
		source.fullSequentialParse();
		
		Element outerTable = source.getAllElements(HTMLElementName.TABLE).get(1).getAllElements(HTMLElementName.TABLE).get(0);

		Element outerTd = outerTable.getAllElements(HTMLElementName.TR).get(0).getAllElements(HTMLElementName.TD).get(1);
		Element innerTable = outerTd.getAllElements(HTMLElementName.TABLE).get(0);
		List innerTd = innerTable.getAllElements(HTMLElementName.TR).get(1).getAllElements(HTMLElementName.TD);

		Iterator tdIter = innerTd.iterator();

		tdIter.next();	// skip empty td
		
		int count = 0;
		while ( tdIter.hasNext() ) {
			
			Element td = (Element) tdIter.next();
			if (td.getChildElements().size() > 0) {
				Element dataTable = td.getAllElements(HTMLElementName.TABLE).get(0);
				String temp = dataTable.getAttributeValue("onmouseover");
				Log.i("table attr",temp);
				Pattern p = Pattern.compile("[0-9.]+");
				Matcher mc = p.matcher(temp);
				if (mc.find()) {
					values[count] = Double.parseDouble(mc.group(0));
					count++;
				}
			}
		}

		source.clearCache();
		return values;
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
