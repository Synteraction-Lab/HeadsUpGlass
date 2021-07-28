package com.hci.nip.android.service.rest.server;

import com.hci.nip.android.service.ErrorCodes;
import com.hci.nip.android.service.rest.beans.ErrorData;
import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.model.ActuatorInfo;
import com.hci.nip.base.model.SensorInfo;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.sensor.Sensor;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.hci.nip.base.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public final class ResponseUtil {

    public static RestServer.Response getAllSensorInfoResponse(List<Sensor> sensorList, String jsonPropertyName) {
        List<SensorInfo> sensorInfoList = new ArrayList<>();
        for (Sensor sensor : sensorList) {
            sensorInfoList.add(new SensorInfo(sensor));
        }
        return RestServer.Response.getSuccessResponse(JsonUtil.getWrappedJsonString(jsonPropertyName, JsonUtil.getJsonString(sensorInfoList)));
    }

    public static RestServer.Response getRequestedSensorDataResponse(List<Sensor> sensorList, String sensorId) {
        List<Sensor> filteredSensorList = DeviceManagerUtil.getFilteredSensorsById(sensorList, sensorId);
        if (filteredSensorList.isEmpty()) {
            return getSensorNotFoundResponse();
        }
        return getSensorReadDataResponse(filteredSensorList.get(0), "data");
    }

    public static RestServer.Response getRequestedSensorInfoResponse(List<Sensor> sensorList, String sensorId) {
        List<Sensor> filteredSensorList = DeviceManagerUtil.getFilteredSensorsById(sensorList, sensorId);
        if (filteredSensorList.isEmpty()) {
            return getSensorNotFoundResponse();
        }
        return getSensorInfoResponse(filteredSensorList.get(0));
    }

    public static RestServer.Response getSensorInfoResponse(Sensor sensor) {
        return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(new SensorInfo(sensor)));
    }

    public static RestServer.Response getSensorReadDataResponse(Sensor sensor, String propertyName) {
        List<?> sensorData = sensor.readData();
        return RestServer.Response.getSuccessResponse(JsonUtil.getWrappedJsonString(propertyName, JsonUtil.getJsonString(sensorData)));
    }

    public static RestServer.Response getSensorNotFoundResponse() {
        return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(ErrorCodes.SENSOR_NOT_FOUND)));
    }

    public static RestServer.Response getSensorUpdateFailedResponse() {
        return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(ErrorCodes.SENSOR_UPDATE_FAILED)));
    }

    public static RestServer.Response getAllActuatorInfoResponse(List<Actuator> actuatorList, String jsonPropertyName) {
        List<ActuatorInfo> actuatorInfoList = new ArrayList<>();
        for (Actuator actuator : actuatorList) {
            actuatorInfoList.add(new ActuatorInfo(actuator));
        }
        return RestServer.Response.getSuccessResponse(JsonUtil.getWrappedJsonString(jsonPropertyName, JsonUtil.getJsonString(actuatorInfoList)));
    }

    public static RestServer.Response getRequestedActuatorInfo(List<Actuator> actuatorList, String actuatorId) {
        List<Actuator> filteredActuatorList = DeviceManagerUtil.getFilteredActuatorsById(actuatorList, actuatorId);
        if (filteredActuatorList.isEmpty()) {
            return getActuatorNotFoundResponse();
        }
        return getActuatorInfoResponse(filteredActuatorList.get(0));
    }

    public static RestServer.Response getActuatorNotFoundResponse() {
        return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(ErrorCodes.ACTUATOR_NOT_FOUND)));
    }

    public static RestServer.Response getActuatorInfoResponse(Actuator actuator) {
        return RestServer.Response.getSuccessResponse(JsonUtil.getJsonString(new ActuatorInfo(actuator)));
    }

    public static RestServer.Response getActuatorUpdateFailedResponse() {
        return RestServer.Response.getBadResponse(JsonUtil.getJsonString(new ErrorData(ErrorCodes.ACTUATOR_UPDATE_FAILED)));
    }
}
