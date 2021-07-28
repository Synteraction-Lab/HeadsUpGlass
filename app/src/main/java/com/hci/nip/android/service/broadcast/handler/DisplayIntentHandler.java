package com.hci.nip.android.service.broadcast.handler;

import android.util.Log;

import com.hci.nip.android.service.broadcast.IntentMessage;
import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.actuator.ActuatorType;
import com.hci.nip.base.actuator.Display;
import com.hci.nip.base.actuator.model.DisplayData;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.hci.nip.base.util.JsonUtil;

import java.util.List;

public class DisplayIntentHandler extends DefaultExternalIntentHandler {

    private static final String TAG = DisplayIntentHandler.class.getName();

    @Override
    public String getBaseUrl() {
        return "/displays";
    }

    @Override
    public IntentMessage process(IntentMessage message) {
        Log.d(TAG, "Process: " + message);
        DisplayData displayDataRequest = JsonUtil.getObjectFromJson(message.getJson(), DisplayData.class);
        getDisplay().changeDisplay(displayDataRequest);

        return message;
    }

    private Display getDisplay() {
        // TODO: fix based on exact url parameters
        List<Actuator> displays = DeviceManagerUtil.getFilteredActuatorsByType(deviceManager.getActuators(), ActuatorType.ACTUATOR_TYPE_DISPLAY);
        return (Display) displays.get(0);
    }
}
