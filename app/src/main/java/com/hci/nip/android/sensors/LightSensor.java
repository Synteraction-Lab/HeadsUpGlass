package com.hci.nip.android.sensors;

import android.content.Context;

import com.hci.nip.base.sensor.SensorType;

/**
 * ref: https://developer.android.com/guide/topics/sensors/sensors_environment.html#java
 */
public class LightSensor extends OneAxisSensor {
    public LightSensor(Context appContext, String id) {
        super(appContext, id);
    }

    @Override
    protected int getAndroidSensorType() {
        return android.hardware.Sensor.TYPE_LIGHT;
    }

    @Override
    protected int getAndroidSensorDelay() {
        return android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
    }

    @Override
    public SensorType getType() {
        return SensorType.SENSOR_TYPE_LIGHT;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.light";
    }

    @Override
    public long getSampleRate() {
        return 1;
    }
}
