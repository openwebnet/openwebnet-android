package com.github.openwebnet.service;

import com.github.niqdev.openwebnet.OpenSession;
import com.github.niqdev.openwebnet.OpenWebNet;
import com.github.niqdev.openwebnet.message.Lighting;
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
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.repository.LightRepository;

import org.junit.After;
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
import rx.plugins.RxJavaTestPlugins;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"android.*"})
@PrepareForTest({Injector.class})
public class LightServiceTest {

    private static final String GATEWAY_UUID = "myGateway";

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    LightRepository lightRepository;

    @Inject
    LightService lightService;

    @Inject
    CommonService commonService;

    @Singleton
    @Component(modules = {
        ApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface LightComponentTest extends ApplicationComponent {

        void inject(LightServiceTest service);

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
        LightComponentTest applicationComponentTest = DaggerLightServiceTest_LightComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
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
    public void lightService_requestByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<LightModel> lights = new ArrayList<>();

        mockClient();
        when(lightRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(lights));

        TestSubscriber<List<LightModel>> tester = new TestSubscriber<>();
        lightService.requestByEnvironment(ENVIRONMENT).subscribe(tester);

        tester.assertValue(lights);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void lightService_requestFavourites() {
        List<LightModel> lights = new ArrayList<>();

        mockClient();
        when(lightRepository.findFavourites()).thenReturn(Observable.just(lights));

        TestSubscriber<List<LightModel>> tester = new TestSubscriber<>();
        lightService.requestFavourites().subscribe(tester);

        tester.assertValue(lights);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void lightService_turnOn() {
        LightModel light = mockLightModel();
        mockClient();

        TestSubscriber<LightModel> tester = new TestSubscriber<>();
        lightService.turnOn(light).subscribe(tester);

        verify(commonService).findClient(GATEWAY_UUID);

        tester.assertValue(light);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void lightService_turnOff() {
        LightModel light = mockLightModel();
        mockClient();

        TestSubscriber<LightModel> tester = new TestSubscriber<>();
        lightService.turnOff(light).subscribe(tester);

        verify(commonService).findClient(GATEWAY_UUID);

        tester.assertValue(light);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    private LightModel mockLightModel() {
        return LightModel.updateBuilder("uuid")
            .environment(108)
            .gateway(GATEWAY_UUID)
            .name("light")
            .where("21")
            .type(Lighting.Type.POINT_TO_POINT)
            .build();
    }

    private OpenWebNet mockClient() {
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
