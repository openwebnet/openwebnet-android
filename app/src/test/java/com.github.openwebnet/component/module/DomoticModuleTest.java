package com.github.openwebnet.component.module;

import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.impl.GatewayServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class DomoticModuleTest {

    @Provides
    @Singleton
    DeviceService provideDeviceService() {
        return mock(DeviceService.class);
    }

    @Provides
    @Singleton
    EnvironmentService provideEnvironmentService() {
        return mock(EnvironmentService.class);
    }

    @Provides
    @Singleton
    GatewayService provideGatewayService() {
        //return mock(GatewayService.class);
        return new GatewayServiceImpl();
    }

    @Provides
    @Singleton
    LightService provideLightService() {
        return mock(LightService.class);
    }

}
