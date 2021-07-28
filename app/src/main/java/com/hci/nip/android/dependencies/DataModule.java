package com.hci.nip.android.dependencies;

import androidx.annotation.NonNull;

import com.hci.nip.android.repository.DataRepository;
import com.hci.nip.android.repository.DataRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @NonNull
    @Singleton
    DataRepository provideDataRepository() {
        return new DataRepositoryImpl();
    }
}
