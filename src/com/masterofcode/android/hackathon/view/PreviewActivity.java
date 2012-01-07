package com.masterofcode.android.hackathon.view;

import com.masterofcode.android.hackathon.utils.ApplicationUtils;
import com.masterofcode.android.hackathon.utils.Constants;
import com.masterofcode.android.hackathon.utils.RestClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.KeyEvent;
import android.widget.ImageView;

public class PreviewActivity extends Activity{
	
	private 		Context	mContext;
	private 		String 	src;
	private 		String	mId;
	private 		Button 	sendBtn;
	private			Button	retakeBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);
		
		mContext 	= this;
		sendBtn 	= (Button) findViewById(R.id.btn_send);
		retakeBtn 	= (Button) findViewById(R.id.btn_retake);
		mId = ApplicationUtils.getPrefProperty(this, "id");
		ImageView img = (ImageView) findViewById(R.id.image_preview);
		
		src=getIntent().getStringExtra(CameraActivity.EXTRA_STRING);
		Bitmap pictFromFile = BitmapFactory.decodeFile(src);
		img.setBackgroundDrawable(new BitmapDrawable(pictFromFile));
		sendBtn.setOnClickListener(sendPhotoListener);
		retakeBtn.setOnClickListener(retakePhotoListener);
	}
	
	OnClickListener retakePhotoListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(PreviewActivity.this,CameraActivity.class);
			intent.putExtra("id", mId);
			startActivity(intent);
		}
	};
	
	OnClickListener sendPhotoListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			AsyncTask task = new ProgressTask().execute(new String[]{});
		}
	};
	
	public class ProgressTask extends AsyncTask<String, Void, Boolean> {

	    public ProgressTask() {
	        dialog = new ProgressDialog(mContext);
	    }

	    /** progress dialog to show user that the backup is processing. */
	    private ProgressDialog dialog;
	    

	    protected void onPreExecute() {
	        this.dialog.setMessage("Sending. Please wait...");
	        this.dialog.show();
	    }

	        @Override
	    protected void onPostExecute(final Boolean success) {
	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
	    }
	    protected Boolean doInBackground(final String... args) {
	    	RestClient.doFileUpload(src, Constants.URL + mId + ".json");
	    	Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEBVIEWURL));
	    	startActivity(myIntent);
	    	finish();
	    	return true;
	    }
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), CameraActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return false;
	}

}
