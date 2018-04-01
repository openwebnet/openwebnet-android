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
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.DeviceRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
public class DeviceServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    DeviceRepository deviceRepository;

    @Inject
    DeviceService deviceService;

    @Inject
    CommonService commonService;

    @Singleton
    @Component(modules = {
        ApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface DeviceComponentTest extends ApplicationComponent {

        void inject(DeviceServiceTest service);

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
        DeviceComponentTest applicationComponentTest = DaggerDeviceServiceTest_DeviceComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((DeviceComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void deviceService_add() {
        String DEVICE_UUID = "myUuid";
        DeviceModel deviceModel = new DeviceModel();

        when(deviceRepository.add(deviceModel)).thenReturn(Observable.just(DEVICE_UUID));

        TestSubscriber<String> tester = new TestSubscriber<>();
        deviceService.add(deviceModel).subscribe(tester);

        verify(deviceRepository).add(deviceModel);

        tester.assertValue(DEVICE_UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void deviceService_update() {
        DeviceModel deviceModel = new DeviceModel();

        when(deviceRepository.update(deviceModel)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        deviceService.update(deviceModel).subscribe(tester);

        verify(deviceRepository).update(deviceModel);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void deviceService_delete() {
        String DEVICE_UUID = "myUuid";

        when(deviceRepository.delete(DEVICE_UUID)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        deviceService.delete(DEVICE_UUID).subscribe(tester);

        verify(deviceRepository).delete(DEVICE_UUID);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void deviceService_findById() {
        String DEVICE_UUID = "myUuid";
        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setUuid(DEVICE_UUID);

        when(deviceRepository.findById(DEVICE_UUID)).thenReturn(Observable.just(deviceModel));

        TestSubscriber<DeviceModel> tester = new TestSubscriber<>();
        deviceService.findById(DEVICE_UUID).subscribe(tester);

        verify(deviceRepository).findById(DEVICE_UUID);

        tester.assertValue(deviceModel);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void deviceService_findByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<DeviceModel> devices = new ArrayList<>();

        when(deviceRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(devices));

        TestSubscriber<List<DeviceModel>> tester = new TestSubscriber<>();
        deviceService.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(deviceRepository).findByEnvironment(ENVIRONMENT);

        tester.assertValue(devices);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void deviceService_findFavourites() {
        List<DeviceModel> devices = new ArrayList<>();

        when(deviceRepository.findFavourites()).thenReturn(Observable.just(devices));

        TestSubscriber<List<DeviceModel>> tester = new TestSubscriber<>();
        deviceService.findFavourites().subscribe(tester);

        verify(deviceRepository).findFavourites();

        tester.assertValue(devices);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void deviceService_requestByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<DeviceModel> devices = new ArrayList<>();

        mockClient();
        when(deviceRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(devices));

        TestSubscriber<List<DeviceModel>> tester = new TestSubscriber<>();
        deviceService.requestByEnvironment(ENVIRONMENT).subscribe(tester);

        tester.assertValue(devices);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void deviceService_requestFavourites() {
        List<DeviceModel> devices = new ArrayList<>();

        mockClient();
        when(deviceRepository.findFavourites()).thenReturn(Observable.just(devices));

        TestSubscriber<List<DeviceModel>> tester = new TestSubscriber<>();
        deviceService.requestFavourites().subscribe(tester);

        tester.assertValue(devices);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Ignore
    @Test
    public void deviceService_sendRequest() {

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
