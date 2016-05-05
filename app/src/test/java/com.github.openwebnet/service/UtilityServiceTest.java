package com.github.openwebnet.service;

import android.content.Context;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.service.impl.CommonServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;
import com.github.openwebnet.service.impl.UtilityServiceImpl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class UtilityServiceTest {

    @Inject
    UtilityService utilityService;

    @Singleton
    @Component(modules = {
        UtilityApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModuleTest.class
    })
    public interface UtilityComponentTest extends ApplicationComponent {

        void inject(UtilityServiceTest service);

    }

    @Module
    public class UtilityApplicationContextModuleTest {

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
            return mock(CommonServiceImpl.class);
        }

        @Provides
        @Singleton
        public UtilityService provideUtilityService() {
            return new UtilityServiceImpl();
        }

    }

    @Before
    public void setupDagger() {
        UtilityComponentTest applicationComponentTest = DaggerUtilityServiceTest_UtilityComponentTest.builder()
            .utilityApplicationContextModuleTest(new UtilityApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModuleTest(new DomoticModuleTest())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((UtilityComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void utilityService_getString() {
        String expected = utilityService.getString(R.string.drawer_menu_example);
        assertEquals("invalid string", "Example environment", expected);
    }

    @Ignore
    @Test
    public void utilityService_hasNetworkAccess() {

    }

    @Ignore
    @Test
    public void utilityService_hasInternetAccess() {

    }

    @Ignore
    @Test
    public void utilityService_isBlankText() {

    }

    @Ignore
    @Test
    public void utilityService_sanitizeText() {

    }

}
