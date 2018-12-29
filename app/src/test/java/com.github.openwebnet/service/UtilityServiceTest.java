package com.github.openwebnet.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
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
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class UtilityServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    UtilityService utilityService;

    @Inject
    Context contextMock;

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

        @Provides
        @Singleton
        public FirebaseService provideFirebaseService() {
            return mock(FirebaseServiceImpl.class);
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

    private void toggleNetwork(int networkType, NetworkInfo.DetailedState detailedState) {
        NetworkInfo networkInfo = null;
        switch (detailedState) {
            case CONNECTED:
                networkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED, networkType, 0, true, true);
                break;
            case DISCONNECTED:
                networkInfo = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.DISCONNECTED, networkType, 0, true, false);
                break;
        }

        ConnectivityManager cmMock = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager shadowConnectivityManager = shadowOf(cmMock);

        shadowConnectivityManager.setNetworkInfo(networkType, networkInfo);
        shadowConnectivityManager.setActiveNetworkInfo(shadowConnectivityManager.getNetworkInfo(networkType));
    }

    @Test
    public void utilityService_hasMobileNetworkAccess() {
        toggleNetwork(ConnectivityManager.TYPE_MOBILE, NetworkInfo.DetailedState.CONNECTED);
        assertTrue("should have network access", utilityService.hasNetworkAccess());
    }

    @Test
    public void utilityService_hasWifiNetworkAccess() {
        toggleNetwork(ConnectivityManager.TYPE_WIFI, NetworkInfo.DetailedState.CONNECTED);
        assertTrue("should have network access", utilityService.hasNetworkAccess());
    }

    @Test
    public void utilityService_hasNotMobileNetworkAccess() {
        toggleNetwork(ConnectivityManager.TYPE_MOBILE, NetworkInfo.DetailedState.DISCONNECTED);
        assertFalse("should have not network access", utilityService.hasNetworkAccess());
    }

    @Test
    public void utilityService_hasNotWifiNetworkAccess() {
        toggleNetwork(ConnectivityManager.TYPE_WIFI, NetworkInfo.DetailedState.DISCONNECTED);
        assertFalse("should have not network access", utilityService.hasNetworkAccess());
    }

    @Ignore
    @Test
    public void utilityService_hasInternetAccess() {
        // not used
    }

    private EditText initEditText(String text) {
        EditText editText = new EditText(contextMock);
        editText.setText(text);
        return editText;
    }

    @Test
    public void utilityService_isBlankText() {
        assertTrue("should be blank", utilityService.isBlankText(null));
        assertTrue("should be blank", utilityService.isBlankText(initEditText("")));
        assertTrue("should be blank", utilityService.isBlankText(initEditText("  ")));
        assertTrue("should be blank", utilityService.isBlankText(initEditText("\n")));
        assertTrue("should be blank", utilityService.isBlankText(initEditText("  \n  \t  ")));

        assertFalse("should not be blank", utilityService.isBlankText(initEditText("a")));
        assertFalse("should not be blank", utilityService.isBlankText(initEditText("  a  a  ")));
    }

    @Test(expected = NullPointerException.class)
    public void utilityService_sanitizedText_isNull() {
        utilityService.sanitizedText(null);
    }

    @Test
    public void utilityService_sanitizedText() {
        assertEquals("should be sanitized", "a", utilityService.sanitizedText(initEditText(" a ")));
        assertEquals("should be sanitized", "a b", utilityService.sanitizedText(initEditText("  \n   a  \t   b  ")));
    }

}
