package com.github.openwebnet.component.module;

import com.github.openwebnet.repository.DeviceRepository;
import com.github.openwebnet.repository.EnvironmentRepository;
import com.github.openwebnet.repository.GatewayRepository;
import com.github.openwebnet.repository.LightRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class RepositoryModuleTest {

    @Provides
    @Singleton
    EnvironmentRepository provideEnvironment() {
        return mock(EnvironmentRepository.class);
    }

    @Provides
    @Singleton
    GatewayRepository provideGateway() {
        return mock(GatewayRepository.class);
    }

    @Provides
    @Singleton
    LightRepository provideLight() {
        return mock(LightRepository.class);
    }

    @Provides
    @Singleton
    DeviceRepository provideDevice() {
        return mock(DeviceRepository.class);
    }

}
