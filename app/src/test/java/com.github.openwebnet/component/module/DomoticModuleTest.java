package com.github.openwebnet.component.module;

import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.impl.AutomationServiceImpl;
import com.github.openwebnet.service.impl.EnvironmentServiceImpl;
import com.github.openwebnet.service.impl.GatewayServiceImpl;
import com.github.openwebnet.service.impl.LightServiceImpl;

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
        return mock(EnvironmentServiceImpl.class);
    }

    @Provides
    @Singleton
    GatewayService provideGatewayService() {
        return mock(GatewayServiceImpl.class);
    }

    @Provides
    @Singleton
    LightService provideLightService() {
        return mock(LightServiceImpl.class);
    }

    @Provides
    @Singleton
    AutomationService provideAutomationService() {
        return mock(AutomationServiceImpl.class);
    }


}
