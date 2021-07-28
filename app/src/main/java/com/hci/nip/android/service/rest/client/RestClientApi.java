package com.hci.nip.android.service.rest.client;

import com.hci.nip.base.model.PlatformInfo;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestClientApi {

    @GET("/platform")
    Call<PlatformInfo> getPlatform();

}
