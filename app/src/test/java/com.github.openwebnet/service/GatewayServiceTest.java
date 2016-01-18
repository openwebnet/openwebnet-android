package com.github.openwebnet.service;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.GatewayRepository;

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

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PrepareForTest({Injector.class, GatewayModel.class})
public class GatewayServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    GatewayRepository gatewayRepository;

    @Inject
    GatewayService gatewayService;

    @Before
    public void setupDagger() {
        ApplicationComponentTest applicationComponentTest = DaggerApplicationComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .domoticModuleTest(new DomoticModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ApplicationComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    @Ignore
    public void gatewayService_add() {

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
    @Ignore
    public void gatewayService_findById() {

    }

}
