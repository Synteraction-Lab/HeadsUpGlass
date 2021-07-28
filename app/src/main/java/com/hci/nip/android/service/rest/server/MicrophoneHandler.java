package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.android.service.rest.beans.ErrorData;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.sensor.Microphone;
import com.hci.nip.base.sensor.Sensor;
import com.hci.nip.base.sensor.SensorType;
import com.hci.nip.base.sensor.model.AudioRecordInfo;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.hci.nip.base.util.JsonUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MicrophoneHandler extends UrlHandler {

    private static final String TAG = MicrophoneHandler.class.getName();

    private static final String PARAM_SENSOR_ID = "sensor_id";

    // NOTE: order matters
    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Arrays.asList(
            "/microphones/",
            "/microphones/:" + PARAM_SENSOR_ID + "/"
    ));

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
        String sensorId = params.get(PARAM_SENSOR_ID);

        if (parameterSize == 0) {
            // url: microphones (request details of all microphones)
            return ResponseUtil.getAllSensorInfoResponse(getAllMicrophones(), "microphones");
        } else if (parameterSize == 1 && sensorId != null) {
            //  url: microphones/:sensor_id/
            return ResponseUtil.getRequestedSensorInfoResponse(getAllMicrophones(), sensorId);
        }
        return getRequestNotSupportedResponse();
    }

    private List<Sensor> getAllMicrophones() {
        return DeviceManagerUtil.getFilteredSensorsByType(deviceManager.getSensors(), SensorType.SENSOR_TYPE_MICROPHONE);
    }

    @Override
    public RestServer.Response post(RestServer.Request request) {
        Log.v(TAG, "POST request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String sensorId = params.get(PARAM_SENSOR_ID);

        if (parameterSize == 1 && sensorId != null) {
            // url: microphones/:sensor_id
            List<Sensor> microphones = getMicrophones(sensorId);
            if (microphones.isEmpty()) {
                return ResponseUtil.getSensorNotFoundResponse();
            }
            AudioRecordInfo audioRequest = JsonUtil.getObjectFromJson(request.getRequestBody(), AudioRecordInfo.class);
            return getRequestedAudioChange((Microphone) microphones.get(0), audioRequest);
        }
        return getRequestNotSupportedResponse();
    }

    private List<Sensor> getMicrophones(String id) {
        return DeviceManagerUtil.getFilteredSensorsById(getAllMicrophones(), id);
    }

    private RestServer.Response getRequestedAudioChange(Microphone microphone, AudioRecordInfo audioRequest) {
        try {
            AudioRecordInfo audioResponse = audioRequest.isStream() ? processStreamingRequest(microphone, audioRequest) : processRecordingRequest(microphone, audioRequest);
            return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(audioResponse));
        } catch (Microphone.MicrophoneException e) {
            Log.e(TAG, "[MICROPHONE] getRequestedAudioChange", e);
            return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(e.getErrorCode(), e.getMessage())));
        }
    }

    private AudioRecordInfo processRecordingRequest(Microphone microphone, AudioRecordInfo audioInfo) {
        switch (audioInfo.getStatus()) {
            case RUNNING:
                microphone.startRecording(audioInfo);
                break;
            case PAUSED:
                microphone.pauseRecording(audioInfo);
                break;
            case STOPPED:
                microphone.stopRecording(audioInfo);
                break;
        }
        return microphone.getRecordingInfo();
    }

    private AudioRecordInfo processStreamingRequest(Microphone microphone, AudioRecordInfo audioInfo) {
        switch (audioInfo.getStatus()) {
            case RUNNING:
                microphone.startLiveStreaming(audioInfo);
                break;
            case STOPPED:
            case PAUSED:
                microphone.stopLiveStreaming(audioInfo);
                break;
        }
        return microphone.getLiveStreamingInfo();
    }

}