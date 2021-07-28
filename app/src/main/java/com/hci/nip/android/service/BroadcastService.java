package com.hci.nip.android.service;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;

import com.hci.nip.android.IntentActionType;

public interface BroadcastService {

    String INTENT_ID = "com.hci.nip.android.service.BroadcastService.id";

    /**
     * Return a new {@link Intent} with given {@code intentActionType} and {@code id}
     *
     * @param intentActionType intent action type
     * @param id               should be positive
     * @return {@link Intent} with given {@link IntentActionType} and {@code id}
     */
    static Intent getBroadcastIntent(IntentActionType intentActionType, long id) {
        return new Intent(intentActionType.getMessage()).putExtra(INTENT_ID, id);
    }

    /**
     * Return the id of the intent if exists, else return 0
     *
     * @param intent
     * @return 0 if the id is not found, else return a positive id
     */
    static long getBroadcastIntentId(@NonNull Intent intent) {
        return intent.getLongExtra(INTENT_ID, 0);
    }

    boolean sendBroadcast(@NonNull Intent intent);

    void registerReceiver(@NonNull BroadcastReceiver receiver, @NonNull IntentFilter filter);

    void unregisterReceiver(@NonNull BroadcastReceiver receiver);
}
