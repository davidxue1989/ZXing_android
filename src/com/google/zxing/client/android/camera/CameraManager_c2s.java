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
//package com.google.zxing.client.android.camera;
//
//import android.app.Activity;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.hardware.Camera;
//import android.hardware.Camera.Size;
//import android.os.Build;
//import android.os.Handler;
//import android.util.Log;
//import android.widget.RelativeLayout;
//
//import com.google.zxing.PlanarYUVLuminanceSource;
//import com.google.zxing.client.android.CaptureActivity;
//import com.google.zxing.client.android.camera.C2SCameraPreview.PreviewReadyCallback;
//
//public final class CameraManager implements PreviewReadyCallback {
//	
//	
////	// dxdelete
//	public void setTorch(boolean b) {
//		// TODO Auto-generated method stub
//
//	}
////	public Rect getFramingRect() {
////		// TODO Auto-generated method stub
////		return null;
////	}
////
////	public void startPreview() {
////		// TODO Auto-generated method stub
////
////	}
////
////	public CameraManager(Context context) {
////		// TODO Auto-generated constructor stub
////	}
////
////	public void openDriver(SurfaceHolder surfaceHolder) throws IOException {
////		// TODO Auto-generated method stub
////
////	}
////
////	public void closeDriver() {
////		// TODO Auto-generated method stub
////
////	}
////
////	public void setManualFramingRect(int width, int height) {
////		// TODO Auto-generated method stub
////
////	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//
//  private static final String TAG = CameraManager.class.getSimpleName();
//
//  private Activity activity;
////  private final CameraConfigurationManager configManager;
//  public Camera camera;
//  private int mCameraId;
//
//  /**
//   * Preview frames are delivered here, which we pass on to the registered handler. Make sure to
//   * clear the handler so it will only receive one message.
//   */
//  private PreviewCallback previewCallback = null;
//	private C2SCameraPreview mPreview;
//    private boolean previewing = false;
//  
//  
////  private AutoFocusManager autoFocusManager;
//  private Rect framingRect;
//  private Rect framingRectInPreview;
//  private final static double cropPreviewFactor = 0.7;
//  
//
//  private AutoFocusManager autoFocusManager;
//  
//
//  public CameraManager(Activity activity) {
//    this.activity = activity;
////    this.configManager = new CameraConfigurationManager(context);
//    previewCallback = new PreviewCallback(mPreview);
//  }
//
//public synchronized boolean isOpen() {
//    return camera != null;
//  }
//  
//  public boolean isPreviewing() {
//	  return previewing;
//  }
//  
//  public void setupCamera() {
//	// Set the second argument by your choice.
//      // Usually, 0 for back-facing camera, 1 for front-facing camera.
//      // If the OS is pre-gingerbread, this does not have any effect.
//		mCameraId = 0;
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//			camera = Camera.open(mCameraId);
//		} else {
//			camera = Camera.open();
//		}
//	}
//  
//	@Override
//	public void onPreviewReady() {
//		previewing = true;
//
//		Size sz = mPreview.getPreviewSize();
//		if (sz == null)
//			Log.d(TAG, "in onPreviewReady(), getPreviewSize() is null!");
//		else
//			previewCallback.cameraResolution = new Point(sz.width, sz.height);
//		
//		((CaptureActivity) activity).startHandler();
//	}
//
//
//  /**
//	 * Asks the camera hardware to begin drawing preview frames to the screen.
//	 */
//  public synchronized void startPreview(RelativeLayout layout) {
//      setupCamera();
//      mPreview = new C2SCameraPreview(activity, camera, C2SCameraPreview.LayoutMode.FitToParent, layout, this);
//      //previewing will be set to true in onPreviewReady callback
//
//      autoFocusManager = new AutoFocusManager(activity, camera);
//  }
//
//  /**
//   * Tells the camera to stop drawing preview frames.
//   */
//  public synchronized void stopPreview() {
//	  if (autoFocusManager != null) {
//	      autoFocusManager.stop();
//	      autoFocusManager = null;
//	  }
//	  
//	  mPreview.stop();
//      mPreview = null;
//      previewCallback.setHandler(null, 0);
//	  previewing = false;
//  }
//
//
//  
/////**
//// * Opens the camera driver and initializes the hardware parameters.
//// *
//// * @param holder The surface object which the camera will draw preview frames into.
//// * @throws IOException Indicates the camera driver failed to open.
//// */
////public synchronized void openDriver(SurfaceHolder holder) throws IOException {	  
////  Camera theCamera = camera;
////  if (theCamera == null) {
////    theCamera = OpenCameraInterface.open();
////    if (theCamera == null) {
////      throw new IOException();
////    }
////    camera = theCamera;
////  }
////  theCamera.setPreviewDisplay(holder);
////
////  if (!initialized) {
////    initialized = true;
////    configManager.initFromCameraParameters(theCamera);
////    if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
////      setManualFramingRect(requestedFramingRectWidth, requestedFramingRectHeight);
////      requestedFramingRectWidth = 0;
////      requestedFramingRectHeight = 0;
////    }
////  }
////
////  Camera.Parameters parameters = theCamera.getParameters();
////  String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save these, temporarily
////  try {
////    configManager.setDesiredCameraParameters(theCamera, false);
////  } catch (RuntimeException re) {
////    // Driver failed
////    Log.w(TAG, "Camera rejected parameters. Setting only minimal safe-mode parameters");
////    Log.i(TAG, "Resetting to saved camera params: " + parametersFlattened);
////    // Reset:
////    if (parametersFlattened != null) {
////      parameters = theCamera.getParameters();
////      parameters.unflatten(parametersFlattened);        
////      try {
////        theCamera.setParameters(parameters);
////        configManager.setDesiredCameraParameters(theCamera, true);
////      } catch (RuntimeException re2) {
////        // Well, darn. Give up
////        Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
////      }
////    }
////  }
////}
//
/////**
////* Closes the camera driver if still in use.
////*/
////public synchronized void closeDriver() {
////	  
////if (camera != null) {
////  camera.release();
////  camera = null;
////  // Make sure to clear these each time we close the camera, so that any scanning rect
////  // requested by intent is forgotten.
////  framingRect = null;
////  framingRectInPreview = null;
////}
////}
//
//  /**
//   * A single preview frame will be returned to the handler supplied. The data will arrive as byte[]
//   * in the message.obj field, with width and height encoded as message.arg1 and message.arg2,
//   * respectively.
//   *
//   * @param handler The handler to send the message to.
//   * @param message The what field of the message to be sent.
//   */
//  public synchronized void requestPreviewFrame(Handler handler, int message) {
//    Camera theCamera = camera;
//    if (theCamera != null && previewing) {
//      previewCallback.setHandler(handler, message);
//      theCamera.setOneShotPreviewCallback(previewCallback);
//    }
//  }
//
////  /**
////   * Calculates the framing rect which the UI should draw to show the user where to place the
////   * barcode. This target helps with alignment as well as forces the user to hold the device
////   * far enough away to ensure the image will be in focus.
////   *
////   * @return The rectangle to draw on screen in window coordinates.
////   */
////  public synchronized Rect getFramingRect() {
////	  
////    if (framingRect == null) {
////      if (camera == null) {
////        return null;
////      }
////      Point screenResolution = configManager.getScreenResolution();
////      if (screenResolution == null) {
////        // Called early, before init even finished
////        return null;
////      }
////
////      int width = findDesiredDimensionInRange(screenResolution.x, MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
////      int height = findDesiredDimensionInRange(screenResolution.y, MIN_FRAME_HEIGHT, MAX_FRAME_HEIGHT);
////
////      int leftOffset = (screenResolution.x - width) / 2;
////      int topOffset = (screenResolution.y - height) / 2;
////      framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
////      Log.d(TAG, "Calculated framing rect: " + framingRect);
////    }
////    return framingRect;
////  }
//  
////  private static int findDesiredDimensionInRange(int resolution, int hardMin, int hardMax) {
////    int dim = 5 * resolution / 8; // Target 5/8 of each dimension
////    if (dim < hardMin) {
////      return hardMin;
////    }
////    if (dim > hardMax) {
////      return hardMax;
////    }
////    return dim;
////  }
//
//  /**
//   * Like {@link #getFramingRect} but coordinates are in terms of the preview frame,
//   * not UI / screen.
//   */
//  public synchronized Rect getFramingRectInPreview() {
//	Size sz = mPreview.getPreviewSize();
//    framingRect = new Rect((int) (sz.width*(1-cropPreviewFactor)*0.5), 
//    					   (int) (sz.height*(1-cropPreviewFactor)*0.5), 
//    					   (int) (sz.width*(1-cropPreviewFactor*0.5)), 
//    					   (int) (sz.height*(1-cropPreviewFactor*0.5)));
//    Log.d(TAG, "Calculated framing rect: " + framingRect); 
//    return framingRectInPreview;
//  }
//  
//  
//  /**
//   * A factory method to build the appropriate LuminanceSource object based on the format
//   * of the preview buffers, as described by Camera.Parameters.
//   *
//   * @param data A preview frame.
//   * @param width The width of the image.
//   * @param height The height of the image.
//   * @return A PlanarYUVLuminanceSource instance.
//   */
//  public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
//    Rect rect = getFramingRectInPreview();
//    if (rect == null) {
//      return null;
//    }
//    // Go ahead and assume it's YUV rather than die.
//    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
//                                        rect.width(), rect.height(), false);
//  }
//
//}
