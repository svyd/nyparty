package com.masterofcode.android.hackathon.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationUtils {
	
	public static SharedPreferences getPreferences(final Context context) {
		return getPreferences(context, Constants.KEY_PREF);
	}
	
	public static SharedPreferences getPreferences(final Context context,final String prefsName) {
		return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
	}
	
	public static void setPrefProperty(final Context context, final String propKey, String propValue) {
		final SharedPreferences sharedPreferences = getPreferences(context);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(propKey, propValue);
		editor.commit();
	}
	public static String getPrefProperty(final Context context, final String propKey) {
		final SharedPreferences sharedPreferences = getPreferences(context);
		return sharedPreferences.getString(propKey, null);		
	}

}
