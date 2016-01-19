package com.github.openwebnet.component;

import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.repository.CommonRealmRepositoryTest;
import com.github.openwebnet.repository.DeviceRepositoryTest;
import com.github.openwebnet.repository.EnvironmentRepositoryTest;
import com.github.openwebnet.repository.LightRepositoryTest;
import com.github.openwebnet.repository.SampleRepository;
import com.github.openwebnet.service.CommonServiceTest;
import com.github.openwebnet.service.EnvironmentServiceTest;
import com.github.openwebnet.service.GatewayServiceTest;
import com.github.openwebnet.view.MainActivityTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationContextModuleTest.class, DomoticModuleTest.class, RepositoryModuleTest.class})
public interface ApplicationComponentTest extends ApplicationComponent {

    void inject(MainActivityTest activity);

    void inject(SampleRepository repository);
    void inject(CommonRealmRepositoryTest repository);
    void inject(DeviceRepositoryTest repository);
    void inject(EnvironmentRepositoryTest repository);
    void inject(LightRepositoryTest repository);

}
