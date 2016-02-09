package com.github.openwebnet.view.device;

import android.content.Context;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
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

import org.junit.After;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import rx.Observable;

import static com.github.openwebnet.view.device.DeviceActivity.EXTRA_DEFAULT_ENVIRONMENT;
import static com.github.openwebnet.view.device.DeviceActivity.EXTRA_DEFAULT_GATEWAY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class DeviceActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Bind(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @Bind(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @BindString(R.string.label_none)
    String labelNone;

    @Inject
    DeviceService deviceService;

    @Inject
    PreferenceService preferenceService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    GatewayService gatewayService;

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
            return new PreferenceServiceImpl();
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

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
    }

    private void createWithIntent(String uuidExtra, Integer defaultEnvironment, String defaultGateway) {
        controller = Robolectric.buildActivity(DeviceActivity.class);

        Intent intent = new Intent(RuntimeEnvironment.application, DeviceActivity.class);
        intent.putExtra(RealmModel.FIELD_UUID, uuidExtra);
        if (defaultEnvironment != null) {
            intent.putExtra(EXTRA_DEFAULT_ENVIRONMENT, defaultEnvironment);
        }
        if (defaultGateway != null) {
            intent.putExtra(EXTRA_DEFAULT_GATEWAY, defaultGateway);
        }
        activity = controller
            .withIntent(intent)
            .create()
            .start()
            .resume()
            .visible()
            .get();

        ButterKnife.bind(this, activity);
    }

    private EnvironmentModel newEnvironment(Integer id, String name) {
        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(id);
        environment.setName(name);
        return environment;
    }

    private GatewayModel newGateway(String uuid, String host, Integer port) {
        GatewayModel gateway = new GatewayModel();
        gateway.setUuid(uuid);
        gateway.setHost(host);
        gateway.setPort(port);
        return gateway;
    }

    @Test
    public void shouldVerifyOnCreate_initSpinner_noResult() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(gatewayService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null, null, null);

        SpinnerAdapter adapterEnvironment = spinnerDeviceEnvironment.getAdapter();
        assertFalse("should not be empty", adapterEnvironment.isEmpty());
        assertEquals("should verify first element", labelNone, adapterEnvironment.getItem(0));

        SpinnerAdapter adapterGateway = spinnerDeviceGateway.getAdapter();
        assertFalse("should not be empty", adapterGateway.isEmpty());
        assertEquals("should verify first element", labelNone, adapterGateway.getItem(0));
    }

    @Test
    public void shouldVerifyOnCreate_initSpinner_selectedDefault() {
        List<EnvironmentModel> environments = Arrays.asList(newEnvironment(100, "env1"),
            newEnvironment(101, "env2"), newEnvironment(102, "env3"));

        List<GatewayModel> gateways = Arrays.asList(newGateway("uuid1", "1.1.1.1", 1),
            newGateway("uuid2", "2.2.2.2", 2), newGateway("uuid3", "3.3.3.3", 3));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));
        when(gatewayService.findAll()).thenReturn(Observable.just(gateways));

        createWithIntent(null, 101, "uuid2");

        SpinnerAdapter adapterEnvironment = spinnerDeviceEnvironment.getAdapter();
        assertFalse("should not be empty", adapterEnvironment.isEmpty());
        assertEquals("should verify 1st element", "env1", adapterEnvironment.getItem(0));
        assertEquals("should verify 2nd element", "env2", adapterEnvironment.getItem(1));
        assertEquals("should verify 3rd element", "env3", adapterEnvironment.getItem(2));
        assertEquals("should be selected", 1, spinnerDeviceEnvironment.getSelectedItemPosition());

        SpinnerAdapter adapterGateway = spinnerDeviceGateway.getAdapter();
        assertFalse("should not be empty", adapterGateway.isEmpty());
        assertEquals("should verify 1st element", "1.1.1.1:1", adapterGateway.getItem(0));
        assertEquals("should verify 2nd element", "2.2.2.2:2", adapterGateway.getItem(1));
        assertEquals("should verify 3rd element", "3.3.3.3:3", adapterGateway.getItem(2));
        assertEquals("should be selected", 1, spinnerDeviceGateway.getSelectedItemPosition());
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
    public void shouldVerifyOnCreate_initPasteRespone() {

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
