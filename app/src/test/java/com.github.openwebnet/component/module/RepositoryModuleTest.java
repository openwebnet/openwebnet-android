package com.github.openwebnet.component.module;

import com.github.openwebnet.repository.AutomationRepository;
import com.github.openwebnet.repository.DeviceRepository;
import com.github.openwebnet.repository.EnergyRepository;
import com.github.openwebnet.repository.EnvironmentRepository;
import com.github.openwebnet.repository.GatewayRepository;
import com.github.openwebnet.repository.IpcamRepository;
import com.github.openwebnet.repository.LightRepository;
import com.github.openwebnet.repository.SampleRepository;
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
    AutomationRepository provideAutomation() {
        return isMocked ? mock(AutomationRepository.class) : new AutomationRepositoryImpl();
    }

    @Provides
    @Singleton
    DeviceRepository provideDevice() {
        return isMocked ? mock(DeviceRepository.class) : new DeviceRepositoryImpl();
    }

    @Provides
    @Singleton
    SampleRepository provideSample() {
        return isMocked ? mock(SampleRepository.class) : new SampleRepository();
    }

    @Provides
    @Singleton
    IpcamRepository provideIpcam() {
        return isMocked ? mock(IpcamRepository.class) : new IpcamRepositoryImpl();
    }

    @Provides
    @Singleton
    TemperatureRepository provideTemperature() {
        return isMocked ? mock(TemperatureRepository.class) : new TemperatureRepositoryImpl();
    }

    @Provides
    @Singleton
    ScenarioRepository provideScenerio() {
        return isMocked ? mock(ScenarioRepositoryImpl.class) : new ScenarioRepositoryImpl();
    }

    @Provides
    @Singleton
    EnergyRepository provideEnergy() {
        return isMocked ? mock(EnergyRepositoryImpl.class) : new EnergyRepositoryImpl();
    }

}
