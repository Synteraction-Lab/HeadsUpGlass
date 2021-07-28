package com.hci.nip.android.dependencies;

// ref: https://android.jlelse.eu/dagger-2-part-i-basic-principles-graph-dependencies-scopes-3dfd032ccd82

import android.app.Application;

import androidx.annotation.NonNull;

import com.hci.nip.android.VuzixBladeManager;
import com.hci.nip.base.DeviceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Application app;

    public AppModule(@NonNull Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    @NonNull
    @Singleton
    DeviceManager provideDeviceManager(Application app) {
        return new VuzixBladeManager(app);
    }
}
