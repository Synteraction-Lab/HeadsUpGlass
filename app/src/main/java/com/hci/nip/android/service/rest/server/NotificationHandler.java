package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.android.actuators.NotificationActuator;
import com.hci.nip.android.actuators.model.NotificationData;
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

public class NotificationHandler extends UrlHandler {

    private static final String TAG = NotificationHandler.class.getName();

    private static final String PARAM_ACTUATOR_ID = "actuator_id";

    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Arrays.asList(
            "/notifiers/",
            "/notifiers/:" + PARAM_ACTUATOR_ID
    ));

    @Override
    public List<String> getStaticUrls() {
        return STATIC_URLS;
    }

    private List<Actuator> getAllNotifiers() {
        return DeviceManagerUtil.getFilteredActuatorsByType(deviceManager.getActuators(), ActuatorType.ACTUATOR_TYPE_NOTIFIER);
    }

    @Override
    public RestServer.Response post(RestServer.Request request) {
        Log.v(TAG, "POST request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String actuatorId = params.get(PARAM_ACTUATOR_ID);

        if (parameterSize == 1 && actuatorId != null) {
            // url: notifiers/:actuator_id/
            List<Actuator> notifiers = getNotifiers(actuatorId);
            if (notifiers.isEmpty()) {
                return ResponseUtil.getActuatorNotFoundResponse();
            }
            NotificationData notificationRequest = JsonUtil.getObjectFromJson(request.getRequestBody(), NotificationData.class);
            return getRequestedNotificationChange((NotificationActuator) notifiers.get(0), notificationRequest);
        }
        return getRequestNotSupportedResponse();
    }

    private RestServer.Response getRequestedNotificationChange(NotificationActuator notificationActuator, NotificationData notificationRequest) {
        try {
            notificationActuator.displayNotification(notificationRequest);
            return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(notificationRequest));
        } catch (NotificationActuator.NotificationActuatorException e) {
            Log.e(TAG, "[NOTIFICATION] getRequestedNotificationChange", e);
            return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(e.getErrorCode(), e.getMessage())));
        }
    }

    private List<Actuator> getNotifiers(String id) {
        return DeviceManagerUtil.getFilteredActuatorsById(getAllNotifiers(), id);
    }

}
