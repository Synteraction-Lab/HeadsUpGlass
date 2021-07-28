package com.hci.nip.android.service.broadcast.handler;

import com.hci.nip.android.BladeHeadpieceApplication;
import com.hci.nip.android.service.broadcast.IntentMessage;
import com.hci.nip.base.DeviceManager;

import javax.inject.Inject;

public abstract class DefaultExternalIntentHandler {

    @Inject
    protected DeviceManager deviceManager;

    protected DefaultExternalIntentHandler() {
        BladeHeadpieceApplication.getComponent().inject(this);
    }

    /**
     * @return the base URL for operation
     */
    public abstract String getBaseUrl();

    /**
     * @param message input data
     * @return result data
     */
    public abstract IntentMessage process(IntentMessage message);
}
