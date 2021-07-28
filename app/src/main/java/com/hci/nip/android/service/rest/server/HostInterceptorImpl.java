package com.hci.nip.android.service.rest.server;

import java.io.IOException;

import javax.inject.Singleton;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Ref: https://stackoverflow.com/questions/36498131/set-dynamic-base-url-using-retrofit-2-0-and-dagger-2
 * <p>
 * Enable to change the host (base url) at runtime
 */
@Singleton
public class HostInterceptorImpl implements HostInterceptor {

    private volatile String host = null;
    private volatile int port = 8080;

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String host = this.host;
        int port = this.port;
        if (host != null) {
            HttpUrl newUrl = request.url().newBuilder()
                    .host(host)
                    .port(port)
                    .build();
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);
    }
}
