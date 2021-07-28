package com.hci.nip.android.sensors;

import android.content.Context;

import com.hci.nip.base.sensor.SensorType;

/**
 * ref: https://developer.android.com/guide/topics/sensors/sensors_motion#java
 * ref: https://developer.android.com/guide/topics/sensors/sensors_motion.html
 * ref: https://www.vuzix.com/Developer/Dashboard/Blade-Code-Samples
 */
public class AccelerometerSensor extends ThreeAxisSensor {
    public AccelerometerSensor(Context appContext, String id) {
        super(appContext, id);
    }

    @Override
    protected int getAndroidSensorType() {
        return android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
    }

    @Override
    protected int getAndroidSensorDelay() {
        return android.hardware.SensorManager.SENSOR_DELAY_NORMAL;
    }

    @Override
    public SensorType getType() {
        return SensorType.SENSOR_TYPE_ACCELEROMETER;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.accelerometer";
    }

    @Override
    public long getSampleRate() {
        return 10;
    }
}
