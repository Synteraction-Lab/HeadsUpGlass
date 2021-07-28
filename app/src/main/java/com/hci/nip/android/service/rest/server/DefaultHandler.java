package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.base.DeviceManager;
import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.model.ActuatorInfo;
import com.hci.nip.base.model.PlatformInfo;
import com.hci.nip.base.model.SensorInfo;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.sensor.Sensor;
import com.hci.nip.base.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultHandler extends UrlHandler {

    private static final String TAG = ActuatorHandler.class.getName();

    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Collections.singletonList(
            "/"
    ));

    @Override
    public List<String> getStaticUrls() {
        return STATIC_URLS;
    }

    @Override
    public RestServer.Response get(RestServer.Request request) {
        Log.v(TAG, "GET request: " + request);

        PlatformInfo platformInfo = getPlatformInfo(deviceManager);
        return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(platformInfo));
    }

    private PlatformInfo getPlatformInfo(DeviceManager deviceManager) {
        List<SensorInfo> sensorInfoList = new ArrayList<>();
        for (Sensor sensor : deviceManager.getSensors()) {
            sensorInfoList.add(new SensorInfo(sensor));
        }

        List<ActuatorInfo> actuatorList = new ArrayList<>();
        for (Actuator actuator : deviceManager.getActuators()) {
            actuatorList.add(new ActuatorInfo(actuator));
        }

        return new PlatformInfo(deviceManager.getId(), deviceManager.getName(), sensorInfoList, actuatorList);
    }

}
