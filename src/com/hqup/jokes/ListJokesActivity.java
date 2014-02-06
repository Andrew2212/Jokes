package com.hqup.jokes;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hqup.jokes.HomeActivity.EnumSource;
import com.hqup.jokes.adapters.ArrayAdapterListJokes;
import com.hqup.jokes.asynctask.AsyncCrudExecutor;
import com.hqup.jokes.asynctask.AsyncFeederListJokes;
import com.hqup.jokes.asynctask.IOnTaskCompleted;
import com.hqup.jokes.dal.CrudDaoBean.EnumAction;
import com.hqup.jokes.entity.Joke;
import com.hqup.jokes.network.NetworkStateChecker;
import com.hqup.jokes.network.Parser;
import com.hqup.jokes.preferencers.Preferencer;
import com.hqup.jokes.sender.Sender;
import com.hqup.jokes.utils.AlertDialoger;
import com.hqup.jokes.utils.Logger;
import com.hqup.jokes.utils.Sounder;

public class ListJokesActivity extends Activity implements OnClickListener {

	private ListView lvData;
	private TextView tvTitle;
	// private LinearLayout ltBtn;
	private Button btnPreferences;
	private Button btnPrev;
	private Button btnNext;

	private final int SAVE_TO_STORAGE = 1;
	private final int DELETE_FROM_STORAGE = 2;
	private final int SEND_AS_SMS = 3;

	private EnumSource enumSource;
	private List<Joke> listJokes = new ArrayList<Joke>();

	private ArrayAdapterListJokes adapterListJokes;
	private AsyncFeederListJokes asyncFeederList;

	private Context context;

	/**
	 * It's needed for executing its own method that is described into this
	 * activity and called into AsyncTask::onPostExecute()
	 */
	private IOnTaskCompleted listener = new IOnTaskCompleted() {

		@Override
		public void onTaskCompleted() {

			Logger.v();
			try {
				listJokes = asyncFeederList.getListJokes();
			} catch (Throwable th) {
				Logger.i("***OutOfMemory!****");
				th.printStackTrace();
			}

			// Load value of 'listJokes' into ListView
			loadValueToListView();

			// Set on tvTitle siteInfo about currentPage
			setModeFromSiteOptions();

			/*
			 * setTextColor and setClickable on btnNext (go to next page) in
			 * accordance with whether pageCurrent is pageMain of site or not
			 */
			setBtnNextOptions();
			/*
			 * setTextColor and setClickable on btnPrev (go to previous page) in
			 * accordance with whether pageCurrent is pageFirst of site or not
			 */
			setBtnPrevOptions();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list_jokes);

		Logger.v();

		getEnumSource();
		init();
		btnPrev.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnPreferences.setOnClickListener(this);
		context = this;

		// Initialize feeder for listJokes and filling List<Joke>
		fillListJokes();

		// Registers a context menu to be shown for the given view (multiple
		// views can show the context menu).
		registerForContextMenu(lvData);

	}

	@Override
	protected void onResume() {
		super.onResume();

		Logger.v();
		/*
		 * It's in order to use existing 'listJokes' without new parsing
		 */
		if (listJokes.size() != 0)
			loadValueToListView();

		/*
		 * Set int 'delta' for Parser::increase/decrease pageCurrent number
		 */
		Parser.setPageDelta(Preferencer.getDeltaPage(context));

	}

	@Override
	protected void onStop() {

		if (isFinishing()) {
			Parser.setIsFirstParsingDone(false);
			asyncFeederList = null;
		}

		super.onStop();
	}

	// ==========Public overridden Methods======================

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_ListJokes_Prev:

			Sounder.doSound(context, R.raw.beep);
			// Check whether NetConnection exists or not
			if (!new NetworkStateChecker(this).checkNetConnection()) {
				break;
			}

			Parser.decreaseCurrentPage();
			fillListJokes();
			break;

		case R.id.btn_ListJokes_Next:

			Sounder.doSound(context, R.raw.beep);
			// Check whether NetConnection exists or not
			if (!new NetworkStateChecker(this).checkNetConnection()) {
				break;
			}

			Parser.increaseCurrentPage();
			fillListJokes();
			break;

