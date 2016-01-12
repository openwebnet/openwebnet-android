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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import io.realm.Realm;
import rx.observers.TestSubscriber;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"io.realm.*"})
@PrepareForTest({Injector.class, Realm.class})
public class EnvironmentRepositoryTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    EnvironmentRepository environmentRepository;

    Realm realmMock;

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

    @Before
    public void setupRealm() {
        realmMock = PowerMockito.mock(Realm.class);
        PowerMockito.mockStatic(Realm.class);
        when(Realm.getDefaultInstance()).thenReturn(realmMock);
        doNothing().when(realmMock).beginTransaction();
        doNothing().when(realmMock).commitTransaction();
    }

    @Test
    public void environmentRepository_getNextId() {

    }

    @Test
    public void environmentRepository_add() {
        Integer ENVIRONMENT_ID = 100;
        String ENVIRONMENT_NAME = "myName";
        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(ENVIRONMENT_ID);
        environment.setName(ENVIRONMENT_NAME);

        TestSubscriber<Integer> tester = new TestSubscriber<>();
        environmentRepository.add(environment).subscribe(tester);

        assertThat(Realm.getDefaultInstance(), is(realmMock));
        verify(realmMock).beginTransaction();
        verify(realmMock).copyToRealm(environment);
        verify(realmMock).commitTransaction();

        tester.assertValue(ENVIRONMENT_ID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void environmentRepository_findAll() {

    }

}
