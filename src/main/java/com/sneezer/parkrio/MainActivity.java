package com.sneezer.parkrio;

import android.app.Service;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AbstractAsyncActivity {
	private static String TAG = "parkrio";

	SharedPreferences preferences = getSharedPreferences("DATA_STORE", Service.MODE_PRIVATE);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.i(TAG, "onCreate");
		setContentView(R.layout.main);
		
		String username = this.preferences.getString("username","");
		String password = this.preferences.getString("password","");

		EditText inputUsername = (EditText) findViewById(R.id.useridEntry);
		inputUsername.setText(username, TextView.BufferType.EDITABLE);
		
		EditText inputPassword = (EditText) findViewById(R.id.passwordEntry);
		inputPassword.setText(password, TextView.BufferType.EDITABLE);
		
		Button loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void displayResponse(Message response) {
		Toast.makeText(this, response.getText(), Toast.LENGTH_LONG).show();
	}

	private class FetchSecuredResourceTask extends
			AsyncTask<Void, Void, Message> {
		private String username;
		private String password;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Message doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected void onPostExecute(Message result) {
			dismissProgressDialog();
			displayResponse(result);
		}

	}
}
