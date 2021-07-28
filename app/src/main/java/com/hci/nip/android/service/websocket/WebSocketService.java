package com.hci.nip.android.service.websocket;

import java.io.IOException;

public interface WebSocketService {

    void start() throws IOException;

    void stop() throws IOException;

    boolean isActive();
}
