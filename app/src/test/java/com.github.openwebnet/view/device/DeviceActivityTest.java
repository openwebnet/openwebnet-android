package com.github.openwebnet.view.device;

import android.content.Context;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.matcher.DeviceModelMatcher;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnergyService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.IpcamService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.ScenarioService;
import com.github.openwebnet.service.SoundService;
import com.github.openwebnet.service.TemperatureService;
import com.github.openwebnet.service.UtilityService;
import com.github.openwebnet.service.impl.AutomationServiceImpl;
import com.github.openwebnet.service.impl.CommonServiceImpl;
import com.github.openwebnet.service.impl.DeviceServiceImpl;
import com.github.openwebnet.service.impl.EnergyServiceImpl;
import com.github.openwebnet.service.impl.EnvironmentServiceImpl;
import com.github.openwebnet.service.impl.GatewayServiceImpl;
import com.github.openwebnet.service.impl.IpcamServiceImpl;
import com.github.openwebnet.service.impl.LightServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;
import com.github.openwebnet.service.impl.ScenarioServiceImpl;
import com.github.openwebnet.service.impl.SoundServiceImpl;
import com.github.openwebnet.service.impl.TemperatureServiceImpl;
import com.github.openwebnet.service.impl.UtilityServiceImpl;

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
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import rx.Observable;

