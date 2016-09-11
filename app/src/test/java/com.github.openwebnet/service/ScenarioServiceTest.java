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
import com.github.openwebnet.model.ScenarioModel;
import com.github.openwebnet.repository.ScenarioRepository;

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
public class ScenarioServiceTest {

    private static final String GATEWAY_UUID = "myGateway";

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    ScenarioRepository scenarioRepository;

    @Inject
    ScenarioService scenarioService;

    @Inject
    CommonService commonService;

    @Singleton
    @Component(modules = {
        ApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface ScenarioComponentTest extends ApplicationComponent {

        void inject(ScenarioServiceTest service);

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
        ScenarioComponentTest applicationComponentTest = DaggerScenarioServiceTest_ScenarioComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ScenarioComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void scenarioService_add() {
        String SCENARIO_UUID = "myUuid";
        ScenarioModel scenarioModel = new ScenarioModel();

        when(scenarioRepository.add(scenarioModel)).thenReturn(Observable.just(SCENARIO_UUID));

        TestSubscriber<String> tester = new TestSubscriber<>();
        scenarioService.add(scenarioModel).subscribe(tester);

        verify(scenarioRepository).add(scenarioModel);

        tester.assertValue(SCENARIO_UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void scenarioService_update() {
        ScenarioModel scenarioModel = new ScenarioModel();

        when(scenarioRepository.update(scenarioModel)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        scenarioService.update(scenarioModel).subscribe(tester);

        verify(scenarioRepository).update(scenarioModel);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void scenarioService_delete() {
        String SCENARIO_UUID = "myUuid";

        when(scenarioRepository.delete(SCENARIO_UUID)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        scenarioService.delete(SCENARIO_UUID).subscribe(tester);

        verify(scenarioRepository).delete(SCENARIO_UUID);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void scenarioService_findById() {
        String SCENARIO_UUID = "myUuid";
        ScenarioModel scenarioModel = new ScenarioModel();
        scenarioModel.setUuid(SCENARIO_UUID);

        when(scenarioRepository.findById(SCENARIO_UUID)).thenReturn(Observable.just(scenarioModel));

        TestSubscriber<ScenarioModel> tester = new TestSubscriber<>();
        scenarioService.findById(SCENARIO_UUID).subscribe(tester);

        verify(scenarioRepository).findById(SCENARIO_UUID);

        tester.assertValue(scenarioModel);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void scenarioService_findByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<ScenarioModel> scenarios = new ArrayList<>();

        when(scenarioRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(scenarios));

        TestSubscriber<List<ScenarioModel>> tester = new TestSubscriber<>();
        scenarioService.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(scenarioRepository).findByEnvironment(ENVIRONMENT);

        tester.assertValue(scenarios);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void scenarioService_findFavourites() {
        List<ScenarioModel> scenarios = new ArrayList<>();

        when(scenarioRepository.findFavourites()).thenReturn(Observable.just(scenarios));

        TestSubscriber<List<ScenarioModel>> tester = new TestSubscriber<>();
        scenarioService.findFavourites().subscribe(tester);

        verify(scenarioRepository).findFavourites();

        tester.assertValue(scenarios);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void scenarioService_requestByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<ScenarioModel> scenarios = new ArrayList<>();

        mockClient();
        when(scenarioRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(scenarios));

        TestSubscriber<List<ScenarioModel>> tester = new TestSubscriber<>();
        scenarioService.requestByEnvironment(ENVIRONMENT).subscribe(tester);

        tester.assertValue(scenarios);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void scenarioService_requestFavourites() {
        List<ScenarioModel> scenarios = new ArrayList<>();

        mockClient();
        when(scenarioRepository.findFavourites()).thenReturn(Observable.just(scenarios));

        TestSubscriber<List<ScenarioModel>> tester = new TestSubscriber<>();
        scenarioService.requestFavourites().subscribe(tester);

        tester.assertValue(scenarios);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void scenarioService_start() {
        ScenarioModel scenario = mockScenarioModel();
        mockClient();

        TestSubscriber<ScenarioModel> tester = new TestSubscriber<>();
        scenarioService.start(scenario).subscribe(tester);

        verify(commonService).findClient(GATEWAY_UUID);

        tester.assertValue(scenario);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void scenarioService_stop() {
        ScenarioModel scenario = mockScenarioModel();
        mockClient();

        TestSubscriber<ScenarioModel> tester = new TestSubscriber<>();
        scenarioService.stop(scenario).subscribe(tester);

        verify(commonService).findClient(GATEWAY_UUID);

        tester.assertValue(scenario);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    private ScenarioModel mockScenarioModel() {
        return ScenarioModel.updateBuilder("uuid")
            .environment(108)
            .gateway(GATEWAY_UUID)
            .name("scenario")
            .where("31")
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
