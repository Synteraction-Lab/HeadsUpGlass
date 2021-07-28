package com.hci.nip.android.service;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import javax.inject.Singleton;

@Singleton
public class BroadcastServiceImpl implements BroadcastService {

    private final LocalBroadcastManager localBroadcastManager;

    public BroadcastServiceImpl(Application application) {
        this.localBroadcastManager = LocalBroadcastManager.getInstance(application);
    }

    @Override
    public boolean sendBroadcast(@NonNull Intent intent) {
        return localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void registerReceiver(@NonNull BroadcastReceiver receiver, @NonNull IntentFilter filter) {
        localBroadcastManager.registerReceiver(receiver, filter);
    }

    @Override
    public void unregisterReceiver(@NonNull BroadcastReceiver receiver) {
        localBroadcastManager.unregisterReceiver(receiver);
    }

}
