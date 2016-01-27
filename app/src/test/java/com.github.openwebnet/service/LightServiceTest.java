package com.github.openwebnet.service;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DomoticModule;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.repository.LightRepository;

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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PrepareForTest({Injector.class})
public class LightServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    LightRepository lightRepository;

    @Inject
    LightService lightService;

    @Inject
    CommonService commonService;

    @Singleton
    @Component(modules = {ApplicationContextModuleTest.class, DomoticModule.class, RepositoryModuleTest.class})
    public interface LightComponentTest extends ApplicationComponent {

        void inject(LightServiceTest service);

    }

    @Before
    public void setupDagger() {
        LightComponentTest applicationComponentTest = DaggerLightServiceTest_LightComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .domoticModule(new DomoticModule())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((LightComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void lightService_add() {
        String LIGHT_UUID = "myUuid";
        LightModel lightModel = new LightModel();

        when(lightRepository.add(lightModel)).thenReturn(Observable.just(LIGHT_UUID));

        TestSubscriber<String> tester = new TestSubscriber<>();
        lightService.add(lightModel).subscribe(tester);

        verify(lightRepository).add(lightModel);

        tester.assertValue(LIGHT_UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void lightService_update() {
        LightModel lightModel = new LightModel();

        when(lightRepository.update(lightModel)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        lightService.update(lightModel).subscribe(tester);

        verify(lightRepository).update(lightModel);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void lightService_delete() {
        String LIGHT_UUID = "myUuid";

        when(lightRepository.delete(LIGHT_UUID)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        lightService.delete(LIGHT_UUID).subscribe(tester);

        verify(lightRepository).delete(LIGHT_UUID);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void lightService_findById() {
        String LIGHT_UUID = "myUuid";
        LightModel lightModel = new LightModel();
        lightModel.setUuid(LIGHT_UUID);

        when(lightRepository.findById(LIGHT_UUID)).thenReturn(Observable.just(lightModel));

        TestSubscriber<LightModel> tester = new TestSubscriber<>();
        lightService.findById(LIGHT_UUID).subscribe(tester);

        verify(lightRepository).findById(LIGHT_UUID);

        tester.assertValue(lightModel);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void lightService_findByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<LightModel> lights = new ArrayList<>();

        when(lightRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(lights));

        TestSubscriber<List<LightModel>> tester = new TestSubscriber<>();
        lightService.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(lightRepository).findByEnvironment(ENVIRONMENT);

        tester.assertValue(lights);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void lightService_findFavourites() {
        List<LightModel> lights = new ArrayList<>();

        when(lightRepository.findFavourites()).thenReturn(Observable.just(lights));

        TestSubscriber<List<LightModel>> tester = new TestSubscriber<>();
        lightService.findFavourites().subscribe(tester);

        verify(lightRepository).findFavourites();

        tester.assertValue(lights);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    @Ignore
    public void lightService_requestByEnvironment() {

    }

    @Test
    @Ignore
    public void lightService_requestFavourites() {

    }

    @Test
    @Ignore
    public void lightService_turnOn() {

    }

    @Test
    @Ignore
    public void lightService_turnOff() {

    }

}
