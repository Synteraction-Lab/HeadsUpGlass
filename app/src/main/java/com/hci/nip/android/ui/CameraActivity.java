package com.hci.nip.android.ui;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hci.nip.android.BaseActivity;
import com.hci.nip.android.IntentActionType;
import com.hci.nip.android.service.BroadcastService;
import com.hci.nip.android.util.FileUtil;
import com.hci.nip.base.sensor.model.PhotoInfo;
import com.hci.nip.base.sensor.model.VideoRecordInfo;
import com.hci.nip.glass.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ref: https://developer.android.com/guide/topics/media/camera.html#java
 */
public class CameraActivity extends BaseActivity {

    private static final String TAG = CameraActivity.class.getName();

    private static final String DEFAULT_VIDEO_PREVIEW_RESOLUTION = "480p";
    private static final String DEFAULT_PICTURE_RESOLUTION = "1200p";

    // UI elements
    private TextView cameraMessage;
    private FrameLayout preview;

    private volatile long photoKey;
    private volatile PhotoInfo photoInfo;

    private final List<IntentActionType> supportedIntentActionTypes = Arrays.asList(
            IntentActionType.CAMERA_TAKE_PICTURE,
            IntentActionType.CAMERA_START_VIDEO_RECORD,
            IntentActionType.CAMERA_STOP_VIDEO_RECORD,
            IntentActionType.CAMERA_DISABLE_PREVIEW
    );

    private final AtomicBoolean recording = new AtomicBoolean(false);

