package com.sneezer.parkrio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class CompareActivity extends Activity {
	private static String TAG = "parkrio_compare";
	private final String uri = "iframe_DayValue.aspx";
	private CookieManager cookieManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare);
		
		
		new FetchDataTask().execute();
		String dayValue = readAsset(this, uri);
		Map <String,Measurement> getData = new HashMap();
		try {
			getData = parseWebpage(dayValue);
		} catch (Exception e) {
		
		}
	}

	private class FetchDataTask extends AsyncTask <Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String base_url = getString(R.string.base_url);
			
			CookieSyncManager.createInstance(getApplicationContext());
			CookieManager cookieManager = CookieManager.getInstance();
						
			// set http client
			DefaultHttpClient httpclient = new DefaultHttpClient();
		    String cookieString = cookieManager.getCookie(base_url);
			
			Log.i(TAG,cookieString);

			return null;
		}
		
	}
	private Map<String,Measurement> parseWebpage(String dayValue) throws Exception {
		Measurement currentMeasurement = new Measurement();
		Measurement todayMeasurement = new Measurement();
		Map resultMap = new HashMap();
		
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
			int currentValue = Integer.parseInt(data.getContent().getTextExtractor().toString());

			// today use value
			tdIter.next();
			data = (Element) tdIter.next();	// fifth TD
			int todayuseValue = Integer.parseInt(data.getContent().getTextExtractor().toString());
			Log.i(TAG, currentValue+","+todayuseValue);
			
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
