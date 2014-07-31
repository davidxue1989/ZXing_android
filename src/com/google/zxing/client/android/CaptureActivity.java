/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;

public class CaptureActivity extends Activity {
	public boolean isCCC = false;
	public static String path = "";
	public static final int CONFIRM_REQCODE = 123;
	
	private CameraManager cameraManager;
	  private CaptureActivityHandler handler;
	  
    private RelativeLayout mLayout;
    

    public CameraManager getCameraManager() {
      return cameraManager;
    }

    public Handler getHandler() {
      return handler;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.capture);
		
	    //get intent extras
		Intent intent = getIntent();
		Bundle b = null;
		if (intent != null)
			b = intent.getExtras();
		if (b != null) {
			isCCC = b.getBoolean("isCCC");
			if (isCCC) {
				path = b.getString("path");
				if (path == null) {
					path = "";
					assert(false);
				}
			}
		}

		ImageButton btnLOScan = (ImageButton) findViewById(R.id.btn_img_scan);
		if (!isCCC) {
			
			LinearLayout botBar = (LinearLayout) findViewById(R.id.bottombar_capture);

			LinearLayout scanLayout = (LinearLayout) findViewById(R.id.btn_lo_scan);
			botBar.removeView(scanLayout);
			//disable scan button
			btnLOScan.setVisibility(View.INVISIBLE);
		}
		btnLOScan.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				cameraManager.takePhoto(); // will call back to onCCCPhoto
			}
		});
		
		ImageButton btnLOBack = (ImageButton) findViewById(R.id.btn_img_back);
		btnLOBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		});

		mLayout = (RelativeLayout) findViewById(R.id.preview_frame);
		
		
	    //dxchanged
	    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	    SharedPreferences.Editor prefsEdit = PreferenceManager.getDefaultSharedPreferences(this).edit();
	    prefsEdit.putBoolean(PreferencesActivity.KEY_DECODE_1D, false);
	    prefsEdit.putBoolean(PreferencesActivity.KEY_DECODE_DATA_MATRIX, false);
	    prefsEdit.putBoolean(PreferencesActivity.KEY_DECODE_QR, true); 
	    prefsEdit.commit();
	}	
	

    @Override
    protected void onResume() {
        super.onResume();

        cameraManager = new CameraManager(this);
        cameraManager.startPreview(mLayout);
        
        handler = null;
    }
    

    @Override
    protected void onPause() {
        if (handler != null && !isCCC) {
          handler.quitSynchronously();
          handler = null;
        }        
        
        cameraManager.stopPreview();
        
        super.onPause();
    }
    
	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
		  handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}
	

	  /**
	   * A valid barcode has been found, so give an indication of success and show the results.
	   *
	   * @param rawResult The contents of the barcode.
	   * @param scaleFactor amount by which thumbnail was scaled
	   * @param barcode   A greyscale bitmap of the camera data which was decoded.
	   */
	  public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
	    ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
	    handleDecodeInternally(rawResult, resultHandler, barcode);
	  }

	// Put up our own UI for how to handle the decoded contents.
	private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {		
//		String BarcodeFormat = rawResult.getBarcodeFormat().toString();
//		String type = resultHandler.getType().toString();//		
//		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
//		String date = formatter.format(new Date(rawResult.getTimestamp()));		  
		//barcodeImageView.setImageBitmap(barcode);

		CharSequence text = resultHandler.getDisplayContents();
	    Intent intent = new Intent();
	    intent.putExtra(ResultHandler.RESULT, text);
		setResult(Activity.RESULT_OK, intent);
		finish(); //return to the activity that called CaptureActivity
	}

	  public void startHandler() { 
		//called by CameraManager's onPreviewReady which is called back from C2SCameraPreview after surface view is created
	    if (handler == null) {
	      handler = new CaptureActivityHandler(this, null, null, null, cameraManager);
	    }
	  }

	public void onCCCPhoto() {
//		Bitmap bm = getCCCPhoto(this);		
//		ImageView iv = (ImageView) findViewById(R.id.debug_image_view);
//		iv.setImageBitmap(bm);
		
		Intent confirmIntent = new Intent(getApplicationContext(), ConfirmScanActivity.class);
		startActivityForResult(confirmIntent, CONFIRM_REQCODE);
	}
	
	public static Bitmap getCCCPhoto(Activity activity, float rotation) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		final float densityMultiplier = activity.getBaseContext().getResources().getDisplayMetrics().density;
		int h = (int) (512 * densityMultiplier);
		int w = (int) (h * bitmap.getWidth() / ((double) bitmap.getHeight()));
		Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, w, h, true);
		bitmap.recycle();
		bitmap = null;
		// createa matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(1, 1);
		// rotate the Bitmap
		matrix.postRotate(90);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapScaled, 0, 0, w, h, matrix, true);
		

		//crop the bitmap
		float cropFactor = 0.6f;				
		int width = (int)(resizedBitmap.getWidth()*cropFactor);
		int height = (int)(resizedBitmap.getHeight()*cropFactor);				
		int dimension = Math.min(width, height);
		int startX = (int) ((resizedBitmap.getWidth() - dimension)*0.5);
		int startY = (int) ((resizedBitmap.getHeight() - dimension)*0.5);	
		Bitmap bm = Bitmap.createBitmap(resizedBitmap, startX, startY, dimension, dimension);
		resizedBitmap.recycle();
		resizedBitmap = null;
		
		return bm;
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (resultCode == RESULT_OK && requestCode == CONFIRM_REQCODE) 
		{
			setResult(Activity.RESULT_OK);
			finish();
		}
	}
}
