package com.masterofcode.android.hackathon.view;

import org.json.JSONException;
import org.json.JSONObject;

import com.masterofcode.android.hackathon.utils.ApplicationUtils;
import com.masterofcode.android.hackathon.utils.Constants;
import com.masterofcode.android.hackathon.utils.RestClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RegActivity extends Activity {
    
	Button 				regBtn;
	JSONObject			mJSONObject;
	String 				mId;
	String				mPhotoDescription;
	Context				mContext;
	
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        
        mContext = this;
        regBtn = (Button) findViewById(R.id.btn_name);
        regBtn.setOnClickListener(getTextDescriptionListener);
    }
	
	OnClickListener getTextDescriptionListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			AsyncTask task = new ProgressTask().execute(new String[]{Constants.URL + Constants.IDSUFF});
			/*new Thread(new Runnable() {
				@Override
				public void run() {
					mJSONObject 	= RestClient.connect(Constants.URL + Constants.IDSUFF);
					if (mJSONObject != null){
						try {
							mId 				= mJSONObject.getString("id");
							mPhotoDescription 	= mJSONObject.getString("caption");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (Constants.ISDEBUG)
							Log.d(Constants.LOGTAG, "id = " + mId + " description = " + mPhotoDescription);
						RestClient.sendPut(Constants.URL + mId + "/" + Constants.CHECKID, mId);
					} else {
						runOnUiThread(new Runnable() {
						    public void run() {
						    	ApplicationUtils.showToast(mContext, R.string.busy_slots);
						    }
						});
					}
				}
			}).start();*/
			
		}
	};
	
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
					mPhotoDescription 	= mJSONObject.getString("caption");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (Constants.ISDEBUG)
					Log.d(Constants.LOGTAG, "id = " + mId + " description = " + mPhotoDescription);
				RestClient.sendPut(Constants.URL + mId + "/" + Constants.CHECKID, mId);
			} else {
				runOnUiThread(new Runnable() {
				    public void run() {
				    	ApplicationUtils.showToast(mContext, R.string.busy_slots);
				    }
				});
			}
	    	return true;
	       }
	    }
}