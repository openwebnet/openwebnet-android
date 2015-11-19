package com.github.openwebnet.component.module;

import com.github.openwebnet.repository.DomoticDeviceRepository;
import com.github.openwebnet.repository.DomoticEnvironmentRepository;
import com.github.openwebnet.repository.impl.DomoticEnvironmentRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    DomoticEnvironmentRepository provideEnvironment() {
        return new DomoticEnvironmentRepositoryImpl();
    }

    @Provides
    @Singleton
    DomoticDeviceRepository provideDevice() {
        return null;
    }

}
