package com.hqup.jokes.dal;

import com.hqup.jokes.utils.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "dbJoke";
	public static final String DB_TABLE = "tableJoke";
	private static final int DB_VERSION = 4;

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NUMBER = "joke_number";
	public static final String COLUMN_TEXT = "joke_text";

	private static final String DB_CREATE = "create table " + DB_TABLE + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NUMBER + " text, " + COLUMN_TEXT + " text not null unique " + ");";

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		Logger.v();
	}

	/*
	 * Called when the database is created for the first time
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Logger.v();
		db.execSQL(DB_CREATE);
	}

	/*
	 * Called when the database needs to be upgraded. The implementation should
	 * use this method to drop tables, add tables, or do anything else it needs
	 * to upgrade to the new schema version
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (oldVersion < newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			onCreate(db);

		}
		Logger.v();

	}

}
