package com.hqup.jokes.utils;

import android.text.TextUtils;

/**
 * @author Somebody from Net
 *         <p>
 *         Customized Logger class from
 *         http://novikovmaxim.livejournal.com/254638.html
 *         <p>
 */
public final class Logger {

	private static final String TAG = "Jokes";
	/**
	 * Set true or false if you want read logs or not
	 */
//	 private static boolean logEnabled_v = true;
//	 private static boolean logEnabled_i = true;
//	 private static boolean logEnabled_e = true;

	 private static boolean logEnabled_v = false;
	 private static boolean logEnabled_i = false;
	 private static boolean logEnabled_e = false;

	public static void v() {
		if (logEnabled_v) {
			android.util.Log.v(TAG, getLocation());
		}
	}

	public static void v(String msg) {
		if (logEnabled_v) {
			android.util.Log.v(TAG, getLocation() + msg);
		}
	}

	/**
	 * Prints time of executing task
	 * 
	 * @param timeStart
	 *            Start time of task
	 */
	public static void time(long timeStart) {
		if (logEnabled_v) {
			long timeEnd = System.currentTimeMillis();
			long deltaTime = timeEnd - timeStart;
			String msg = null;
			if (timeStart == 0) {
				msg = "timeStart hasn't been initialized!!!";
			} else {
				msg = "***Task time is = " + deltaTime;
			}
			android.util.Log.v(TAG, getLocation() + msg);
		}
	}

	/**
	 * Prints time of executing task
	 * 
	 * @param timeStart
	 *            Start time of task
	 * @param message
	 *            String that we want to add
	 */
	public static void time(long timeStart, String message) {

		if (logEnabled_v) {
			long timeEnd = System.currentTimeMillis();
			long deltaTime = timeEnd - timeStart;
			String msg = null;
			if (timeStart == 0) {
				msg = "timeStart hasn't been initialized!!!";
			} else {
				msg = message + "***Task time is = " + deltaTime;
			}
			android.util.Log.v(TAG, getLocation() + msg);
		}
	}

	public static void i(String msg) {
		if (logEnabled_i) {
			android.util.Log.i(TAG, getLocation() + msg);
		}
	}

	public static void i() {
		if (logEnabled_i) {
			android.util.Log.i(TAG, getLocation());
		}
	}

	public static void e(String msg) {
		if (logEnabled_e) {
			android.util.Log.e(TAG, getLocation() + msg);
		}
	}

	public static void e() {
		if (logEnabled_e) {
			android.util.Log.e(TAG, getLocation());
		}
	}

	private static String getLocation() {
		final String className = Logger.class.getName();
		final StackTraceElement[] traces = Thread.currentThread()
				.getStackTrace();
		boolean found = false;

		for (int i = 0; i < traces.length; i++) {
			StackTraceElement trace = traces[i];

			try {
				if (found) {
					if (!trace.getClassName().startsWith(className)) {
						Class<?> clazz = Class.forName(trace.getClassName());
						return "[" + getClassName(clazz) + ":"
								+ trace.getMethodName() + ":"
								+ trace.getLineNumber() + "]: ";
					}
				} else if (trace.getClassName().startsWith(className)) {
					found = true;
					continue;
				}
			} catch (ClassNotFoundException e) {
			}
		}

		return "[]: ";
	}

	private static String getClassName(Class<?> clazz) {
		if (clazz != null) {
			if (!TextUtils.isEmpty(clazz.getSimpleName())) {
				return clazz.getSimpleName();
			}

			return getClassName(clazz.getEnclosingClass());
		}

		return "";
	}
}