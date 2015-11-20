package com.github.openwebnet.component;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.component.module.OpenWebNetModule;
import com.github.openwebnet.component.module.RepositoryModule;
import com.github.openwebnet.service.impl.DomoticServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;
import com.github.openwebnet.view.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {OpenWebNetModule.class, RepositoryModule.class})
public interface OpenWebNetComponent {

    void inject(OpenWebNetApplication application);

    void inject(MainActivity activity);

    // dagger needs concrete class
    void inject(PreferenceServiceImpl preferenceService);
    void inject(DomoticServiceImpl domoticService);

}
