package com.github.openwebnet.component.module;

import android.app.Application;

import com.github.openwebnet.service.DomoticService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.impl.DomoticServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class OpenWebNetModule {

    private final Application application;

    public OpenWebNetModule(Application application) {
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
        return new PreferenceServiceImpl(application);
    }

    @Provides
    @Singleton
    DomoticService provideDomoticService(Application application) {
        return new DomoticServiceImpl(application);
    }

}
