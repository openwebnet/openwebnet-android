package com.github.openwebnet.component.module;

import com.github.openwebnet.repository.AutomationRepository;
import com.github.openwebnet.repository.DeviceRepository;
import com.github.openwebnet.repository.EnergyRepository;
import com.github.openwebnet.repository.EnvironmentRepository;
import com.github.openwebnet.repository.GatewayRepository;
import com.github.openwebnet.repository.IpcamRepository;
import com.github.openwebnet.repository.LightRepository;
import com.github.openwebnet.repository.ScenarioRepository;
import com.github.openwebnet.repository.TemperatureRepository;
import com.github.openwebnet.repository.impl.AutomationRepositoryImpl;
import com.github.openwebnet.repository.impl.DeviceRepositoryImpl;
import com.github.openwebnet.repository.impl.EnergyRepositoryImpl;
import com.github.openwebnet.repository.impl.EnvironmentRepositoryImpl;
import com.github.openwebnet.repository.impl.GatewayRepositoryImpl;
import com.github.openwebnet.repository.impl.IpcamRepositoryImpl;
import com.github.openwebnet.repository.impl.LightRepositoryImpl;
import com.github.openwebnet.repository.impl.ScenarioRepositoryImpl;
import com.github.openwebnet.repository.impl.TemperatureRepositoryImpl;

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
    AutomationRepository provideAutomation() {
        return new AutomationRepositoryImpl();
    }

    @Provides
    @Singleton
    DeviceRepository provideDevice() {
        return new DeviceRepositoryImpl();
    }

    @Provides
    @Singleton
    IpcamRepository provideIpcam() {
        return new IpcamRepositoryImpl();
    }

    @Provides
    @Singleton
    TemperatureRepository provideTemperature() {
        return new TemperatureRepositoryImpl();
    }

    @Provides
    @Singleton
    ScenarioRepository provideScenerio() {
        return new ScenarioRepositoryImpl();
    }

    @Provides
    @Singleton
    EnergyRepository provideEnergy() {
        return new EnergyRepositoryImpl();
    }

}
