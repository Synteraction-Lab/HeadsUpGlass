package com.hci.nip.android.service.rest.server;

import android.util.Log;

import com.hci.nip.base.model.SensorInfo;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.sensor.Sensor;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.hci.nip.base.util.JsonUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SensorHandler extends UrlHandler {
    private static final String TAG = SensorHandler.class.getName();

    private static final String PARAM_SENSOR_ID = "sensor_id";
    private static final String URL_SUFFIX_DATA = "data";

    // NOTE: order matters
    private static final List<String> STATIC_URLS = Collections.unmodifiableList(Arrays.asList(
            "/sensors/",
            "/sensors/:" + PARAM_SENSOR_ID + "/" + URL_SUFFIX_DATA,
            "/sensors/:" + PARAM_SENSOR_ID
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
        String sensorId = params.get(PARAM_SENSOR_ID);
        String url = request.getUrl();

        if (parameterSize == 0) {
            // url: sensors (request details of all sensors)
            return ResponseUtil.getAllSensorInfoResponse(deviceManager.getSensors(), "sensors");
        } else if (parameterSize == 1 && sensorId != null && url.endsWith(URL_SUFFIX_DATA)) {
            // url: /sensors/:sensor_id/data (request data from one sensor)
            return ResponseUtil.getRequestedSensorDataResponse(deviceManager.getSensors(), sensorId);
        } else if (parameterSize == 1 && sensorId != null) {
            // url: sensors/:sensor_id (request details about one sensor)
            return ResponseUtil.getRequestedSensorInfoResponse(deviceManager.getSensors(), sensorId);
        }
        return getRequestNotSupportedResponse();
    }

    @Override
    public RestServer.Response post(RestServer.Request request) {
        Log.v(TAG, "POST request: " + request);
        Map<String, String> params = request.getUrlParams();
        int parameterSize = params.size();
        String sensorId = params.get(PARAM_SENSOR_ID);

        if (parameterSize == 1 && sensorId != null) {
            // url: sensors/:sensor_id (change the property of the requested sensor)
            SensorInfo updateInfo = JsonUtil.getObjectFromJson(request.getRequestBody(), SensorInfo.class);
            Log.v(TAG, "update:" + updateInfo);
            return getRequestedSensorUpdatedInfoResponse(deviceManager.getSensors(), sensorId, updateInfo);
        }
        return getRequestNotSupportedResponse();
    }

    private RestServer.Response getRequestedSensorUpdatedInfoResponse(List<Sensor> sensorList, String sensorId, SensorInfo updateInfo) {
        List<Sensor> filteredSensorList = DeviceManagerUtil.getFilteredSensorsById(sensorList, sensorId);
        if (filteredSensorList.isEmpty()) {
            return ResponseUtil.getSensorNotFoundResponse();
        }
        return getUpdatedSensorInfoResponse(filteredSensorList.get(0), updateInfo);
    }

    private RestServer.Response getUpdatedSensorInfoResponse(Sensor sensor, SensorInfo updateInfo) {
        boolean changedSensorSuccessfully = changeSensorState(sensor, updateInfo);
        if (changedSensorSuccessfully) {
            return ResponseUtil.getSensorInfoResponse(sensor);
        } else {
            return ResponseUtil.getSensorUpdateFailedResponse();
        }
    }

    private boolean changeSensorState(Sensor sensor, SensorInfo updateInfo) {
        final boolean newActiveStatus = updateInfo.isActive();
        boolean needToChangeSensor = sensor.isActive() != newActiveStatus;
        if (needToChangeSensor) {
            if (newActiveStatus) {
                sensor.activate();
            } else {
                sensor.deactivate();
            }
        }
        return sensor.isActive() == newActiveStatus;
    }
}
