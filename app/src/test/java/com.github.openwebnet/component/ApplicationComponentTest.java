package com.github.openwebnet.component;

import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.repository.CommonRealmRepositoryTest;
import com.github.openwebnet.repository.DomoticRepositoryTest;
import com.github.openwebnet.repository.EnvironmentRepositoryTest;
import com.github.openwebnet.repository.SampleRepository;
import com.github.openwebnet.view.MainActivityTest;
import com.github.openwebnet.view.NavigationViewClickListenerTest;
import com.github.openwebnet.view.NavigationViewItemSelectedListenerTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    ApplicationContextModuleTest.class,
    DatabaseModuleTest.class,
    RepositoryModuleTest.class,
    DomoticModuleTest.class})
public interface ApplicationComponentTest extends ApplicationComponent {

    void inject(MainActivityTest activity);
    void inject(NavigationViewClickListenerTest listener);
    void inject(NavigationViewItemSelectedListenerTest listener);

    void inject(SampleRepository repository);
    void inject(CommonRealmRepositoryTest repository);
    void inject(EnvironmentRepositoryTest repository);
    void inject(DomoticRepositoryTest repository);

}
