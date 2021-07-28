package com.hci.nip.android.dependencies;

import com.hci.nip.android.BaseActivity;
import com.hci.nip.android.BladeHeadpieceApplication;
import com.hci.nip.android.service.ServiceProvider;
import com.hci.nip.android.service.broadcast.handler.DefaultExternalIntentHandler;
import com.hci.nip.android.service.rest.server.UrlHandler;
import com.hci.nip.android.service.websocket.server.DefaultMessageHandler;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Please register any other module you want to use in here {@link AppComponent} and
 * {@link BladeHeadpieceApplication#buildComponent(android.app.Application)}
 * <p>
 * Ref: https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2
 */
@Component(modules = {
        AppModule.class,
        RestModule.class,
        DataModule.class,
        NetModule.class,
        BroadcastModule.class,
})
@Singleton
public interface AppComponent {
    void inject(BaseActivity baseActivity);

    void inject(UrlHandler urlHandler);

    void inject(ServiceProvider serviceProvider);

    void inject(DefaultMessageHandler messageHandler);

    void inject(DefaultExternalIntentHandler intentHandler);

}
