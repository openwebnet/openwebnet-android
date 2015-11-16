package com.github.openwebnet.component;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.component.module.OpenWebNetModule;
import com.github.openwebnet.component.module.RepositoryModule;
import com.github.openwebnet.view.activity.BaseActivity;
import com.github.openwebnet.view.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {OpenWebNetModule.class, RepositoryModule.class})
public interface OpenWebNetComponent {

    void inject(OpenWebNetApplication application);

    void inject(BaseActivity activity);
    void inject(MainActivity activity);

}
