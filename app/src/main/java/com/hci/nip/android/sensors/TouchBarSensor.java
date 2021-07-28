package com.hci.nip.android.sensors;


import com.hci.nip.android.sensors.model.TouchBarData;
import com.hci.nip.android.sensors.model.TouchBarEventType;
import com.hci.nip.android.util.KeyEventUtil;
import com.hci.nip.base.sensor.KeyBoard;
import com.hci.nip.base.sensor.SensorLocation;
import com.hci.nip.base.sensor.SensorType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ref: https://www.vuzix.com/Developer/KnowledgeBase/Detail/65
 * ref: https://developer.android.com/reference/android/view/KeyEvent.html
 */
public class TouchBarSensor implements KeyBoard {

    private static final int BUFFER_SIZE = 16;
    private final String id;
    private final AtomicBoolean active;

    private final ArrayBlockingQueue<TouchBarData> touchBarData;

    public TouchBarSensor(String id) {
        this.id = id;
        this.active = new AtomicBoolean(false);
        this.touchBarData = new ArrayBlockingQueue<>(BUFFER_SIZE);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SensorType getType() {
        return SensorType.SENSOR_TYPE_TOUCH_BAR;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.touchbar";
    }

    @Override
    public SensorLocation getLocation() {
        return SensorLocation.SENSOR_LOCATION_HEAD;
    }

    @Override
    public long getSampleRate() {
        return 0;
    }

    @Override
    public long getBufferSize() {
        return BUFFER_SIZE;
    }

    @Override
    public String getResolution() {
        return "";
    }

    @Override
    public String getDataFormat() {
        return "{timestamp,type}";
    }

    @Override
    public void open() {
        // DO NOTHING
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public void activate() {
        active.compareAndSet(false, true);
    }

    @Override
    public void deactivate() {
        if (active.compareAndSet(true, false)) {
            touchBarData.clear();
        }
    }

    @Override
    public void close() {
        deactivate();
    }


    @Override
    public List<?> readData() {
        List<TouchBarData> data = new ArrayList<>(touchBarData);
//        touchBarData.clear();
        return data;
    }

    @Override
    public void onKeyDown(long timestamp, int keyCode) {
        if (active.get()) {
            TouchBarEventType type = KeyEventUtil.getTouchBarEventType(keyCode);
            if (TouchBarEventType.UNKNOWN != type) {
                addData(timestamp, type);
            }
        }
    }

    private void addData(long timestamp, TouchBarEventType type) {
        if (touchBarData.remainingCapacity() == 0) {
            touchBarData.poll();
        }
        touchBarData.offer(new TouchBarData(timestamp, type));
    }
}
