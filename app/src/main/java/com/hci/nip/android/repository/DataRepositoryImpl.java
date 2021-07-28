package com.hci.nip.android.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hci.nip.base.model.BaseData;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Singleton;

@Singleton
public class DataRepositoryImpl implements DataRepository {

    private static final int RESPONSE_WAIT_MILLIS = 50;

    private static final String TAG = DataRepositoryImpl.class.getName();

    private final ConcurrentHashMap<Long, BaseData> requestHashMap;
    private final ConcurrentHashMap<Long, BaseData> responseHashMap;
    private static AtomicLong uniqueKey = new AtomicLong(new Random().nextInt());

    public DataRepositoryImpl() {
        this.requestHashMap = new ConcurrentHashMap<>(10);
        this.responseHashMap = new ConcurrentHashMap<>(10);
    }

    @Override
    public void clear() {
        this.requestHashMap.clear();
        this.responseHashMap.clear();
    }

    @Override
    public Long getUniqueKey() {
        return uniqueKey.incrementAndGet();
    }

    @Override
    public void addRequest(@NonNull Long key, @NonNull BaseData request) {
        requestHashMap.put(key, request);
    }

    @Override
    public BaseData getRequest(@NonNull Long key) {
        return requestHashMap.remove(key);
    }

    @Override
    public void addResponse(@NonNull Long key, @NonNull BaseData response) {
        responseHashMap.put(key, response);
    }

    @Override
    public BaseData waitForResponse(@NonNull Long key, long minimumMillis) {
        waitUntilResponseAvailableOrTimeExceed(key, minimumMillis);
        // remove the request also
//        requestHashMap.remove(key);
        return responseHashMap.remove(key);
    }

    private void waitUntilResponseAvailableOrTimeExceed(long key, long minimumMillis) {
        long count = 0;
        final long maxCount = minimumMillis / RESPONSE_WAIT_MILLIS;
        while (!responseHashMap.containsKey(key) && count < maxCount) {
            count++;
            try {
                TimeUnit.MILLISECONDS.sleep(RESPONSE_WAIT_MILLIS);
            } catch (InterruptedException e) {
                Log.e(TAG, "Failed at waiting", e);
            }
        }
    }

}