		case R.id.btn_ListJokes_Preferences:
			Sounder.doSound(context, R.raw.beep_notify);
			Intent intent = new Intent(this, PreferencesActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if (enumSource == EnumSource.SITE) {
			menu.add(0, SAVE_TO_STORAGE, 0,
					getResources().getString(R.string.menu_ctx_save_to_storage));
		}

		if (enumSource == EnumSource.STORAGE) {
			menu.add(
					0,
					DELETE_FROM_STORAGE,
					0,
					getResources().getString(
							R.string.menu_ctx_delete_from_storage));
		}

		menu.add(0, SEND_AS_SMS, 0,
				getResources().getString(R.string.menu_ctx_send_as_sms));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo adapterInfo = null;
		adapterInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		long itemId = adapterInfo.id;

		int position = adapterInfo.position;

		Logger.v("Save to storage itemID = " + itemId + ", position = "
				+ position);

		Joke joke = listJokes.get((int) itemId);

		switch (item.getItemId()) {

		case SAVE_TO_STORAGE:
			Logger.v();
			Sounder.doSound(context, R.raw.beep_notify);

			AsyncCrudExecutor asyncCrud = new AsyncCrudExecutor(this, joke,
					EnumAction.INSERT);
			asyncCrud.execute();
			break;

		case DELETE_FROM_STORAGE:
			Logger.v();
			Sounder.doSound(context, R.raw.beep);
			deleteJokeFromStorage(joke);
			break;

		case SEND_AS_SMS:
			Logger.v();
			Sounder.doSound(context, R.raw.beep);
			Sender.sendSms(context, joke);
			break;

		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	// ===========Private Methods======================

	/**
	 * Calls </br> asyncFeederList = new AsyncFeederListJokes(this, enumSource,
	 * listener);</br> asyncFeederList.execute();</br> </br> Parsing of the site
	 * or iterating DB Cursor and receive listJokes</br> by
	 * 'AsyncTasker::receiveJokeFrom_Site/Cursor_()' within
	 * AsyncTask::doInBackground() </br>and getting List of Joke by
	 * 'listenerOfAsyncTasker::onTaskCompleted()' within
	 * AsyncTask::onPostExecute()
	 */
	private void fillListJokes() {
		Logger.v();
		asyncFeederList = new AsyncFeederListJokes(this, enumSource, listener);
		asyncFeederList.execute();

	}

	/**
	 * Creates adapter and loads value of 'listJokes' into ListView
	 */
	private void loadValueToListView() {

		// Initialize Adapter and set values into ListView
		adapterListJokes = new ArrayAdapterListJokes(getApplicationContext(),
				R.layout.list_item, listJokes);

		// Try to set fontSize to TextView 'tvText'
		adapterListJokes.setFontSize(Preferencer.getFontSize(context));
		// Try to set fontColor to TextView 'tvText'
		adapterListJokes.setFontColor(Preferencer.getFontColor(context));

		lvData.setAdapter(adapterListJokes);

	}

	/**
	 * 
	 * @return enumSource that defines joke source i.e. whether SITE or STORAGE
	 */
	private EnumSource getEnumSource() {
		enumSource = (EnumSource) getIntent().getSerializableExtra(
				HomeActivity.JOKE_SOURCE);
		return enumSource;
	}

	/**
	 * </br>Removes 'joke' from lisJokes </br>Notifies adapter and refresh
	 * ListView </br>Removes 'joke' from Storage (i.e. from DB)
	 * 
	 * @param joke
	 *            Joke that should be removed
	 */
	private void deleteJokeFromStorage(final Joke joke) {

		AlertDialoger alertDialoger = new AlertDialoger(context,
				R.string.dialog_msg_item_delete) {

			@Override
			public void doThatIfYes() {
				Logger.v();
				// Remove 'joke' from lisJokes
				listJokes.remove(joke);
				// Notify adapter and refresh ListView
				adapterListJokes.notifyDataSetChanged();
				// Remove 'joke' from Storage (i.e. from DB)
				AsyncCrudExecutor asyncCrud = new AsyncCrudExecutor(context,
						joke, EnumAction.DELETE);
				asyncCrud.execute();

			}
		};

		alertDialoger.buildDialog();
	}

	/**
	 * Sets TextColor and sets isClickable on btnNext (go to next page) in
	 * accordance with whether pageCurrent is pageMain of site or not
	 */
	private void setBtnNextOptions() {

		if (Parser.isCurrentPageMain()) {
			btnNext.setTextColor(getResources().getColor(R.color.gray));
			btnNext.setClickable(false);
		} else {
			btnNext.setTextColor(getResources().getColor(R.color.blue));
			btnNext.setClickable(true);
		}
	}

	/**
	 * Sets TextColor and sets isClickable on btnPrev (go to previous page) in
	 * accordance with whether pageCurrent is pageFirst of site or not
	 */
	private void setBtnPrevOptions() {

		if (Parser.isCurrentPageFirst()) {
			btnPrev.setTextColor(getResources().getColor(R.color.gray));
			btnPrev.setClickable(false);
		} else {
			btnPrev.setTextColor(getResources().getColor(R.color.blue));
			btnPrev.setClickable(true);
		}
	}

	private void setModeFromSiteOptions() {

		if (enumSource == EnumSource.SITE) {

			tvTitle.setText(getResources()
					.getString(R.string.screen_jokes_site)
					+ " "
					+ Parser.getCurrentPage());
		}
	}

	private void setModeFromStorageOptions() {

		if (enumSource == EnumSource.STORAGE) {

			// If we get jokes from Storage - buttons are not needed
			btnPrev.setVisibility(View.INVISIBLE);
			btnNext.setVisibility(View.INVISIBLE);

			// Set Title text (joke source)
			tvTitle.setText(R.string.screen_jokes_storage);
		}
	}

	private void init() {

		lvData = (ListView) findViewById(R.id.lv_ListJokes);
		tvTitle = (TextView) findViewById(R.id.tv_ListJokes_Title);
		// ltBtn = (LinearLayout) findViewById(R.id.lt_ListJokes_Btn);
		btnPrev = (Button) findViewById(R.id.btn_ListJokes_Prev);
		btnNext = (Button) findViewById(R.id.btn_ListJokes_Next);
		btnPreferences = (Button) findViewById(R.id.btn_ListJokes_Preferences);

		setModeFromSiteOptions();

		setModeFromStorageOptions();

	}

}
