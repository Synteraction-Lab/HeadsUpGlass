package com.hci.nip.android.sensors;

import android.content.Context;

import com.hci.nip.base.sensor.SensorType;

/**
 * ref: https://developer.android.com/guide/topics/sensors/sensors_motion#java
 * ref: https://developer.android.com/guide/topics/sensors/sensors_motion.html
 */
public class GyroscopeSensor extends ThreeAxisSensor {
    public GyroscopeSensor(Context appContext, String id) {
        super(appContext, id);
    }

    @Override
    protected int getAndroidSensorType() {
        return android.hardware.Sensor.TYPE_GYROSCOPE;
    }

    @Override
    protected int getAndroidSensorDelay() {
        return android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
    }

    @Override
    public SensorType getType() {
        return SensorType.SENSOR_TYPE_GYROSCOPE;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.gyroscope";
    }

    @Override
    public long getSampleRate() {
        return 10;
    }
}
