package com.hci.nip.android.sensors.model;

import com.hci.nip.base.model.BaseData;

public class OneAxisData implements BaseData {
    private final long timestamp;
    private final float value;

    public OneAxisData(long timestamp, float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "OneAxisData{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}