package com.hqup.jokes.asynctask;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.hqup.jokes.R;
import com.hqup.jokes.HomeActivity.EnumSource;
import com.hqup.jokes.dal.CrudDaoBean;
import com.hqup.jokes.dal.DbHelper;
import com.hqup.jokes.entity.Joke;
import com.hqup.jokes.network.Parser;
import com.hqup.jokes.utils.Logger;
import com.hqup.jokes.utils.ProgressDialoger;
import com.hqup.jokes.utils.Toaster;

public class AsyncFeederListJokes extends AsyncTask<Void, Void, List<Joke>> {

	private ProgressDialoger pd;
	private Context context;
	private CrudDaoBean crudDaoBean;
	private Parser parser;
	private EnumSource enumSource;
	private DbHelper dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;

	private long timeStart;

	private List<Joke> listJokes = new ArrayList<Joke>();
	/**
	 * It's needed for executing its own method that is described into this
	 * activity and called into AsyncTask::onPostExecute()
	 */
	private IOnTaskCompleted listener;

	/**
	 * 
	 * @param context
	 *            - application Context
	 * @param enumSource
	 *            - source for joke getting - SITE or STORAGE
	 * @param listener
	 *            - IOnTaskCompleted listener, it's needed for executing its own
	 *            method that is described into ListJOkesActivity and called
	 *            into AsyncTask::onPostExecute()
	 */
	public AsyncFeederListJokes(Context context, EnumSource enumSource,
			IOnTaskCompleted listener) {
		this.context = context;
		this.enumSource = enumSource;
		this.listener = listener;

		parser = Parser.getParser(context);

	}

	// =============Overridden AsyncTask Methods======================

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		Logger.v();
		timeStart = System.currentTimeMillis();

		pd = new ProgressDialoger(context);
		pd.showPD();

		switch (enumSource) {

		case SITE:

			break;

		case STORAGE:
			// Try to get writable DB
			dbHelper = new DbHelper(context);
			try {
				db = dbHelper.getWritableDatabase();
			} catch (SQLException e) {
				db = dbHelper.getReadableDatabase();
				e.printStackTrace();
			} catch (Exception e) {
				Toaster.doToastLong(context, R.string.toast_something_wrong);
				e.printStackTrace();
			}

			crudDaoBean = new CrudDaoBean(db);
			break;

		}

	}

	@Override
	protected List<Joke> doInBackground(Void... params) {
		Logger.v();

		switch (enumSource) {

		case SITE:
			listJokes = parser.receiveListJokeFromSite();
			Logger.time(timeStart);
			break;

		case STORAGE:
			cursor = crudDaoBean.fetchALL();
			listJokes = crudDaoBean.receiveListJokeFromCursor(cursor);
			break;
		}

		return listJokes;
	}

	@Override
	protected void onPostExecute(List<Joke> resultList) {
		Logger.v();

		switch (enumSource) {

		case SITE:

			break;

		case STORAGE:

			if (listJokes.size() == 0) {
				Toaster.doToastLong(context, R.string.toast_storage_is_empty);
			}

			if (cursor != null)
				cursor.close();
			if (db != null)
				db.close();
			break;
		}

		listener.onTaskCompleted();
		pd.dismissPD();

		Logger.time(timeStart);
	}

	// ==========Getters and Setters==================

	public List<Joke> getListJokes() {
		return listJokes;
	}

}
