package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.android.actuators.HapticActuator;
import com.hci.nip.android.actuators.model.HapticData;
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

public class HapticHandler extends UrlHandler {

    private static final String TAG = HapticHandler.class.getName();

    private static final String PARAM_ACTUATOR_ID = "actuator_id";

    // NOTE: order matters
    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Arrays.asList(
            "/haptics/",
            "/haptics/:" + PARAM_ACTUATOR_ID
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
            // url: haptics (request details of all haptics)
            return ResponseUtil.getAllActuatorInfoResponse(getAllHapticActuators(), "haptics");
        } else if (parameterSize == 1 && actuatorId != null) {
            //  url: haptics/:actuator_id/
            return ResponseUtil.getRequestedActuatorInfo(getAllHapticActuators(), actuatorId);
        }
        return getRequestNotSupportedResponse();
    }

    private List<Actuator> getAllHapticActuators() {
        return DeviceManagerUtil.getFilteredActuatorsByType(deviceManager.getActuators(), ActuatorType.ACTUATOR_TYPE_VIBRATOR);
    }

    @Override
    public RestServer.Response post(RestServer.Request request) {
        Log.v(TAG, "POST request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String actuatorId = params.get(PARAM_ACTUATOR_ID);

        if (parameterSize == 1 && actuatorId != null) {
            // url: haptics/:actuator_id/
            List<Actuator> actuators = getHapticActuator(actuatorId);
            if (actuators.isEmpty()) {
                return ResponseUtil.getActuatorNotFoundResponse();
            }
            HapticRequest hapticRequest = JsonUtil.getObjectFromJson(request.getRequestBody(), HapticRequest.class);
            return getProcessHapticResponse(actuators.get(0), hapticRequest);

        }
        return getRequestNotSupportedResponse();
    }

    private RestServer.Response getProcessHapticResponse(Actuator actuator, HapticRequest hapticRequest) {
        try {
            actuator.processData(hapticRequest.getData());
            return RestServer.Response.getSuccessResponse(EMPTY_JSON_STRING);
        } catch (HapticActuator.HapticActuatorException e) {
            Log.e(TAG, "[HAPTIC] getProcessHapticResponse", e);
            return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(e.getErrorCode(), e.getMessage())));
        }
    }

    private List<Actuator> getHapticActuator(String id) {
        return DeviceManagerUtil.getFilteredActuatorsById(getAllHapticActuators(), id);
    }

    private static class HapticRequest {
        private List<HapticData> data;

        public List<HapticData> getData() {
            return data;
        }
    }
}
