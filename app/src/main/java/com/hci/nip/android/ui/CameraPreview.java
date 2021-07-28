package com.hci.nip.android.ui;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getName();

    private final SurfaceHolder mHolder;
    private final Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void changeCameraPreviewResolution(int w, int h) {
        Log.d(TAG, "changeCameraPreviewResolution");
        if (mHolder.getSurface() == null) {
            return;
        }

        stopCameraPreview();
        setCameraPreviewParameters(w, h);
        startCameraPreview(mHolder);
    }

    private void stopCameraPreview() {
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.e(TAG, "Ignored:", e);
        }
    }

    /**
     * ref: https://stackoverflow.com/questions/8744994/android-camera-set-resolution
     */
    private void setCameraPreviewParameters(int w, int h) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(w, h);
        mCamera.setParameters(parameters);
    }

    private void startCameraPreview(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Error starting camera preview", e);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        startCameraPreview(holder);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Take care of releasing the Camera preview in your activity
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null) {
            return;
        }

        stopCameraPreview();
        // set preview size and make any resize, rotate or reformatting changes here
        startCameraPreview(mHolder);
    }

}
