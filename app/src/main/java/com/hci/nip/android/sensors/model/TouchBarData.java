package com.hci.nip.android.sensors.model;

import com.hci.nip.base.model.BaseData;

public class TouchBarData implements BaseData {
    private final long timestamp;
    private final TouchBarEventType type;

    public TouchBarData(long timestamp, TouchBarEventType type) {
        this.timestamp = timestamp;
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public TouchBarEventType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TouchBarData{" +
                "timestamp=" + timestamp +
                ", type=" + type +
                '}';
    }
}
