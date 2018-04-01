package com.github.openwebnet.service;

import com.github.niqdev.openwebnet.OpenSession;
import com.github.niqdev.openwebnet.OpenWebNet;
import com.github.niqdev.openwebnet.message.OpenMessage;
import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModule;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.TemperatureModel;
import com.github.openwebnet.repository.TemperatureRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaTestPlugins;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"android.*"})
@PrepareForTest({Injector.class})
public class TemperatureServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    TemperatureRepository temperatureRepository;

    @Inject
    TemperatureService temperatureService;

    @Inject
    CommonService commonService;

    @Singleton
    @Component(modules = {
        ApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface TemperatureComponentTest extends ApplicationComponent {

        void inject(TemperatureServiceTest service);

    }

    @Before
    public void setupRxJava() {
        RxJavaTestPlugins.immediateAndroidSchedulers();
    }

    @After
    public void tearDownRxJava() {
        RxJavaTestPlugins.resetPlugins();
    }

    @Before
    public void setupDagger() {
        TemperatureComponentTest applicationComponentTest = DaggerTemperatureServiceTest_TemperatureComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((TemperatureComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void temperatureService_add() {
        String TEMPERATURE_UUID = "myUuid";
        TemperatureModel temperatureModel = new TemperatureModel();

        when(temperatureRepository.add(temperatureModel)).thenReturn(Observable.just(TEMPERATURE_UUID));

        TestSubscriber<String> tester = new TestSubscriber<>();
        temperatureService.add(temperatureModel).subscribe(tester);

        verify(temperatureRepository).add(temperatureModel);

        tester.assertValue(TEMPERATURE_UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void temperatureService_update() {
        TemperatureModel temperatureModel = new TemperatureModel();

        when(temperatureRepository.update(temperatureModel)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        temperatureService.update(temperatureModel).subscribe(tester);

        verify(temperatureRepository).update(temperatureModel);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void temperatureService_delete() {
        String TEMPERATURE_UUID = "myUuid";

        when(temperatureRepository.delete(TEMPERATURE_UUID)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        temperatureService.delete(TEMPERATURE_UUID).subscribe(tester);

        verify(temperatureRepository).delete(TEMPERATURE_UUID);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void temperatureService_findById() {
        String TEMPERATURE_UUID = "myUuid";
        TemperatureModel temperatureModel = new TemperatureModel();
        temperatureModel.setUuid(TEMPERATURE_UUID);

        when(temperatureRepository.findById(TEMPERATURE_UUID)).thenReturn(Observable.just(temperatureModel));

        TestSubscriber<TemperatureModel> tester = new TestSubscriber<>();
        temperatureService.findById(TEMPERATURE_UUID).subscribe(tester);

        verify(temperatureRepository).findById(TEMPERATURE_UUID);

        tester.assertValue(temperatureModel);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void temperatureService_findByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<TemperatureModel> temperatures = new ArrayList<>();

        when(temperatureRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(temperatures));

        TestSubscriber<List<TemperatureModel>> tester = new TestSubscriber<>();
        temperatureService.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(temperatureRepository).findByEnvironment(ENVIRONMENT);

        tester.assertValue(temperatures);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void temperatureService_findFavourites() {
        List<TemperatureModel> temperatures = new ArrayList<>();

        when(temperatureRepository.findFavourites()).thenReturn(Observable.just(temperatures));

        TestSubscriber<List<TemperatureModel>> tester = new TestSubscriber<>();
        temperatureService.findFavourites().subscribe(tester);

        verify(temperatureRepository).findFavourites();

        tester.assertValue(temperatures);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void temperatureService_requestByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<TemperatureModel> temperatures = new ArrayList<>();

        mockClient();
        when(temperatureRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(temperatures));

        TestSubscriber<List<TemperatureModel>> tester = new TestSubscriber<>();
        temperatureService.requestByEnvironment(ENVIRONMENT).subscribe(tester);

        tester.assertValue(temperatures);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void temperatureService_requestFavourites() {
        List<TemperatureModel> temperatures = new ArrayList<>();

        mockClient();
        when(temperatureRepository.findFavourites()).thenReturn(Observable.just(temperatures));

        TestSubscriber<List<TemperatureModel>> tester = new TestSubscriber<>();
        temperatureService.requestFavourites().subscribe(tester);

        tester.assertValue(temperatures);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    private OpenWebNet mockClient() {
        String GATEWAY_UUID = "myGateway";
        String GATEWAY_HOST = "1.1.1.1";
        Integer GATEWAY_PORT = 1;

        GatewayModel gateway = new GatewayModel();
        gateway.setUuid(GATEWAY_UUID);
        gateway.setHost(GATEWAY_HOST);
        gateway.setPort(GATEWAY_PORT);

        OpenMessage request = () -> "REQUEST";
        OpenMessage response = () -> "RESPONSE";
        OpenSession session = OpenSession.newSession(request);
        session.addResponse(response);

        OpenWebNet client = OpenWebNet.newClient(OpenWebNet.gateway(GATEWAY_HOST, GATEWAY_PORT));
        OpenWebNet clientSpy = PowerMockito.mock(OpenWebNet.class, invocation -> Observable.just(client));

        when(clientSpy.send(request)).thenReturn(Observable.just(session));
        when(commonService.findClient(GATEWAY_UUID)).thenReturn(clientSpy);

        return clientSpy;
    }

}
