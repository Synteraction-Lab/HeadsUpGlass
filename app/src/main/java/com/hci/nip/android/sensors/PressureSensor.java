package com.hci.nip.android.sensors;

import android.content.Context;

import com.hci.nip.base.sensor.SensorType;

/**
 * ref: https://developer.android.com/guide/topics/sensors/sensors_environment.html#java
 */
public class PressureSensor extends OneAxisSensor {
    public PressureSensor(Context appContext, String id) {
        super(appContext, id);
    }

    @Override
    protected int getAndroidSensorType() {
        return android.hardware.Sensor.TYPE_PRESSURE;
    }

    @Override
    protected int getAndroidSensorDelay() {
        return android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
    }

    @Override
    public SensorType getType() {
        return SensorType.SENSOR_TYPE_PRESSURE;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.pressure";
    }

    @Override
    public long getSampleRate() {
        return 1;
    }
}
