package com.github.openwebnet.view.device;

import android.content.Context;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.IpcamService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.impl.AutomationServiceImpl;
import com.github.openwebnet.service.impl.CommonServiceImpl;
import com.github.openwebnet.service.impl.DeviceServiceImpl;
import com.github.openwebnet.service.impl.EnvironmentServiceImpl;
import com.github.openwebnet.service.impl.GatewayServiceImpl;
import com.github.openwebnet.service.impl.IpcamServiceImpl;
import com.github.openwebnet.service.impl.LightServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;

import org.junit.After;
import org.junit.Before;
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

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class IpcamActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Bind(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @BindString(R.string.label_none)
    String labelNone;

    @Inject
    EnvironmentService environmentService;

    private ActivityController<IpcamActivity> controller;
    private IpcamActivity activity;

    @Singleton
    @Component(modules = {IpcamActivityModuleTest.class, DatabaseModuleTest.class, RepositoryModuleTest.class})
    public interface IpcamActivityComponentTest extends ApplicationComponent {

        void inject(IpcamActivityTest activity);

    }

    @Module
    public class IpcamActivityModuleTest {

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
            return new DeviceServiceImpl();
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

        @Provides
        @Singleton
        AutomationService provideAutomationService() {
            return new AutomationServiceImpl();
        }

        @Provides
        @Singleton
        IpcamService provideIpcamService() {
            return mock(IpcamServiceImpl.class);
        }

    }

    @Before
    public void setupDagger() {
        IpcamActivityComponentTest applicationComponentTest = DaggerIpcamActivityTest_IpcamActivityComponentTest.builder()
            .ipcamActivityModuleTest(new IpcamActivityModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((IpcamActivityComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
    }

    private void createWithIntent(String uuidExtra) {
        controller = Robolectric.buildActivity(IpcamActivity.class);

        Intent intent = new Intent(RuntimeEnvironment.application, IpcamActivity.class);
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
    public void shouldVerifyOnCreate_hideSpinnerGateway() {

    }

    @Test
    public void shouldVerifyOnCreate_initSpinnerStreamType() {

    }

    @Test
    public void shouldVerifyOnCreate_initSpinnerEnvironment_noResult() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        SpinnerAdapter adapterEnvironment = spinnerDeviceEnvironment.getAdapter();
        assertFalse("should not be empty", adapterEnvironment.isEmpty());
        assertEquals("should verify first element", labelNone, adapterEnvironment.getItem(0));
    }

    @Test
    public void shouldVerifyOnCreate_initSpinnerEnvironment() {

    }

    @Test
    public void shouldVerifyOnCreate_initAuthentication() {

    }

    @Test
    public void shouldVerifyOnCreate_initEditWithoutUuid() {

    }

    @Test
    public void shouldVerifyOnCreate_initEditWithUuid() {

    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalid() {

    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validAdd() {

    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validEdit() {

    }
}
