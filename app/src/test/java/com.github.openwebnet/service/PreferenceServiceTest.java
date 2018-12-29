package com.github.openwebnet.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModule;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.service.impl.CommonServiceImpl;
import com.github.openwebnet.service.impl.FirebaseServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;
import com.github.openwebnet.service.impl.UtilityServiceImpl;

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
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

import static com.github.niqdev.openwebnet.message.Heating.TemperatureScale.CELSIUS;
import static com.github.niqdev.openwebnet.message.Heating.TemperatureScale.FAHRENHEIT;
import static com.github.openwebnet.service.impl.PreferenceServiceImpl.KEY_FIRST_RUN;
import static com.github.openwebnet.service.impl.PreferenceServiceImpl.PREFERENCE_DEFAULT;
import static com.github.openwebnet.service.impl.PreferenceServiceImpl.PREFERENCE_MAIN;
import static com.github.openwebnet.view.settings.GatewayListPreference.PREF_DEFAULT_GATEWAY_KEY;
import static com.github.openwebnet.view.settings.SettingsFragment.PREF_KEY_DEBUG_DEVICE;
import static com.github.openwebnet.view.settings.SettingsFragment.PREF_KEY_TEMPERATURE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class PreferenceServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    PreferenceService preferenceService;

    @Singleton
    @Component(modules = {
        PreferenceApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface PreferenceComponentTest extends ApplicationComponent {

        void inject(PreferenceServiceTest service);

    }

    @Module
    public class PreferenceApplicationContextModuleTest {

        @Provides
        @Singleton
        public Context applicationContext() {
            return RuntimeEnvironment.application.getApplicationContext();
        }

        @Provides
        @Singleton
        public PreferenceService providePreferenceService() {
            return new PreferenceServiceImpl();
        }

        @Provides
        @Singleton
        public CommonService provideCommonService() {
            return mock(CommonServiceImpl.class);
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
        PreferenceComponentTest applicationComponentTest = DaggerPreferenceServiceTest_PreferenceComponentTest.builder()
            .preferenceApplicationContextModuleTest(new PreferenceApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((PreferenceComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    private SharedPreferences getMockedMainSharedPreferences() {
        return RuntimeEnvironment.application.getSharedPreferences(PREFERENCE_MAIN, Context.MODE_PRIVATE);
    }

    private SharedPreferences getMockedDefaultSharedPreferences() {
        return RuntimeEnvironment.application.getSharedPreferences(PREFERENCE_DEFAULT, Context.MODE_PRIVATE);
    }

    @Test
    public void testIsFirstRun() {
        assertTrue("should be first run", preferenceService.isFirstRun());
        getMockedMainSharedPreferences().edit().putBoolean(KEY_FIRST_RUN, false).commit();
        assertFalse("should not be first run", preferenceService.isFirstRun());
    }

    @Test
    public void testInitFirstRun() {
        assertTrue("should not init first run", preferenceService.isFirstRun());
        preferenceService.initFirstRun();
        assertFalse("should init first run", preferenceService.isFirstRun());
    }

    @Test
    public void testGetDefaultGateway() {
        String GATEWAY = "myGateway";
        assertNull("should be null gateway", preferenceService.getDefaultGateway());
        getMockedDefaultSharedPreferences().edit().putString(PREF_DEFAULT_GATEWAY_KEY, GATEWAY).commit();
        assertEquals("invalid gateway", GATEWAY, preferenceService.getDefaultGateway());
    }

    @Test
    public void testGetDefaultTemperatureScale() {
        assertEquals("invalid temperature scale", CELSIUS, preferenceService.getDefaultTemperatureScale());
        getMockedDefaultSharedPreferences().edit().putString(PREF_KEY_TEMPERATURE, FAHRENHEIT.name()).commit();
        assertEquals("invalid temperature scale", FAHRENHEIT, preferenceService.getDefaultTemperatureScale());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultTemperatureScale_invalidKey() {
        getMockedDefaultSharedPreferences().edit().putString(PREF_KEY_TEMPERATURE, "INVALID_TS").commit();
        preferenceService.getDefaultTemperatureScale();
    }

    @Test
    public void testIsDeviceDebugEnabled() {
        assertFalse("debug should not be enabled", preferenceService.isDeviceDebugEnabled());
        getMockedDefaultSharedPreferences().edit().putBoolean(PREF_KEY_DEBUG_DEVICE, true).commit();
        assertTrue("debug should be enabled", preferenceService.isDeviceDebugEnabled());
    }

    @Ignore
    @Test
    public void testGetSecurePreferences() {
        //throws java.lang.IllegalStateException: java.security.NoSuchAlgorithmException: PBKDF2WithHmacSHA1 SecretKeyFactory not available
    }

}
