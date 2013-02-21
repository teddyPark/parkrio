package com.sneezer.parkrio;

import android.os.Bundle;

public class MainActivity extends AbstractAsyncActivity {
	private static String TAG = "parkrio";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.i(TAG, "onCreate");
		setContentView(R.layout.main);

	}
}
