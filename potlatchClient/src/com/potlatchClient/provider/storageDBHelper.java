package com.potlatchClient.provider;

import android.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;



public class storageDBHelper extends SQLiteOpenHelper {

	private static final String tag = storageDBHelper.class.getCanonicalName();

	private static SQLiteDatabase db;
	
	static final String DATABASE_NAME = "test";
	static final int DATABASE_VERSION = 1;
	
	static final String CREATE_TABLE_GIFT = " CREATE TABLE " + dataContract.TABLE_GIFT +
			   " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			   "ownerid TEXT NOT NULL, " +
			   "title TEXT NOT NULL, " +
			   "description TEXT, " +
			   "type TEXT NOT NULL, " +
			   "counter_touched INTEGER, " +
			   "counter_inprop INTEGER, " +
			   "counter_obscene INTEGER, " +
			   "src_url TEXT, " +
			   " thumb_url TEXT);";

	static final String CREATE_TABLE_TOUCHCOUNT = " CREATE TABLE " + dataContract.TABLE_TOUCHCOUNT +
				" (id INTEGER PRIMARY KEY, " +
				" title TEXT NOT NULL, " +
				" counter_touched INTEGER);";
	
	static final String CREATE_TABLE_USER = " CREATE TABLE " + dataContract.TABLE_USER +
			" (id TEXT NOT NULL," +
			" ownerid TEXT NOT NULL," +
			" touched INTEGER," +
			" inappropriate INTEGER," +
			" obscene INTEGER); "; 
	
	public storageDBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public storageDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i(tag  , "onCreate");

		db.execSQL(CREATE_TABLE_GIFT);		
		db.execSQL(CREATE_TABLE_TOUCHCOUNT);
		db.execSQL(CREATE_TABLE_USER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public SQLiteDatabase getDB()
	{
		return db;
	}
	

	
	

}
