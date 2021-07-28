package com.hci.nip.android.actuators;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import com.hci.nip.android.actuators.model.HapticData;
import com.hci.nip.android.service.ErrorCodes;
import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.actuator.ActuatorLocation;
import com.hci.nip.base.actuator.ActuatorType;
import com.hci.nip.base.error.BaseException;
import com.vuzix.system.resources.haptics.Haptics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ref: https://developer.android.com/reference/android/os/Vibrator
 * ref: https://www.vuzix.com/Developer/Dashboard/Blade-Code-Samples
 * <p>
 * NOTE: Depends on AndroidVersion you may need to change APIs
 */
public class HapticActuator implements Actuator {

    private static final String TAG = HapticActuator.class.getName();

    private final String id;
    private final Context applicationContext;

    private final AtomicBoolean active;

    private Vibrator leftVibrator;
    private Vibrator rightVibrator;
    private Vibrator bothVibrator;

    /**
     * Setup your Vuzix Haptics class variable
     */
    private final Haptics hapticsManager;

    public HapticActuator(Context appContext, String id) {
        this.id = id;
        this.applicationContext = appContext;
        this.hapticsManager = new Haptics(appContext);
        this.active = new AtomicBoolean(false);

        this.leftVibrator = null;
        this.rightVibrator = null;
        this.bothVibrator = null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ActuatorType getType() {
        return ActuatorType.ACTUATOR_TYPE_VIBRATOR;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.vibrator";
    }

    @Override
    public ActuatorLocation getLocation() {
        return ActuatorLocation.ACTUATOR_LOCATION_HEAD;
    }

    @Override
    public String getResolution() {
        return "";
    }

    @Override
    public String getDataFormat() {
        return "List<startDelayMillis,leftMillis,rightMillis>";
    }

    @Override
    public void open() {
        // Setup individual controls for the leftMillis and rightMillis feedback motors
        leftVibrator = hapticsManager.getLeftVibrator();
        rightVibrator = hapticsManager.getRightVibrator();
        // Setup standard control for both sides together
        bothVibrator = (Vibrator) applicationContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public void activate() {
        active.set(bothVibrator != null && isVibratorEnabled());
    }

    private boolean isVibratorEnabled() {
        return hapticsManager.isVibratorEnabled(applicationContext.getContentResolver());
    }

    @Override
    public void deactivate() {
        if (active.compareAndSet(true, false)) {
            leftVibrator.cancel();
            rightVibrator.cancel();
            bothVibrator.cancel();
        }
    }

    @Override
    public void close() {
        deactivate();
        leftVibrator = null;
        rightVibrator = null;
        bothVibrator = null;
    }

    /**
     * @param data {@link HapticData}
     * @throws HapticActuatorException if the data is invalid or if vibrator can not operate
     */
    @Override
    public boolean processData(List<?> data) {
        validateState();
        return vibrateAccordingToPattern(getHapticData(data));
    }

    /**
     * @param vibratePattern how the vibration should occur
     */
    public boolean vibrate(List<HapticData> vibratePattern) {
        return vibrateAccordingToPattern(vibratePattern);
    }

    private boolean vibrateAccordingToPattern(List<HapticData> hapticDataList) {
        Log.d(TAG, "[HAPTIC] vibrateAccordingToPattern(.)");
        // use  vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)) for API >= 26
        if (hapticDataList.size() == 1) {
            HapticData dataItem = hapticDataList.get(0);
            if (dataItem.getStartDelayMillis() > 0) {
                bothVibrator.vibrate(getHapticPattern(hapticDataList), -1);
            } else {
                leftVibrator.vibrate(dataItem.getLeftMillis());
                rightVibrator.vibrate(dataItem.getRightMillis());
            }
        } else {
            bothVibrator.vibrate(getHapticPattern(hapticDataList), -1);
        }
        return true;
    }


    private void validateState() {
        if (!active.get()) {
            throw new HapticActuatorException(ErrorCodes.ACTUATOR_NOT_ACTIVE);
        }
    }

    private static long[] getHapticPattern(List<HapticData> data) {
        int size = data.size() * 2 + 1;
        int index = 0;
        long[] pattern = new long[size];

        for (HapticData dataItem : data) {
            pattern[index++] = dataItem.getStartDelayMillis();
            pattern[index++] = dataItem.getLeftMillis();
        }
        pattern[index++] = 1000;
        return pattern;
    }

    /**
     * @param data
     * @return {@link HapticData}
     * @throws HapticActuatorException if the data is invalid or empty
     */
    private static List<HapticData> getHapticData(List<?> data) {
        if (data == null || data.isEmpty() | !(data.get(0) instanceof HapticData)) {
            throw new HapticActuatorException(ErrorCodes.HAPTIC_DATA_INVALID);
        }

        List<HapticData> castedData = new ArrayList<>();
        for (Object dataItem : data) {
            castedData.add((HapticData) dataItem);
        }
        return castedData;
    }

    public static class HapticActuatorException extends BaseException {
        public HapticActuatorException(ErrorCodes errorCode) {
            super(errorCode);
        }

        @Override
        public ErrorCodes getErrorCode() {
            return (ErrorCodes) super.getErrorCode();
        }
    }

}