    private Camera camera = null;
    private CameraPreview cameraPreview;
    private MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        configureUIElements();
    }

    private void configureUIElements() {
        cameraMessage = findViewById(R.id.textCameraMessage);
        preview = findViewById(R.id.frameCameraPreview);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "[CAMERA ACTIVITY] getCameraInstance: ", e);
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCameraPreview();
    }

    private void initCameraPreview() {
        if (camera == null) {
            camera = getCameraInstance();

            preview.removeAllViews();

            cameraPreview = new CameraPreview(this, camera);
            setVideoPreviewResolution(DEFAULT_VIDEO_PREVIEW_RESOLUTION);
            setPictureResolution(DEFAULT_PICTURE_RESOLUTION);
            logCameraInfo();

            preview.addView(cameraPreview);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }


    @Override
    public void onIntentReceive(Context context, IntentActionType intentActionType, Intent intent) {
        Log.d(TAG, "[CAMERA ACTIVITY] onIntentReceive");
        switch (intentActionType) {
            case CAMERA_TAKE_PICTURE:
                Log.i(TAG, "onReceive: CAMERA_TAKE_PICTURE");

                Long photoKey = BroadcastService.getBroadcastIntentId(intent);
                PhotoInfo photoInfo = (PhotoInfo) dataRepository.getRequest(photoKey);

                if (photoInfo.getResolution() != null) {
                    setPictureResolution(photoInfo.getResolution());
                }

                takePicture(photoKey, photoInfo);
                break;

            case CAMERA_START_VIDEO_RECORD:
                if (recording.compareAndSet(false, true)) {
                    Log.d(TAG, "onReceive: CAMERA_START_VIDEO_RECORD");

                    Long videoKey = BroadcastService.getBroadcastIntentId(intent);
                    VideoRecordInfo videoRecordInfo = (VideoRecordInfo) dataRepository.getRequest(videoKey);

                    if (videoRecordInfo.getResolution() != null) {
                        setVideoPreviewResolution(videoRecordInfo.getResolution());
                    }

                    startVideoRecording(videoKey, videoRecordInfo);
                }
                break;

            case CAMERA_STOP_VIDEO_RECORD:
                if (recording.compareAndSet(true, false)) {
                    Log.d(TAG, "onReceive: CAMERA_STOP_VIDEO_RECORD");

                    Long videoKey = BroadcastService.getBroadcastIntentId(intent);
                    VideoRecordInfo videoRecordInfo = (VideoRecordInfo) dataRepository.getRequest(videoKey);

                    stopVideoRecording(videoKey, videoRecordInfo);
                }
                break;

            case CAMERA_DISABLE_PREVIEW:
                Log.i(TAG, "onReceive: CAMERA_DISABLE_PREVIEW");

                onBackPressed();
                break;
        }
    }

    private void takePicture(Long newPhotoKey, PhotoInfo newPhotoInfo) {
        photoKey = newPhotoKey;
        photoInfo = newPhotoInfo;

        camera.takePicture(null, null, pictureCallback);

        String takePictureMsg = getString(R.string.cameraTakePhoto);
        Toast.makeText(this, takePictureMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public List<IntentActionType> getIntentActionTypes() {
        return supportedIntentActionTypes;
    }

    private void setVideoPreviewResolution(String resolution) {
        Log.i(TAG, "[CAMERA ACTIVITY] setVideoResolution: " + resolution);
        switch (resolution) {
            case "144p":
                cameraPreview.changeCameraPreviewResolution(176, 144);
                break;
            case "240p":
                cameraPreview.changeCameraPreviewResolution(320, 240);
                break;
            case "480p_2":
                cameraPreview.changeCameraPreviewResolution(720, 480);
                break;
            case "480p_3":
                cameraPreview.changeCameraPreviewResolution(800, 480);
                break;
            case "720p":
                cameraPreview.changeCameraPreviewResolution(1280, 720);
                break;
            case "1080p":
                cameraPreview.changeCameraPreviewResolution(1920, 1080);
                break;
            case "480p":
            default:
                cameraPreview.changeCameraPreviewResolution(640, 480);
                break;
        }
    }

    private void setPictureResolution(String resolution) {
        Log.i(TAG, "[CAMERA ACTIVITY] setPictureResolution: " + resolution);
        Camera.Parameters parameters = camera.getParameters();
        switch (resolution) {
            case "480p":
                parameters.setPictureSize(640, 480);
                break;
            case "1536p":
                parameters.setPictureSize(2048, 1536);
                break;
            case "1944p":
                parameters.setPictureSize(2592, 1944);
                break;
            case "2448p":
                parameters.setPictureSize(3264, 2448);
                break;
            case "1200p":
            default:
                parameters.setPictureSize(1600, 1200);
                break;
        }
        camera.setParameters(parameters);
    }


    private void logCameraInfo() {
//        Camera.Size pictureSize = camera.getParameters().getPictureSize();
//        Log.i(TAG, "CameraInfo: Picture size: " + pictureSize.width + "," + pictureSize.height);
//        Camera.Size previewSize = camera.getParameters().getPreviewSize();
//        Log.i(TAG, "CameraInfo: Preview size: " + previewSize.width + "," + previewSize.height);

//        List<Camera.Size> cameraSize = parameters.getSupportedPreviewSizes();
//        for (Camera.Size size : cameraSize) {
//            Log.i(TAG, "Supported: Preview sizes: " + size.width + "x" + size.height);
//        }
//
//        cameraSize = parameters.getSupportedPictureSizes();
//        for (Camera.Size size : cameraSize) {
//            Log.i(TAG, "Supported: Picture sizes: " + size.width + "x" + size.height);
//        }
//
//        cameraSize = parameters.getSupportedVideoSizes();
//        for (Camera.Size size : cameraSize) {
//            Log.i(TAG, "Supported: Video sizes: " + size.width + "x" + size.height);
//        }
    }


    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String imageName = getImageNameWithExtension(photoInfo.getDest());
            try {
                FileUtil.writeFile(data, imageName);

                dataRepository.addResponse(photoKey, new PhotoInfo(photoInfo).setDest(imageName));
                startImageReview(imageName, photoInfo.getPostViewMillis());
            } catch (FileUtil.FileException e) {
                Log.e(TAG, "[CAMERA ACTIVITY]Error in saving image " + imageName, e);
            }
        }
    };

    private void startImageReview(String imageName, long millis) {
        Log.i(TAG, "[CAMERA ACTIVITY] startImageReview: " + imageName + ", " + millis + "ms");
        if (millis > 0) {
            Intent intent = new Intent(this, ImageReview.class);
            intent.putExtra(ImageReview.IMAGE_NAME, imageName);
            intent.putExtra(ImageReview.IMAGE_POST_VIEW_MILLIS, millis);
            startActivity(intent);
        }
    }

    private static String getImageNameWithExtension(String name) {
        String pathname = name;
        if (pathname == null || pathname.trim().isEmpty()) {
            pathname = "IMG_" + FileUtil.getFormattedCurrentDateTime() + ".jpg";
        }
        return pathname;
    }

    private static String getVideoNameWithExtension(String name) {
        String pathname = name;
        if (pathname == null || pathname.trim().isEmpty()) {
            pathname = "VID_" + FileUtil.getFormattedCurrentDateTime() + ".3gp";
        }
        return pathname;
    }


    private void startVideoRecording(Long videoKey, VideoRecordInfo videoRecordInfo) {

        Camera.Size previewSize = camera.getParameters().getPreviewSize();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mediaRecorder.setCamera(camera);

        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        String videoFileName = getVideoNameWithExtension(videoRecordInfo.getDest());
        mediaRecorder.setOutputFile(FileUtil.getAbsoluteFilePath(videoFileName));
        // hint: the video size must be consistent with preview size!
        mediaRecorder.setVideoSize(previewSize.width, previewSize.height);

        // Step 5: Set the preview output
        mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

        try {
            // Step 6: Prepare configured MediaRecorder
            mediaRecorder.prepare();

            mediaRecorder.start();

            cameraMessage.setText(getString(R.string.cameraRecording));
            Toast.makeText(this, getString(R.string.cameraRecordingStarting), Toast.LENGTH_SHORT).show();
            dataRepository.addResponse(videoKey, new VideoRecordInfo(videoRecordInfo).setDest(videoFileName));
        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException preparing MediaRecorder", e);
            releaseMediaRecorder();
        } catch (IOException e) {
            Log.e(TAG, "IOException preparing MediaRecorder", e);
            releaseMediaRecorder();
        }
    }

    private void stopVideoRecording(Long videoKey, VideoRecordInfo videoRecordInfo) {
        // stop recording and release camera
        mediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object

        cameraMessage.setText(getString(R.string.cameraPreview));
        Toast.makeText(this, getString(R.string.cameraRecordingStopping), Toast.LENGTH_SHORT).show();
        dataRepository.addResponse(videoKey, videoRecordInfo);
    }


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock();           // lock camera for later use
        }

        recording.set(false);
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

}
