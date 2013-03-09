package com.sneezer.parkrio;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class CompareActivity extends AbstractAsyncActivity {
	private final static String TAG = "compareActivity";
	private CookieManager cookieManager;
	private String serverHostName;
	private String cookieString;
	
	Map<String, Object> resultMap = new HashMap<String, Object>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare);
		this.serverHostName = getString(R.string.base_url);
		
		CookieSyncManager.createInstance(this);
		cookieManager = CookieManager.getInstance();
		CookieSyncManager.getInstance().startSync();
		cookieString = cookieManager.getCookie(serverHostName);
		Log.i("oncreate",cookieManager.getCookie(serverHostName));

		String intentDateParam = new String();
		if ( getIntent().getStringExtra("date") != null ) {
			intentDateParam = getIntent().getStringExtra("date");
		} else {
			intentDateParam = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		}
		
		final String todayString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		findViewById(R.id.elect).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent chartIntent = new Intent(getApplicationContext(), ChartActivity_Monthly.class);
				chartIntent.putExtra("type", "elec");
				chartIntent.putExtra("date",todayString);
				startActivity(chartIntent);
			}
		});

		findViewById(R.id.heat).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent chartIntent = new Intent(getApplicationContext(), ChartActivity_Monthly.class);
				chartIntent.putExtra("kind", "heat");
				chartIntent.putExtra("date",todayString);
				startActivity(chartIntent);
			}
		});

		findViewById(R.id.hotwater).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent chartIntent = new Intent(getApplicationContext(), ChartActivity_Monthly.class);
				chartIntent.putExtra("kind", "hotwater");
				chartIntent.putExtra("date",todayString);
				startActivity(chartIntent);
			}
		});

		findViewById(R.id.gas).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent chartIntent = new Intent(getApplicationContext(), ChartActivity_Monthly.class);
				chartIntent.putExtra("kind", "gas");
				chartIntent.putExtra("date",todayString);
				startActivity(chartIntent);
			}
		});

		findViewById(R.id.water).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent chartIntent = new Intent(getApplicationContext(), ChartActivity_Monthly.class);
				chartIntent.putExtra("kind", "water");
				chartIntent.putExtra("date",todayString);
				startActivity(chartIntent);
			}
		});
		
		new FetchDailyDataTask().execute(intentDateParam);
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
			String postParams = new String();

			try { 
				URL url = new URL(serverHostName+"/hwork/iframe_DayValue.aspx");
				
				java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
				java.util.Date today = format.parse(params[0]);
				// 금일 검침 데이터 가져오기
				//Date today = new java.util.Date();
				HttpClientForParkrio client = new HttpClientForParkrio("dayly");
				String dateString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(today);
				postParams = "__VIEWSTATE="
						+ URLEncoder.encode(client.paramViewState, client.SERVER_CHARSET)
						+ "&txtFDate=" + dateString;
				Log.i("url", postParams);
				resultMap.put("today", client.fetch(url, cookieString, postParams));
	
			    // 전일 검침 데이터 가져오기
				client = new HttpClientForParkrio("dayly");
				dateString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(today.getTime()-(long)86400*1000);
				postParams = "__VIEWSTATE="
						+ URLEncoder.encode(client.paramViewState, client.SERVER_CHARSET)
						+ "&txtFDate=" + dateString;
				Log.i("params",postParams);
				resultMap.put("yesterday", client.fetch(url, cookieString, postParams));

				// 금월 1일 데이터 가져오기
				client = new HttpClientForParkrio("dayly");
				dateString = new java.text.SimpleDateFormat("yyyy-MM-01").format(today.getTime());
				postParams = "__VIEWSTATE="
						+ URLEncoder.encode(client.paramViewState, client.SERVER_CHARSET)
						+ "&txtFDate=" + dateString;
				Log.i("params",postParams);
				resultMap.put("thismonth_firstday", client.fetch(url, cookieString, postParams));
				
				// 전월 1일 데이터 가져오기
				client = new HttpClientForParkrio("dayly");
				dateString = new java.text.SimpleDateFormat("yyyy-MM-01").format(today.getTime()-(long)86400*30*1000);
				postParams = "__VIEWSTATE="
						+ URLEncoder.encode(client.paramViewState, client.SERVER_CHARSET)
						+ "&txtFDate=" + dateString;
				Log.i("params",postParams);
				resultMap.put("lastmonth_firstday", client.fetch(url, cookieString, postParams));

			} catch (LogoutException e) {
				return "LOGOUT";
			} catch (Exception e) {
				e.printStackTrace();
				return "EXCEPTION";
			}
			return "OK";
		}
		
		@Override
		protected void onPostExecute(String resultStr) {
			dismissProgressDialog();

			if ( resultStr.equals("LOGOUT") ) {
				Toast.makeText(getApplicationContext(), "로그아웃되었습니다. 재로그인 해 주세요.", Toast.LENGTH_SHORT).show();		
				logout();
			} else if ( resultStr.equals("EXCEPTION") ) {				
				Toast.makeText(CompareActivity.this, "데이터를 가져오는데 실패하였습니다.\n밤12시부터 새벽 1시까지는 데이터 취합으로 조회가 되지 않을 수 있습니다.", Toast.LENGTH_LONG).show();
			} else {
				try {
					// 금일 검침 setText
					Map <String,Measurement> getData1 = parseDayValuePage(resultMap.get("today").toString());
					setMeasurementText("today", getData1.get("today"));
					
					// 전일 검침 setText
					Map <String,Measurement> getData2 = parseDayValuePage(resultMap.get("yesterday").toString());
					setMeasurementText("yesterday", getData2.get("today"));
					
					// 이번달 검침 setText
					Map <String,Measurement> getData3 = parseDayValuePage(resultMap.get("thismonth_firstday").toString());
					Log.i("today",getData3.get("current").toString());
					Log.i("firstday",getData1.get("current").toString());
					setMeasurementText("thismonth", getData1.get("current").compare(getData3.get("current")));
	
					// 지난달 검침 setText
					Map <String,Measurement> getData4 = parseDayValuePage(resultMap.get("lastmonth_firstday").toString());
					Log.i("today",getData4.get("current").toString());
					Log.i("firstday",getData3.get("current").toString());
					setMeasurementText("lastmonth", getData3.get("current").compare(getData4.get("current")));
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.onPostExecute(resultStr);
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
				currentMeasurement.setElec(currentValue);
				todayMeasurement.setElec(todayuseValue);
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
	
}
