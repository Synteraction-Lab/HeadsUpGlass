package com.hci.nip.android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hci.nip.android.ui.BLEBroadcastActivity;
import com.hci.nip.android.ui.DisplayEdiTalkActivity;
import com.hci.nip.android.util.FileUtil;
import com.hci.nip.android.util.NetworkUtils;
import com.hci.nip.glass.R;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final String LOG_FILE_NAME = "nip.log";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TAG);

    private TextView mainTitle;
    private TextView footNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();

        verifyStoragePermissionGranted();

        configureLogbackDirectly();

        startServices();

        initializeUIListeners();
    }

    private void initializeUI() {
        mainTitle = findViewById(R.id.main_title);
        footNote = findViewById(R.id.textFootNote);
    }

    // TODO: Do we need this?
    private void initializeUIListeners() {
        Button btnDisplay = findViewById(R.id.btnDisplay);
        btnDisplay.setOnClickListener(v -> {
            startActivity(new Intent(this, DisplayEdiTalkActivity.class));
        });
    }

    /**
     * Order matters
     * Release the services at {@link #releaseServices()}
     */
    private void startServices() {
        String ipAddress = NetworkUtils.getIpAddress(getApplicationContext());
        footNote.setText(ipAddress);

        Log.i(TAG, "IP address:" + ipAddress);
        LOGGER.debug("IP Address: {}", ipAddress);

        deviceManager.initialize();
        dataRepository.clear();
        try {
            restService.startRestService();
        } catch (IOException e) {
            Log.e(TAG, "[REST] service starting failed", e);
        }
        try {
            webSocketService.start();
        } catch (IOException e) {
            Log.e(TAG, "[WEB SOCKET] service starting failed", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");

        releaseServices();
    }

    @Override
    public void onIntentReceive(Context context, IntentActionType intentActionType, Intent intent) {
        // DO NOTHING
    }

    @Override
    public List<IntentActionType> getIntentActionTypes() {
        // FIXME
        return Collections.EMPTY_LIST;
    }

    /**
     * release order is the opposite of {@link #startServices()}
     */
    private void releaseServices() {
        try {
            webSocketService.stop();
        } catch (IOException e) {
            Log.e(TAG, "[WEB SOCKET] service stopping failed", e);
        }
        try {
            restService.stopRestService();
        } catch (IOException e) {
            Log.e(TAG, "[REST] service stopping failed", e);
        }
        dataRepository.clear();
        deviceManager.release();
    }

    private boolean verifyStoragePermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Storage Permission is granted");
            return true;
        } else {
            Log.e(TAG, "Storage Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }

    private void configureLogbackDirectly() {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.stop();

        // setup FileAppender
        PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
        encoder1.setContext(lc);
        encoder1.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder1.start();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(lc);
        fileAppender.setFile(FileUtil.getAbsoluteFilePath(LOG_FILE_NAME));
        fileAppender.setEncoder(encoder1);
        fileAppender.start();

        // setup LogcatAppender
        PatternLayoutEncoder encoder2 = new PatternLayoutEncoder();
        encoder2.setContext(lc);
        encoder2.setPattern("[%thread] %msg%n");
        encoder2.start();

        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(lc);
        logcatAppender.setEncoder(encoder2);
        logcatAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(fileAppender);
        root.addAppender(logcatAppender);
    }

}
