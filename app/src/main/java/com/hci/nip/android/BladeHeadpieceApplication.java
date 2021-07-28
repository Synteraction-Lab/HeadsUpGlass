package com.hci.nip.android;

import android.app.Application;

import com.hci.nip.android.dependencies.AppComponent;
import com.hci.nip.android.dependencies.AppModule;
import com.hci.nip.android.dependencies.BroadcastModule;
import com.hci.nip.android.dependencies.DaggerAppComponent;
import com.hci.nip.android.dependencies.DataModule;
import com.hci.nip.android.dependencies.NetModule;
import com.hci.nip.android.dependencies.RestModule;
import com.hci.nip.glass.R;
import com.vuzix.hud.resources.DynamicThemeApplication;

public class BladeHeadpieceApplication extends DynamicThemeApplication {

    @Override
    protected int getNormalThemeResId() {
        return R.style.AppTheme;
    }

    @Override
    protected int getLightThemeResId() {
        return R.style.AppTheme_Light;
    }

    private static AppComponent component;

    public static AppComponent getComponent() {
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = buildComponent(this);
    }

    /**
     * Dependencies should be added here {@link #buildComponent(Application)} and {@link AppComponent}
     * <p>
     * NOTE: This class should be defined in AndroidManifest as the starting application
     */
    private AppComponent buildComponent(Application app) {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(app))
                // NOTE: inject with default constructors is not required
                .restModule(new RestModule())
                .netModule(new NetModule("http://google.com:8080/"))
                .broadcastModule(new BroadcastModule())
                .dataModule(new DataModule())
                .build();
    }
}
