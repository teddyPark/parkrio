package com.sneezer.parkrio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MeasurementDBHelper extends SQLiteOpenHelper {

	public MeasurementDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table year_data (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"YEAR INTEGER," + "JAN INTEGER," +
				"FEB INTEGER," + "MAR INTEGER," +
				"APR INTEGER," + "MAY INTEGER," +
				"JUN INTEGER," + "JUL INTEGER," +
				"AUG INTEGER," + "SEP INTEGER," +
				"OCT INTEGER," + "NOV INTEGER," +
				"DEC INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
