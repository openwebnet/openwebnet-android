package com.github.openwebnet.service;

import com.github.niqdev.openwebnet.OpenSession;
import com.github.niqdev.openwebnet.OpenWebNet;
import com.github.niqdev.openwebnet.message.Automation;
import com.github.niqdev.openwebnet.message.OpenMessage;
import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModule;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.AutomationRepository;

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
public class AutomationServiceTest {

    private static final String GATEWAY_UUID = "myGateway";

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    AutomationRepository automationRepository;

    @Inject
    AutomationService automationService;

    @Inject
    CommonService commonService;

    @Singleton
    @Component(modules = {
        ApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface AutomationComponentTest extends ApplicationComponent {

        void inject(AutomationServiceTest service);

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
        AutomationComponentTest applicationComponentTest = DaggerAutomationServiceTest_AutomationComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((AutomationComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void automationService_add() {
        String AUTOMATION_UUID = "myUuid";
        AutomationModel automationModel = new AutomationModel();

        when(automationRepository.add(automationModel)).thenReturn(Observable.just(AUTOMATION_UUID));

        TestSubscriber<String> tester = new TestSubscriber<>();
        automationService.add(automationModel).subscribe(tester);

        verify(automationRepository).add(automationModel);

        tester.assertValue(AUTOMATION_UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationService_update() {
        AutomationModel automationModel = new AutomationModel();

        when(automationRepository.update(automationModel)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        automationService.update(automationModel).subscribe(tester);

        verify(automationRepository).update(automationModel);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationService_delete() {
        String AUTOMATION_UUID = "myUuid";

        when(automationRepository.delete(AUTOMATION_UUID)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        automationService.delete(AUTOMATION_UUID).subscribe(tester);

        verify(automationRepository).delete(AUTOMATION_UUID);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationService_findById() {
        String AUTOMATION_UUID = "myUuid";
        AutomationModel automationModel = new AutomationModel();
        automationModel.setUuid(AUTOMATION_UUID);
        // filter invalid where
        automationModel.setWhere("11");
        automationModel.setAutomationType(Automation.Type.POINT_TO_POINT);
        automationModel.setBus("");

        when(automationRepository.findById(AUTOMATION_UUID)).thenReturn(Observable.just(automationModel));

        TestSubscriber<AutomationModel> tester = new TestSubscriber<>();
        automationService.findById(AUTOMATION_UUID).subscribe(tester);

        verify(automationRepository).findById(AUTOMATION_UUID);

        tester.assertValue(automationModel);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationService_findByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<AutomationModel> automationModels = new ArrayList<>();

        when(automationRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(automationModels));

        TestSubscriber<List<AutomationModel>> tester = new TestSubscriber<>();
        automationService.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(automationRepository).findByEnvironment(ENVIRONMENT);

        tester.assertValue(automationModels);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationService_findFavourites() {
        List<AutomationModel> automationModels = new ArrayList<>();

        when(automationRepository.findFavourites()).thenReturn(Observable.just(automationModels));

        TestSubscriber<List<AutomationModel>> tester = new TestSubscriber<>();
        automationService.findFavourites().subscribe(tester);

        verify(automationRepository).findFavourites();

        tester.assertValue(automationModels);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationService_requestByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<AutomationModel> automations = new ArrayList<>();

        when(automationRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(automations));
        mockClient();

        TestSubscriber<List<AutomationModel>> tester = new TestSubscriber<>();
        automationService.requestByEnvironment(ENVIRONMENT).subscribe(tester);

        tester.assertValue(automations);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationService_requestFavourites() {
        List<AutomationModel> automations = new ArrayList<>();

        when(automationRepository.findFavourites()).thenReturn(Observable.just(automations));
        mockClient();

        TestSubscriber<List<AutomationModel>> tester = new TestSubscriber<>();
        automationService.requestFavourites().subscribe(tester);

        tester.assertValue(automations);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationService_stop() {
        AutomationModel automation = mockAutomationModel();
        mockClient();

        TestSubscriber<AutomationModel> tester = new TestSubscriber<>();
        automationService.stop(automation).subscribe(tester);

        verify(commonService).findClient(GATEWAY_UUID);

        tester.assertValue(automation);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationService_moveUp() {
        AutomationModel automation = mockAutomationModel();
        mockClient();

        TestSubscriber<AutomationModel> tester = new TestSubscriber<>();
        automationService.moveUp(automation).subscribe(tester);

        verify(commonService).findClient(GATEWAY_UUID);

        tester.assertValue(automation);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationService_moveDown() {
        AutomationModel automation = mockAutomationModel();
        mockClient();

        TestSubscriber<AutomationModel> tester = new TestSubscriber<>();
        automationService.moveDown(automation).subscribe(tester);

        verify(commonService).findClient(GATEWAY_UUID);

        tester.assertValue(automation);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    private AutomationModel mockAutomationModel() {
        return AutomationModel.updateBuilder("uuid")
            .environment(108)
            .gateway(GATEWAY_UUID)
            .name("automation")
            .where("21")
            .type(Automation.Type.POINT_TO_POINT)
            .bus(Automation.NO_BUS)
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
