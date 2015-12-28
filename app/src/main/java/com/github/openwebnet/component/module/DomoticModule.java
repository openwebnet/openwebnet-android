package com.github.openwebnet.component.module;

import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.impl.DeviceServiceImpl;
import com.github.openwebnet.service.impl.EnvironmentServiceImpl;
import com.github.openwebnet.service.impl.GatewayServiceImpl;
import com.github.openwebnet.service.impl.LightServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DomoticModule {

    @Provides
    @Singleton
    DeviceService provideDeviceService() {
        return new DeviceServiceImpl();
    }

    @Provides
    @Singleton
    EnvironmentService provideEnvironmentService() {
        return new EnvironmentServiceImpl();
    }

    @Provides
    @Singleton
    GatewayService provideGatewayService() {
        return new GatewayServiceImpl();
    }

    @Provides
    @Singleton
    LightService provideLightService() {
        return new LightServiceImpl();
    }
}
