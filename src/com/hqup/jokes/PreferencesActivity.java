package com.hqup.jokes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.hqup.jokes.network.NetworkStateChecker;
import com.hqup.jokes.utils.Sounder;

public class PreferencesActivity extends PreferenceActivity implements
		OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

	private static final int ID_BUTTON_COMPLAIN = 99;
	private static final String URL_TO_COMPLAIN = "http://natribu.org/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.activity_preferences);

		// Add for the ColorDialogPreferencer
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

		// Add button 'About Developers'
		ListView v = getListView();

		Button btn_Complain = new Button(this);
		btn_Complain.setId(ID_BUTTON_COMPLAIN);
		btn_Complain.setText(getResources().getString(R.string.btn_complain));
		int color = getResources().getColor(R.color.orange);
		btn_Complain.setTextColor(color);

		v.addFooterView(btn_Complain);

		btn_Complain.setOnClickListener(this);
	}

	@Override
	protected void onStop() {

		if (isFinishing()) {
			Sounder.doSound(this, R.raw.beep);
		}

		super.onStop();
	}

	@Override
	public void onClick(View v) {

		Intent intent;

		switch (v.getId()) {

		case ID_BUTTON_COMPLAIN:
			// Check whether NetConnection exists or not
			if (!new NetworkStateChecker(this).checkNetConnection()) {
				Sounder.doSound(this, R.raw.wilhelm_scream);
				break;
			}

			Sounder.doSound(this, R.raw.beep_notify);
			Uri uri = Uri.parse(URL_TO_COMPLAIN);
			intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			break;
		}
	}

	// ====Added for the ColorDialogPreferencer=====

	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);

		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

	}

}
