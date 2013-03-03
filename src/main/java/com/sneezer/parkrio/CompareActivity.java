package com.sneezer.parkrio;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class CompareActivity extends AbstractAsyncActivity {
	private static String TAG = "compareActivity";
	private CookieManager cookieManager;
	private String base_url;
	private String cookieString;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare);
		this.base_url = getString(R.string.base_url);
		
		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		CookieSyncManager.getInstance().startSync();
		this.cookieString = cookieManager.getCookie(base_url);
		Log.i("oncreate",cookieManager.getCookie(base_url));

		String intentDateParam = new String();
		if ( getIntent().getStringExtra("date") != null ) {
			intentDateParam = getIntent().getStringExtra("date");
		} else {
			intentDateParam = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		}
		
		new FetchDailyDataTask().execute(intentDateParam);
		
		final String todayString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		findViewById(R.id.elect).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent chartIntent = new Intent(getApplicationContext(), ChartActivity.class);
				chartIntent.putExtra("type", "elec");
				chartIntent.putExtra("date",todayString);
				startActivity(chartIntent);
			}
		});

		findViewById(R.id.heat).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent chartIntent = new Intent(getApplicationContext(), ChartActivity.class);
				chartIntent.putExtra("kind", "heat");
				chartIntent.putExtra("date",todayString);
				startActivity(chartIntent);
			}
		});

		findViewById(R.id.hotwater).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent chartIntent = new Intent(getApplicationContext(), ChartActivity.class);
				chartIntent.putExtra("kind", "hotwater");
				chartIntent.putExtra("date",todayString);
				startActivity(chartIntent);
			}
		});

		findViewById(R.id.gas).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent chartIntent = new Intent(getApplicationContext(), ChartActivity.class);
				chartIntent.putExtra("kind", "gas");
				chartIntent.putExtra("date",todayString);
				startActivity(chartIntent);
			}
		});

		findViewById(R.id.water).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent chartIntent = new Intent(getApplicationContext(), ChartActivity.class);
				chartIntent.putExtra("kind", "water");
				chartIntent.putExtra("date",todayString);
				startActivity(chartIntent);
			}
		});
		
	}
	
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
	
	private void setMeasurementText (String columnId, Measurement mesurement) {
		Class<R.id> Ids = R.id.class;
		Resources res = getResources();
		String[] typeList = new String[] {"Elect", "Gas", "Hotwater", "Water", "Heat"};
		Map<String,Float> mesu = mesurement.getObject();
		
		if ( mesurement != null ) {
			try {
				for (String type : typeList) {				
					int resId = res.getIdentifier("com.sneezer.parkrio:id/"+columnId+type, null, null);
					String valueStr = String.format("%.2f",mesu.get(type));
					((TextView) findViewById(resId)).setText(valueStr);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			String html = new String();

			try { 
				URL url = new URL(base_url+"hwork/iframe_DayValue.aspx");
				
				java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
				java.util.Date today = format.parse(params[0]);
				
				// 금일 검침 데이터 가져오기
				//Date today = new java.util.Date();
				String dateString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(today);
				String postParams = "__VIEWSTATE="+URLEncoder.encode(viewstateParam,serverCharset) + "&txtFDate=" + dateString;
				htmls.put("today", HttpClientForParkrio.fetch(url, cookieString, postParams));
	
			    // 전일 검침 데이터 가져오기
				dateString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(today.getTime()-(long)86400*1000);
				Log.i("date",dateString);
				postParams = "__VIEWSTATE="+URLEncoder.encode(viewstateParam,serverCharset) + "&txtFDate=" + dateString;
				Log.i("params",postParams);
				htmls.put("yesterday", HttpClientForParkrio.fetch(url, cookieString, postParams));

				// 금월 1일 데이터 가져오기
				dateString = new java.text.SimpleDateFormat("yyyy-MM-01").format(today.getTime());
				Log.i("date",dateString);
				postParams = "__VIEWSTATE="+URLEncoder.encode(viewstateParam,serverCharset) + "&txtFDate=" + dateString;
				Log.i("params",postParams);
				htmls.put("thismonth_firstday", HttpClientForParkrio.fetch(url, cookieString, postParams));

				// 전월 1일 데이터 가져오기
				dateString = new java.text.SimpleDateFormat("yyyy-MM-01").format(today.getTime()-(long)86400*30*1000);
				Log.i("date",dateString);
				postParams = "__VIEWSTATE="+URLEncoder.encode(viewstateParam,serverCharset) + "&txtFDate=" + dateString;
				Log.i("params",postParams);
				htmls.put("lastmonth_firstday", HttpClientForParkrio.fetch(url, cookieString, postParams));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return html.toString();
		}
		
		@Override
		protected void onPostExecute(String html) {
			dismissProgressDialog();

			try {
				// 금일 검침 setText
				Map <String,Measurement> getData1 = parseDayValuePage(htmls.get("today"));
				setMeasurementText("today", getData1.get("today"));
				
				// 전일 검침 setText
				Map <String,Measurement> getData2 = parseDayValuePage(htmls.get("yesterday"));
				setMeasurementText("yesterday", getData2.get("today"));
				
				// 이번달 검침 setText
				Map <String,Measurement> getData3 = parseDayValuePage(htmls.get("thismonth_firstday"));
				Log.i("today",getData3.get("current").toString());
				Log.i("firstday",getData1.get("current").toString());
				setMeasurementText("thismonth", getData1.get("current").compare(getData3.get("current")));

				// 지난달 검침 setText
				Map <String,Measurement> getData4 = parseDayValuePage(htmls.get("lastmonth_firstday"));
				Log.i("today",getData4.get("current").toString());
				Log.i("firstday",getData3.get("current").toString());
				setMeasurementText("lastmonth", getData3.get("current").compare(getData4.get("current")));
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
			super.onPostExecute(html);
		}
		
	}

	private Map<String,Measurement> parseDayValuePage(String dayValue) throws Exception {
		Measurement currentMeasurement = new Measurement();
		Measurement todayMeasurement = new Measurement();
		Map<String,Measurement> resultMap = new HashMap<String,Measurement>();
		
		Source source = new Source(dayValue);
		source.fullSequentialParse();
		
		Element table = source.getAllElements(HTMLElementName.TABLE).get(0);
		List trList = table.getAllElements(HTMLElementName.TR);
		Iterator trIter = trList.iterator();
		
		trIter.next();
		trIter.next();
		trIter.next();
 
		for ( int i = 0 ; i < 5 ; i++ ) {
			Element tr = (Element) trIter.next();
			List dataList = tr.getAllElements(HTMLElementName.TD);
			Iterator tdIter = dataList.iterator();
			
			//current total value
			tdIter.next();
			tdIter.next();
			Element data = (Element) tdIter.next();	// third TD
			float currentValue = Float.parseFloat(data.getContent().getTextExtractor().toString());

			// today use value
			tdIter.next();
			data = (Element) tdIter.next();	// fifth TD
			float todayuseValue = Float.parseFloat(data.getContent().getTextExtractor().toString());
			
			if (i == 0) {
				// 전기
				currentMeasurement.setElect(currentValue);
				todayMeasurement.setElect(todayuseValue);
			} else if ( i == 1 ) {
				// 수도
				currentMeasurement.setWater(currentValue);
				todayMeasurement.setWater(todayuseValue);
			} else if ( i == 2 ) {
				// 온수
				currentMeasurement.setHotwater(currentValue);
				todayMeasurement.setHotwater(todayuseValue);
			} else if ( i == 3 ) {
				// 가스
				currentMeasurement.setGas(currentValue);
				todayMeasurement.setGas(todayuseValue);
			} else if ( i == 4 ) {
				// 난방
				currentMeasurement.setHeat(currentValue);
				todayMeasurement.setHeat(todayuseValue);
			}
			 
			trIter.next();				
		}

		resultMap.put("current",currentMeasurement);
		resultMap.put("today",todayMeasurement);
		source.clearCache();
		return resultMap;
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
