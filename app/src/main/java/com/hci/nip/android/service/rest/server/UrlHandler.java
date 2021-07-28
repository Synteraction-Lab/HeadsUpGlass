package com.hci.nip.android.service.rest.server;

import com.hci.nip.android.BladeHeadpieceApplication;
import com.hci.nip.android.service.ErrorCodes;
import com.hci.nip.android.service.rest.beans.ErrorData;
import com.hci.nip.android.service.rest.client.RestClientApi;
import com.hci.nip.base.DeviceManager;
import com.hci.nip.base.network.RestServer;
import com.hci.nip.base.util.JsonUtil;

import javax.inject.Inject;

/**
 * implement this to handle urls
 */
public abstract class UrlHandler extends RestServer.BaseUrlHandler {

    private static final RestServer.Response REQUEST_NOT_SUPPORTED_RESPONSE
            = new RestServer.Response(RestServer.StatusCode.BAD_REQUEST, JsonUtil.getJsonString(new ErrorData(ErrorCodes.REQUEST_NOT_SUPPORTED)));

    @Inject
    protected DeviceManager deviceManager;
    @Inject
    protected RestClientApi restClientApi;

    public UrlHandler() {
        super();
        BladeHeadpieceApplication.getComponent().inject(this);
    }

    @Override
    public RestServer.Response get(RestServer.Request request) {
        return getRequestNotSupportedResponse();
    }

    @Override
    public RestServer.Response post(RestServer.Request request) {
        return getRequestNotSupportedResponse();
    }

    @Override
    public RestServer.Response put(RestServer.Request request) {
        return getRequestNotSupportedResponse();
    }

    protected RestServer.Response getRequestNotSupportedResponse() {
        return REQUEST_NOT_SUPPORTED_RESPONSE;
    }

}
