package com.google.zxing.client.android;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.SurfaceView;

import com.google.zxing.client.android.camera.CameraManager;

public final class C2SCameraPreview extends SurfaceView {

	public List<Camera.Size> mSupportedPreviewSizes;
	public Camera.Size mPreviewSize;
	
	public C2SCameraPreview(Context context) {
		super(context);
	}

	private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
	
	public void setPreviewSizeAndStart(CameraManager cameraManager) {
		Camera camera = cameraManager.camera;
		Camera.Parameters parameters = camera.getParameters();
		if (mPreviewSize != null) {
			parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
			camera.setParameters(parameters);
			cameraManager.startPreview();

			Parameters p = camera.getParameters();
			Size sz = p.getPreviewSize();
			int a = 0;
			a++;
		}
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
           mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }
	
}
