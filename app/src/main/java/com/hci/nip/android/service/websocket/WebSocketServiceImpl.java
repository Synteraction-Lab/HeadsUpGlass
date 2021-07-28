package com.hci.nip.android.service.websocket;

import com.hci.nip.android.service.websocket.server.DisplayMessageHandler;
import com.hci.nip.base.network.WebSocketServer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

@Singleton
public class WebSocketServiceImpl implements WebSocketService {

    private static final Logger LOG = Logger.getLogger(WebSocketServiceImpl.class.getName());

    private final WebSocketServer server;

    public WebSocketServiceImpl() {
        this.server = new WebSocketServer(WebSocketServer.DEFAULT_WEB_SOCKET_SERVER_PORT);
        configureMessageHandlers();
    }

    /**
     * add the required handlers
     */
    private void configureMessageHandlers() {
        this.server.addHandler(new DisplayMessageHandler());
    }

    @Override
    public void start() throws IOException {
        if (!server.isActive()) {
            server.start();
            LOG.log(Level.INFO, "[WEB SOCKET] Starting REST server");
        }
    }

    @Override
    public void stop() throws IOException {
        if (server.isActive()) {
            server.stop();
            LOG.log(Level.INFO, "[WEB SOCKET] Stopping REST server");
        }
    }

    @Override
    public boolean isActive() {
        return server.isActive();
    }
}
