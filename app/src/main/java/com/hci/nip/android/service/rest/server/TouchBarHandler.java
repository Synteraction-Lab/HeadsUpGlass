package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.android.actuators.HapticActuator;
import com.hci.nip.android.sensors.model.TouchBarEventType;
import com.hci.nip.android.service.rest.beans.ErrorData;
import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.actuator.ActuatorType;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.hci.nip.base.util.JsonUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TouchBarHandler extends UrlHandler {

    private static final String TAG = TouchBarHandler.class.getName();

    private static final String PARAM_ACTUATOR_ID = "actuator_id";

    // NOTE: order matters
    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Arrays.asList(
            "/touch/",
            "/touch/:" + PARAM_ACTUATOR_ID
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
            // url: touch (request details of all haptics)
            return ResponseUtil.getAllActuatorInfoResponse(getAllTouchActuators(), "touch");
        } else if (parameterSize == 1 && actuatorId != null) {
            //  url: touch/:actuator_id/
            return ResponseUtil.getRequestedActuatorInfo(getAllTouchActuators(), actuatorId);
        }
        return getRequestNotSupportedResponse();
    }

    private List<Actuator> getAllTouchActuators() {
        return DeviceManagerUtil.getFilteredActuatorsByType(deviceManager.getActuators(), ActuatorType.ACTUATOR_TYPE_TOUCH_BAR);
    }

    @Override
    public RestServer.Response post(RestServer.Request request) {
        Log.v(TAG, "POST request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String actuatorId = params.get(PARAM_ACTUATOR_ID);

        if (parameterSize == 1 && actuatorId != null) {
            // url: touch/:actuator_id/
            List<Actuator> actuators = getTouchActuator(actuatorId);
            if (actuators.isEmpty()) {
                return ResponseUtil.getActuatorNotFoundResponse();
            }
            TouchRequest touchRequest = JsonUtil.getObjectFromJson(request.getRequestBody(), TouchRequest.class);
            return getProcessTouchResponse(actuators.get(0), touchRequest);

        }
        return getRequestNotSupportedResponse();
    }

    private RestServer.Response getProcessTouchResponse(Actuator actuator, TouchRequest touchRequest) {
        try {
            actuator.processData(Collections.singletonList(touchRequest.getData()));
            return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(touchRequest));
        } catch (HapticActuator.HapticActuatorException e) {
            Log.e(TAG, "[TOUCH] getProcessTouchResponse", e);
            return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(e.getErrorCode(), e.getMessage())));
        }
    }

    private List<Actuator> getTouchActuator(String id) {
        return DeviceManagerUtil.getFilteredActuatorsById(getAllTouchActuators(), id);
    }

    private static class TouchRequest {
        private TouchBarEventType type;

        public TouchBarEventType getData() {
            return type;
        }
    }
}
