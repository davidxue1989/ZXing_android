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
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;

public class CaptureActivity extends Activity {
	boolean isCCC = false;
	String path = "";
	
	private CameraManager cameraManager;
	  private CaptureActivityHandler handler;
	  private Result lastResult;
	  
    private RelativeLayout mLayout;
    

    CameraManager getCameraManager() {
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

		LinearLayout btnLOScan = (LinearLayout) findViewById(R.id.btn_lo_scan);
		if (!isCCC) {
			//disable scan button
			btnLOScan.setVisibility(View.INVISIBLE);
		}
		btnLOScan.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		LinearLayout btnLOBack = (LinearLayout) findViewById(R.id.btn_lo_back);
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
        super.onPause();

        if (handler != null) {
          handler.quitSynchronously();
          handler = null;
        }        
        
        cameraManager.stopPreview();
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
	    lastResult = rawResult;
	    ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
	    handleDecodeInternally(rawResult, resultHandler, barcode);
	  }

	// Put up our own UI for how to handle the decoded contents.
	private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
		
//		String BarcodeFormat = rawResult.getBarcodeFormat().toString();
//		String type = resultHandler.getType().toString();
//		
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
	    if (handler == null) {
	      handler = new CaptureActivityHandler(this, null, null, null, cameraManager);
	    }
	  }

}
