package com.hci.nip.android.dependencies;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hci.nip.android.service.BroadcastService;
import com.hci.nip.android.service.BroadcastServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BroadcastModule {

    @Provides
    @Singleton
    BroadcastService provideBroadcastService(@NonNull Application application) {
        return new BroadcastServiceImpl(application);
    }
}
