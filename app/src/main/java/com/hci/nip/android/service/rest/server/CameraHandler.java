package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.android.service.rest.beans.ErrorData;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.sensor.Camera;
import com.hci.nip.base.sensor.Sensor;
import com.hci.nip.base.sensor.SensorType;
import com.hci.nip.base.sensor.model.CameraPreviewInfo;
import com.hci.nip.base.sensor.model.PhotoInfo;
import com.hci.nip.base.sensor.model.VideoRecordInfo;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.hci.nip.base.util.JsonUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CameraHandler extends UrlHandler {

    private static final String TAG = CameraHandler.class.getName();

    private static final String PARAM_CAMERA_ID = "camera_id";
    private static final String PARAM_CAMERA_COMMAND = "camera_command";
    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Arrays.asList(
            "/cameras/",
            "/cameras/:" + PARAM_CAMERA_ID + "/:" + PARAM_CAMERA_COMMAND
    ));

    private static final String COMMAND_PREVIEW = "preview";
    private static final String COMMAND_PICTURE = "picture";
    private static final String COMMAND_VIDEO = "video";

    private static final String STATUS_START = "START";
    private static final String STATUS_STOP = "STOP";

    @Override
    public List<String> getStaticUrls() {
        return STATIC_URLS;
    }

    @Override
    public RestServer.Response get(RestServer.Request request) {
        Log.v(TAG, "GET request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String url = request.getUrl();
        String cameraId = params.get(PARAM_CAMERA_ID);

        if (parameterSize == 0) {
            // url: cameras (request details of all cameras)
            return ResponseUtil.getAllSensorInfoResponse(getAllCameras(), "cameras");
        } else if (parameterSize == 1 && cameraId != null) {
            //  url: cameras/:camera_id
            return ResponseUtil.getRequestedSensorInfoResponse(getAllCameras(), cameraId);
        }
        return getRequestNotSupportedResponse();
    }

    private List<Sensor> getAllCameras() {
        return DeviceManagerUtil.getFilteredSensorsByType(deviceManager.getSensors(), SensorType.SENSOR_TYPE_CAMERA);
    }

    @Override
    public RestServer.Response post(RestServer.Request request) {
        Log.v(TAG, "request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String cameraId = params.get(PARAM_CAMERA_ID);
        String command = params.get(PARAM_CAMERA_COMMAND);

        if (parameterSize == 2 && cameraId != null && command != null) {
            //  url: cameras/:camera_id/:camera_command
            List<Sensor> cameras = getCameras(cameraId);
            if (cameras.isEmpty()) {
                return ResponseUtil.getSensorNotFoundResponse();
            }

            Camera camera = (Camera) cameras.get(0);
            switch (command) {
                case COMMAND_PREVIEW:
                    CameraPreviewInfo cameraPreviewRequest = JsonUtil.getObjectFromJson(request.getRequestBody(), CameraPreviewInfo.class);
                    return getCameraPreviewResponse(camera, cameraPreviewRequest);
                case COMMAND_PICTURE:
                    PhotoInfo photoInfoRequest = JsonUtil.getObjectFromJson(request.getRequestBody(), PhotoInfo.class);
                    return getCameraTakePictureResponse(camera, photoInfoRequest);
                case COMMAND_VIDEO:
                    VideoRecordInfo videoRecordRequest = JsonUtil.getObjectFromJson(request.getRequestBody(), VideoRecordInfo.class);
                    return getCameraRecordResponse(camera, videoRecordRequest);
                default:
                    return getRequestNotSupportedResponse();
            }
        }
        return getRequestNotSupportedResponse();
    }

    private List<Sensor> getCameras(String id) {
        return DeviceManagerUtil.getFilteredSensorsById(getAllCameras(), id);
    }

    private RestServer.Response getCameraPreviewResponse(Camera camera, CameraPreviewInfo cameraPreviewRequest) {
        String previewStatus = cameraPreviewRequest.getStatus();

        switch (previewStatus) {
            case STATUS_START:
                camera.enablePreview(cameraPreviewRequest);
                break;
            case STATUS_STOP:
            default:
                camera.disablePreview(cameraPreviewRequest);
                break;
        }
        return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(cameraPreviewRequest));
    }

    private RestServer.Response getCameraTakePictureResponse(Camera camera, PhotoInfo photoInfoRequest) {
        try {
            PhotoInfo photoInfoResponse = camera.takePicture(photoInfoRequest);
            return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(photoInfoResponse));
        } catch (Camera.CameraException e) {
            Log.e(TAG, "[CAMERA] getCameraTakePictureResponse", e);
            return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(e.getErrorCode(), e.getMessage())));
        }
    }

    private RestServer.Response getCameraRecordResponse(Camera camera, VideoRecordInfo videoRecordInfoRequest) {
        String recordingStatus = videoRecordInfoRequest.getStatus();

        VideoRecordInfo videoRecordRes;
        try {
            switch (recordingStatus) {
                case STATUS_START:
                    videoRecordRes = camera.startRecording(videoRecordInfoRequest);
                    break;
                case STATUS_STOP:
                default:
                    videoRecordRes = camera.stopRecording(videoRecordInfoRequest);
                    break;
            }
            return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(videoRecordRes));
        } catch (Camera.CameraException e) {
            Log.e(TAG, "[CAMERA] getCameraRecordResponse", e);
            return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(e.getErrorCode(), e.getMessage())));
        }
    }

}
