package com.sneezer.parkrio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;

import org.springframework.web.client.RestTemplate;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AbstractAsyncActivity {
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

		final SharedPreferences preferences = getSharedPreferences("USER_INFO",
				MODE_PRIVATE);
		final CookieManager cookieManager = CookieManager.getInstance();

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
					Toast.makeText(getApplicationContext(), "committed",Toast.LENGTH_LONG).show();
				}
				// Toast.makeText(getApplicationContext(),
				// "FetchSecuredResourceTask", Toast.LENGTH_LONG).show();
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

		@Override
		protected void onPreExecute() {
			String base_url = getString(R.string.base_url);

			EditText editText = (EditText) findViewById(R.id.useridEntry);
			this.username = editText.getText().toString();
			editText = (EditText) findViewById(R.id.passwordEntry);
			this.password = editText.getText().toString();

			//Toast.makeText(getApplicationContext(),"id=" + this.username + "/pwd=" + this.password,Toast.LENGTH_LONG).show();

			/*
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			//String response = restTemplate.getForObject(base_url, String.class,	"");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.TEXT_HTML);
			Map<String,String> reqParams = new HashMap<String, String>();
			reqParams.put("uid",this.username);
			reqParams.put("upwd", this.password);
			
			HttpEntity<Map> requestEntity = new HttpEntity<Map>(reqParams, headers);
			//HttpEntity<String> httpEntity = restTemplate.exchange(base_url, HttpMethod.POST, requestEntity, String.class);
			HttpEntity<String> httpEntity = restTemplate.postForObject(base_url, requestEntity, HttpEntity.class);
			HttpHeaders responseHeader = httpEntity.getHeaders();
			
			//http://cherrykyun.tistory.com/archive/201204?page=1
			List<String> cookies = responseHeader.get("Set-Cookie");
			*/
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpost = new HttpPost(base_url);
			List <NameValuePair> requestParams = new ArrayList<NameValuePair>();
			requestParams.add(new BasicNameValuePair("uid",this.username));
			requestParams.add(new BasicNameValuePair("upwd",this.password));
			//httpost.setEntity(new UrlEncodedFormEntity(requestParams, "euc-kr"));
			
			HttpResponse response = null;
			try {
				response = httpclient.execute(httpost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpEntity entity = response.getEntity();
			List<Cookie> cookies = httpclient.getCookieStore().getCookies();
			Toast.makeText(getApplicationContext(),	cookies.toString(), Toast.LENGTH_LONG).show();
			
		}

		@Override
		protected Message doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(Message result) {
			// dismissProgressDialog();
			// displayResponse(result);

		}

	}
}
