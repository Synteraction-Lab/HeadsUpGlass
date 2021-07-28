package com.hci.nip.android.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.hci.nip.android.BaseActivity;
import com.hci.nip.android.IntentActionType;
import com.hci.nip.android.MainActivity;
import com.hci.nip.android.service.BLEBroadcastService;
import com.hci.nip.glass.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BLEBroadcastActivity extends BaseActivity {
    private static final String TAG = BLEBroadcastActivity.class.getName();

    private final List<IntentActionType> supportedIntentActionTypes = Collections.unmodifiableList(Arrays.asList(
            IntentActionType.BLE_STATE_CONNECTED,
            IntentActionType.BLE_STATE_DISCONNECTED
    ));

    // UI elements
    private TextView textView;
    private Button backBtn;
    // service
    private BLEBroadcastService bleBroadcastService;
    private boolean bleBroadcastServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blebroadcast);

        textView = findViewById(R.id.textBroadcastState);
        backBtn = findViewById(R.id.btnBroadcastToMain);

        registerListeners();

        Intent bleBroadcastIntent = new Intent(this, BLEBroadcastService.class);
        bindService(bleBroadcastIntent, bleBroadcastServiceConnection, Context.BIND_AUTO_CREATE);

        Log.i(TAG, "onCreate: BLEBroadcastActivity created!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(bleBroadcastServiceConnection);
    }

    @Override
    public void onIntentReceive(Context context, IntentActionType intentActionType, Intent intent) {
        Log.d(TAG, "[BLE] onIntentReceive");
        switch (intentActionType) {
            case BLE_STATE_CONNECTED:
                textView.setText(getString(R.string.ble_connected));
                break;

            case BLE_STATE_DISCONNECTED:
                textView.setText(getString(R.string.ble_disconnected));
                break;
        }
    }

    private final ServiceConnection bleBroadcastServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: BLEBroadcastService");
            BLEBroadcastService.LocalBLEBroadcastBinder binder = (BLEBroadcastService.LocalBLEBroadcastBinder) service;
            bleBroadcastService = binder.getService();
            bleBroadcastServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: BLEBroadcastService");
            bleBroadcastServiceBound = false;
        }
    };

    @Override
    public List<IntentActionType> getIntentActionTypes() {
        return supportedIntentActionTypes;
    }

    private void registerListeners() {
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(BLEBroadcastActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

}
