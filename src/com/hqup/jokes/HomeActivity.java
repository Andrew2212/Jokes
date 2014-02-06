package com.hqup.jokes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hqup.jokes.network.NetworkStateChecker;
import com.hqup.jokes.network.Parser;
import com.hqup.jokes.utils.Sounder;

public class HomeActivity extends Activity implements OnClickListener {

	/**
	 * Defines the source for joke obtain
	 */
	public static final String JOKE_SOURCE = "Joke Source";

	private Button btnFromSite;
	private Button btnFromStorage;
	private Button btnGoToSite;

	public enum EnumSource {
		SITE, STORAGE
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_home);

		btnFromSite = (Button) findViewById(R.id.btn_Home_jokeSite);
		btnFromStorage = (Button) findViewById(R.id.btn_Home_jokeStorage);
		btnGoToSite = (Button) findViewById(R.id.btn_Home_goToSite);
		btnFromSite.setOnClickListener(this);
		btnFromStorage.setOnClickListener(this);
		btnGoToSite.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		Sounder.doSound(this, R.raw.beep);
		Intent intent;
		switch (v.getId()) {

		case R.id.btn_Home_jokeSite:

			// Check whether NetConnection exists or not
			if (!new NetworkStateChecker(this).checkNetConnection()) {
				break;
			}

			intent = new Intent(this, ListJokesActivity.class);
			intent.putExtra(JOKE_SOURCE, EnumSource.SITE);
			startActivity(intent);
			break;

		case R.id.btn_Home_jokeStorage:
			intent = new Intent(this, ListJokesActivity.class);
			intent.putExtra(JOKE_SOURCE, EnumSource.STORAGE);
			startActivity(intent);
			break;

		case R.id.btn_Home_goToSite:

			// Check whether NetConnection exists or not
			if (!new NetworkStateChecker(this).checkNetConnection()) {
				break;
			}

			Uri uri = Uri.parse(Parser.URL);
			intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);

			break;
		}

	}

}
