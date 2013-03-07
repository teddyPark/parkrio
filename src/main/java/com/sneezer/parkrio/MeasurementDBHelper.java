package com.sneezer.parkrio;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MeasurementDBHelper extends SQLiteOpenHelper {
	private final static int DATABASE_VERSION = 1;
	private final static String DATABASE_NAME = "MEASUREMENT";
	private final static String TABLE_YEARLY = "yearly_data";
	private final static String TABLE_MONTHLY = "monthly_data";
	private final static String FIELDTYPE_MONTH = "REAL";
	private final static String FIELDTYPE_DAY = "REAL";
	
	private final static String[] monthList = new String[] { "JAN", "FEB", "MAR", "APR", "MAY",
			"JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	private final static String[] dayList = new String[] { 
		"day1" ,"day2" ,"day3" ,"day4" ,"day5" ,"day6" ,"day7" ,"day8" ,"day9" ,"day10",
		"day11","day12","day13","day14","day15","day16","day17","day18","day19","day20",
		"day21","day22","day23","day24","day25","day26","day27","day28","day29","day30","day31" };
	
	public MeasurementDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		StringBuilder query = new StringBuilder();
		
		query.append("create table "+ TABLE_YEARLY + " (");
		query.append(		"Id INTEGER PRIMARY KEY AUTOINCREMENT, ");
		query.append(		"Year INTEGER "); 
		for (String month : monthList) {
			query.append("," + month + " " + FIELDTYPE_MONTH);
		}
		query.append(")");
		db.execSQL(query.toString());

		//
		query.append("create table "+ TABLE_MONTHLY +" (");
		query.append(		"Id INTEGER PRIMARY KEY AUTOINCREMENT, " );
		query.append(		"Year INTEGER, " );
		query.append(		"Month INTEGER " );
		for (String day : dayList) {
			query.append("," + day + " " + FIELDTYPE_DAY);
		}
		query.append(")");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_YEARLY);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_MONTHLY);
		onCreate(db);
	}
	
	public void  setYearlyData (int year, List<Double> values) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues qryValues = new ContentValues();
		qryValues.put("Year", year);
		int index = 0;
		for ( String month : monthList ) {
			qryValues.put(month,values.get(index));
			index++;
		}
		db.insert(TABLE_YEARLY, null, qryValues);
		db.close();		
	}
	
	public List<Double> getYearlyData (int year) {
		List<Double> resultSet = new ArrayList<Double>();
		
		String selectQry = "SELECT * FROM " + TABLE_YEARLY;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQry, null);
		
		if (cursor.moveToFirst()) {
			do {
				
				for (int i=2;i<cursor.getColumnCount();i++) {
					resultSet.add((double) cursor.getInt(i));
				}
			} while (cursor.moveToNext());
			
		}
		db.close();
		return resultSet;
	}
	
	public int checkExistsData (int year) {
		String selectQry = "SELECT * FROM " + TABLE_YEARLY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQry, null);
		cursor.close();
		db.close();
		
		return cursor.getCount();
	}
	
	public boolean checkExistsData (int year, int month) {
		return false;
	}
	public void setMonthlyData (int year, int month, List<Double> values) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues qryValues = new ContentValues();
		qryValues.put("Year", year);
		qryValues.put("Month", month);
		int index = 0;
		for ( String day : dayList ) {
			qryValues.put(day,values.get(index));
			index++;
		}
		db.insert(TABLE_MONTHLY, null, qryValues);
		db.close();
	}
	
	public List<Double> getMonthlyData (int year) {
		List<Double> resultSet = new ArrayList<Double>();
		
		String selectQry = "SELECT * FROM " + TABLE_MONTHLY;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQry, null);
		
		if (cursor.moveToFirst()) {
			do {
				for (int i=3; i<cursor.getColumnCount(); i++) {
					resultSet.add((double) cursor.getDouble(i));
				}
			} while (cursor.moveToNext());
			
		}
		db.close();
		return resultSet;
	}
}
