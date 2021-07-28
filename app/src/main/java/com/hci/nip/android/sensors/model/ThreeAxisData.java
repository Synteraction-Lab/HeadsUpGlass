package com.hci.nip.android.sensors.model;

import com.hci.nip.base.model.BaseData;

public class ThreeAxisData implements BaseData {
    private final long timestamp;
    private final float valueX;
    private final float valueY;
    private final float valueZ;

    public ThreeAxisData(long timestamp, float valueX, float valueY, float valueZ) {
        this.timestamp = timestamp;
        this.valueX = valueX;
        this.valueY = valueY;
        this.valueZ = valueZ;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getValueX() {
        return valueX;
    }

    public float getValueY() {
        return valueY;
    }

    public float getValueZ() {
        return valueZ;
    }

    @Override
    public String toString() {
        return "ThreeAxisData{" +
                "timestamp=" + timestamp +
                ", valueX=" + valueX +
                ", valueY=" + valueY +
                ", valueZ=" + valueZ +
                '}';
    }
}