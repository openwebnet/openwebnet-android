package com.github.openwebnet.component.module;

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

import static org.mockito.Mockito.mock;

@Module
public class RepositoryModuleTest {

    private boolean isMocked;

    public RepositoryModuleTest(boolean isMocked) {
        this.isMocked = isMocked;
    }

    @Provides
    @Singleton
    EnvironmentRepository provideEnvironment() {
        return isMocked ? mock(EnvironmentRepository.class) : new EnvironmentRepositoryImpl();
    }

    @Provides
    @Singleton
    GatewayRepository provideGateway() {
        return isMocked ? mock(GatewayRepository.class) : new GatewayRepositoryImpl();
    }

    @Provides
    @Singleton
    LightRepository provideLight() {
        return isMocked ? mock(LightRepository.class) : new LightRepositoryImpl();
    }

    @Provides
    @Singleton
    DeviceRepository provideDevice() {
        return isMocked ? mock(DeviceRepository.class) : new DeviceRepositoryImpl();
    }

}
