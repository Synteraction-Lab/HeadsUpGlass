package com.hci.nip.android.dependencies;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hci.nip.android.service.rest.server.HostInterceptor;
import com.hci.nip.android.service.rest.server.HostInterceptorImpl;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetModule {

    private final String baseUrl;

    /**
     * @param baseUrl url (i.e. protocol://hostname:port/)
     */
    public NetModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    //    @Provides
//    @Singleton
//    Cache provideOkHttpCache(Application application) {
//        int cacheSize = 10 * 1024 * 1024; // 10 MiB
//        return new Cache(application.getCacheDir(), cacheSize);
//    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
//                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(HostInterceptor hostInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(hostInterceptor)
                .connectTimeout(4000, TimeUnit.MILLISECONDS)
                .writeTimeout(7000, TimeUnit.MILLISECONDS)
                .readTimeout(10000, TimeUnit.MILLISECONDS)
//                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    HostInterceptor provideHostInterceptor() {
        return new HostInterceptorImpl();
    }

}
