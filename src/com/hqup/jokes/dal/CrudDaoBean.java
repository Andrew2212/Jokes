package com.hqup.jokes.dal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hqup.jokes.entity.Joke;
import com.hqup.jokes.utils.Logger;

/**
 * 
 * @author Andrew2212 </br> This class is used to provide access to insert and
 *         delete joke from/to DB and clearing DB
 */
public class CrudDaoBean {

	private SQLiteDatabase db;
	private String where;
	private String[] toTake;
	private Joke joke;

	/**
	 * Defines what we want to do by CrudDaoBean
	 */
	public enum EnumAction {
		FIND, FETCH_ALL, INSERT, DELETE, CLEAR_DB
	}

	/**
	 * 
	 * @param context
	 *            Context for Toaster::DB is Empty!
	 * @param db
	 *            SQLiteDatabase
	 */
	public CrudDaoBean(SQLiteDatabase db) {
		this.db = db;
	}

	public Cursor fetchALL() {
		Logger.v();
		Cursor cursor = db.query(DbHelper.DB_TABLE, null, null, null, null,
				null, null);
		return cursor;
	}

	/**
	 * 
	 * @param enumAction
	 *            Defines what we want to do by CrudDaoBean
	 * @param joke
	 * @return Cursor cursor != null only if enumAction == FETCH_ALL or FIND ,
	 *         other action return null after execution
	 */
	public Cursor execute(EnumAction enumAction, Joke joke) {

		this.joke = joke;
		Cursor cursor = null;

		if (db == null) {
			Logger.v("DB == " + db);
			return null;
		}

		switch (enumAction) {

		case FIND:
			// TODO switch case FIND
			break;

		case FETCH_ALL:
			cursor = fetchALL();
			break;

		case INSERT:
			insert(joke);
			break;

		case DELETE:
			delete(joke);
			break;

		case CLEAR_DB:
			clearDB();
			break;
		}

		return cursor;
	}

	/**
	 * 
	 * @return List sorted by ABC listJokes from current 'cursor'</br> If cursor
	 *         == null then listJokes == null
	 */
	public List<Joke> receiveListJokeFromCursor(Cursor cursor) {

		if (cursor == null)
			return null;

		List<Joke> listJokes = new ArrayList<Joke>();
		while (cursor.moveToNext()) {

			String number = cursor.getString(cursor
					.getColumnIndex(DbHelper.COLUMN_NUMBER));
			String text = cursor.getString(cursor

			.getColumnIndex(DbHelper.COLUMN_TEXT));
			listJokes.add(new Joke(number, text));
		}

		if (listJokes.size() != 0) {
			Collections.sort(listJokes, Joke.byAbcComparator);
		}

		return listJokes;
	}

	// ==============Private Methods=============

	@SuppressWarnings("unused")
	private Cursor find(Joke joke) {
		// TODO find Joke
		Logger.v();
		return null;
	}

	private void insert(Joke joke) {

		if (isJokeNull(joke))
			return;

		ContentValues cv = new ContentValues();
		putValues(cv);

		// It's the row ID of the newly inserted row, or -1 if an error
		// occurred
		long resultID = db.insert(DbHelper.DB_TABLE, null, cv);
		Logger.v("resultID = " + resultID);

	}

	private void delete(Joke joke) {

		if (isJokeNull(joke))
			return;

		where = " joke_number = ?";
		toTake = new String[] { joke.getNumber() };
		/*
		 * It's the number of rows affected if a whereClause is passed in, 0
		 * otherwise. To remove all rows and get a count pass "1" as the
		 * whereClause
		 */
		int result = db.delete(DbHelper.DB_TABLE, where, toTake);
		Logger.v("result = " + result);

	}

	private void clearDB() {
		/*
		 * It's the number of rows affected if a whereClause is passed in, 0
		 * otherwise. To remove all rows and get a count pass "1" as the
		 * whereClause
		 */
		where = "1";
		int result = db.delete(DbHelper.DB_TABLE, where, null);
		Logger.v("result = " + result);
	}

	/**
	 * 
	 * @param cv
	 *            ContentValues (this class is used to store a set of values)
	 *            </br> 'cv' adds a value of current 'joke' to the set.
	 */
	private void putValues(ContentValues cv) {

		Logger.v();
		cv.put(DbHelper.COLUMN_NUMBER, joke.getNumber());
		cv.put(DbHelper.COLUMN_TEXT, joke.getText());
	}

	private boolean isJokeNull(Joke joke) {

		boolean result = (joke == null);
		if (result)
			Logger.v("Current joke == null! It's::" + result);

		Logger.v("Current joke = " + joke);
		return result;
	}

}
