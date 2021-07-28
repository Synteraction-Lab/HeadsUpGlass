package com.hci.nip.android.service.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hci.nip.android.IntentActionType;
import com.hci.nip.android.service.broadcast.handler.DefaultExternalIntentHandler;
import com.hci.nip.android.service.broadcast.handler.DisplayIntentHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExternalIntentReceiver extends BroadcastReceiver {

    private static final String TAG = ExternalIntentReceiver.class.getName();

    public static final String INTENT_URL = "external.intent.url";
    public static final String INTENT_JSON = "external.intent.json";

    public static final int MAX_CONCURRENT_OPERATIONS = 5;

    private final ExecutorService executorService;
    private final List<DefaultExternalIntentHandler> handlerList;

    public ExternalIntentReceiver() {
        super();

        handlerList = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_OPERATIONS);

        configureIntentReceivers();

    }

    /**
     * add the required handlers
     */
    private void configureIntentReceivers() {
        addHandler(new DisplayIntentHandler());
    }

    private void addHandler(DefaultExternalIntentHandler handler) {
        handlerList.add(handler);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "[EXTERNAL_INTENT] onReceive");
        executorService.submit(() -> processIntent(intent));
    }

    private void processIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        String url = intent.getStringExtra(INTENT_URL);

        Log.d(TAG, "[EXTERNAL_INTENT] Action:" + action + ", url:" + url);

        // TODO: handle error cases & non-supported case
        if (IntentActionType.EXTERNAL_APP_INTENT.getMessage().equals(action) && type != null && url != null) {
            for (DefaultExternalIntentHandler handler : handlerList) {
                if (url.startsWith(handler.getBaseUrl())) {
                    IntentMessage result = handler.process(new IntentMessage(url, intent.getStringExtra(INTENT_JSON)));
                    return;
                }
            }
        }
    }

}
