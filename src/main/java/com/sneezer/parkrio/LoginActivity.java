package com.sneezer.parkrio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AbstractAsyncActivity {

	private static String TAG = "parkrio";
	private SharedPreferences preferences;
	private CookieManager cookieManager;
	private String serverHostName;
	
	private String userId;
	private String userPassword;
	private boolean isRemember;
	private boolean isAutoLogin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		serverHostName = getString(R.string.base_url);

		cookieManager = CookieManager.getInstance();
		CookieSyncManager.createInstance(getApplicationContext());
		CookieSyncManager.getInstance().startSync();

		final EditText inputUsername = (EditText) findViewById(R.id.useridEntry);
		final EditText inputPassword = (EditText) findViewById(R.id.passwordEntry);
		final CheckBox rememberChk = (CheckBox) findViewById(R.id.rememberChk);
		final CheckBox autologinChk = (CheckBox) findViewById(R.id.autologinChk);

		// App description
		TextView appDesc = (TextView) findViewById(R.id.AppDescription);
		appDesc.setText("이 앱을 사용하기 위해서는\n"+serverHostName+" 로 접속하셔서\n회원가입을 하시고 관리자가 승인하여야 합니다.");
		Linkify.addLinks(appDesc, Linkify.ALL);
		appDesc.setMovementMethod(LinkMovementMethod.getInstance());
		

		findViewById(R.id.autologinChk).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (autologinChk.isChecked()) {
					rememberChk.setChecked(true);
				}
			}
		});
		
		findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userId = inputUsername.getText().toString();
				userPassword = inputPassword.getText().toString();
				isRemember = rememberChk.isChecked();
				isAutoLogin = autologinChk.isChecked();
				
				new FetchSecuredResourceTask().execute();
			}
		});
		
		findViewById(R.id.exitBtn).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});

		preferences = getSharedPreferences("USER_INFO",	MODE_PRIVATE);

		// shared preference 값을 읽어옴.
		String username = preferences.getString("username", "");
		String password = preferences.getString("password", "");
		boolean isRemember = preferences.getBoolean("isRemember", false);
		boolean isAutoLogin = preferences.getBoolean("isAutoLogin", false);
		

		// ID 입력칸은 영문 키보드로...
		inputUsername.setPrivateImeOptions("defaultInputmode=english;");

		// 저장되어 있는 userid/password 를 셋팅
		if (isRemember) {
			inputUsername.setText(username, TextView.BufferType.EDITABLE);
			inputPassword.setText(password, TextView.BufferType.EDITABLE);
			rememberChk.setChecked(isRemember);
			autologinChk.setChecked(isAutoLogin);
		}

		if (isAutoLogin && !(getIntent().getBooleanExtra("needLogin", false))) {
			new FetchSecuredResourceTask().execute();
		}
	}

	@Override
	public void showLoadingProgressDialog() {
		this.showProgressDialog("로그인 중 입니다.\n잠시만 기다려주세요...");
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
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	private class FetchSecuredResourceTask extends AsyncTask<Void, Void, String> {
		private String username;
		private String password;
		private boolean isLogon = false;
		
		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
			
			// get username / password
			EditText editText = (EditText) findViewById(R.id.useridEntry);
			this.username = editText.getText().toString();
			
			editText = (EditText) findViewById(R.id.passwordEntry);
			this.password = editText.getText().toString();
			
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			boolean isLogon = false;
			HttpResponse response = null;

			// set http client
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpost = new HttpPost(serverHostName+"/index_iframe.aspx");
			
			// set parameter
			List <NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("uid",this.username));
			requestParams.add(new BasicNameValuePair("upwd",this.password));
			
			try {
				httpost.setEntity(new UrlEncodedFormEntity(requestParams));
				httpclient.getParams().setParameter("http.connection.timeout", 3000);
				httpclient.getParams().setParameter("http.socket.timeout", 2000);
				response = httpclient.execute(httpost);
			} catch (Exception e) {
				e.printStackTrace();
	        	return "SERVER FAIL";
			}
			
			if ( response != null ) {
				HttpEntity httpEntity = response.getEntity();

				StringBuilder html = new StringBuilder();
		        try {
		        	BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
		        	while (true) {
		        		String line = br.readLine();
		        		if (line == null) break;
		        		html.append(line + '\n');
		        		Log.i(TAG, line);
		        	}
		        	br.close();
		        } catch (Exception e) {
		        	e.printStackTrace();
		        	return "SERVER FAIL";
		        }

		        if ( html.toString().indexOf("alert") == -1 ) {
		        	this.isLogon = true;
		        }
			}
			
			// success login
	        if (this.isLogon) {
	        	// save cookie value
	        	List<Cookie> cookies = httpclient.getCookieStore().getCookies();
	        	Log.i(TAG, "Cookies: " + cookies.toString());

	        	for ( int i=0 ; i< cookies.size(); i++ ) {
					if (cookies.get(i).getName().equals("ASP.NET_SessionId")) {
						String cookieString = cookies.get(i).getName()+"="+cookies.get(i).getValue() +"; path=" + cookies.get(i).getPath();
						cookieManager.setCookie(serverHostName, cookieString);
						CookieSyncManager.getInstance().sync();
					}
				}
	        }			

	        return "OK";
		}

		@Override
		protected void onPostExecute(String result) {
			dismissProgressDialog();
			
			if ( isLogon ) {
				if (isRemember) {
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("username", userId);
					editor.putString("password", userPassword);
					editor.putBoolean("isRemember", isRemember);
					editor.putBoolean("isAutoLogin", isAutoLogin);
						
					editor.commit();
				}
				
				Intent compareIntent = new Intent(getApplicationContext(), CompareActivity.class);
				String todayString = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
				compareIntent.putExtra("date",todayString);
				compareIntent.putExtra("userId",userId);
				startActivity(compareIntent);
				finish();
			} else {
				if ( result.equals("SERVER FAIL")) {
					Toast.makeText(getApplicationContext(), "일시적으로 서버 접속이 안되고 있습니다.\n잠시 후 이용해 주세요..", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "로그인 실패.\nID/비밀번호를 확인하세요.", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
