package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.android.service.rest.beans.ErrorData;
import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.actuator.ActuatorType;
import com.hci.nip.base.actuator.Speaker;
import com.hci.nip.base.actuator.model.AudioPlayInfo;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.hci.nip.base.util.JsonUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SpeakerHandler extends UrlHandler {

    private static final String TAG = SpeakerHandler.class.getName();

    private static final String PARAM_ACTUATOR_ID = "actuator_id";

    // NOTE: order matters
    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Arrays.asList(
            "/speakers/",
            "/speakers/:" + PARAM_ACTUATOR_ID
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
        String actuatorId = params.get(PARAM_ACTUATOR_ID);

        if (parameterSize == 0) {
            // url: speakers (request details of all speakers)
            return ResponseUtil.getAllActuatorInfoResponse(getAllSpeakers(), "speakers");
        } else if (parameterSize == 1 && actuatorId != null) {
            //  url: speakers/:actuator_id/
            return ResponseUtil.getRequestedActuatorInfo(getAllSpeakers(), actuatorId);
        }
        return getRequestNotSupportedResponse();
    }

    private List<Actuator> getAllSpeakers() {
        return DeviceManagerUtil.getFilteredActuatorsByType(deviceManager.getActuators(), ActuatorType.ACTUATOR_TYPE_SPEAKER);
    }

    @Override
    public RestServer.Response post(RestServer.Request request) {
        Log.v(TAG, "POST request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String actuatorId = params.get(PARAM_ACTUATOR_ID);

        if (parameterSize == 1 && actuatorId != null) {
            // url: speakers/:actuator_id/
            List<Actuator> speakers = getSpeakers(actuatorId);
            if (speakers.isEmpty()) {
                return ResponseUtil.getActuatorNotFoundResponse();
            }
            AudioPlayInfo audioRequest = JsonUtil.getObjectFromJson(request.getRequestBody(), AudioPlayInfo.class);
            return getRequestedSpeakerChange((Speaker) speakers.get(0), audioRequest);
        }
        return getRequestNotSupportedResponse();
    }

    private List<Actuator> getSpeakers(String id) {
        return DeviceManagerUtil.getFilteredActuatorsById(getAllSpeakers(), id);
    }

    private RestServer.Response getRequestedSpeakerChange(Speaker speaker, AudioPlayInfo audioRequest) {
        try {
            AudioPlayInfo audioResponse = processPlayingRequest(speaker, audioRequest);
            return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(audioResponse));
        } catch (Speaker.SpeakerException e) {
            Log.e(TAG, "[SPEAKER] getRequestedSpeakerChange", e);
            return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(e.getErrorCode(), e.getMessage())));
        }
    }

    private AudioPlayInfo processPlayingRequest(Speaker speaker, AudioPlayInfo audioInfo) {
        switch (audioInfo.getStatus()) {
            case RUNNING:
                speaker.startPlaying(audioInfo);
                break;
            case PAUSED:
                speaker.pausePlaying(audioInfo);
                break;
            case STOPPED:
                speaker.stopPlaying(audioInfo);
                break;
        }
        return speaker.getPlayingInfo();
    }
}
