/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sneezer.parkrio;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * @author Roy Clarkson
 * @author Pierre-Yves Ricau
 */
public abstract class AbstractAsyncActivity extends Activity {

	protected static final String TAG = AbstractAsyncActivity.class.getSimpleName();

	private ProgressDialog progressDialog;

	private boolean destroyed = false;

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	protected void onDestroy() {
		super.onDestroy();
		destroyed = true;
	}

	// ***************************************
	// Public methods
	// ***************************************
	public void showLoadingProgressDialog() {
		this.showProgressDialog("Loading. Please wait...");
	}

	public void showProgressDialog(CharSequence message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
		}

		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void dismissProgressDialog() {
		if (progressDialog != null && !destroyed) {
			progressDialog.dismiss();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.compare, menu);
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.add(0,1,0,"로그아웃");
		//menu.add(0,2,0,"자동로그인 해제");
		return true;
	}
	
	public void changeChart() {
		// todo
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case 1: logout();
			return true;
		case 2: changeChart();
			return true;
		}
		return false;
	}
		
	
	public boolean logout() {
		Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startIntent.putExtra("needLogin", true);
		startActivity(startIntent);
		finish();		
		return true;
	}

	public boolean cancelAutologin() {
		Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startIntent.putExtra("needLogin", true);
		startActivity(startIntent);
		finish();		
		return true;
	}
	
}
