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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
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
	private MeasurementDBAdapter dbAdapter;
	private String userId;
	
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
		menu.add(0,2,0,"Cache data 삭제");
		return true;
	}
	
	public void changeChart() {
		// todo
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case 1: createLogoutDialogBox().show();
			return true;
		case 2: createCacheClearDialogBox().show();
			return true;
		}
		return false;
	}
		
	public void onClickCacheClear() {
		
		dbAdapter = new MeasurementDBAdapter(this,this.userId);
		dbAdapter.open();
		dbAdapter.resetCacheData();
		dbAdapter.close();
	}
	
	public AlertDialog createLogoutDialogBox() {
		AlertDialog logoutDialogBox = new AlertDialog.Builder(this)
		.setTitle("안내")
		.setMessage("로그아웃하시겠습니까?")
		.setPositiveButton("예",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				logout();
			}
		})
		.setNeutralButton("아니요",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
								
			}
		})
		.create();
		return logoutDialogBox;
	}
	
	public AlertDialog createCacheClearDialogBox() {
		AlertDialog cacheClearDialogBox = new AlertDialog.Builder(this)
		.setTitle("안내")
		.setMessage("임시데이터는 조회했던 과거자료를 저장하고 있습니다.\n임시저장 데이터를 삭제하시겠습니까?")
		.setPositiveButton("예",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				onClickCacheClear();
			}
		})
		.setNeutralButton("아니요",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
								
			}
		})
		.create();
		
		return cacheClearDialogBox;
	}
	
	public boolean logout() {
		Intent startIntent = new Intent(getApplicationContext(), LoginActivity.class);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startIntent.putExtra("needLogin", true);
		startActivity(startIntent);
		finish();		
		return true;
	}

	public boolean cancelAutologin() {
		Intent startIntent = new Intent(getApplicationContext(), LoginActivity.class);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startIntent.putExtra("needLogin", true);
		startActivity(startIntent);
		finish();		
		return true;
	}

	protected void onSavedInstranceState(Bundle outState) {
		// TODO Auto-generated method stub
		
	}
	
}
