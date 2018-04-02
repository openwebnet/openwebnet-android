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
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.EnergyRepository;

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
public class EnergyServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    EnergyRepository energyRepository;

    @Inject
    EnergyService energyService;

    @Inject
    CommonService commonService;

    @Singleton
    @Component(modules = {
        ApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface EnergyComponentTest extends ApplicationComponent {

        void inject(EnergyServiceTest service);

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
        EnergyComponentTest applicationComponentTest = DaggerEnergyServiceTest_EnergyComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((EnergyComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void energyService_add() {
        String ENERGY_UUID = "myUuid";
        EnergyModel energyModel = new EnergyModel();

        when(energyRepository.add(energyModel)).thenReturn(Observable.just(ENERGY_UUID));

        TestSubscriber<String> tester = new TestSubscriber<>();
        energyService.add(energyModel).subscribe(tester);

        verify(energyRepository).add(energyModel);

        tester.assertValue(ENERGY_UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void energyService_update() {
        EnergyModel energyModel = new EnergyModel();

        when(energyRepository.update(energyModel)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        energyService.update(energyModel).subscribe(tester);

        verify(energyRepository).update(energyModel);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void energyService_delete() {
        String ENERGY_UUID = "myUuid";

        when(energyRepository.delete(ENERGY_UUID)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        energyService.delete(ENERGY_UUID).subscribe(tester);

        verify(energyRepository).delete(ENERGY_UUID);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void energyService_findById() {
        String ENERGY_UUID = "myUuid";
        EnergyModel energyModel = new EnergyModel();
        energyModel.setUuid(ENERGY_UUID);

        when(energyRepository.findById(ENERGY_UUID)).thenReturn(Observable.just(energyModel));

        TestSubscriber<EnergyModel> tester = new TestSubscriber<>();
        energyService.findById(ENERGY_UUID).subscribe(tester);

        verify(energyRepository).findById(ENERGY_UUID);

        tester.assertValue(energyModel);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void energyService_findByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<EnergyModel> energies = new ArrayList<>();

        when(energyRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(energies));

        TestSubscriber<List<EnergyModel>> tester = new TestSubscriber<>();
        energyService.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(energyRepository).findByEnvironment(ENVIRONMENT);

        tester.assertValue(energies);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void energyService_findFavourites() {
        List<EnergyModel> energies = new ArrayList<>();

        when(energyRepository.findFavourites()).thenReturn(Observable.just(energies));

        TestSubscriber<List<EnergyModel>> tester = new TestSubscriber<>();
        energyService.findFavourites().subscribe(tester);

        verify(energyRepository).findFavourites();

        tester.assertValue(energies);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void energyService_requestByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<EnergyModel> energies = new ArrayList<>();

        mockClient();
        when(energyRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(energies));

        TestSubscriber<List<EnergyModel>> tester = new TestSubscriber<>();
        energyService.requestByEnvironment(ENVIRONMENT).subscribe(tester);

        tester.assertValue(energies);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void energyService_requestFavourites() {
        List<EnergyModel> energies = new ArrayList<>();

        mockClient();
        when(energyRepository.findFavourites()).thenReturn(Observable.just(energies));

        TestSubscriber<List<EnergyModel>> tester = new TestSubscriber<>();
        energyService.requestFavourites().subscribe(tester);

        tester.assertValue(energies);
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
