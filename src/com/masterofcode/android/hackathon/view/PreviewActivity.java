package com.masterofcode.android.hackathon.view;

import org.json.JSONException;

import com.masterofcode.android.hackathon.utils.ApplicationUtils;
import com.masterofcode.android.hackathon.utils.Constants;
import com.masterofcode.android.hackathon.utils.RestClient;
import com.masterofcode.android.hackathon.utils.ServerCommunication;
import com.masterofcode.android.hackathon.view.RegActivity.ProgressTask;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews.ActionException;

public class PreviewActivity extends Activity{
	
	Context				mContext;
	String 				src;
	String				mId;
	Button 				sendBtn;
	Button				retakeBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);
		
		mContext 	= this;
		sendBtn 	= (Button) findViewById(R.id.btn_send);
		retakeBtn 	= (Button) findViewById(R.id.btn_retake);
		mId = (String) getIntent().getCharSequenceExtra("id");
		ImageView img = (ImageView) findViewById(R.id.image_preview);
		
		src=getIntent().getStringExtra(CameraActivity.EXTRA_STRING);
		Bitmap PictFromFile = BitmapFactory.decodeFile(src);
		img.setBackgroundDrawable(new BitmapDrawable(PictFromFile));
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
	    	ServerCommunication.getInstance().doFileUpload(src, Constants.URL + mId + ".json", mContext);
	    	Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEBVIEWURL));
	    	startActivity(myIntent);
	    	return true;
	    }
	}

}
