package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.model.ActuatorInfo;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.hci.nip.base.util.JsonUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ActuatorHandler extends UrlHandler {
    private static final String TAG = ActuatorHandler.class.getName();

    private static final String PARAM_ACTUATOR_ID = "actuator_id";

    // NOTE: order matters
    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Arrays.asList(
            "/actuators/",
            "/actuators/:" + PARAM_ACTUATOR_ID
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
        String url = request.getUrl();

        if (parameterSize == 0) {
            // url: actuators (request details of all actuators)
            return ResponseUtil.getAllActuatorInfoResponse(deviceManager.getActuators(), "actuators");
        } else if (parameterSize == 1 && actuatorId != null) {
            // url: actuators/:actuator_id (request details about one actuator)
            return ResponseUtil.getRequestedActuatorInfo(deviceManager.getActuators(), actuatorId);
        }
        return getRequestNotSupportedResponse();
    }

    @Override
    public RestServer.Response post(RestServer.Request request) {
        Log.v(TAG, "POST request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String actuatorId = params.get(PARAM_ACTUATOR_ID);

        if (parameterSize == 1 && actuatorId != null) {
            // url: actuators/:actuator_id (change the property of the requested sensor)
            ActuatorInfo updateInfo = JsonUtil.getObjectFromJson(request.getRequestBody(), ActuatorInfo.class);
            Log.v(TAG, "update:" + updateInfo);
            return getRequestedActuatorUpdatedInfoResponse(deviceManager.getActuators(), actuatorId, updateInfo);
        }
        return getRequestNotSupportedResponse();
    }

    private RestServer.Response getRequestedActuatorUpdatedInfoResponse(List<Actuator> actuatorList, String actuatorId, ActuatorInfo updateInfo) {
        List<Actuator> filteredActuatorList = DeviceManagerUtil.getFilteredActuatorsById(actuatorList, actuatorId);
        if (filteredActuatorList.isEmpty()) {
            return ResponseUtil.getActuatorNotFoundResponse();
        }
        return getUpdatedActuatorInfoResponse(filteredActuatorList.get(0), updateInfo);
    }

    private RestServer.Response getUpdatedActuatorInfoResponse(Actuator actuator, ActuatorInfo updateInfo) {
        boolean changedActuatorSuccessfully = changeActuatorState(actuator, updateInfo);
        if (changedActuatorSuccessfully) {
            return ResponseUtil.getActuatorInfoResponse(actuator);
        } else {
            return ResponseUtil.getActuatorUpdateFailedResponse();
        }
    }

    private boolean changeActuatorState(Actuator actuator, ActuatorInfo updateInfo) {
        final boolean newActiveStatus = updateInfo.isActive();
        boolean needToChangeActuator = actuator.isActive() != newActiveStatus;
        if (needToChangeActuator) {
            if (newActiveStatus) {
                actuator.activate();
            } else {
                actuator.deactivate();
            }
        }
        return actuator.isActive() == newActiveStatus;
    }
}
