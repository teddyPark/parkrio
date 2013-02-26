package com.sneezer.parkrio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.params.*;



import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class CompareActivity extends AbstractAsyncActivity {
	private static String TAG = "parkrio_compare";
	private final String uri = "iframe_DayValue.aspx";
	private CookieManager cookieManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare);
		
		
		String todayString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		new FetchDailyDataTask().execute(todayString);
		
		/*
		String dayValue = readAsset(this, uri);
		Map <String,Measurement> getData = new HashMap();
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

	private void SetText (String columnId, Measurement mesurement) {
		Class<R.id> Ids = R.id.class;
		Resources res = getResources();
		String[] typeList = new String[] {"Elect", "Gas", "Hotwater", "Water", "Heat"};
		Map<String,Float> mesu = mesurement.getObject();
		
		if ( mesurement != null ) {
			try {
				for (String type : typeList) {				
					int resId = res.getIdentifier("com.sneezer.parkrio:id/"+columnId+type, null, null);
					((TextView) findViewById(resId)).setText(Float.toString(mesu.get(type)));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class FetchDailyDataTask extends AsyncTask<String, Void, String> {
		Map <String, String> htmls = new HashMap<String, String>();
		
			
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
				
				GregorianCalendar gc = new GregorianCalendar();
				
				
				Date today = new java.util.Date();
				String dateString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
				String postParams = "__VIEWSTATE="+URLEncoder.encode("/wEPDwUKMTExNDI5NDgyMmRkxn5PRekHb9BgiR+zMd0FnM0Fa5I=","EUC-KR") + "&txtFDate=" + dateString;
				htmls.put("today", HttpClientForParkrio.fetch(url, cookieManager.getCookie(base_url), postParams));
		
			    Date yesterday = new Date(new java.util.Date().getTime()-(long)86400*1000);
			    
				dateString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date().getTime()-(long)86400*1000);
				Log.i("date",dateString);
				postParams = "__VIEWSTATE="+URLEncoder.encode("/wEPDwUKMTExNDI5NDgyMmRkxn5PRekHb9BgiR+zMd0FnM0Fa5I=","EUC-KR") + "&txtFDate=" + dateString;
				Log.i("params",postParams);
				htmls.put("yesterday", HttpClientForParkrio.fetch(url, cookieManager.getCookie(base_url), postParams));

				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return html.toString();
		}
		
		@Override
		protected void onPostExecute(String html) {
			dismissProgressDialog();
			Measurement todayMeasument = new Measurement();			
			Map <String,Measurement> getData = new HashMap();
			try {
				getData = parseDayValuePage(htmls.get("today"));
				SetText("today", getData.get("today"));
				Log.i("htmls",this.htmls.get("yesterday"));
				getData = parseDayValuePage(htmls.get("yesterday"));
				
				SetText("yesterday", getData.get("today"));

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
