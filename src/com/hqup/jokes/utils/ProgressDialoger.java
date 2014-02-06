package com.hqup.jokes.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.hqup.jokes.R;

/**
 * @author Andrew2212
 *         <p>
 *         Makes simple custom wrapper for ProgressDialog simply
 *         </p>
 */
public class ProgressDialoger {

	private ProgressDialog pd;
	private Context context;

	public ProgressDialoger(Context context) {
		this.context = context;
		Logger.v();
	}

	public void showPD() {
		Logger.v();
		pd = new ProgressDialog(context);
		pd.setMessage(context.getText(R.string.dialog_msg_progress_bar_message));
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCancelable(false);
		pd.show();
	}

	public void dismissPD() {
		Logger.v();
		if (pd != null)
			pd.dismiss();
	}
}