package com.hci.nip.android.sensors;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.hci.nip.android.sensors.model.OneAxisData;
import com.hci.nip.base.sensor.Sensor;
import com.hci.nip.base.sensor.SensorLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * NOTE: Only extend this if it has 1-axis data
 */
public abstract class OneAxisSensor implements Sensor, SensorEventListener {

    private static final int BUFFER_SIZE = 256;

    private final Context applicationContext;

    private android.hardware.Sensor sensor = null;
    private SensorManager sensorManager;

    private final String id;
    private final AtomicBoolean active;

    private final ArrayBlockingQueue<OneAxisData> oneAxisData;

    protected OneAxisSensor(Context appContext, String id) {
        this.applicationContext = appContext;
        this.id = id;
        this.active = new AtomicBoolean(false);
        this.oneAxisData = new ArrayBlockingQueue<>(BUFFER_SIZE);
    }

    @Override
    public void open() {
        // TODO: Move this to activateSensor ?? (see close also)
        sensorManager = (SensorManager) applicationContext.getSystemService(Context.SENSOR_SERVICE);
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
        return "List<timestamp,value>";
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
            oneAxisData.clear();
        }
    }

    @Override
    public void close() {
        deactivate();
        sensorManager = null;
        sensor = null;
    }

    @Override
    public List<OneAxisData> readData() {
        List<OneAxisData> data = new ArrayList<>(oneAxisData);
//        oneAxisData.clear();
        return data;
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (oneAxisData.remainingCapacity() == 0) {
            oneAxisData.poll();
        }
        oneAxisData.offer(new OneAxisData(event.timestamp, event.values[0]));
    }

    @Override
    public final void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
        // DO NOTHING
    }
}
