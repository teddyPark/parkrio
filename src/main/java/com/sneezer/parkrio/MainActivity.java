package com.sneezer.parkrio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import android.view.View;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AbstractAsyncActivity {
	private static final boolean DEBUG = false;

	private static String TAG = "parkrio";

	private EditText inputUsername;
	private EditText inputPassword;

	private SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		// Log.i(TAG, "onCreate");
		setContentView(R.layout.main);

		if (DEBUG) {
			String base_url = getString(R.string.base_url);
			final CookieManager cookieManager = CookieManager.getInstance();
			CookieSyncManager.createInstance(getApplicationContext());
			
			String cookieString = "ASP.NET_SessionId=asdasdfasdfasdfasdf; path=/";
			cookieManager.setCookie(base_url, cookieString);
			CookieSyncManager.getInstance().sync();
			
			Intent compareIntent = new Intent(getApplicationContext(), CompareActivity.class);
			compareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(compareIntent);
		}
		final SharedPreferences preferences = getSharedPreferences("USER_INFO",	MODE_PRIVATE);

		String username = preferences.getString("username", "");
		String password = preferences.getString("password", "");
		boolean isRemember = preferences.getBoolean("isRemember", false);

		final EditText inputUsername = (EditText) findViewById(R.id.useridEntry);
		final EditText inputPassword = (EditText) findViewById(R.id.passwordEntry);
		final CheckBox rememberChk = (CheckBox) findViewById(R.id.rememberChk);

		// 저장되어 있는 userid/password 를 셋팅
		if (isRemember) {
			inputUsername.setText(username, TextView.BufferType.EDITABLE);
			inputPassword.setText(password, TextView.BufferType.EDITABLE);
			rememberChk.setChecked(isRemember);
		}

		Button loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (((CheckBox) findViewById(R.id.rememberChk)).isChecked()) {

					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("username", inputUsername.getText().toString());
					editor.putString("password", inputPassword.getText().toString());
					editor.putBoolean("isRemember", rememberChk.isChecked());
					editor.commit();
				}
				new FetchSecuredResourceTask().execute();

			}
		});

		Button exitBtn = (Button) findViewById(R.id.exitBtn);
		exitBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void displayResponse(Message response) {
		Toast.makeText(this, response.getText(), Toast.LENGTH_LONG).show();
	}

	private class FetchSecuredResourceTask extends AsyncTask<Void, Void, Message> {
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
		protected Message doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String base_url = getString(R.string.base_url);
			boolean isLogon = false;
			HttpResponse response = null;
			
			final CookieManager cookieManager = CookieManager.getInstance();
			CookieSyncManager.createInstance(getApplicationContext());

			// get username / password
			EditText editText = (EditText) findViewById(R.id.useridEntry);
			this.username = editText.getText().toString();
			editText = (EditText) findViewById(R.id.passwordEntry);
			this.password = editText.getText().toString();

			// set http client
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpost = new HttpPost(base_url+"index_iframe.aspx");
			
			// set parameter
			List <NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("uid",this.username));
			requestParams.add(new BasicNameValuePair("upwd",this.password));
			
			try {
				((HttpPost) httpost).setEntity(new UrlEncodedFormEntity(requestParams));
				response = httpclient.execute(httpost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
						cookieManager.setCookie(base_url, cookieString);
						CookieSyncManager.getInstance().sync();
					}
				}
	        }			
	        return null;
		}

		@Override
		protected void onPostExecute(Message result) {
			dismissProgressDialog();
			
			if ( isLogon ) {
				Intent compareIntent = new Intent(getApplicationContext(), CompareActivity.class);
				compareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(compareIntent);
			} else {
				Toast.makeText(getApplicationContext(), "로그인 실패. ID/비밀번호를 확인하세요.", Toast.LENGTH_LONG).show();
			}

		}

	}
}
