package com.github.openwebnet.service;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModule;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.GatewayRepository;

import org.junit.Before;
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
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PrepareForTest({Injector.class, GatewayModel.class})
public class GatewayServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    GatewayRepository gatewayRepository;

    @Inject
    GatewayService gatewayService;

    @Singleton
    @Component(modules = {
        ApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface GatewayComponentTest extends ApplicationComponent {

        void inject(GatewayServiceTest service);

    }

    @Before
    public void setupDagger() {
        GatewayComponentTest applicationComponentTest = DaggerGatewayServiceTest_GatewayComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((GatewayComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void gatewayService_add() {
        String GATEWAY_UUID = "gatewayUuid";
        String GATEWAY_HOST = "1.1.1.1";
        Integer GATEWAY_PORT = 88;

        GatewayModel gateway = new GatewayModel();
        gateway.setUuid(GATEWAY_UUID);
        gateway.setHost(GATEWAY_HOST);
        gateway.setPort(GATEWAY_PORT);

        PowerMockito.mockStatic(GatewayModel.class);
        when(GatewayModel.newGateway(GATEWAY_HOST, GATEWAY_PORT)).thenReturn(gateway);
        when(gatewayRepository.add(gateway)).thenReturn(Observable.just(GATEWAY_UUID));

        TestSubscriber<String> tester = new TestSubscriber<>();
        gatewayService.add(GATEWAY_HOST, GATEWAY_PORT).subscribe(tester);

        verify(gatewayRepository).add(gateway);

        tester.assertValue(GATEWAY_UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void gatewayService_findAll() {
        List<GatewayModel> gateways = new ArrayList<>();

        when(gatewayRepository.findAll()).thenReturn(Observable.just(gateways));

        TestSubscriber<List<GatewayModel>> tester = new TestSubscriber<>();
        gatewayService.findAll().subscribe(tester);

        verify(gatewayRepository).findAll();

        tester.assertValue(gateways);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void gatewayService_findById() {
        String GATEWAY_UUID = "gatewayUuid";
        GatewayModel gateway = new GatewayModel();
        gateway.setUuid(GATEWAY_UUID);
        gateway.setHost("1.1.1.1");
        gateway.setPort(88);

        when(gatewayRepository.findById(GATEWAY_UUID)).thenReturn(Observable.just(gateway));

        TestSubscriber<GatewayModel> tester = new TestSubscriber<>();
        gatewayService.findById(GATEWAY_UUID).subscribe(tester);

        verify(gatewayRepository).findById(GATEWAY_UUID);

        tester.assertValue(gateway);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

}