import static com.github.openwebnet.view.device.DeviceActivity.EXTRA_DEFAULT_ENVIRONMENT;
import static com.github.openwebnet.view.device.DeviceActivity.EXTRA_DEFAULT_GATEWAY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class DeviceActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @BindView(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @BindView(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @BindView(R.id.editTextDeviceName)
    EditText editTextDeviceName;

    @BindView(R.id.editTextDeviceRequest)
    EditText editTextDeviceRequest;

    @BindView(R.id.editTextDeviceResponse)
    EditText editTextDeviceResponse;

    @BindView(R.id.checkBoxDeviceFavourite)
    CheckBox checkBoxDeviceFavourite;

    @BindView(R.id.checkBoxDeviceRunOnLoad)
    CheckBox checkBoxDeviceRunOnLoad;

    @BindView(R.id.checkBoxDeviceConfirm)
    CheckBox checkBoxDeviceConfirm;

    @BindView(R.id.checkBoxDeviceAccept)
    CheckBox checkBoxDeviceAccept;

    @BindView(R.id.textViewDevicePasteResponse)
    TextView textViewDevicePasteResponse;

    @BindView(R.id.imageButtonDevicePasteResponse)
    ImageButton imageButtonDevicePasteResponse;

    @BindString(R.string.label_none)
    String labelNone;

    @BindString(R.string.label_missing_gateway)
    String labelMissingGateway;

    @BindString(R.string.validation_required)
    String validationRequired;

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
    @Component(modules = {DeviceActivityModuleTest.class, DatabaseModuleTest.class, RepositoryModuleTest.class})
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
        public UtilityService provideUtilityService() {
            return new UtilityServiceImpl();
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

        @Provides
        @Singleton
        AutomationService provideAutomationService() {
            return new AutomationServiceImpl();
        }

        @Provides
        @Singleton
        IpcamService provideIpcamService() {
            return new IpcamServiceImpl();
        }

        @Provides
        @Singleton
        TemperatureService provideTemperatureService() {
            return new TemperatureServiceImpl();
        }

        @Provides
        @Singleton
        ScenarioService provideScenarioService() {
            return new ScenarioServiceImpl();
        }

        @Provides
        @Singleton
        EnergyService provideEnergyService() {
            return new EnergyServiceImpl();
        }

        @Provides
        @Singleton
        SoundService provideSoundService() {
            return new SoundServiceImpl();
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

    private void startActivity() {
        createActivityWithIntent(null, null, null);
    }

    private void createActivityWithIntent(String uuidExtra, Integer defaultEnvironment, String defaultGateway) {
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

        startActivity();

        SpinnerAdapter adapterEnvironment = spinnerDeviceEnvironment.getAdapter();
        assertFalse("should not be empty", adapterEnvironment.isEmpty());
        assertEquals("should verify first element", labelNone, adapterEnvironment.getItem(0));

        SpinnerAdapter adapterGateway = spinnerDeviceGateway.getAdapter();
        assertFalse("should not be empty", adapterGateway.isEmpty());
        assertEquals("should verify first element", labelMissingGateway, adapterGateway.getItem(0));
    }

    @Test
    public void shouldVerifyOnCreate_initSpinner_selectedDefault() {
        List<EnvironmentModel> environments = Arrays.asList(newEnvironment(100, "env1"),
            newEnvironment(101, "env2"), newEnvironment(102, "env3"));

        List<GatewayModel> gateways = Arrays.asList(newGateway("uuid1", "1.1.1.1", 1),
            newGateway("uuid2", "2.2.2.2", 2), newGateway("uuid3", "3.3.3.3", 3));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));
        when(gatewayService.findAll()).thenReturn(Observable.just(gateways));

        createActivityWithIntent(null, 101, "uuid2");

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
    public void shouldVerifyOnCreate_initEditWithoutUuid() {
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());
        when(gatewayService.findAll()).thenReturn(Observable.<List<GatewayModel>>empty());

        startActivity();

        verify(mock(DeviceServiceImpl.class), never()).findById(anyString());

        assertEquals("invalid value", "", editTextDeviceName.getText().toString());
        assertEquals("invalid value", "", editTextDeviceRequest.getText().toString());
        assertEquals("invalid value", "", editTextDeviceResponse.getText().toString());

        assertEquals("invalid value", false, checkBoxDeviceFavourite.isChecked());
        assertEquals("invalid value", false, checkBoxDeviceRunOnLoad.isChecked());
        assertEquals("invalid value", true, checkBoxDeviceConfirm.isChecked());
        assertEquals("invalid value", false, checkBoxDeviceAccept.isChecked());

        assertEquals("invalid value", -1, spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", -1, spinnerDeviceGateway.getSelectedItemPosition());

        assertFalse("should not be visible", textViewDevicePasteResponse.isShown());
        assertFalse("should not be visible", imageButtonDevicePasteResponse.isShown());
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithUuid() {
        String DEVICE_UUID = "myUuid";
        String DEVICE_NAME = "myName";
        String DEVICE_GATEWAY_SELECTED = "uuid8";
        String DEVICE_REQUEST = "myRequest";
        String DEVICE_RESPONSE = "myResponse";
        Integer DEVICE_ENVIRONMENT_SELECTED = 108;
        boolean DEVICE_FAVOURITE = true;
        boolean DEVICE_RUN_ON_LOAD = true;
        boolean DEVICE_SHOW_CONFIRMATION = false;

        DeviceModel deviceModel = DeviceModel.updateBuilder(DEVICE_UUID)
            .name(DEVICE_NAME)
            .request(DEVICE_REQUEST)
            .response(DEVICE_RESPONSE)
            .environment(DEVICE_ENVIRONMENT_SELECTED)
            .gateway(DEVICE_GATEWAY_SELECTED)
            .favourite(DEVICE_FAVOURITE)
            .runOnLoad(DEVICE_RUN_ON_LOAD)
            .showConfirmation(DEVICE_SHOW_CONFIRMATION)
            .build();

        List<EnvironmentModel> environments = Arrays.asList(newEnvironment(100, "env1"),
            newEnvironment(DEVICE_ENVIRONMENT_SELECTED, "env8"), newEnvironment(102, "env3"));

        List<GatewayModel> gateways = Arrays.asList(newGateway("uuid1", "1.1.1.1", 1),
            newGateway(DEVICE_GATEWAY_SELECTED, "8.8.8.8", 8), newGateway("uuid3", "3.3.3.3", 3));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));
        when(gatewayService.findAll()).thenReturn(Observable.just(gateways));
        when(deviceService.findById(anyString())).thenReturn(Observable.just(deviceModel));

        createActivityWithIntent(DEVICE_UUID, DEVICE_ENVIRONMENT_SELECTED, DEVICE_GATEWAY_SELECTED);

        assertEquals("invalid value", DEVICE_NAME, editTextDeviceName.getText().toString());
        assertEquals("invalid value", DEVICE_REQUEST, editTextDeviceRequest.getText().toString());
        assertEquals("invalid value", DEVICE_RESPONSE, editTextDeviceResponse.getText().toString());

        assertEquals("invalid value", DEVICE_FAVOURITE, checkBoxDeviceFavourite.isChecked());
        assertEquals("invalid value", DEVICE_RUN_ON_LOAD, checkBoxDeviceRunOnLoad.isChecked());
        assertEquals("invalid value", DEVICE_SHOW_CONFIRMATION, checkBoxDeviceConfirm.isChecked());
        assertEquals("invalid value", false, checkBoxDeviceAccept.isChecked());

        assertEquals("invalid value", 1, spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", 1, spinnerDeviceGateway.getSelectedItemPosition());
    }

    @Test
    @Ignore
    public void shouldVerifyOnCreate_initPasteRespone() {

    }

    private void beforeExpectRequired() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(gatewayService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        startActivity();
    }

    private void expectRequired(TextView view) {
        activity.onOptionsItemSelected(new RoboMenuItem(R.id.action_device_save));
        verify(deviceService, never()).add(any(DeviceModel.class));
        verify(deviceService, never()).update(any(DeviceModel.class));
        assertEquals("bad validation", validationRequired, view.getError());
        //assertTrue("bad focus", view.hasFocus());
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalidRequiredAccept() {
        beforeExpectRequired();
        expectRequired(checkBoxDeviceAccept);
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalidRequiredName() {
        beforeExpectRequired();
        checkBoxDeviceAccept.setChecked(true);
        expectRequired(editTextDeviceName);
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalidRequiredRequest() {
        beforeExpectRequired();
        checkBoxDeviceAccept.setChecked(true);
        editTextDeviceName.setText("myName");
        expectRequired(editTextDeviceRequest);
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalidRequiredResponse() {
        beforeExpectRequired();
        checkBoxDeviceAccept.setChecked(true);
        editTextDeviceName.setText("myName");
        editTextDeviceRequest.setText("myRequest");
        expectRequired(editTextDeviceResponse);
    }

    private DeviceModel common_onMenuSave_valid(String uuidExtra) {
        String DEVICE_NAME = "myName";
        String DEVICE_GATEWAY_SELECTED = "uuid8";
        String DEVICE_REQUEST = "myRequest";
        String DEVICE_RESPONSE = "myResponse";
        Integer DEVICE_ENVIRONMENT_SELECTED = 108;
        boolean DEVICE_FAVOURITE = true;
        boolean DEVICE_RUN_ON_LOAD = true;
        boolean DEVICE_SHOW_CONFIRMATION = false;

        when(environmentService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newEnvironment(DEVICE_ENVIRONMENT_SELECTED, "env8"))));
        when(gatewayService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newGateway(DEVICE_GATEWAY_SELECTED, "8.8.8.8", 8))));

        String DEVICE_UUID = uuidExtra != null ? uuidExtra : "myNewUuid";

        when(deviceService.add(any(DeviceModel.class))).thenReturn(Observable.just(DEVICE_UUID));
        when(deviceService.update(any(DeviceModel.class))).thenReturn(Observable.just(null));

        createActivityWithIntent(uuidExtra, null, null);

        editTextDeviceName.setText(DEVICE_NAME);
        editTextDeviceRequest.setText(DEVICE_REQUEST);
        editTextDeviceResponse.setText(DEVICE_RESPONSE);
        checkBoxDeviceFavourite.setChecked(DEVICE_FAVOURITE);
        checkBoxDeviceRunOnLoad.setChecked(DEVICE_RUN_ON_LOAD);
        checkBoxDeviceConfirm.setChecked(DEVICE_SHOW_CONFIRMATION);
        checkBoxDeviceAccept.setChecked(true);

        spinnerDeviceEnvironment.setSelection(0);
        spinnerDeviceGateway.setSelection(0);

        activity.onOptionsItemSelected(new RoboMenuItem(R.id.action_device_save));

        return DeviceModel.updateBuilder(DEVICE_UUID)
            .name(DEVICE_NAME)
            .request(DEVICE_REQUEST)
            .response(DEVICE_RESPONSE)
            .environment(DEVICE_ENVIRONMENT_SELECTED)
            .gateway(DEVICE_GATEWAY_SELECTED)
            .favourite(DEVICE_FAVOURITE)
            .runOnLoad(DEVICE_RUN_ON_LOAD)
            .showConfirmation(DEVICE_SHOW_CONFIRMATION)
            .build();
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validAdd() {
        DeviceModel deviceMock = common_onMenuSave_valid(null);

        verify(deviceService, times(1)).add(DeviceModelMatcher.equalsTo(deviceMock));
        verify(deviceService, never()).update(any(DeviceModel.class));
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validEdit() {
        when(deviceService.findById(anyString())).thenReturn(Observable.<DeviceModel>empty());

        DeviceModel deviceMock = common_onMenuSave_valid("anyUuid");

        verify(deviceService, never()).add(any(DeviceModel.class));
        verify(deviceService, times(1)).update(DeviceModelMatcher.equalsTo(deviceMock));
    }

}
