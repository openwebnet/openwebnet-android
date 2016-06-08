package com.github.openwebnet.repository;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.SampleModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PrepareForTest({Injector.class})
public class DomoticRepositoryTest {

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
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(false))
            .domoticModuleTest(new DomoticModuleTest())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ApplicationComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void lightRepository_findByEnvironment() {
        Integer ENVIRONMENT = 108;

        SampleModel model1 = new SampleModel();
        model1.setUuid("uuid1");
        model1.setEnvironmentId(ENVIRONMENT);
        SampleModel model2 = new SampleModel();
        model2.setUuid("uuid2");
        model2.setEnvironmentId(ENVIRONMENT);
        List<SampleModel> models = Arrays.asList(model1, model2);

        when(databaseRealm.findCopyWhere(SampleModel.class, DomoticModel.FIELD_ENVIRONMENT_ID,
            ENVIRONMENT, DomoticModel.FIELD_NAME)).thenReturn(models);

        TestSubscriber<List<SampleModel>> tester = new TestSubscriber<>();
        sampleRepository.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(databaseRealm).findCopyWhere(SampleModel.class, DomoticModel.FIELD_ENVIRONMENT_ID,
            ENVIRONMENT, DomoticModel.FIELD_NAME);

        tester.assertValue(models);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void lightRepository_findFavourites() {
        SampleModel model = new SampleModel();
        model.setUuid("uuid1");
        model.setEnvironmentId(108);
        model.setFavourite(true);

        List<SampleModel> lights = Arrays.asList(model);

        when(databaseRealm.findCopyWhere(SampleModel.class, DomoticModel.FIELD_FAVOURITE,
            true, DomoticModel.FIELD_NAME)).thenReturn(lights);

        TestSubscriber<List<SampleModel>> tester = new TestSubscriber<>();
        sampleRepository.findFavourites().subscribe(tester);

        verify(databaseRealm).findCopyWhere(SampleModel.class, DomoticModel.FIELD_FAVOURITE,
            true, DomoticModel.FIELD_NAME);

        tester.assertValue(lights);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

}
