package com.github.openwebnet.service;

import android.content.Context;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.service.impl.CommonServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
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
    EnvironmentService environmentService;

    @Singleton
    @Component(modules = {CommonApplicationContextModuleTest.class, DomoticModuleTest.class, RepositoryModuleTest.class})
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

    }

    @Before
    public void setupDagger() {
        CommonComponentTest applicationComponentTest = DaggerCommonServiceTest_CommonComponentTest.builder()
            .commonApplicationContextModuleTest(new CommonApplicationContextModuleTest())
            .domoticModuleTest(new DomoticModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
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

        when(Mockito.mock(CommonServiceImpl.class).getString(ID_LABEL)).thenReturn(LABEL_ENVIRONMENT);
        when(environmentService.add(LABEL_ENVIRONMENT)).thenReturn(Observable.just(ID_ENVIRONMENT));

        commonService.initApplication();

        verify(environmentService, times(1)).add(LABEL_ENVIRONMENT);
        verify(preferenceService, times(1)).initFirstRun();
    }

    @Test
    @Ignore
    public void commonService_initApplication_isNotFirstTime() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test
    @Ignore
    public void commonService_findClient() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test
    @Ignore
    public void commonService_getDefaultGateway() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test
    @Ignore
    public void commonService_getString() {
        throw new UnsupportedOperationException("not implemented yet");
    }

}
