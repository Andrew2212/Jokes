package com.hqup.jokes.network;

import com.hqup.jokes.R;
import com.hqup.jokes.utils.Logger;
import com.hqup.jokes.utils.Toaster;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//http://idev.by/android/snippets-android/9939/

public class NetworkStateChecker {

	private Context context;

	/**
	 * This class indicates whether network connectivity (MOBILE or WIFI) exists
	 * and it is possible to establish connections and pass data.
	 * 
	 * @param context
	 *            Context
	 */
	public NetworkStateChecker(Context context) {
		this.context = context;
	}
	
	/**
	 * 
	 * @return true if NetworkConnection WIFI or MOBILE is connected
	 */
	public boolean checkNetConnection() {

		boolean result = hasInternetConnection();
		if (!result)
			Toaster.doToastLong(context, R.string.toast_connection_is_absent);
		return result;
	}

	/**
	 * Monitors network connections (Wi-Fi, GPRS, UMTS, etc.)
	 * 
	 * @return
	 */
	private boolean hasInternetConnection() {
		/*
		 * Return the handle to a system-level service by name. The class of the
		 * returned object varies by the requested name.
		 */
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		/*
		 * NetworkInfo - describes the status of a network interface.
		 * cm.getAllNetworkInfo() - returns connection status information about
		 * all network types supported by the device.
		 */
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		if (netInfo == null) {
			return false;
		}
		for (NetworkInfo ni : netInfo) {
			Logger.v(ni.getTypeName());
			// Check WIFI connection
			if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
				/*
				 * Indicates whether network connectivity exists and it is
				 * possible to establish connections and pass data.
				 */
				if (ni.isConnected()) {
					Logger.v("WIFI connection found");
					return true;
				}
			}

			// Check MOBILE connection
			if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
				if (ni.isConnected()) {
					Logger.v("MOBILE connection is found");
					return true;
				}
			}

		}

		return false;
	}
}
