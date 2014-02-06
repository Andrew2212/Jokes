package com.hqup.jokes.preferencers;

import com.hqup.jokes.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

///http://stackoverflow.com/questions/2457215/how-does-one-declare-the-type-of-an-android-preference

public class Preferencer {

	/**
	 * 
	 * @param context
	 *            Context
	 * @return float parsed value 'fontSize' from 'preferences'
	 */
	public static float getFontSize(Context context) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		String fontSizeString = sharedPreferences.getString(context
				.getResources().getString(R.string.pref_font_size_key), context
				.getResources().getString(R.string.pref_font_size_value));

		float fontSizeFloat;
		try {
			fontSizeFloat = Float.parseFloat(fontSizeString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			fontSizeFloat = Float.parseFloat(context.getResources().getString(
					R.string.pref_font_size_value));
		}

		return fontSizeFloat;
	}

	/**
	 * 
	 * @param context
	 *            Context
	 * @return int value 'fontColor' from 'preferences'
	 */
	public static int getFontColor(Context context) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		int fontColorString = sharedPreferences.getInt(context.getResources()
				.getString(R.string.pref_color_key), context.getResources()
				.getColor(R.color.white));

		return fontColorString;

	}

	public static int getDeltaPage(Context context) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		String deltaString = sharedPreferences.getString(context.getResources()
				.getString(R.string.pref_delta_key), context.getResources()
				.getString(R.string.pref_delta_value));

		int delta;
		try {
			delta = Integer.parseInt(deltaString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			delta = Integer.parseInt(context.getResources().getString(
					R.string.pref_delta_value));
		}

		return delta;
	}

}
