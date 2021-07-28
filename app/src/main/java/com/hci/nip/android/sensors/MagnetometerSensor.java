package com.hci.nip.android.sensors;

import android.content.Context;

import com.hci.nip.base.sensor.SensorType;

/**
 * ref: https://developer.android.com/guide/topics/sensors/sensors_position.html#java
 */
public class MagnetometerSensor extends ThreeAxisSensor {
    public MagnetometerSensor(Context appContext, String id) {
        super(appContext, id);
    }

    @Override
    protected int getAndroidSensorType() {
        return android.hardware.Sensor.TYPE_MAGNETIC_FIELD;
    }

    @Override
    protected int getAndroidSensorDelay() {
        return android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
    }

    @Override
    public SensorType getType() {
        return SensorType.SENSOR_TYPE_MAGNETOMETER;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.magnetometer";
    }

    @Override
    public long getSampleRate() {
        return 10;
    }
}
