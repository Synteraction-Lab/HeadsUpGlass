package com.hci.nip.android.service.websocket.server;

import com.hci.nip.base.actuator.Actuator;
import com.hci.nip.base.actuator.ActuatorType;
import com.hci.nip.base.actuator.Display;
import com.hci.nip.base.actuator.model.DisplayData;
import com.hci.nip.base.network.WebSocketServer;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.hci.nip.base.util.JsonUtil;

import java.util.List;

public class DisplayMessageHandler extends DefaultMessageHandler {

    @Override
    public String getBaseUrl() {
        return "/displays";
    }

    @Override
    public WebSocketServer.Message onMessage(WebSocketServer.Message message) {
        DisplayData displayDataRequest = JsonUtil.getObjectFromJson(message.getData(), DisplayData.class);
        getDisplay().changeDisplay(displayDataRequest);

        return message;
    }

    private Display getDisplay() {
        List<Actuator> displays = DeviceManagerUtil.getFilteredActuatorsByType(deviceManager.getActuators(), ActuatorType.ACTUATOR_TYPE_DISPLAY);
        return (Display) displays.get(0);
    }
}
