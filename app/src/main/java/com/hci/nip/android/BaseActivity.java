package com.hci.nip.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.hci.nip.android.repository.DataRepository;
import com.hci.nip.android.service.BroadcastService;
import com.hci.nip.android.service.rest.RestService;
import com.hci.nip.android.service.rest.client.RestClientApi;
import com.hci.nip.android.service.rest.server.HostInterceptor;
import com.hci.nip.android.service.websocket.WebSocketService;
import com.hci.nip.base.DeviceManager;
import com.hci.nip.base.sensor.KeyBoard;
import com.hci.nip.base.sensor.Sensor;
import com.hci.nip.base.sensor.SensorType;
import com.hci.nip.base.util.DeviceManagerUtil;
import com.vuzix.hud.actionmenu.ActionMenuActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * BaseActivity class
 * Extend this class to create new activities
 */
public abstract class BaseActivity extends ActionMenuActivity {

    private static final String TAG = BaseActivity.class.getName();

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
//            Log.v(TAG, "onReceive" + intent.toString());
            BaseActivity.this.onIntentReceive(context, IntentActionType.getIntentActionType(intent.getAction()), intent);
        }
    };

    @Inject
    protected BroadcastService broadcastService;
    @Inject
    protected DataRepository dataRepository;
    @Inject
    protected RestService restService;
    @Inject
    protected DeviceManager deviceManager;
    @Inject
    protected RestClientApi restClientApi;
    @Inject
    protected HostInterceptor restClientHostInterceptor;
    @Inject
    protected WebSocketService webSocketService;

    // specific to blade (to handle keyboard inputs, since Android does not allow to use
    private final List<KeyBoard> keyBoard = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // dependency injection (TODO: refactor to support dependencies in correct way)
        BladeHeadpieceApplication.getComponent().inject(this);
        registerIntentListener();

        addKeyBoardListeningSensors();
    }

    private void registerIntentListener() {
        final IntentFilter intentFilter = new IntentFilter();
        for (IntentActionType intentActionType : getIntentActionTypes()) {
            intentFilter.addAction(intentActionType.getMessage());
        }
        Log.v(TAG, "Registering Intent Listener");
        broadcastService.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void addKeyBoardListeningSensors() {
        Log.v(TAG, "addKeyBoardSensors()");
        List<Sensor> sensorsList = DeviceManagerUtil.getFilteredSensorsByType(deviceManager.getSensors(), SensorType.SENSOR_TYPE_TOUCH_BAR);
        for (Sensor sensor : sensorsList) {
            if (sensor instanceof KeyBoard) {
                keyBoard.add((KeyBoard) sensor);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterIntentListener();
        removeKeyboardListeningSensors();
    }

    private void unregisterIntentListener() {
        Log.v(TAG, "Unregistering Intent Listener");
        broadcastService.unregisterReceiver(broadcastReceiver);
    }

    private void removeKeyboardListeningSensors() {
        Log.v(TAG, "removeKeyboardSensors()");
        keyBoard.clear();
    }

    /**
     * ref: https://developer.android.com/training/keyboard-input/commands
     * ref: https://developer.android.com/guide/topics/media-apps/mediabuttons
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(TAG, "[KEY]: " + event.toString());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            for (KeyBoard keyBoard : keyBoard) {
                keyBoard.onKeyDown(event.getEventTime(), event.getKeyCode());
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Implement this receive local intents
     *
     * @param context
     * @param intentActionType {@link IntentActionType}
     * @param intent           NOTE: This will receive events only registered at {@link BaseActivity#getIntentActionTypes()}
     */
    public abstract void onIntentReceive(final Context context, IntentActionType intentActionType, final Intent intent);


    /**
     * Implement this to listen to local Intents
     *
     * @return the required intent action list (immutable lists are preferred)
     */
    public abstract List<IntentActionType> getIntentActionTypes();

}
