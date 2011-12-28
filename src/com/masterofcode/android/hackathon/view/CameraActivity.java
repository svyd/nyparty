package com.masterofcode.android.hackathon.view;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{
	private Camera camera=null;
	private SurfaceHolder surfaceHolder = null;
	private boolean previewRunning = false;
	private Button btnDone;
	private Bitmap mBitmap;
	private boolean oneTouch=true;
	TextView txtView;
	public static final String EXTRA_STRING = "extra_string";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		txtView.setText("Sample Text for test Bubble");
		btnDone=(Button) findViewById(R.id.btn_takePhoto);
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
				
				Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
				Bitmap bubble = BitmapFactory.decodeResource(getResources(), R.drawable.speechbubbleup);
				
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
			        b.setDensity(160);
			       
					Bitmap bmOverlay = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
					
					Canvas canvas = new Canvas(bmOverlay);
					Paint paint = new Paint();
					paint.setColor(Color.BLACK);
					paint.setFlags(Paint.ANTI_ALIAS_FLAG);
					
					
					canvas.drawBitmap(bmp, 0, 0, null);
					canvas.drawBitmap(bubble, bmp.getWidth()-bubble.getWidth(), 0, null);
					canvas.drawBitmap(b, bmp.getWidth()-bubble.getWidth()+40, 70, paint);
					
				
				
				
				
			        
				
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
		parameters.setRotation(90);
		List<Size> sizes = parameters.getSupportedPreviewSizes();
//		Size optimalSize = getOptimalPreviewSize(sizes, w, h);
//		parameters.setPreviewSize(optimalSize.width, optimalSize.height);
		parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
		camera.setParameters(parameters);
		try {
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			Log.d("IOException", e.getMessage());
		}
		camera.startPreview();
		previewRunning = true;
	}



	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null) return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		camera = Camera.open();
		camera.setDisplayOrientation(90);
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