package com.hqup.jokes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;

import com.hqup.jokes.R;

/**
 * @author Andrew2212
 *         <p>
 *         Launches Toast if that allowed into Preferences
 *         </p>
 */
public class Toaster {

	/**
	 * X-Offset for 'Toast.setGravity (int gravity, int xOffset, int yOffset) '
	 */
	private static final int OFFSET_X = 0;

	/**
	 * Y-Offset for 'Toast.setGravity (int gravity, int xOffset, int yOffset) '
	 */
	private static final int OFFSET_Y = 300;

	/**
	 * @param context
	 * @param id
	 *            - message from resources R.string
	 *            <p>
	 *            Show toast if that allowed; duration = Toast.LENGTH_LONG;
	 *            </p>
	 */
	public static void doToastLong(Context context, int id) {

		int duration = Toast.LENGTH_LONG;
		doToastFromResources(context, id, duration);

	}

	/**
	 * @param context
	 * @param id
	 *            - message from resources R.string
	 *            <p>
	 *            Show toast if that allowed; duration = Toast.LENGTH_SHORT;
	 *            </p>
	 */
	public static void doToastShort(Context context, int id) {

		int duration = Toast.LENGTH_SHORT;
		doToastFromResources(context, id, duration);
	}

	/**
	 * @param context
	 * @param string
	 *            - message from code String
	 *            <p>
	 *            Show toast if that allowed; duration = Toast.LENGTH_LONG;
	 *            </p>
	 */
	public static void doToastLong(Context context, String string) {

		int duration = Toast.LENGTH_LONG;
		doToastFromString(context, string, duration);
	}

	/**
	 * @param context
	 * @param string
	 *            - message from code String
	 *            <p>
	 *            Show toast if that allowed; duration = Toast.LENGTH_SHORT;
	 *            </p>
	 */
	public static void doToastShort(Context context, String string) {

		int duration = Toast.LENGTH_SHORT;
		doToastFromString(context, string, duration);
	}

	// =============Private Methods===================

	private static void doToastFromResources(Context context, int id,
			int duration) {

		boolean chbToastPref = receivePreference(context);
		Toast t = Toast.makeText(context, id, duration);

		if (chbToastPref) {
			t.setGravity(Gravity.BOTTOM, OFFSET_X, OFFSET_Y);
			t.show();
		}
	}

	private static void doToastFromString(Context context, String string,
			int duration) {

		boolean chbToastPref = receivePreference(context);
		Toast t = Toast.makeText(context, string, duration);

		if (chbToastPref) {
			t.setGravity(Gravity.BOTTOM, OFFSET_X, OFFSET_Y);
			t.show();
		}

	}

	private static boolean receivePreference(Context context) {

		// Create Preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		// read value Toast CheckBox from CheckBoxPreference
		boolean chbToastPref = prefs.getBoolean(context.getResources()
				.getString(R.string.pref_toast_key), true);

		return chbToastPref;
	}
}