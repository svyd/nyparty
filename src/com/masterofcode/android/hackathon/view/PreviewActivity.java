package com.masterofcode.android.hackathon.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

}
