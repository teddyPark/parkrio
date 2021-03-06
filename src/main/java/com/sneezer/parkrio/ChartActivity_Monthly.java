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
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class ChartActivity_Monthly extends AbstractAsyncActivity implements OnGestureListener {
	private final static String DEFAULT_CHART = "lineChart";
	private final static int COUNT_DISPLAY_CHART = 2; 
	private final static String TAG = "MonthChartActivity";
	
	private CookieManager cookieManager;
	private SharedPreferences preferences;
	private String cookieString;
	private String serverHostName;
	private MeasurementDBAdapter dbAdapter;
	
	private String kind;
	private String dateString;
	private TextView currentDateView;
	private String chartType;

	private String userId;
	private int mYear;
	private int mMonth;
	private int mDay;
	private DatePickerDialog datePickerDialog;
	
	private GestureDetector ges;
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private static final int[] chartColorSet = new int[] { Color.RED, Color.BLUE, Color.MAGENTA, Color.GREEN };
	private static final PointStyle[] chartPointStyleSet = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND, PointStyle.TRIANGLE, PointStyle.SQUARE };
	
	private double maxYValue = 0.0; 
	private int maxXValue = 0;
	private XYMultipleSeriesDataset dataset;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart_monthly);

		CookieSyncManager.createInstance(getApplicationContext());
		cookieManager = CookieManager.getInstance();
		serverHostName = getString(R.string.base_url);
		cookieString = cookieManager.getCookie(serverHostName);

		preferences = getSharedPreferences("USER_INFO",	MODE_PRIVATE);
		if ( savedInstanceState != null) {
			userId = savedInstanceState.getString("userId");
			kind = savedInstanceState.getString("kind");
			dateString = savedInstanceState.getString("dateString");
			chartType = savedInstanceState.getString("chartType");
			mYear = savedInstanceState.getInt("mYear");
			mMonth = savedInstanceState.getInt("mMonth");
			mDay = savedInstanceState.getInt("mDay");
		} else {
			this.userId=getIntent().getStringExtra("userId");
			
			if (getIntent().getStringExtra("kind") != null) {
				kind = getIntent().getStringExtra("kind");
			} else {
				kind = "elec";
			}
			if (getIntent().getStringExtra("date") != null) {
				dateString = getIntent().getStringExtra("date");
			} else {
				dateString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
			}
			String[] dateStrArray = dateString.split("-");
			mYear = Integer.parseInt(dateStrArray[0]);
			mMonth = Integer.parseInt(dateStrArray[1]);
			mDay = 1;
			chartType = preferences.getString("chartType", DEFAULT_CHART);
		}
		Log.i("userId",userId);

		currentDateView = (TextView) findViewById(R.id.currentDateEntry);
		currentDateView.setText(mYear + "-" + mMonth);

		currentDateView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					Log.i("setOnClickListener", "called");
					datePickerDialog = new DatePickerDialog(ChartActivity_Monthly.this,	dateSetListener, mYear, mMonth - 1, 1);
					datePickerDialog.setTitle("년/월 선택");

					Field[] f = datePickerDialog.getClass().getDeclaredFields();

					for (Field dateField : f) {
						if (dateField.getName().equals("mDatePicker")) {
							dateField.setAccessible(true);
							DatePicker datePicker = (DatePicker) dateField.get(datePickerDialog);
							Field datePickerFields[] = dateField.getType().getDeclaredFields();

							for (Field datePickerField : datePickerFields) {
								if ("mDaySpinner".equals(datePickerField.getName())	|| "mDayPicker".equals(datePickerField.getName())) {
									datePickerField.setAccessible(true);
									Object dayPicker = new Object();
									dayPicker = datePickerField.get(datePicker);
									((View) dayPicker).setVisibility(View.GONE);
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

		findViewById(R.id.prevYearBtn).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View arg0) {
						calDate(-12);
						updateDateDisplay();
					}
				});

		findViewById(R.id.prevMonthBtn).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View arg0) {
						calDate(-1);
						updateDateDisplay();
					}
		});

		findViewById(R.id.nextMonthBtn).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View arg0) {
						calDate(1);
						updateDateDisplay();
					}
		});

		findViewById(R.id.nextYearBtn).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View arg0) {
						calDate(12);
						updateDateDisplay();
					}
		});
		findViewById(R.id.yearlyChartBtn).setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View arg0) {
						Intent chartIntent = new Intent(ChartActivity_Monthly.this, ChartActivity_Yearly.class);
						chartIntent.putExtra("kind", kind);
						chartIntent.putExtra("userId",userId);
						chartIntent.putExtra("date", Integer.toString(mYear)+"-"+Integer.toString(mMonth));
						startActivity(chartIntent);
					}
		});
		
		ges = new GestureDetector(this);

		dbAdapter = new MeasurementDBAdapter(this,this.userId);
		dbAdapter.open();
		
		new FetchDailyDataTask().execute(kind, dateString);
	}

	public void calDate(int termMonth) {
		java.util.Date date = new java.util.Date();
		try {
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
			date = format.parse(String.format("%4d-%2d-%2d", mYear, mMonth,mDay));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(c.MONTH, termMonth);

		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH) + 1;
		mDay = c.get(Calendar.DATE);

		String selDate = String.format("%4d-%2d-%2d", mYear, mMonth, mDay);
		Log.i("onDateSetListener", selDate);

	}

	@Override
	public void onNewIntent(Intent newIntent) {
		Intent receivedIntent = getIntent();
		chartType = preferences.getString("chartType", DEFAULT_CHART);

		dbAdapter = new MeasurementDBAdapter(getApplicationContext(),this.userId);
		dbAdapter.open();
		new FetchDailyDataTask().execute(kind, dateString);
		super.onNewIntent(newIntent);
	}

	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

			if (year != mYear || monthOfYear + 1 != mMonth || dayOfMonth != mDay) {
				mYear = year;
				mMonth = monthOfYear + 1;
				mDay = dayOfMonth;
				String selDate = String.format("%4d-%2d-%2d", mYear, mMonth, mDay);
				Log.i("onDateSetListener", selDate);
				updateDateDisplay();
			}
		}

	};

	private void updateDateDisplay() {
		Log.i("updateDateDisplay", "called");
		currentDateView.setText(new StringBuilder().append(mYear).append("-").append(mMonth));
		Intent chartIntent = new Intent(getApplicationContext(),ChartActivity_Monthly.class);
		chartIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(chartIntent);
	}

	@Override
	protected void onResume() {
		Log.i("onResume", "called");
		super.onResume();
		CookieSyncManager.getInstance().startSync();
	}

	@Override
	protected void onPause() {
		Log.i("onPause", "called");
		super.onPause();
		CookieSyncManager.getInstance().stopSync();
	}

	@Override
	protected void onSaveInstanceState(Bundle outstate) {
		outstate.putString("userId", userId);
		outstate.putString("kind", kind);
		outstate.putString("date", dateString);
		outstate.putString("chartType",chartType);
		outstate.putInt("mYear", mYear);
		outstate.putInt("mMonth", mMonth);
		outstate.putInt("mDay", mDay);

		super.onSaveInstanceState(outstate);
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
			dataset = new XYMultipleSeriesDataset();

			try {
				
				if (cookieString != null) {
					Log.i("cookie", cookieString);

					String postParams = new String();
					String htmlBody = new String();
					HttpClientForParkrio client;
					// 과거 데이터 DB insert
					Calendar cal = Calendar.getInstance();
					for ( int i=0 ; i < COUNT_DISPLAY_CHART ; i++ ) {
						if ( dbAdapter.checkExistsData((mYear-i), mMonth, kind) > 0 ) {
							// DB 에 있다면 읽어온다.
							addXYSeries(dbAdapter.getMonthlyData(mYear-i, mMonth, kind),String.format("%4d-%02d", mYear-i, mMonth),0);
						} else {
							// 서버에서 가져온다.
							client = new HttpClientForParkrio("monthly");
							URL url = new URL(getString(R.string.base_url) + client.uri);
/*							postParams = "__VIEWSTATE=" + client.paramViewState
									+ "&selYear=" + (mYear-i) + "&selMonth=" + mMonth
									+ "&sKind=" + kind.toUpperCase();
*/							
							postParams = "selYear=" + (mYear-i) + "&selMonth=" + mMonth
									+ "&sKind=" + kind.toUpperCase();
							Log.i("url", postParams);
							
							htmlBody = client.fetch(url, cookieString, postParams);
							
							// get values from htmlBody;
							addXYSeries(htmlBody,String.format("%4d-%02d", mYear-i, mMonth),0);
							
							if ( mYear-i != cal.get(Calendar.YEAR)) {
								// 현재 달의 값은 DB 에 저장하지 않는다. 계속 변하고 있기 때문에...
								dbAdapter.setMonthlyData(mYear-i, mMonth, kind, HttpClientForParkrio.parseMonthlyDataFromHtml(htmlBody));
							}
						}
						if ( chartType.equals("barChart") )
							break;
					}
				} else {
					// if no-cookie then debug mode
					addXYSeries(HttpClientForParkrio.readParkrioAsset(getApplicationContext(), HttpClientForParkrio.MONTHLY_URI),String.format("%4d-%2d", mYear, mMonth),0);
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
			dbAdapter.close();
			if (resultStr == "LOGOUT") {
				Toast.makeText(getApplicationContext(), "로그아웃되었습니다. 재로그인 해 주세요.", Toast.LENGTH_SHORT).show();
				logout();
			} else if (resultStr == "EXCEPTION") {
				Toast.makeText(getApplicationContext(), "알 수 없는 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
			} else {
				try {
					if ( chartType.equals("barChart") ) {
						drawBarChart();
					} else {
						drawLineChart();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
				
			super.onPostExecute(resultStr);
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("onCreateOptionsMenu","called");
		MenuItem item = menu.add(0,1,0,"로그아웃");
		menu.add(0,2,0,"챠트 바꾸기");
		menu.add(0,3,0,"Cache data 삭제");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case 1: createLogoutDialogBox().show();
			return true;
		case 2: changeChart();
			return true;
		case 3: createCacheClearDialogBox().show();
			return true;
		}
		return false;
	}
	
	@Override
	public void changeChart() {
		SharedPreferences.Editor editor = preferences.edit();
		if ( chartType.equals("barChart") ) {
			editor.putString("chartType", "lineChart");
		} else {
			editor.putString("chartType", "barChart");
		}
		editor.commit();
		updateDateDisplay();
	}
	
	private Map<String, Object> getKindAttribute(String kind) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (kind.equals("elec")) {
			result.put("title", "일별 전기 사용량");
			result.put("unit", "㎾");
			result.put("name", "전기");
			result.put("barColor", Color.BLUE);
		} else if (kind.equals("hotwater")) {
			result.put("title", "일별 온수 사용량");
			result.put("unit", "㎥");
			result.put("name", "온수");
			result.put("barColor", Color.BLUE);
		} else if (kind.equals("water")) {
			result.put("title", "일별 수도 사용량");
			result.put("unit", "㎥");
			result.put("name", "수도");
			result.put("barColor", Color.BLUE);
		} else if (kind.equals("heat")) {
			result.put("title", "일별 난방 사용량");
			result.put("unit", "㎥");
			result.put("name", "난방");
			result.put("barColor", Color.BLUE);
		} else if (kind.equals("gas")) {
			result.put("title", "일별 )가스 사용량");
			result.put("unit", "㎥");
			result.put("name", "가스");
			result.put("barColor", Color.BLUE);
		}
		return result;
	}

	private void drawBarChart() throws Exception {
		
		Map kindInfo = getKindAttribute(kind);

		int [] colors = new int[dataset.getSeriesCount()];
		PointStyle[] styles = new PointStyle[dataset.getSeriesCount()];
		for ( int j =0 ; j < dataset.getSeriesCount() ; j++) {
			colors[j] = chartColorSet[j];
			styles[j] = chartPointStyleSet[j];
		}
		
	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	    renderer.setChartTitle(Integer.toString(mYear) + "년 "
	    		+ Integer.toString(mMonth) + "월 "
	    		+ kindInfo.get("title").toString());
	    renderer.setChartTitleTextSize(30);
	    // set margin (top,left,bottom,right
	    renderer.setMargins(new int[] { 40, 25, 15, 15 });
	    renderer.setMarginsColor(Color.parseColor("#E4E4E4"));
	    renderer.setLegendTextSize(15);

	    int length = renderer.getSeriesRendererCount();
	    for (int i = 0; i < length; i++) {
	      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
	    }
	    
		renderer.setXTitle("날짜");
		renderer.setYTitle(kindInfo.get("unit").toString());
		renderer.setAxisTitleTextSize(20);
		renderer.setBarSpacing(0.1);

		renderer.setLabelsTextSize(18);
		renderer.setXAxisMin(0);
		renderer.setYAxisMin(0);
	    
		SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);

		// 챠트 value 표시 설정
		r.setDisplayChartValues(true);
		r.setChartValuesTextSize(9);
		r.setChartValuesSpacing(2);

		// 그라데이션 설정
		r.setGradientEnabled(true);
		r.setGradientStart(0, Color.parseColor("#ffffff"));
		r.setGradientStop(maxYValue * 0.8, (Integer) kindInfo.get("barColor"));

		// for LOG
		Log.i("CountOfDataSet",Integer.toString(dataset.getSeriesCount()));
		for ( int j =0 ; j < dataset.getSeriesCount() ; j++) {
			XYSeries temp = dataset.getSeriesAt(j);
			Log.i("title",temp.getTitle());
			Log.i("scale",Integer.toString(temp.getScaleNumber()));
			StringBuilder sb = new StringBuilder();
			for ( int i=0;i< temp.getItemCount() ;i++) {
				sb.append(Double.toString(temp.getX(i))+"="+Double.toString(temp.getY(i))+", ");
			}
			Log.i("XY value",sb.toString());
		}
		
		// 그래프 객체 생성
		GraphicalView gv = ChartFactory.getBarChartView(ChartActivity_Monthly.this, dataset, renderer, Type.STACKED);
		// 그래프를 LinearLayout에 추가
		LinearLayout llBody = (LinearLayout) findViewById(R.id.chart_area);
		llBody.removeAllViewsInLayout();
		llBody.addView(gv);
	}
	
	private void drawLineChart() throws Exception {

		Map kindInfo = getKindAttribute(kind);

		int [] colors = new int[dataset.getSeriesCount()];
		PointStyle[] styles = new PointStyle[dataset.getSeriesCount()];
		for ( int j =0 ; j < dataset.getSeriesCount() ; j++) {
			colors[j] = chartColorSet[j];
			styles[j] = chartPointStyleSet[j];
		}

	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	    renderer.setChartTitle(Integer.toString(mYear) + "년 "
	    		+ Integer.toString(mMonth) + "월 "
	    		+ kindInfo.get("title").toString());
	    renderer.setChartTitleTextSize(30);
	    // set margin (top,left,bottom,right
	    renderer.setMargins(new int[] { 40, 30, 40, 15 });
	    renderer.setMarginsColor(Color.parseColor("#E4E4E4"));
	    renderer.setLegendTextSize(15);
	    renderer.setChartTitleTextSize(30);

	    int length = renderer.getSeriesRendererCount();
	    for (int i = 0; i < length; i++) {
	      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
	    }
	    
		renderer.setXTitle("날짜");
		renderer.setYTitle(kindInfo.get("unit").toString());
		renderer.setAxisTitleTextSize(28);
		renderer.setShowAxes(true);		
		renderer.setBarSpacing(0.1);

		renderer.setLabelsTextSize(23);
		renderer.setXAxisMin(0.5);
		renderer.setYAxisMin(0);

		SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);

		// 챠트 value 표시 설정
		r.setDisplayChartValues(true);
		r.setChartValuesTextSize(9);
		r.setChartValuesSpacing(2);
		
		// for LOG
		Log.i("CountOfDataSet",Integer.toString(dataset.getSeriesCount()));
		for ( int j =0 ; j < dataset.getSeriesCount() ; j++) {
			XYSeries temp = dataset.getSeriesAt(j);
			Log.i("title",temp.getTitle());
			Log.i("scale",Integer.toString(temp.getScaleNumber()));
			StringBuilder sb = new StringBuilder();
			for ( int i=0;i< temp.getItemCount() ;i++) {
				sb.append(Double.toString(temp.getX(i))+"="+Double.toString(temp.getY(i))+", ");
			}
			Log.i("XY value",sb.toString());
		}
		// 그래프 객체 생성
		GraphicalView gv = ChartFactory.getLineChartView(getApplicationContext(), dataset, renderer);
		// 그래프를 LinearLayout에 추가
		LinearLayout llBody = (LinearLayout) findViewById(R.id.chart_area);
		llBody.removeAllViewsInLayout();
		llBody.addView(gv);
	}

	protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
		renderer.setAxisTitleTextSize(20);
		renderer.setChartTitleTextSize(30);
		renderer.setLabelsTextSize(18);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(3f);
		renderer.setMargins(new int[] { 40, 25, 15, 15 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
		renderer.setAxesColor(Color.BLACK);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setYLabelsColor(0,Color.BLACK);
		renderer.setShowGrid(true);
		renderer.setShowGridX(true);
		renderer.setShowLegend(true);
		renderer.setAntialiasing(true);
		renderer.setGridColor(Color.parseColor("#c9c9c9"));
		renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(true, true);
		renderer.setXAxisMax(maxXValue + 1);
		renderer.setYAxisMax(maxYValue * 1.2);
	}
	
	protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}

	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues, List<double[]> yValues, int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
	}
	
	public void addXYSeries (List<Double> valueList, String title, int scale ) throws Exception {
		XYSeries series = new XYSeries(title, scale);
		if ( valueList.size() > maxXValue ) {
			maxXValue = valueList.size();
		}
		for (int i = 0; i < valueList.size(); i++) {
			series.add((double)i+1,valueList.get(i));
			if (valueList.get(i) > maxYValue) {
				maxYValue = valueList.get(i);
			}
		}
		dataset.addSeries(series);		
	}

	private void addXYSeries (String html, String title, int scale) throws Exception {
		List<Double> valueList = new ArrayList<Double>();

		valueList = HttpClientForParkrio.parseMonthlyDataFromHtml(html);
		
		if ( valueList.size() > maxXValue ) {
			maxXValue = valueList.size();
		}
		XYSeries series = new XYSeries(title, scale);
		for (int i = 0; i < valueList.size(); i++) {
			series.add((double)i+1,valueList.get(i));
			if (valueList.get(i) > maxYValue) {
				maxYValue = valueList.get(i);
			}
		}
		
		dataset.addSeries(series);

	}
	
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent me)
    {
        return ges.onTouchEvent(me);
    }
	
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY) {
		// TODO Auto-generated method stub
		if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
            return false;
         
        // 오른쪽->왼쪽
        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
        {
            Toast.makeText(getApplicationContext(), "왼쪽으로", Toast.LENGTH_SHORT).show();
        }
        // 왼쪽->오른쪽
        else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
        {
            Toast.makeText(getApplicationContext(), "오른쪽으로", Toast.LENGTH_SHORT).show();
        }
		return false;
	}

	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
