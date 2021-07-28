package com.hci.nip.android.service.websocket.server;

import com.hci.nip.android.BladeHeadpieceApplication;
import com.hci.nip.base.DeviceManager;
import com.hci.nip.base.network.WebSocketServer;

import javax.inject.Inject;

public abstract class DefaultMessageHandler implements WebSocketServer.BaseMessageHandler {

    @Inject
    protected DeviceManager deviceManager;

    protected DefaultMessageHandler() {
        BladeHeadpieceApplication.getComponent().inject(this);
    }
}
