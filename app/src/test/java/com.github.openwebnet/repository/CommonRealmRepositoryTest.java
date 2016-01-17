package com.github.openwebnet.repository;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.SampleModel;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PrepareForTest({Injector.class})
public class CommonRealmRepositoryTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    SampleRepository sampleRepository;

    @Inject
    DatabaseRealm databaseRealm;

    @Before
    public void setupDagger() {
        ApplicationComponentTest applicationComponentTest = DaggerApplicationComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .domoticModuleTest(new DomoticModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(false))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ApplicationComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void commonRealmRepository_add() {
        String UUID = "modelUuid";

        SampleModel model = new SampleModel();
        model.setUuid(UUID);

        when(databaseRealm.add(model)).thenReturn(model);

        TestSubscriber<String> tester = new TestSubscriber<>();
        sampleRepository.add(model).subscribe(tester);

        verify(databaseRealm).add(model);

        tester.assertValue(UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    @Ignore
    public void commonRealmRepository_update() {

    }

    @Test
    @Ignore
    public void commonRealmRepository_delete() {

    }

    @Test
    @Ignore
    public void commonRealmRepository_findById() {

    }

    @Test
    @Ignore
    public void commonRealmRepository_findAll() {

    }

}
