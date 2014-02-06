package com.hqup.jokes.asynctask;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.hqup.jokes.R;
import com.hqup.jokes.dal.CrudDaoBean;
import com.hqup.jokes.dal.CrudDaoBean.EnumAction;
import com.hqup.jokes.dal.DbHelper;
import com.hqup.jokes.entity.Joke;
import com.hqup.jokes.utils.Logger;
import com.hqup.jokes.utils.ProgressDialoger;
import com.hqup.jokes.utils.Toaster;

/**
 * 
 * @author Andrew2212 </br>This class executes actions with DB values by using
 *         CrudDaoBean within doInBackground()
 * 
 */
public class AsyncCrudExecutor extends AsyncTask<Void, Void, List<Joke>> {

	private ProgressDialoger pd;
	private CrudDaoBean crudDaoBean;
	private List<Joke> listJokes;
	private SQLiteDatabase db;
	private Cursor cursor;
	private Joke joke;
	private Context context;
	private EnumAction enumAction;

	/**
	 * 
	 * @param context
	 *            Context
	 * @param joke
	 *            Joke for specified action
	 * @param enumAction
	 *            EnumAction specified action
	 * @param listener
	 *            IOnTaskCompleted
	 */
	public AsyncCrudExecutor(Context context, Joke joke, EnumAction enumAction
	// , IOnTaskCompleted listener
	) {
		this.context = context;
		this.joke = joke;
		this.enumAction = enumAction;
		// this.listener = listener;
	}

	// =============Overridden AsyncTask Methods======================

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Logger.v();
		pd = new ProgressDialoger(context);
		pd.showPD();

		// Try to get writable DB
		DbHelper dbHelper = new DbHelper(context);
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLException e) {
			db = dbHelper.getReadableDatabase();
			e.printStackTrace();
		} catch (Exception e) {
			Toaster.doToastLong(context, R.string.toast_something_wrong);
			e.printStackTrace();
		}

		// Initialize 'crudDaoBean'
		crudDaoBean = new CrudDaoBean(db);
	}

	@Override
	protected List<Joke> doInBackground(Void... params) {
		// Execute specified action
		cursor = crudDaoBean.execute(enumAction, joke);

		if (cursor != null) {
			// Get listJokes if previous statement return not null
			listJokes = crudDaoBean.receiveListJokeFromCursor(cursor);
		}
		return listJokes;
	}

	@Override
	protected void onPostExecute(List<Joke> resultList) {
		Logger.v();

		if (cursor != null)
			cursor.close();
		if (db != null)
			db.close();

		pd.dismissPD();
	}

	// ==========Getters and Setters==================

	public List<Joke> getListJokes() {
		return listJokes;
	}

}
