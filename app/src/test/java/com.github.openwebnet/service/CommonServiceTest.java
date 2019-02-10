package com.github.openwebnet.service;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.niqdev.openwebnet.OpenWebNet;
import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.service.impl.CommonServiceImpl;
import com.github.openwebnet.service.impl.FirebaseServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;
import com.github.openwebnet.service.impl.UtilityServiceImpl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class CommonServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    CommonService commonService;

    @Inject
    PreferenceService preferenceService;

    @Inject
    UtilityService utilityService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    GatewayService gatewayService;

    @Singleton
    @Component(modules = {
        CommonApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModuleTest.class
    })
    public interface CommonComponentTest extends ApplicationComponent {

        void inject(CommonServiceTest service);

    }

    @Module
    public class CommonApplicationContextModuleTest {

        @Provides
        @Singleton
        public Context applicationContext() {
            return RuntimeEnvironment.application.getApplicationContext();
        }

        @Provides
        @Singleton
        public PreferenceService providePreferenceService() {
            return mock(PreferenceServiceImpl.class);
        }

        @Provides
        @Singleton
        public CommonService provideCommonService() {
            return new CommonServiceImpl();
        }

        @Provides
        @Singleton
        public UtilityService provideUtilityService() {
            return mock(UtilityServiceImpl.class);
        }

        @Provides
        @Singleton
        public FirebaseService provideFirebaseService() {
            return mock(FirebaseServiceImpl.class);
        }

    }

    @Before
    public void setupDagger() {
        CommonComponentTest applicationComponentTest = DaggerCommonServiceTest_CommonComponentTest.builder()
            .commonApplicationContextModuleTest(new CommonApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModuleTest(new DomoticModuleTest())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((CommonComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void commonService_initApplication_isFirstTime() {
        int ID_ENVIRONMENT = 108;
        int ID_LABEL = R.string.drawer_menu_example;
        String LABEL_ENVIRONMENT = "Example environment";

        when(preferenceService.isFirstRun()).thenReturn(true);

        when(utilityService.getString(ID_LABEL)).thenReturn(LABEL_ENVIRONMENT);
        when(environmentService.add(LABEL_ENVIRONMENT)).thenReturn(Observable.just(ID_ENVIRONMENT));

        commonService.initApplication(mock(AppCompatActivity.class));

        verify(environmentService, times(1)).add(LABEL_ENVIRONMENT);
        verify(preferenceService, times(1)).initFirstRun();
    }

    @Test
    public void commonService_initApplication_isNotFirstTime() {
        when(preferenceService.isFirstRun()).thenReturn(false);

        commonService.initApplication(mock(AppCompatActivity.class));

        verify(environmentService, never()).add(anyString());
        verify(preferenceService, never()).initFirstRun();
    }

    @Test
    public void commonService_findClient() {
        String GATEWAY_UUID = "gatewayUuid";
        GatewayModel gateway = new GatewayModel();
        gateway.setUuid(GATEWAY_UUID);
        gateway.setHost("host");
        gateway.setPort(123);

        when(preferenceService.isFirstRun()).thenReturn(false);
        when(gatewayService.findById(GATEWAY_UUID)).thenReturn(Observable.just(gateway));

        commonService.initApplication(mock(AppCompatActivity.class));
        OpenWebNet client = commonService.findClient(GATEWAY_UUID);

        assertNotNull("null client", client);
        verify(gatewayService).findById(GATEWAY_UUID);
    }

    @Test
    public void commonService_getDefaultGateway() {
        String DEFAULT_GATEWAY = "gatewayUuid";
        when(preferenceService.getDefaultGateway()).thenReturn(DEFAULT_GATEWAY);

        assertEquals("bad value", DEFAULT_GATEWAY, commonService.getDefaultGateway());

        verify(preferenceService).getDefaultGateway();
    }

}
