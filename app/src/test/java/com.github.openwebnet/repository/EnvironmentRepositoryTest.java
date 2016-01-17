package com.github.openwebnet.repository;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.EnvironmentModel;

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
public class EnvironmentRepositoryTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    EnvironmentRepository environmentRepository;

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
    public void environmentRepository_getNextIdDefault() {
        Integer INITIAL_SEQ = 100;

        when(databaseRealm.findMax(EnvironmentModel.class, EnvironmentModel.FIELD_ID)).thenReturn(null);

        TestSubscriber<Integer> tester = new TestSubscriber<>();
        environmentRepository.getNextId().subscribe(tester);

        verify(databaseRealm).findMax(EnvironmentModel.class, EnvironmentModel.FIELD_ID);

        tester.assertValue(INITIAL_SEQ);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void environmentRepository_getNextId() {
        Integer CURRENT_MAX_ID = 108;
        Integer NEXT_SEQ = 109;


        when(databaseRealm.findMax(EnvironmentModel.class, EnvironmentModel.FIELD_ID)).thenReturn(CURRENT_MAX_ID);

        TestSubscriber<Integer> tester = new TestSubscriber<>();
        environmentRepository.getNextId().subscribe(tester);

        verify(databaseRealm).findMax(EnvironmentModel.class, EnvironmentModel.FIELD_ID);

        tester.assertValue(NEXT_SEQ);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void environmentRepository_add() {
        Integer ENVIRONMENT_ID = 100;
        String ENVIRONMENT_NAME = "myName";
        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(ENVIRONMENT_ID);
        environment.setName(ENVIRONMENT_NAME);

        when(databaseRealm.add(environment)).thenReturn(environment);

        TestSubscriber<Integer> tester = new TestSubscriber<>();
        environmentRepository.add(environment).subscribe(tester);

        verify(databaseRealm).add(environment);

        tester.assertValue(ENVIRONMENT_ID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void environmentRepository_findAll() {
        EnvironmentModel environment1 = new EnvironmentModel();
        environment1.setId(100);
        environment1.setName("environment1");
        EnvironmentModel environment2 = new EnvironmentModel();
        environment2.setId(101);
        environment2.setName("environment2");
        List<EnvironmentModel> environments = Arrays.asList(environment1, environment2);

        when(databaseRealm.findAllSortedAscending(EnvironmentModel.class, EnvironmentModel.FIELD_NAME))
            .thenReturn(environments);

        TestSubscriber<List<EnvironmentModel>> tester = new TestSubscriber<>();
        environmentRepository.findAll().subscribe(tester);

        verify(databaseRealm).findAllSortedAscending(EnvironmentModel.class, EnvironmentModel.FIELD_NAME);

        tester.assertValue(environments);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

}
