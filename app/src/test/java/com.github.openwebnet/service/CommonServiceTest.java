package com.github.openwebnet.service;

import android.content.Context;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplication;
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
import org.powermock.api.mockito.PowerMockito;
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

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
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
    @Component(modules = {SimpleApplicationContextModuleTest.class, DomoticModuleTest.class, RepositoryModuleTest.class})
    public interface CommonComponentTest extends ApplicationComponent {

        void inject(CommonServiceTest service);

    }

    @Module
    public class SimpleApplicationContextModuleTest {

        @Provides
        @Singleton
        public OpenWebNetApplication application() {
            return (OpenWebNetApplication) RuntimeEnvironment.application;
        }

        @Provides
        @Singleton
        public Context applicationContext() {
            return RuntimeEnvironment.application.getApplicationContext();
        }

        @Provides
        @Singleton
        public PreferenceService providePreferenceService() {
            // TODO PreferenceServiceImpl
            return mock(PreferenceServiceImpl.class);
        }

        @Provides
        @Singleton
        public CommonService provideCommonService() {
            // TODO CommonServiceImpl
            return mock(CommonServiceImpl.class);
        }

    }

    @Before
    public void setupDagger() {
        CommonComponentTest applicationComponentTest = DaggerCommonServiceTest_CommonComponentTest.builder()
            .simpleApplicationContextModuleTest(new SimpleApplicationContextModuleTest())
            .domoticModuleTest(new DomoticModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((CommonComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    @Ignore
    public void commonService_isFirstTime() {
        int ID_ENVIRONMENT = 108;
        int ID_LABEL = 8888;
        String LABEL_ENVIRONMENT = "myEnvironment";

        when(preferenceService.isFirstRun()).thenReturn(true);
        when(commonService.getString(ID_LABEL)).thenReturn(LABEL_ENVIRONMENT);
        when(environmentService.add(LABEL_ENVIRONMENT)).thenReturn(Observable.just(ID_ENVIRONMENT));
        doCallRealMethod().when(commonService).initApplication();

        // TODO error dependecy injection Context is null
        commonService.initApplication();

        verify(environmentService, times(1)).add(LABEL_ENVIRONMENT);
        verify(preferenceService, times(1)).initFirstRun();
    }

    @Test
    @Ignore
    public void commonService_isNotFirstTime() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test
    @Ignore
    public void commonService_findClient() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test
    @Ignore
    public void commonService_getString() {
        throw new UnsupportedOperationException("not implemented yet");
    }

}
