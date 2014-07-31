package com.google.zxing.client.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ConfirmScanActivity extends Activity {
	Bitmap bm = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_confirm_scan);
		
		ImageButton btnLORescan = (ImageButton) findViewById(R.id.btn_img_rescan);
		btnLORescan.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			} 
		});
		
		ImageButton btnLOConfirm = (ImageButton) findViewById(R.id.btn_img_confirm);
		btnLOConfirm.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_OK);
				finish();
			}
		});
	}
	  
		@Override
		protected void onResume() {
			super.onResume();
			
			if (bm != null) {
				bm.recycle();
				bm = null;
			}
			
			bm = CaptureActivity.getCCCPhoto(this, 90);			
			
			ImageView iv = (ImageView) findViewById(R.id.image);
			
			iv.setImageBitmap(bm);
		}
		

		@Override
		protected void onPause() {

			if (bm != null) {
				bm.recycle();
				bm = null;
			}
			
			super.onPause();
		}
}
