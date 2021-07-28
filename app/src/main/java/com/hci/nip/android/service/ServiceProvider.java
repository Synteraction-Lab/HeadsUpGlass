package com.hci.nip.android.service;

import com.hci.nip.android.BladeHeadpieceApplication;
import com.hci.nip.android.repository.DataRepository;

import javax.inject.Inject;

/**
 * Only extend this if you need services such as {@link DataRepository} ,
 * {@link BroadcastService}
 */
public abstract class ServiceProvider {

    @Inject
    protected BroadcastService broadcastService;
    @Inject
    protected DataRepository dataRepository;

    public ServiceProvider() {
        // dependency injection
        BladeHeadpieceApplication.getComponent().inject(this);
    }
}
