package com.hci.nip.android.actuators.model;

import com.hci.nip.base.model.BaseData;

public class HapticData implements BaseData {
    private final long startDelayMillis;
    private final long leftMillis;
    private final long rightMillis;

    /**
     * @param startDelayMillis The number of milliseconds to delay before starting the vibrations
     * @param leftMillis       The number of milliseconds to vibrate left vibrator
     * @param rightMillis      The number of milliseconds to vibrate right vibrator
     */
    public HapticData(long startDelayMillis, long leftMillis, long rightMillis) {
        this.startDelayMillis = startDelayMillis;
        this.leftMillis = leftMillis;
        this.rightMillis = rightMillis;
    }

    /**
     * @param leftMillis  The number of milliseconds to vibrate left vibrator
     * @param rightMillis The number of milliseconds to vibrate right vibrator
     */
    public HapticData(long leftMillis, long rightMillis) {
        this.startDelayMillis = 0;
        this.leftMillis = leftMillis;
        this.rightMillis = rightMillis;
    }

    public long getLeftMillis() {
        return leftMillis;
    }

    public long getRightMillis() {
        return rightMillis;
    }

    public long getStartDelayMillis() {
        return startDelayMillis;
    }

    @Override
    public String toString() {
        return "HapticData{" +
                "startDelayMillis=" + startDelayMillis +
                ", leftMillis=" + leftMillis +
                ", rightMillis=" + rightMillis +
                '}';
    }
}
