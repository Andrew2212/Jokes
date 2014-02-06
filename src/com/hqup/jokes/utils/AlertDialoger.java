package com.hqup.jokes.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.hqup.jokes.R;

public abstract class AlertDialoger {

	private Context context;

	private int message;
	private int yes;
	private int no;

	/**
	 * @param context
	 * @param message
	 *            Dialog message
	 * @param yes
	 *            stringFromResources onto button 'Yes'
	 * @param no
	 *            stringFromResources onto button 'Yes'
	 */
	public AlertDialoger(Context context, int message, int yes, int no) {
		this.context = context;
		this.message = message;
		this.yes = yes;
		this.no = no;
	}

	/**
	 * 
	 * @param context
	 *            Context
	 * @param message
	 *            Dialog message from R.string.'resources'
	 */
	public AlertDialoger(Context context, int message) {
		this.context = context;
		this.message = message;
		yes = R.string.btn_yes;
		no = R.string.btn_no;
	}

	/**
	 * Creates and shows AlertDialog window
	 */
	public void buildDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setMessage(context.getResources().getString(message)
				+ "\n"
				+ context.getResources().getString(
						R.string.dialog_msg_do_you_want_to_continue));

		builder.setPositiveButton(context.getResources().getString(yes),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						Sounder.doSound(context, R.raw.wilhelm_scream);
						doThatIfYes();
					}
				});

		builder.setNegativeButton(context.getResources().getString(no),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						Sounder.doSound(context, R.raw.beep);
						doThatIfNo();

						dialog.cancel();
					}
				});
		@SuppressWarnings("unused")
		AlertDialog alert = builder.show();

	}

	/**
	 * Makes that is needed if we click "Yes" button </br>(i.e. what exists into
	 * onClick(DialogInterface dialog, int id))
	 */
	public abstract void doThatIfYes();

	/**
	 * Makes that is needed if we click "No" button </br>(i.e. what exists into
	 * onClick(DialogInterface dialog, int id))
	 */
	public void doThatIfNo() {
		Logger.v();
	}

}