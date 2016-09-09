package com.github.openwebnet.view.device;

import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.matcher.TemperatureModelMatcher;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.model.TemperatureModel;
import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.IpcamService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.ScenarioService;
import com.github.openwebnet.service.TemperatureService;
import com.github.openwebnet.service.UtilityService;
import com.github.openwebnet.service.impl.AutomationServiceImpl;
import com.github.openwebnet.service.impl.CommonServiceImpl;
import com.github.openwebnet.service.impl.DeviceServiceImpl;
import com.github.openwebnet.service.impl.EnvironmentServiceImpl;
import com.github.openwebnet.service.impl.GatewayServiceImpl;
import com.github.openwebnet.service.impl.IpcamServiceImpl;
import com.github.openwebnet.service.impl.LightServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;
import com.github.openwebnet.service.impl.ScenarioServiceImpl;
import com.github.openwebnet.service.impl.TemperatureServiceImpl;
import com.github.openwebnet.service.impl.UtilityServiceImpl;

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

import static org.junit.Assert.assertEquals;
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
public class TemperatureActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @BindView(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @BindView(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @BindView(R.id.checkBoxDeviceFavourite)
    CheckBox checkBoxDeviceFavourite;

    @BindView(R.id.editTextTemperatureName)
    EditText editTextTemperatureName;

    @BindView(R.id.editTextTemperatureWhere)
    EditText editTextTemperatureWhere;

    @BindString(R.string.validation_required)
    String validationRequired;

    @BindString(R.string.label_none)
    String labelNone;

    @Inject
    TemperatureService temperatureService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    GatewayService gatewayService;

    private ActivityController<TemperatureActivity> controller;
    private TemperatureActivity activity;

    @Singleton
    @Component(modules = {TemperatureActivityModuleTest.class, DatabaseModuleTest.class, RepositoryModuleTest.class})
    public interface TemperatureActivityComponentTest extends ApplicationComponent {

        void inject(TemperatureActivityTest activity);

    }

    @Module
    public class TemperatureActivityModuleTest {

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
            return new IpcamServiceImpl();
        }

        @Provides
        @Singleton
        TemperatureService provideTemperatureService() {
            return mock(TemperatureServiceImpl.class);
        }

        @Provides
        @Singleton
        ScenarioService provideScenarioService() {
            return new ScenarioServiceImpl();
        }

    }

    @Before
    public void setupDagger() {
        TemperatureActivityComponentTest applicationComponentTest = DaggerTemperatureActivityTest_TemperatureActivityComponentTest.builder()
            .temperatureActivityModuleTest(new TemperatureActivityModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((TemperatureActivityComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    private void createWithIntent(String uuidExtra) {
        controller = Robolectric.buildActivity(TemperatureActivity.class);

        Intent intent = new Intent(RuntimeEnvironment.application, TemperatureActivity.class);
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
    public void shouldVerifyOnCreate_initEditWithoutUuid() {
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());
        when(gatewayService.findAll()).thenReturn(Observable.<List<GatewayModel>>empty());

        createWithIntent(null);

        verify(mock(LightServiceImpl.class), never()).findById(anyString());

        assertEquals("invalid value", "", editTextTemperatureName.getText().toString());
        assertEquals("invalid value", "", editTextTemperatureWhere.getText().toString());
        assertEquals("invalid value", false, checkBoxDeviceFavourite.isChecked());

        assertEquals("invalid value", -1, spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", -1, spinnerDeviceGateway.getSelectedItemPosition());
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithUuid() {
        String TEMPERATURE_UUID = "myUuid";
        String TEMPERATURE_NAME = "myName";
        String TEMPERATURE_GATEWAY_SELECTED = "uuid2";
        String TEMPERATURE_WHERE = "08";
        Integer TEMPERATURE_ENVIRONMENT_SELECTED = 108;
        boolean TEMPERATURE_FAVOURITE = true;

        List<EnvironmentModel> environments = Arrays.
            asList(newEnvironment(100, "env1"), newEnvironment(TEMPERATURE_ENVIRONMENT_SELECTED, "env8"));

        List<GatewayModel> gateways = Arrays.
            asList(newGateway("uuid1", "1.1.1.1", 1), newGateway(TEMPERATURE_GATEWAY_SELECTED, "2.2.2.2", 2));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));
        when(gatewayService.findAll()).thenReturn(Observable.just(gateways));

        TemperatureModel temperatureModel = TemperatureModel.updateBuilder(TEMPERATURE_UUID)
            .name(TEMPERATURE_NAME)
            .where(TEMPERATURE_WHERE)
            .environment(TEMPERATURE_ENVIRONMENT_SELECTED)
            .gateway(TEMPERATURE_GATEWAY_SELECTED)
            .favourite(TEMPERATURE_FAVOURITE)
            .build();

        when(temperatureService.findById(TEMPERATURE_UUID)).thenReturn(Observable.just(temperatureModel));

        createWithIntent(TEMPERATURE_UUID);

        assertEquals("invalid value", TEMPERATURE_NAME, editTextTemperatureName.getText().toString());
        assertEquals("invalid value", String.valueOf(TEMPERATURE_WHERE), editTextTemperatureWhere.getText().toString());
        assertEquals("invalid value", TEMPERATURE_FAVOURITE, checkBoxDeviceFavourite.isChecked());

        EnvironmentModel environmentSelected = environments.get(spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", environmentSelected.getId(), TEMPERATURE_ENVIRONMENT_SELECTED);

        GatewayModel gatewaySelected = gateways.get(spinnerDeviceGateway.getSelectedItemPosition());
        assertEquals("invalid value", TEMPERATURE_GATEWAY_SELECTED, gatewaySelected.getUuid());
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalid() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(gatewayService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        expectRequired(editTextTemperatureName);
        editTextTemperatureName.setText("nameValue");

        expectRequired(editTextTemperatureWhere);
        editTextTemperatureWhere.setText("21");

        expectRequired((TextView) spinnerDeviceEnvironment.getSelectedView());
        ArrayAdapter<String> adapterEnvironment = new ArrayAdapter<>(activity,
            android.R.layout.simple_spinner_dropdown_item, Arrays.asList("ENVIRONMENT"));
        spinnerDeviceEnvironment.setAdapter(adapterEnvironment);

        expectRequired((TextView) spinnerDeviceGateway.getSelectedView());
        ArrayAdapter<String> adapterGateway = new ArrayAdapter<>(activity,
            android.R.layout.simple_spinner_dropdown_item, Arrays.asList("GATEWAY"));
        spinnerDeviceGateway.setAdapter(adapterGateway);

        // now is valid
    }

    private void expectRequired(TextView view) {
        activity.onOptionsItemSelected(new RoboMenuItem(R.id.action_device_save));
        verify(temperatureService, never()).add(any(TemperatureModel.class));
        verify(temperatureService, never()).update(any(TemperatureModel.class));
        assertEquals("bad validation", validationRequired, view.getError());
    }

    private TemperatureModel common_onMenuSave_valid(String uuidExtra) {
        String TEMPERATURE_NAME = "myName";
        String TEMPERATURE_GATEWAY_SELECTED = "uuid2";
        String TEMPERATURE_WHERE = "08";
        Integer TEMPERATURE_ENVIRONMENT_SELECTED = 101;
        boolean TEMPERATURE_DIMMER = true;
        boolean TEMPERATURE_FAVOURITE = true;

        when(environmentService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newEnvironment(TEMPERATURE_ENVIRONMENT_SELECTED, "env1"))));
        when(gatewayService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newGateway(TEMPERATURE_GATEWAY_SELECTED, "2.2.2.2", 2))));

        String TEMPERATURE_UUID = uuidExtra != null ? uuidExtra : "myNewUuid";
        when(temperatureService.add(any(TemperatureModel.class))).thenReturn(Observable.just(TEMPERATURE_UUID));
        when(temperatureService.update(any(TemperatureModel.class))).thenReturn(Observable.just(null));

        createWithIntent(uuidExtra);

        editTextTemperatureName.setText(String.valueOf(TEMPERATURE_NAME));
        editTextTemperatureWhere.setText(String.valueOf(TEMPERATURE_WHERE));
        checkBoxDeviceFavourite.setChecked(TEMPERATURE_FAVOURITE);

        // for simplicity only 1 items
        spinnerDeviceEnvironment.setSelection(0);
        spinnerDeviceGateway.setSelection(0);

        activity.onOptionsItemSelected(new RoboMenuItem(R.id.action_device_save));

        TemperatureModel temperatureMock = new TemperatureModel();
        temperatureMock.setUuid(uuidExtra);
        temperatureMock.setName(TEMPERATURE_NAME);
        temperatureMock.setWhere(TEMPERATURE_WHERE);
        temperatureMock.setEnvironmentId(TEMPERATURE_ENVIRONMENT_SELECTED);
        temperatureMock.setGatewayUuid(TEMPERATURE_GATEWAY_SELECTED);
        temperatureMock.setFavourite(TEMPERATURE_FAVOURITE);
        return temperatureMock;
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validAdd() {
        TemperatureModel temperatureMock = common_onMenuSave_valid(null);

        verify(temperatureService, times(1)).add(TemperatureModelMatcher.temperatureModelEq(temperatureMock));
        verify(temperatureService, never()).update(any(TemperatureModel.class));
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validEdit() {
        when(temperatureService.findById(anyString())).thenReturn(Observable.<TemperatureModel>empty());

        TemperatureModel temperatureMock = common_onMenuSave_valid("anyUuid");

        verify(temperatureService, never()).add(any(TemperatureModel.class));
        verify(temperatureService, times(1)).update(TemperatureModelMatcher.temperatureModelEq(temperatureMock));
    }

}
