package com.masterofcode.android.hackathon.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

public class PreviewActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);
		
		ImageView img = (ImageView) findViewById(R.id.image_preview);
		
		String src=getIntent().getStringExtra(CameraActivity.EXTRA_STRING);
		Bitmap PictFromFile = BitmapFactory.decodeFile(src);
		img.setBackgroundDrawable(new BitmapDrawable(PictFromFile));
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
