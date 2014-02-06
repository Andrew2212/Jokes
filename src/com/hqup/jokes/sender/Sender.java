package com.hqup.jokes.sender;

import android.content.Context;
import android.content.Intent;

import com.hqup.jokes.R;
import com.hqup.jokes.entity.Joke;
import com.hqup.jokes.utils.Toaster;

public class Sender {

	public static final String SMS_BODY = "sms_body";

	public static String sendSms(Context context, Joke joke) {

		String smsText = joke.getText();
		String phoneNum = null;

		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra(SMS_BODY, smsText);
			intent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNum);
			intent.setType("vnd.android-dir/mms-sms");
			context.startActivity(intent);

		} catch (Exception e) {
			Toaster.doToastShort(context, R.string.toast_something_wrong);
			e.printStackTrace();
		}

		return smsText;
	}

}
