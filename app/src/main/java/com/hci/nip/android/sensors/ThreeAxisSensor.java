package com.hci.nip.android.sensors;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.hci.nip.android.sensors.model.ThreeAxisData;
import com.hci.nip.base.sensor.Sensor;
import com.hci.nip.base.sensor.SensorLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * NOTE: Only extend this if it has 3-axis data
 * <p>
 * https://developer.android.com/guide/topics/sensors/sensors_overview
 * Used Android API Classes:
 * https://developer.android.com/reference/android/hardware/SensorManager
 * https://developer.android.com/reference/android/hardware/SensorEvent
 * https://developer.android.com/reference/android/hardware/SensorEventListener
 */
public abstract class ThreeAxisSensor implements Sensor, SensorEventListener {

    private static final int BUFFER_SIZE = 256;

    private final Context applicationContext;

    private android.hardware.Sensor sensor = null;
    private android.hardware.SensorManager sensorManager;

    private final String id;
    private final AtomicBoolean active;

    private final ArrayBlockingQueue<ThreeAxisData> threeAxisData;

    protected ThreeAxisSensor(Context appContext, String id) {
        this.applicationContext = appContext;
        this.id = id;
        this.active = new AtomicBoolean(false);
        this.threeAxisData = new ArrayBlockingQueue<>(BUFFER_SIZE);
    }

    @Override
    public void open() {
        // TODO: Move this to activateSensor ?? (see close also)
        sensorManager = (android.hardware.SensorManager) applicationContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(getAndroidSensorType());
    }

    protected abstract int getAndroidSensorType();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SensorLocation getLocation() {
        return SensorLocation.SENSOR_LOCATION_HEAD;
    }

    @Override
    public long getBufferSize() {
        return BUFFER_SIZE;
    }

    @Override
    public String getResolution() {
        if (sensor != null) {
            return Float.toString(sensor.getResolution());
        }
        return "";
    }

    @Override
    public String getDataFormat() {
        return "List<timestamp,valueX,valueY,valueZ>";
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public void activate() {
        if (active.compareAndSet(false, true)) {
            sensorManager.registerListener(this, sensor, getAndroidSensorDelay());
        }
    }

    protected abstract int getAndroidSensorDelay();

    @Override
    public void deactivate() {
        if (active.compareAndSet(true, false)) {
            sensorManager.unregisterListener(this);
            threeAxisData.clear();
        }
    }

    @Override
    public void close() {
        deactivate();
        sensorManager = null;
        sensor = null;
    }

    @Override
    public List<ThreeAxisData> readData() {
        List<ThreeAxisData> data = new ArrayList<>(threeAxisData);
//        threeAxisData.clear();
        return data;
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (threeAxisData.remainingCapacity() == 0) {
            threeAxisData.poll();
        }
        threeAxisData.offer(new ThreeAxisData(event.timestamp, event.values[0], event.values[1], event.values[2]));
    }

    @Override
    public final void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
        // DO NOTHING
    }
}
