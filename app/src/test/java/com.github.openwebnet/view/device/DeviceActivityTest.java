package com.github.openwebnet.view.device;

import android.content.Context;
import android.content.Intent;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.impl.CommonServiceImpl;
import com.github.openwebnet.service.impl.DeviceServiceImpl;
import com.github.openwebnet.service.impl.EnvironmentServiceImpl;
import com.github.openwebnet.service.impl.GatewayServiceImpl;
import com.github.openwebnet.service.impl.LightServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import javax.inject.Singleton;

import butterknife.ButterKnife;
import dagger.Component;
import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class DeviceActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ActivityController<DeviceActivity> controller;
    private DeviceActivity activity;

    @Singleton
    @Component(modules = {DeviceActivityModuleTest.class, RepositoryModuleTest.class})
    public interface DeviceActivityComponentTest extends ApplicationComponent {

        void inject(DeviceActivityTest activity);

    }

    @Module
    public class DeviceActivityModuleTest {

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
        DeviceService provideDeviceService() {
            return mock(DeviceServiceImpl.class);
        }

        @Provides
        @Singleton
        EnvironmentService provideEnvironmentService() {
            return new EnvironmentServiceImpl();
        }

        @Provides
        @Singleton
        GatewayService provideGatewayService() {
            return new GatewayServiceImpl();
        }

        @Provides
        @Singleton
        LightService provideLightService() {
            return new LightServiceImpl();
        }

    }

    @Before
    public void setupDagger() {
        DeviceActivityComponentTest applicationComponentTest = DaggerDeviceActivityTest_DeviceActivityComponentTest.builder()
            .deviceActivityModuleTest(new DeviceActivityModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((DeviceActivityComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    private void createWithIntent(String uuidExtra) {
        controller = Robolectric.buildActivity(DeviceActivity.class);

        Intent intent = new Intent(RuntimeEnvironment.application, DeviceActivity.class);
        intent.putExtra(RealmModel.FIELD_UUID, uuidExtra);
        activity = controller
            .withIntent(intent)
            .create()
            .start()
            .resume()
            .visible()
            .get();

        ButterKnife.bind(this, activity);
    }

    @Test
    @Ignore
    public void shouldVerifyOnCreate_initSpinner_noResult() {

    }

    @Test
    @Ignore
    public void shouldVerifyOnCreate_initSpinner_selectedDefault() {

    }

    @Test
    @Ignore
    public void shouldVerifyOnCreate_initEditWithoutUuid() {

    }

    @Test
    @Ignore
    public void shouldVerifyOnCreate_initEditWithUuid() {

    }

    @Test
    @Ignore
    public void shouldVerifyOnCreate_initAcceptDisclaimer() {

    }

    @Test
    @Ignore
    public void shouldVerifyOnCreate_onMenuSave_invalid() {

    }

    @Test
    @Ignore
    public void shouldVerifyOnCreate_onMenuSave_validAdd() {

    }

    @Test
    @Ignore
    public void shouldVerifyOnCreate_onMenuSave_validEdit() {

    }

}
