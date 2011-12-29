package com.masterofcode.android.hackathon.view;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.masterofcode.android.hackathon.utils.ApplicationUtils;
import com.masterofcode.android.hackathon.utils.Constants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{
	private Camera camera=null;
	private SurfaceHolder surfaceHolder = null;
	private boolean previewRunning = false;
	private Button btnDone;
	private boolean oneTouch=true;
	private static Bitmap bmp;
	private static Bitmap bmOverlay;
	private static Bitmap bubble;
	private static Bitmap rotatedBMP;
	TextView txtView;
	private String id 	= null;
	private String desc = null;
	public static final String EXTRA_STRING = "extra_string";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Constants.ISDEBUG)
			Log.d(Constants.LOGTAG, "CameraActivity onCreate...");
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.setFixedSize(getWindow().getWindowManager()
				.getDefaultDisplay().getWidth(), getWindow().getWindowManager()
				.getDefaultDisplay().getHeight());
		txtView = (TextView) findViewById(R.id.text);
		btnDone=(Button) findViewById(R.id.btn_takePhoto);
		id = ApplicationUtils.getPrefProperty(this, "id");
		desc = ApplicationUtils.getPrefProperty(this, "desc");
		if (!TextUtils.isEmpty(desc)){
			txtView.setText(desc);
		} else {
			txtView.setText("Sample Text for test Bubble");
		}
		bubble = BitmapFactory.decodeResource(getResources(), R.drawable.speechbubbleup);
		btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(oneTouch){
					camera.takePicture(null, picCalBac, picCalBac);
					oneTouch=false;
				}
			}
			});
		
	}

	Camera.PictureCallback picCalBac = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if (data != null) {	
				previewRunning = false;
				
				Options opts = new Options();
				opts.inSampleSize = 4;

				bmp = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
				int h = bmp.getHeight();
				int w = bmp.getWidth();
				if (Constants.ISDEBUG)
					Log.d(Constants.LOGTAG, "h = " + h + " w = " + w);
				
				/*
				 *start section special for TouchWiz 
				 */
				if (h < w){
					Matrix mtx = new Matrix();
					mtx.postRotate(90);
					// Rotating Bitmap
					rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
					bmp = rotatedBMP;
				}
				//end section for TouchWiz
				
				OutputStream fOut = null;
				File f=new File("/sdcard/bubble");
				if(!f.exists()){
					f.mkdir();
				}
				String name  = ""+System.currentTimeMillis();
				File file = new File("/sdcard/bubble/"+name+".jpg");
				try {
					fOut = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 TextView tv=(TextView) findViewById(R.id.text);
			        tv.setDrawingCacheEnabled(true); 
			        tv.buildDrawingCache(); 
			        Bitmap b=tv.getDrawingCache(true);
			        if (Constants.ISDEBUG)
						Log.d(Constants.LOGTAG, "b.getWidth() = " + b.getWidth() + " bmp.getWidth() = " + bmp.getWidth() + " bubble.getWidth()= " + bubble.getWidth());
			      //  b.setDensity(200);
					bmOverlay = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
					
					Canvas canvas = new Canvas(bmOverlay);
					Paint paint = new Paint();
					paint.setAntiAlias(true);
					paint.setFlags(Paint.ANTI_ALIAS_FLAG);
					
					
					canvas.drawBitmap(bmp, 0, 0, null);
					canvas.drawBitmap(bubble, bmp.getWidth()-bubble.getWidth(), 0, null);
					canvas.drawBitmap(b, bmp.getWidth()-bubble.getWidth()+55, 45, paint);
					bmOverlay.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
				try {
					fOut.flush();
					fOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent intent = new Intent(CameraActivity.this,PreviewActivity.class);
				intent.putExtra(EXTRA_STRING,"/sdcard/bubble/"+name+".jpg");
				startActivity(intent);
				finish();
				oneTouch=true;
			}
		}
	};
	
	@Override
	public void onPause(){
		super.onPause();
		clearMemory();
	}
	
	private void clearMemory(){
		
		if (Constants.ISDEBUG)
			Log.d(Constants.LOGTAG, "in clear_memory");
		
    	if (bmp != null) {
    		if (Constants.ISDEBUG)
    			Log.d(Constants.LOGTAG, "bmp is null");
    		bmp.recycle();
    		bmp = null;
       }
    	
    	if (rotatedBMP != null) {
    		if (Constants.ISDEBUG)
    			Log.d(Constants.LOGTAG, "rotatedBMP is null");
    		rotatedBMP.recycle();
    		rotatedBMP = null;
       }
    	
    	if (bmOverlay != null) {
    		if (Constants.ISDEBUG)
    			Log.d(Constants.LOGTAG, "bmOverlay is null");
    		bmOverlay.recycle();
    		bmOverlay = null;
       }
    	
    	System.gc();
	}
	
	public static Bitmap loadBitmapFromView(View v) {
	    Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);                
	    Canvas c = new Canvas(b);
	    v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
	    v.draw(c);
	    return b;
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		oneTouch=true;
		
		if (previewRunning) {
			camera.stopPreview();
		}
		Camera.Parameters parameters = camera.getParameters();
			parameters.set("orientation", "portrait");
			parameters.setRotation(90);
			if (Constants.ISDEBUG)
				Log.d(Constants.LOGTAG, "rotating + 90");
			
		camera.setParameters(parameters);
		try {
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			Log.d("IOException", e.getMessage());
		}
		camera.startPreview();
		previewRunning = true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		camera = Camera.open();
		camera.setDisplayOrientation(90);
		if (Constants.ISDEBUG)
			Log.d(Constants.LOGTAG, "rotating + 90");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		previewRunning = false;
		camera.stopPreview();
		camera.release();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), RegActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return false;
	}
	
	
}