package com.masterofcode.android.hackathon.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ApplicationUtils {
	public static void showToast(Context context, int stringId){
		Toast msg = Toast.makeText(context, stringId, Toast.LENGTH_LONG);
		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
		msg.show();
	}
}
