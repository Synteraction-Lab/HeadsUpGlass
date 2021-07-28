package com.hci.nip.android.actuators;

import android.app.Instrumentation;
import android.util.Log;

import com.hci.nip.android.sensors.model.TouchBarEventType;
import com.hci.nip.android.service.ErrorCodes;
import com.hci.nip.android.util.KeyEventUtil;
import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.actuator.ActuatorLocation;
import com.hci.nip.base.actuator.ActuatorType;
import com.hci.nip.base.error.BaseException;

import java.util.List;

public class TouchBarActuator implements Actuator {
    private static final String TAG = TouchBarActuator.class.getName();

    private final String id;

    public TouchBarActuator(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ActuatorType getType() {
        return ActuatorType.ACTUATOR_TYPE_TOUCH_BAR;
    }

    @Override
    public String getModel() {
        return "vuzix.blade.touchbar";
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
        // TODO: support multiple key events
        return "type";
    }

    @Override
    public void open() {
        // DO NOTHING
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void activate() {
        // DO NOTHING
    }

    @Override
    public void deactivate() {
        // DO NOTHING
    }

    @Override
    public void close() {
        deactivate();
    }

    @Override
    public boolean processData(List<?> data) {
        TouchBarEventType type = getTouchBarEventType(data);
        return sendTouchEvent(type);
    }

    private TouchBarEventType getTouchBarEventType(List<?> data) {
        if (data == null || data.isEmpty() | !(data.get(0) instanceof TouchBarEventType)) {
            throw new TouchActuatorException(ErrorCodes.TOUCH_DATA_INVALID);
        }
        return (TouchBarEventType) data.get(0);
    }

    public boolean sendTouchEvent(TouchBarEventType type) {
        Log.v(TAG, "[TOUCH] sendTouchEvent:" + type);
        if (type == null || type == TouchBarEventType.UNKNOWN) {
            return false;
        }
        int keyCode = KeyEventUtil.getKeyCode(type);

        Instrumentation instrumentation = new Instrumentation();
        instrumentation.sendKeyDownUpSync(keyCode);
        return true;
    }

    public static class TouchActuatorException extends BaseException {
        public TouchActuatorException(ErrorCodes errorCode) {
            super(errorCode);
        }

        @Override
        public ErrorCodes getErrorCode() {
            return (ErrorCodes) super.getErrorCode();
        }
    }
}
