package com.sneezer.parkrio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MeasurementDBAdapter {
	
	private final static int DATABASE_VERSION = 1;
	private final static String DATABASE_NAME = "MEASUREMENT.db";
	private final static String TABLE_YEARLY = "yearly_data";
	private final static String TABLE_MONTHLY = "monthly_data";
	private final static String FIELDTYPE_MONTH = "REAL";
	private final static String FIELDTYPE_DAY = "REAL";
	public final static int START_YEAR = 2008;
	public final static int START_MONTH = 9;
	private final static String[] monthList = new String[] { "JAN", "FEB", "MAR", "APR", "MAY",
		"JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	private final static String[] dayList = new String[] { 
		"day1" ,"day2" ,"day3" ,"day4" ,"day5" ,"day6" ,"day7" ,"day8" ,"day9" ,"day10",
		"day11","day12","day13","day14","day15","day16","day17","day18","day19","day20",
		"day21","day22","day23","day24","day25","day26","day27","day28","day29","day30","day31" };
	
	private MeasurementDBHelper mDBHelper;
	private SQLiteDatabase mDb;

	public final Context mContext;
	
	public MeasurementDBAdapter (Context ctx) {
		this.mContext = ctx;
	}
	
	public MeasurementDBAdapter open() throws SQLException {
		mDBHelper = new MeasurementDBHelper(mContext);
		try {
			mDb = mDBHelper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public void close() {
		mDBHelper.close();
	}
	
	public class MeasurementDBHelper extends SQLiteOpenHelper {
		public MeasurementDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			// create yearly data table
			StringBuilder query = new StringBuilder();
			
			query.append("create table if not exists "+ TABLE_YEARLY + " (");
			query.append(		"Id INTEGER PRIMARY KEY AUTOINCREMENT, ");
			query.append(		"Year INTEGER, "); 
			query.append(		"Kind TEXT "); 
			for (String month : monthList) {
				query.append("," + month + " " + FIELDTYPE_MONTH);
			}
			query.append(");");

			try {
				Log.i("DB_createTable",query.toString());
				db.execSQL(query.toString());
			} catch (Exception e) {
				Log.e("DB", "can't create table "+TABLE_YEARLY);
				e.printStackTrace();
			}
	
			//db.endTransaction();
			
			query.delete(0, query.length());
			
			// create monthly data table
			query.append("create table if not exists "+ TABLE_MONTHLY +" (");
			query.append(		"Id INTEGER PRIMARY KEY AUTOINCREMENT, " );
			query.append(		"Year INTEGER, " );
			query.append(		"Month INTEGER, " );
			query.append(		"Kind TEXT ");		
			for (String day : dayList) {
				query.append("," + day + " " + FIELDTYPE_DAY);
			}
			query.append(");");
	
			try {
				Log.i("DB_createTable",query.toString());
				db.execSQL(query.toString());
			} catch (Exception e) {
				Log.e("DB", "can't create table "+TABLE_MONTHLY);
				e.printStackTrace();
			}

		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_YEARLY);
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_MONTHLY);
			Log.i("DB_onUpgrade","drop the tables");
			onCreate(db);
		}
	}
	
	public void setYearlyData (int year, String kind, List<Double> values) {

		ContentValues qryValues = new ContentValues();
		qryValues.put("Year", year);
		qryValues.put("Kind", kind);
		int index = 0;
		for ( int i=0 ; i < values.size() ; i++ ) {
			qryValues.put(monthList[i],values.get(index));
			index++;
		}
		Log.i("Qry_insertYearData",qryValues.toString());
		mDb.insert(TABLE_YEARLY, null, qryValues);
	}
	
	public List<Double> getYearlyData (int year, String kind) {
		List<Double> resultSet = new ArrayList<Double>();
		
		String selectQry = "SELECT * FROM " + TABLE_YEARLY + " WHERE year=" + year + " AND Kind='" + kind + "';";
		Cursor cursor = mDb.rawQuery(selectQry, null);
		
		if (cursor.moveToFirst()) {
			do {
				
				for (int i=3;i<cursor.getColumnCount();i++) {
					resultSet.add((double) cursor.getInt(i));
				}
			} while (cursor.moveToNext());
			
		}
		cursor.close();
		return resultSet;
	}
	
	public int checkExistsData (int year, String kind) {
		int count = 0;
		String selectQry = "SELECT * FROM " + TABLE_YEARLY + " WHERE year="+year+" AND kind='"+kind+"';";
		Log.i("checkExistsQry",selectQry);
		Cursor cursor = mDb.rawQuery(selectQry, null);
		count = cursor.getCount();
		cursor.close();
				
		return count;
	}
	
	public int checkExistsData (int year, int month, String kind) {
		int count = 0;
		String selectQry = "SELECT * FROM " + TABLE_MONTHLY + " WHERE year="+year+" AND month=" + month + " AND kind='"+kind+"';";
		Log.i("checkExistsQry",selectQry);
		Cursor cursor = mDb.rawQuery(selectQry, null);
		count = cursor.getCount();
		cursor.close();

		return count;
	}
	public void setMonthlyData (int year, int month, String kind, List<Double> values) {
		
		ContentValues qryValues = new ContentValues();
		qryValues.put("Year", year);
		qryValues.put("Month", month);
		qryValues.put("Kind", kind);
		int index = 0;
		for ( int i=0 ; i < values.size() ; i++ ) {
			qryValues.put(dayList[i],values.get(index));
			index++;
		}
		Log.i("Qry_insertMonthData",qryValues.toString());
		mDb.insert(TABLE_MONTHLY, null, qryValues);
	}
	
	public List<Double> getMonthlyData (int year, int month, String kind) {
		List<Double> resultSet = new ArrayList<Double>();
		
		// 마지막날 구하기
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, 1);
	    int lastDayOfMonth = cal.getActualMaximum(Calendar.DATE);
	    
	    // select * from monthly_data where kind='elec' ORDER BY year DESC, month DESC;
		String selectQry = "SELECT * FROM " + TABLE_MONTHLY + " WHERE Year=" + year + " AND Month=" + month + " AND Kind='" + kind + "';";
		Cursor cursor = mDb.rawQuery(selectQry, null);
		
		if (cursor.moveToFirst()) {
			do {
				for (int i=4; i < lastDayOfMonth+4; i++) {
					resultSet.add((double) cursor.getDouble(i));
				}
			} while (cursor.moveToNext());
			
		}
		cursor.close();

		return resultSet;
	
	}
}
