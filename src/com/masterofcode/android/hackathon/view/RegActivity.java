package com.masterofcode.android.hackathon.view;

import org.json.JSONException;
import org.json.JSONObject;

import com.masterofcode.android.hackathon.utils.ApplicationUtils;
import com.masterofcode.android.hackathon.utils.Constants;
import com.masterofcode.android.hackathon.utils.RestClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class RegActivity extends Activity {
    
	Button 				regBtn;
	TextView			nickName;
	JSONObject			mJSONObject;
	String 				mId;
	String				mPhotoDescription;
	String				nickNameTxt;
	Animation 			shake;
	Context				mContext;
	
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TextUtils.isEmpty(ApplicationUtils.getPrefProperty(this, "id")) && 
        		!TextUtils.isEmpty(ApplicationUtils.getPrefProperty(this, "desc"))){
        	gotoNextActivity();
        }
        setContentView(R.layout.signin);
        
        mContext = this;
        // go to CameraActivity for testing
        gotoNextActivity();
        
        shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        regBtn = (Button) findViewById(R.id.btn_name);
        nickName = (TextView) findViewById(R.id.edit_text_name);
        regBtn.setOnClickListener(getTextDescriptionListener);
    }
	
	OnClickListener getTextDescriptionListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (!TextUtils.isEmpty(nickName.getText())){
				nickNameTxt = nickName.getText().toString();
				AsyncTask task = new ProgressTask().execute(new String[]{nickNameTxt});
				//gotoNextActivity();
			} else {
				nickName.startAnimation(shake);
				showAlertDialog(R.string.empty_name);
			}
		}
	};
	
	@Override
	public void onResume(){
		super.onResume();
		if (!TextUtils.isEmpty(ApplicationUtils.getPrefProperty(this, "id")) && 
        		!TextUtils.isEmpty(ApplicationUtils.getPrefProperty(this, "desc"))){
        	gotoNextActivity();
        }
	}
	
	public class ProgressTask extends AsyncTask<String, Void, Boolean> {

	    public ProgressTask() {
	        dialog = new ProgressDialog(mContext);
	    }

	    /** progress dialog to show user that the backup is processing. */
	    private ProgressDialog dialog;
	    

	    protected void onPreExecute() {
	        this.dialog.setMessage("Processing. Please wait...");
	        this.dialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
	    }

	    protected Boolean doInBackground(final String... args) {
	    	mJSONObject 	= RestClient.connect(Constants.URL + Constants.IDSUFF);
	    	if (mJSONObject != null){
				try {
					mId 				= mJSONObject.getString("id");
					ApplicationUtils.setPrefProperty(mContext, "id", mId);
					mPhotoDescription 	= mJSONObject.getString("caption");
					ApplicationUtils.setPrefProperty(mContext, "desc", mPhotoDescription);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				gotoNextActivity();
				if (Constants.ISDEBUG)
					Log.d(Constants.LOGTAG, "id = " + mId + " description = " + mPhotoDescription);
				RestClient.sendPut(Constants.URL + mId + "/" + Constants.CHECKID, mId, nickNameTxt);
			} else {
				runOnUiThread(new Runnable() {
				    public void run() {
				    	showAlertDialog(R.string.busy_slots);
				    }
				});
			}
	    	return true;
	       }
	}
	
	private void showAlertDialog(int textId){
		AlertDialog.Builder alert_dialog = new AlertDialog.Builder(this);
		alert_dialog.setCancelable(true)
		.setTitle(R.string.dialog_title)
		.setIcon(R.drawable.alert_icon_32)
		.setMessage(textId)
		.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
	}
	
	private void gotoNextActivity(){
		Intent intent = new Intent(RegActivity.this, CameraActivity.class);
		startActivity(intent);
	}
}