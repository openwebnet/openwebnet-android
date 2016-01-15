package com.github.openwebnet.component.module;

import com.github.openwebnet.repository.DatabaseHelper;
import com.github.openwebnet.repository.DeviceRepository;
import com.github.openwebnet.repository.EnvironmentRepository;
import com.github.openwebnet.repository.GatewayRepository;
import com.github.openwebnet.repository.LightRepository;
import com.github.openwebnet.repository.impl.DeviceRepositoryImpl;
import com.github.openwebnet.repository.impl.EnvironmentRepositoryImpl;
import com.github.openwebnet.repository.impl.GatewayRepositoryImpl;
import com.github.openwebnet.repository.impl.LightRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public DatabaseHelper provideDatabaseHelper() {
        return new DatabaseHelper();
    }

    @Provides
    @Singleton
    EnvironmentRepository provideEnvironment() {
        return new EnvironmentRepositoryImpl();
    }

    @Provides
    @Singleton
    GatewayRepository provideGateway() {
        return new GatewayRepositoryImpl();
    }

    @Provides
    @Singleton
    LightRepository provideLight() {
        return new LightRepositoryImpl();
    }

    @Provides
    @Singleton
    DeviceRepository provideDevice() {
        return new DeviceRepositoryImpl();
    }

}
