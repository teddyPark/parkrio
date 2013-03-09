package com.sneezer.parkrio;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

public class CompareActivity extends AbstractAsyncActivity {
	private final static String TAG = "compareActivity";
	private CookieManager cookieManager;
	private String serverHostName;
	private String cookieString;
	private String intentDateParam;
	
	private MeasurementDBAdapter dbAdapter;
	
	Map<String, Object> resultMap = new HashMap<String, Object>();
	private List<Measurement> amountMeasure = new ArrayList<Measurement>();
	private List<Measurement> usedMeasure = new ArrayList<Measurement>();
	
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

		if ( getIntent().getStringExtra("date") != null ) {
			intentDateParam = getIntent().getStringExtra("date");
		} else {
			intentDateParam = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		}
		
		final String todayString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		findViewById(R.id.elec).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startChartActivity("elec",todayString);
			}
		});

		findViewById(R.id.heat).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startChartActivity("heat",todayString);
			}
		});

		findViewById(R.id.hotwater).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startChartActivity("hotwater",todayString);			}
		});

		findViewById(R.id.gas).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startChartActivity("gas",todayString);			}
		});

		findViewById(R.id.water).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startChartActivity("water",todayString);
			}
		});
		
		dbAdapter = new MeasurementDBAdapter(this);
		dbAdapter.open();
		
		new FetchDailyDataTask().execute(intentDateParam);
	}
	
	public void startChartActivity ( String kind, String date ) {
		Intent chartIntent = new Intent(getApplicationContext(), ChartActivity_Monthly.class);
		chartIntent.putExtra("type", kind);
		chartIntent.putExtra("date", date);
		startActivity(chartIntent);
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
	
	@Override
	public void onNewIntent(Intent newIntent) {
		dbAdapter = new MeasurementDBAdapter(getApplicationContext());
		dbAdapter.open();
		
		new FetchDailyDataTask().execute(intentDateParam);
		super.onNewIntent(newIntent);
	}	

	private void setMeasurementText (String columnId, Measurement measure) {
		Class<R.id> Ids = R.id.class;
		Resources res = getResources();
		String[] typeList = new String[] {"Elec", "Gas", "Hotwater", "Water", "Heat"};
		Map<String,Double> mesu = measure.getObject();
		
		if ( measure != null ) {
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
			
		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String postParams = new String();

			try { 
				URL url = new URL(serverHostName+"/hwork/iframe_DayValue.aspx");

		        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
				java.util.Date today = format.parse(params[0]);
				// ���� ��ħ ������ ��������
				//Date today = new java.util.Date();
				
				List<String> dateList = new ArrayList<String>();
				
				dateList.add(new java.text.SimpleDateFormat("yyyy-MM-dd").format(today));	// ����
				dateList.add(new java.text.SimpleDateFormat("yyyy-MM-dd").format(today.getTime()-(long)86400*1000));	//����
				dateList.add(new java.text.SimpleDateFormat("yyyy-MM-01").format(today.getTime()));	// �̹��� 1��
				dateList.add(new java.text.SimpleDateFormat("yyyy-MM-01").format(today.getTime()-(long)86400*30*1000)); // ������ 1��
				
				for ( int i=0; i < dateList.size(); i++) {
					if ( i > 0 && dbAdapter.checkExistsData(dateList.get(i)) == 2 ) {
						amountMeasure.add(dbAdapter.getDailyData(dateList.get(i),"amount"));
						usedMeasure.add(dbAdapter.getDailyData(dateList.get(i),"used"));
						
					} else {
						HttpClientForParkrio client = new HttpClientForParkrio("daily");
						postParams = "__VIEWSTATE="	+ client.paramViewState	+ "&txtFDate=" + dateList.get(i);
						Log.i("postParams", postParams);
						
						String htmlBody = client.fetch(url, cookieString, postParams);
						
						Map<String,Measurement> parseValue = client.parseDayValuePage(htmlBody);
						
						// ���� �����ʹ� �������� ����.
						if ( i > 0 ) {
							dbAdapter.setDailyData(dateList.get(i),"amount",parseValue.get("amount"));
							dbAdapter.setDailyData(dateList.get(i),"used",parseValue.get("used"));
						}
						amountMeasure.add(parseValue.get("amount"));
						usedMeasure.add(parseValue.get("used"));
					}
				}
				
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
				Toast.makeText(getApplicationContext(), "�α׾ƿ��Ǿ����ϴ�. ��α��� �� �ּ���.", Toast.LENGTH_SHORT).show();		
				logout();
			} else if ( resultStr.equals("EXCEPTION") ) {				
				Toast.makeText(CompareActivity.this, "�����͸� �������µ� �����Ͽ����ϴ�.\n��12�ú��� ���� 1�ñ����� ������ �������� ��ȸ�� ���� ���� �� �ֽ��ϴ�.", Toast.LENGTH_LONG).show();
			} else {
				try {
					// ���� ��ħ setText
					
					//Map <String,Measurement> getData1 = parseDayValuePage(resultMap.get("today").toString());
					setMeasurementText("today", usedMeasure.get(0));
					
					// ���� ��ħ setText
					//Map <String,Measurement> getData2 = parseDayValuePage(resultMap.get("yesterday").toString());
					setMeasurementText("yesterday", usedMeasure.get(1));
					
					// �̹��� ��ħ setText
					Measurement baseMeasure1 = amountMeasure.get(2).compare(usedMeasure.get(2));	// ��ħ - ��뷮 = �ش� ��¥ 00:00
					setMeasurementText("thismonth", amountMeasure.get(0).compare(baseMeasure1));
	
					// ������ ��ħ setText
					baseMeasure1 = amountMeasure.get(3).compare(usedMeasure.get(3));	// ��ħ - ��뷮 = �ش� ��¥ 00:00
					Measurement baseMeasure2 = amountMeasure.get(2).compare(usedMeasure.get(2));	// ��ħ - ��뷮 = �ش� ��¥ 00:00
					setMeasurementText("lastmonth", baseMeasure2.compare(baseMeasure1));
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.onPostExecute(resultStr);
		}
		
	}
	
}
