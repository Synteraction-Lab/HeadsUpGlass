package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.android.service.rest.beans.ErrorData;
import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.actuator.ActuatorType;
import com.hci.nip.base.actuator.Display;
import com.hci.nip.base.actuator.model.DisplayData;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.hci.nip.base.util.JsonUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DisplayHandler extends UrlHandler {
    private static final String TAG = DisplayHandler.class.getName();

    private static final String PARAM_ACTUATOR_ID = "actuator_id";
    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Arrays.asList(
            "/displays/",
            "/displays/:" + PARAM_ACTUATOR_ID
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
            // url: displays (request details of all displays)
            return ResponseUtil.getAllActuatorInfoResponse(getAllDisplays(), "displays");
        } else if (parameterSize == 1 && actuatorId != null) {
            //  url: displays/:actuator_id/
            return ResponseUtil.getRequestedActuatorInfo(getAllDisplays(), actuatorId);
        }
        return getRequestNotSupportedResponse();
    }

    private List<Actuator> getAllDisplays() {
        return DeviceManagerUtil.getFilteredActuatorsByType(deviceManager.getActuators(), ActuatorType.ACTUATOR_TYPE_DISPLAY);
    }

    @Override
    public RestServer.Response post(RestServer.Request request) {
        Log.v(TAG, "POST request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String actuatorId = params.get(PARAM_ACTUATOR_ID);

        if (parameterSize == 1 && actuatorId != null) {
            // url: displays/:actuator_id/
            List<Actuator> displays = getDisplays(actuatorId);
            if (displays.isEmpty()) {
                return ResponseUtil.getActuatorNotFoundResponse();
            }
            DisplayData displayDataRequest = JsonUtil.getObjectFromJson(request.getRequestBody(), DisplayData.class);
            return getDisplayChangeResponse((Display) displays.get(0), displayDataRequest);
        }
        return getRequestNotSupportedResponse();
    }

    private RestServer.Response getDisplayChangeResponse(Display display, DisplayData displayDataRequest) {
        try {
            display.changeDisplay(displayDataRequest);
            return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(displayDataRequest));
        } catch (Display.DisplayException e) {
            Log.e(TAG, "[DISPLAY] change display", e);
            return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(e.getErrorCode(), e.getMessage())));
        }
    }

    private List<Actuator> getDisplays(String displayId) {
        return DeviceManagerUtil.getFilteredActuatorsById(getAllDisplays(), displayId);
    }
}
