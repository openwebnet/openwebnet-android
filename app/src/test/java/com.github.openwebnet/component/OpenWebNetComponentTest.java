package com.github.openwebnet.component;

import com.github.openwebnet.component.module.OpenWebNetModuleTest;
import com.github.openwebnet.component.module.RepositoryModule;
import com.github.openwebnet.view.MainActivity;
import com.github.openwebnet.view.MainActivityTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {OpenWebNetModuleTest.class, RepositoryModule.class})
public interface OpenWebNetComponentTest extends OpenWebNetComponent {

    void inject(MainActivity activity);

}
