package com.hci.nip.android.service.rest;

import com.hci.nip.android.service.rest.server.ActuatorHandler;
import com.hci.nip.android.service.rest.server.CameraHandler;
import com.hci.nip.android.service.rest.server.DefaultHandler;
import com.hci.nip.android.service.rest.server.DisplayHandler;
import com.hci.nip.android.service.rest.server.FileHandler;
import com.hci.nip.android.service.rest.server.HapticHandler;
import com.hci.nip.android.service.rest.server.MicrophoneHandler;
import com.hci.nip.android.service.rest.server.NotificationHandler;
import com.hci.nip.android.service.rest.server.SensorHandler;
import com.hci.nip.android.service.rest.server.SpeakerHandler;
import com.hci.nip.android.service.rest.server.TouchBarHandler;
import com.hci.nip.base.network.RestServer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

@Singleton
public class RestServiceImpl implements RestService {
    private static final Logger LOG = Logger.getLogger(RestServiceImpl.class.getName());

    private final RestServer server;

    public RestServiceImpl() {
        this.server = new RestServer(RestServer.DEFAULT_REST_SERVER_PORT);
        configureUrlHandlers();
    }

    private void configureUrlHandlers() {
        this.server.addUrlHandler(new DisplayHandler());
        this.server.addUrlHandler(new SensorHandler());
        this.server.addUrlHandler(new CameraHandler());
        this.server.addUrlHandler(new MicrophoneHandler());
        this.server.addUrlHandler(new FileHandler());
        this.server.addUrlHandler(new SpeakerHandler());
        this.server.addUrlHandler(new ActuatorHandler());
        this.server.addUrlHandler(new HapticHandler());
        this.server.addUrlHandler(new NotificationHandler());
        this.server.addUrlHandler(new TouchBarHandler());

        this.server.addUrlHandler(new DefaultHandler());
    }

    @Override
    public void startRestService() throws IOException {
        if (!server.isAlive()) {
            server.startServer();
            LOG.log(Level.INFO, "[REST] Starting REST server");
        }
    }

    @Override
    public void stopRestService() throws IOException {
        if (server.isAlive()) {
            server.stopService();
            LOG.log(Level.INFO, "[REST] Stopping REST server");
        }
    }

    @Override
    public boolean isActive() {
        return server.isAlive();
    }
}
