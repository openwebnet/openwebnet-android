package com.github.openwebnet.component.module;

import android.app.Application;

import com.github.openwebnet.service.DomoticService;
import com.github.openwebnet.service.OpenWebNetService;
import com.github.openwebnet.service.PreferenceService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@Module
public class OpenWebNetModuleTest {

    private final Application application;

    public OpenWebNetModuleTest(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    PreferenceService providePreferenceService(Application application) {
        PreferenceService preferenceServiceMock = mock(PreferenceService.class);

        return preferenceServiceMock;
    }

    @Provides
    @Singleton
    DomoticService provideDomoticService() {
        DomoticService domoticServiceMock = mock(DomoticService.class);

        doNothing().when(domoticServiceMock).initRepository();

        return domoticServiceMock;
    }

    @Provides
    @Singleton
    OpenWebNetService provideOpenWebNetService() {
        OpenWebNetService openWebNetServiceMock = mock(OpenWebNetService.class);

        return openWebNetServiceMock;
    }
}
