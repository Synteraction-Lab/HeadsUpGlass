package com.hci.nip.android.service.rest;

import java.io.IOException;

public interface RestService {
    void startRestService() throws IOException;

    void stopRestService() throws IOException;

    boolean isActive();
}
