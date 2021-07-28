package com.hci.nip.android.service.rest.server;

import okhttp3.Interceptor;

/**
 * intercepts the OkHttp requests
 */
public interface HostInterceptor extends Interceptor {

    /**
     * Set the host
     *
     * @param host new hostname or IP address (e.g. www.google.com, 172.15.9.12)
     */
    void setHost(String host);

    /**
     * set the port
     *
     * @param port
     */
    void setPort(int port);

    String getHost();

    int getPort();
}
