package com.hci.nip.android.sensors;

import android.util.Log;

import com.hci.nip.android.IntentActionType;
import com.hci.nip.android.service.BroadcastService;
import com.hci.nip.android.service.ErrorCodes;
import com.hci.nip.android.service.ServiceProvider;
import com.hci.nip.base.sensor.Camera;
import com.hci.nip.base.sensor.SensorLocation;
import com.hci.nip.base.sensor.SensorType;
import com.hci.nip.base.sensor.model.CameraPreviewInfo;
import com.hci.nip.base.sensor.model.PhotoInfo;
import com.hci.nip.base.sensor.model.VideoRecordInfo;

import java.util.List;

public class CameraSensor extends ServiceProvider implements Camera {

    private static final String TAG = CameraSensor.class.getName();
    private static final int DEFAULT_PICTURE_WAIT_MILLIS = 3000;
    private static final int DEFAULT_VIDEO_RECORD_WAIT_MILLIS = 3000;

    private final String id;

    private volatile boolean active = false;

    public CameraSensor(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SensorType getType() {
        return SensorType.SENSOR_TYPE_CAMERA;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.camera";
    }

    @Override
    public SensorLocation getLocation() {
        return SensorLocation.SENSOR_LOCATION_HEAD;
    }

    @Override
    public long getSampleRate() {
        return 0;
    }

    @Override
    public long getBufferSize() {
        return 0;
    }

    @Override
    public String getResolution() {
        return "480p";
    }

    @Override
    public String getDataFormat() {
        return "";
    }

    @Override
    public void open() {
        // DO NOTHING
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void activate() {
        // DO NOTHING
    }

    @Override
    public void deactivate() {
        // DO NOTHING
    }

    @Override
    public void close() {
        deactivate();
    }

    @Override
    public List<?> readData() {
        return null;
    }

    @Override
    public void enablePreview(CameraPreviewInfo cameraPreviewInfo) {
        Log.i(TAG, "[CAMERA] enablePreview");
        // TODO: control the preview display (show or not show)
        Long uniqueKey = dataRepository.getUniqueKey();
        broadcastService.sendBroadcast(BroadcastService.getBroadcastIntent(IntentActionType.CAMERA_ENABLE_PREVIEW, uniqueKey));
    }

    @Override
    public void disablePreview(CameraPreviewInfo cameraPreviewInfo) {
        Log.i(TAG, "[CAMERA] disablePreview");
        Long uniqueKey = dataRepository.getUniqueKey();
        broadcastService.sendBroadcast(BroadcastService.getBroadcastIntent(IntentActionType.CAMERA_DISABLE_PREVIEW, uniqueKey));
    }

    @Override
    public PhotoInfo takePicture(PhotoInfo photoInfo) throws CameraException {
        Long uniqueKey = dataRepository.getUniqueKey();
        dataRepository.addRequest(uniqueKey, new PhotoInfo(photoInfo));
        broadcastService.sendBroadcast(BroadcastService.getBroadcastIntent(IntentActionType.CAMERA_TAKE_PICTURE, uniqueKey));

        PhotoInfo photoInfoResponse = (PhotoInfo) dataRepository.waitForResponse(uniqueKey, DEFAULT_PICTURE_WAIT_MILLIS);
        if (photoInfoResponse == null) {
            throw new CameraException(ErrorCodes.PICTURE_REQUEST_TIMEOUT);
        }
        return photoInfoResponse;
    }

    @Override
    public VideoRecordInfo startRecording(VideoRecordInfo videoRecordInfo) throws CameraException {
        Long uniqueKey = dataRepository.getUniqueKey();
        dataRepository.addRequest(uniqueKey, new VideoRecordInfo(videoRecordInfo));
        broadcastService.sendBroadcast(BroadcastService.getBroadcastIntent(IntentActionType.CAMERA_START_VIDEO_RECORD, uniqueKey));

        VideoRecordInfo recordInfoResponse = (VideoRecordInfo) dataRepository.waitForResponse(uniqueKey, DEFAULT_VIDEO_RECORD_WAIT_MILLIS);
        if (recordInfoResponse == null) {
            throw new CameraException(ErrorCodes.VIDEO_START_FAILED);
        }
        return recordInfoResponse;
    }

    @Override
    public VideoRecordInfo stopRecording(VideoRecordInfo videoRecordInfo) throws CameraException {
        Long uniqueKey = dataRepository.getUniqueKey();
        dataRepository.addRequest(uniqueKey, new VideoRecordInfo(videoRecordInfo));
        broadcastService.sendBroadcast(BroadcastService.getBroadcastIntent(IntentActionType.CAMERA_STOP_VIDEO_RECORD, uniqueKey));

        VideoRecordInfo recordInfoResponse = (VideoRecordInfo) dataRepository.waitForResponse(uniqueKey, DEFAULT_VIDEO_RECORD_WAIT_MILLIS);
        if (recordInfoResponse == null) {
            throw new CameraException(ErrorCodes.VIDEO_STOP_FAILED);
        }
        return recordInfoResponse;
    }

    // TODO: IMPLEMENT
    @Override
    public VideoRecordInfo getRecordingInfo() {
        return new VideoRecordInfo("0");
    }
}
