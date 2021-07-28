package com.hci.nip.android.repository;

import androidx.annotation.NonNull;

import com.hci.nip.base.model.BaseData;


public interface DataRepository {

    /**
     * clear all the stored data
     */
    void clear();

    /**
     * @return a unique key
     */
    Long getUniqueKey();

    /**
     * Add a request hoping for a response {@link #waitForResponse(Long, long)}
     * NOTE:  {@code request} and {@code response} should be of the same type, and if you add make sure to process it
     *
     * @param key
     * @param request
     */
    void addRequest(@NonNull Long key, @NonNull BaseData request);

    /**
     * Get the request to process the request
     * NOTE: Do not call this method unless request is added early {@link #addRequest(Long, BaseData)}
     *
     * @param key the key that needs to be retrieved
     * @return request {@code null} if there was no mapping for {@code key}
     */
    BaseData getRequest(@NonNull Long key);

    /**
     * Add a response to a given request by {@code key}
     *
     * @param key
     * @param response
     */
    void addResponse(@NonNull Long key, @NonNull BaseData response);

    /**
     * Wait until a response become available for a given {@code key} (request)
     *
     * @param key
     * @param minimumMillis (minimum) milliseconds to wait
     * @return response {@code null} if there was no mapping for {@code key}
     */
    BaseData waitForResponse(@NonNull Long key, long minimumMillis);
}
