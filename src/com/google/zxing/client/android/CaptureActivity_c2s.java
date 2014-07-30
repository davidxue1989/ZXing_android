///*
// * Copyright (C) 2008 ZXing authors
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.google.zxing.client.android;
//
//import java.text.DateFormat;
//import java.util.Date;
//
//import android.app.Activity;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Handler;
//import android.preference.PreferenceManager;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.RelativeLayout;
//
//import com.google.zxing.Result;
//import com.google.zxing.client.android.camera.CameraManager;
//import com.google.zxing.client.android.result.ResultHandler;
//import com.google.zxing.client.android.result.ResultHandlerFactory;
//
//public class CaptureActivity extends Activity {
//	private CameraManager cameraManager;
//	  private CaptureActivityHandler handler;
//	
//
//	  private Result lastResult;
//	
//    private RelativeLayout mLayout;
//    
//
//    CameraManager getCameraManager() {
//      return cameraManager;
//    }
//
//    public Handler getHandler() {
//      return handler;
//    }
//    
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//
//		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		setContentView(R.layout.capture);
//		
//		mLayout = (RelativeLayout) findViewById(R.id.preview_frame);
//        
//
//	    //dxchanged
//	    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//	    SharedPreferences.Editor prefsEdit = PreferenceManager.getDefaultSharedPreferences(this).edit();
//	    prefsEdit.putBoolean(PreferencesActivity.KEY_DECODE_1D, false);
//	    prefsEdit.putBoolean(PreferencesActivity.KEY_DECODE_DATA_MATRIX, false);
//	    prefsEdit.putBoolean(PreferencesActivity.KEY_DECODE_QR, true); 
//	    prefsEdit.commit();
//	}
//	
//	
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        cameraManager = new CameraManager(this);
//        cameraManager.startPreview(mLayout);
//        
//        handler = null;
//    }
//    
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        if (handler != null) {
//          handler.quitSynchronously();
//          handler = null;
//        }
//        
//        
//        cameraManager.stopPreview();
//
//        
//    }
//	public void restartPreviewAfterDelay(long delayMS) {
//		if (handler != null) {
//		  handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
//		}
//	}
//	
//
//	  /**
//	   * A valid barcode has been found, so give an indication of success and show the results.
//	   *
//	   * @param rawResult The contents of the barcode.
//	   * @param scaleFactor amount by which thumbnail was scaled
//	   * @param barcode   A greyscale bitmap of the camera data which was decoded.
//	   */
//	  public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
//	    lastResult = rawResult;
//	    ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
//	    handleDecodeInternally(rawResult, resultHandler, barcode);
//	  }
//
//	// Put up our own UI for how to handle the decoded contents.
//	private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
//		
//		String BarcodeFormat = rawResult.getBarcodeFormat().toString();
//		String type = resultHandler.getType().toString();
//		
//		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
//		String date = formatter.format(new Date(rawResult.getTimestamp()));
//
//		CharSequence displayContents = resultHandler.getDisplayContents();
//		String content = (String) displayContents;
//		
//		int a = 0;
//		a++;
//		
//		
//	  //barcodeImageView.setImageBitmap(barcode);
//		
//	//formatTextView.setText(rawResult.getBarcodeFormat().toString());
//		
//	//typeTextView.setText(resultHandler.getType().toString());
//		
//	//DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
//	//timeTextView.setText(formatter.format(new Date(rawResult.getTimestamp())));
//	
//	
//	//Map<ResultMetadataType,Object> metadata = rawResult.getResultMetadata();
//	//if (metadata != null) {
//	//  StringBuilder metadataText = new StringBuilder(20);
//	//  for (Map.Entry<ResultMetadataType,Object> entry : metadata.entrySet()) {
//	//    if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
//	//      metadataText.append(entry.getValue()).append('\n');
//	//    }
//	//  }
//	//  if (metadataText.length() > 0) {
//	//    metadataText.setLength(metadataText.length() - 1);
//	//    metaTextView.setText(metadataText);
//	//    metaTextView.setVisibility(View.VISIBLE);
//	//    metaTextViewLabel.setVisibility(View.VISIBLE);
//	//  }
//	//}
//	
//	//CharSequence displayContents = resultHandler.getDisplayContents();
//	//contentsTextView.setText(displayContents);
//		
//	
//	
//	}
//
//	  public void startHandler() {
//	    if (handler == null) {
//	      handler = new CaptureActivityHandler(this, null, null, null, cameraManager);
//	    }
//	  }
//
//}
