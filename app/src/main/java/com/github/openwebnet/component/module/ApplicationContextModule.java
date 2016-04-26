package com.github.openwebnet.component.module;

import android.content.Context;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.impl.CommonServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationContextModule {

    private final OpenWebNetApplication application;

    public ApplicationContextModule(OpenWebNetApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public OpenWebNetApplication application() {
        return application;
    }

    @Provides
    @Singleton
    public Context applicationContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    public PreferenceService providePreferenceService() {
        return new PreferenceServiceImpl();
    }

    @Provides
    @Singleton
    public CommonService provideCommonService() {
        return new CommonServiceImpl();
    }

}
