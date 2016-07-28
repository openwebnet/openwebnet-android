package com.github.openwebnet.service;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModule;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.repository.EnvironmentRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"android.*"})
@PrepareForTest({Injector.class})
public class EnvironmentServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    EnvironmentRepository environmentRepository;

    @Inject
    EnvironmentService environmentService;

    @Singleton
    @Component(modules = {
        ApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface EnvironmentComponentTest extends ApplicationComponent {

        void inject(EnvironmentServiceTest service);

    }

    @Before
    public void setupDagger() {
        EnvironmentComponentTest applicationComponentTest = DaggerEnvironmentServiceTest_EnvironmentComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((EnvironmentComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void environmentService_add() {
        Integer ENVIRONMENT_ID = 110;
        String ENVIRONMENT_NAME = "myName";

        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(ENVIRONMENT_ID);
        environment.setName(ENVIRONMENT_NAME);

        when(environmentRepository.getNextId()).thenReturn(Observable.just(ENVIRONMENT_ID));
        when(environmentRepository.add(any(EnvironmentModel.class))).thenReturn(Observable.just(ENVIRONMENT_ID));

        TestSubscriber<Integer> tester = new TestSubscriber<>();
        environmentService.add(ENVIRONMENT_NAME).subscribe(tester);

        verify(environmentRepository).getNextId();
        verify(environmentRepository).add(any(EnvironmentModel.class));

        tester.assertValue(ENVIRONMENT_ID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void environmentService_update() {
        EnvironmentModel environment = new EnvironmentModel();

        when(environmentRepository.update(environment)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        environmentService.update(environment).subscribe(tester);

        verify(environmentRepository).update(environment);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void environmentService_findAll() {
        List<EnvironmentModel> environments = new ArrayList<>();
        when(environmentRepository.findAll()).thenReturn(Observable.just(environments));

        TestSubscriber<List<EnvironmentModel>> tester = new TestSubscriber<>();
        environmentService.findAll().subscribe(tester);

        verify(environmentRepository).findAll();

        tester.assertValue(environments);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void environmentService_findById() {
        Integer ENVIRONMENT_ID = 110;
        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(ENVIRONMENT_ID);

        when(environmentRepository.findById(ENVIRONMENT_ID)).thenReturn(Observable.just(environment));

        TestSubscriber<EnvironmentModel> tester = new TestSubscriber<>();
        environmentService.findById(ENVIRONMENT_ID).subscribe(tester);

        verify(environmentRepository).findById(ENVIRONMENT_ID);

        tester.assertValue(environment);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void environmentService_delete() {
        Integer ENVIRONMENT_ID = 42;

        when(environmentRepository.delete(ENVIRONMENT_ID)).thenReturn(Observable.empty());

        TestSubscriber<Void> tester = new TestSubscriber<>();
        environmentService.delete(ENVIRONMENT_ID).subscribe(tester);

        verify(environmentRepository).delete(ENVIRONMENT_ID);

        tester.assertCompleted();
        tester.assertNoErrors();
    }
}
