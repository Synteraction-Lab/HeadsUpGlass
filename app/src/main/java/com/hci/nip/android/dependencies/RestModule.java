package com.hci.nip.android.dependencies;

import androidx.annotation.NonNull;

import com.hci.nip.android.service.rest.RestService;
import com.hci.nip.android.service.rest.RestServiceImpl;
import com.hci.nip.android.service.rest.client.RestClientApi;
import com.hci.nip.android.service.websocket.WebSocketService;
import com.hci.nip.android.service.websocket.WebSocketServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class RestModule {

    @Provides
    @NonNull
    @Singleton
    RestService provideRestService() {
        return new RestServiceImpl();
    }

    @Provides
    @NonNull
    @Singleton
    RestClientApi provideRestClientApi(Retrofit retrofit) {
        return retrofit.create(RestClientApi.class);
    }

    @Provides
    @NonNull
    @Singleton
    WebSocketService provideWebSocketService() {
        return new WebSocketServiceImpl();
    }
}
