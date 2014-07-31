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

package com.google.zxing.client.android.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.camera.C2SCameraPreview.PreviewReadyCallback;

public final class CameraManager implements PreviewReadyCallback, Camera.AutoFocusCallback {
	
	
//	// dxdelete
	public void setTorch(boolean b) {
		// TODO Auto-generated method stub

	}
	
	
  private static final String TAG = CameraManager.class.getSimpleName();
  
  private static final boolean portraitMode = true;
  
  private CaptureActivity activity;
  public Camera camera;
  private int mCameraId;
  private Handler takePhotoHandle;
  private boolean startedPictureProcess = false;

  /**
   * Preview frames are delivered here, which we pass on to the registered handler. Make sure to
   * clear the handler so it will only receive one message.
   */
  private PreviewCallback previewCallback = null;
	private C2SCameraPreview mPreview;
    private boolean previewing = false;
    private Rect framingRectInPreview;
    private final static double cropPreviewFactor = 0.6;
    private AutoFocusManager autoFocusManager;
  

  public CameraManager(CaptureActivity activity) {
    this.activity = activity;
    previewCallback = new PreviewCallback(activity);
    takePhotoHandle = new Handler();
  }

public synchronized boolean isOpen() {
    return camera != null;
  }
  
  public boolean isPreviewing() {
	  return previewing;
  }
  
  public void setupCamera() {
	// Set the second argument by your choice.
      // Usually, 0 for back-facing camera, 1 for front-facing camera.
      // If the OS is pre-gingerbread, this does not have any effect.
		mCameraId = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			camera = Camera.open(mCameraId);
		} else {
			camera = Camera.open();
		}
	}
  
	@Override
	public void onPreviewReady() {
		previewing = true;		
		if (!activity.isCCC) {
			if (autoFocusManager != null) {
				autoFocusManager.start();
			}
			activity.startHandler();
		}
	}


  /**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */
  public synchronized void startPreview(RelativeLayout layout) {
      setupCamera();
      mPreview = new C2SCameraPreview(activity, camera, C2SCameraPreview.LayoutMode.FitToParent, layout, this);//previewing will be set to true in onPreviewReady callback
      autoFocusManager = new AutoFocusManager(activity, camera);
  }

  /**
   * Tells the camera to stop drawing preview frames.
   */
  public synchronized void stopPreview() {
	  if (autoFocusManager != null) {
	      autoFocusManager.stop();
	      autoFocusManager = null;
	  }

      previewCallback.setHandler(null, 0);
	  mPreview.stop();
      mPreview = null;
	  previewing = false;
  }

  /**
   * A single preview frame will be returned to the handler supplied. The data will arrive as byte[]
   * in the message.obj field, with width and height encoded as message.arg1 and message.arg2,
   * respectively.
   *
   * @param handler The handler to send the message to.
   * @param message The what field of the message to be sent.
   */
  public synchronized void requestPreviewFrame(Handler handler, int message) {
    Camera theCamera = camera;
    if (theCamera != null && previewing) {
      previewCallback.setHandler(handler, message);
      theCamera.setOneShotPreviewCallback(previewCallback);
    }
  }
  
  /**
   * Like {@link #getFramingRect} but coordinates are in terms of the preview frame,
   * not UI / screen.
   */
  public synchronized Rect getFramingRectInPreview() {
	Size sz = mPreview.getPreviewSize();
	framingRectInPreview = new Rect((int) (sz.width*(1-cropPreviewFactor)*0.5), 
    					   (int) (sz.height*(1-cropPreviewFactor)*0.5), 
    					   (int) (sz.width*(1-cropPreviewFactor*0.5)), 
    					   (int) (sz.height*(1-cropPreviewFactor*0.5)));
    Log.d(TAG, "Calculated framing rect in preview: " + framingRectInPreview); 
    return framingRectInPreview;
  }
  
  
  /**
   * A factory method to build the appropriate LuminanceSource object based on the format
   * of the preview buffers, as described by Camera.Parameters.
   *
   * @param data A preview frame.
   * @param width The width of the image.
   * @param height The height of the image.
   * @return A PlanarYUVLuminanceSource instance.
   */
  public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
    Rect rect = getFramingRectInPreview();
    if (rect == null) {
      return null;
    }
    // Go ahead and assume it's YUV rather than die.
    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                                        rect.width(), rect.height(), false, portraitMode);
  }

	public void takePhoto() {
		if (!startedPictureProcess) {
			autoFocusManager.start(this);
			startedPictureProcess = true;
		}
	}
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		takePhotoHandle.post(takePhoto);
	}
	private Runnable takePhoto = new Runnable() {
		@Override
		public void run() {
			if (!previewing){
				return; //already quited previewing, we are only here because this thread is delayed
			}
			camera.takePicture(shutterCallback, null, pictureCallback);
			Log.v("take", "about to take");
		}
	};
	// code to execute after picture has been taken but 'before' its finished being processed
	Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
		public void onShutter() {
			// dont need to add anything here, just by making it, it will add
			// the defualt shutter handler which is a "clicking noise" when a
			// picture is taken
		}
	};
	private PictureCallback pictureCallback = new PictureCallback() {
	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	        File pictureFile = new File(CaptureActivity.path);
	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	        } catch (FileNotFoundException e) {
	        } catch (IOException e) {
	        }
	        
//			camera.startPreview();
			System.gc();
			Log.v("got photo", "got photo");
			
			startedPictureProcess = false;
			activity.onCCCPhoto();
	    }
	};
}
