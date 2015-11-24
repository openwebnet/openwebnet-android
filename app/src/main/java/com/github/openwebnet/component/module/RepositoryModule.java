package com.github.openwebnet.component.module;

import com.github.openwebnet.repository.DeviceRepository;
import com.github.openwebnet.repository.EnvironmentRepository;
import com.github.openwebnet.repository.impl.EnvironmentRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    EnvironmentRepository provideEnvironment() {
        return new EnvironmentRepositoryImpl();
    }

    @Provides
    @Singleton
    DeviceRepository provideDevice() {
        throw new UnsupportedOperationException("not implemented yet");
    }

}
