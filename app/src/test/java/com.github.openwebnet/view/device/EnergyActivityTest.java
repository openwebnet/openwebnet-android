package com.github.openwebnet.view.device;

import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.niqdev.openwebnet.message.EnergyManagement;
import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.matcher.EnergyModelMatcher;
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnergyService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.FirebaseService;
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
import com.github.openwebnet.service.impl.FirebaseServiceImpl;
import com.github.openwebnet.service.impl.GatewayServiceImpl;
import com.github.openwebnet.service.impl.IpcamServiceImpl;
import com.github.openwebnet.service.impl.LightServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;
import com.github.openwebnet.service.impl.ScenarioServiceImpl;
import com.github.openwebnet.service.impl.SoundServiceImpl;
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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;

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

@RunWith(RobolectricTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class EnergyActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @BindView(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @BindView(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @BindView(R.id.checkBoxDeviceFavourite)
    CheckBox checkBoxDeviceFavourite;

    @BindView(R.id.editTextEnergyName)
    EditText editTextEnergyName;

    @BindView(R.id.editTextEnergyWhere)
    EditText editTextEnergyWhere;

    @BindView(R.id.spinnerEnergyVersion)
    Spinner spinnerEnergyVersion;

    @BindView(R.id.textViewEnergyPrefix)
    TextView textViewEnergyPrefix;

    @BindView(R.id.textViewEnergySuffix)
    TextView textViewEnergySuffix;

    @BindString(R.string.validation_required)
    String validationRequired;

    @BindString(R.string.label_none)
    String labelNone;

    @Inject
    EnergyService energyService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    GatewayService gatewayService;

    private ActivityController<EnergyActivity> controller;
    private EnergyActivity activity;

    @Singleton
    @Component(modules = {EnergyActivityModuleTest.class, DatabaseModuleTest.class, RepositoryModuleTest.class})
    public interface EnergyActivityComponentTest extends ApplicationComponent {

        void inject(EnergyActivityTest activity);

    }

    @Module
    public class EnergyActivityModuleTest {

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
        public FirebaseService provideFirebaseService() {
            return new FirebaseServiceImpl();
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
            return mock(EnergyServiceImpl.class);
        }

        @Provides
        @Singleton
        SoundService provideSoundService() {
            return new SoundServiceImpl();
        }

    }

    @Before
    public void setupDagger() {
        EnergyActivityComponentTest applicationComponentTest = DaggerEnergyActivityTest_EnergyActivityComponentTest.builder()
            .energyActivityModuleTest(new EnergyActivityModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((EnergyActivityComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    private void createWithIntent(String uuidExtra) {
        Intent intent = new Intent(RuntimeEnvironment.application, EnergyActivity.class);
        intent.putExtra(RealmModel.FIELD_UUID, uuidExtra);

        controller = Robolectric.buildActivity(EnergyActivity.class, intent);

        activity = controller
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

        verify(mock(EnergyServiceImpl.class), never()).findById(anyString());

        assertEquals("invalid value", "", editTextEnergyName.getText().toString());
        assertEquals("invalid value", "", editTextEnergyWhere.getText().toString());
        assertEquals("invalid value", 0, spinnerEnergyVersion.getSelectedItemPosition());
        assertEquals("invalid prefix value", "*#18*5", textViewEnergyPrefix.getText().toString());
        assertEquals("invalid suffix value", "*X##", textViewEnergySuffix.getText().toString());

        assertEquals("invalid value", false, checkBoxDeviceFavourite.isChecked());
        assertEquals("invalid value", -1, spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", -1, spinnerDeviceGateway.getSelectedItemPosition());
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithUuid() {
        String ENERGY_UUID = "myUuid";
        String ENERGY_NAME = "myName";
        String ENERGY_GATEWAY_SELECTED = "uuid2";
        String ENERGY_WHERE = "08";
        EnergyManagement.Version ENERGY_VERSION = EnergyManagement.Version.MODEL_F523_A;
        Integer ENERGY_ENVIRONMENT_SELECTED = 108;
        boolean ENERGY_FAVOURITE = true;

        List<EnvironmentModel> environments = Arrays.
            asList(newEnvironment(100, "env1"), newEnvironment(ENERGY_ENVIRONMENT_SELECTED, "env8"));

        List<GatewayModel> gateways = Arrays.
            asList(newGateway("uuid1", "1.1.1.1", 1), newGateway(ENERGY_GATEWAY_SELECTED, "2.2.2.2", 2));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));
        when(gatewayService.findAll()).thenReturn(Observable.just(gateways));

        EnergyModel energyModel = EnergyModel.updateBuilder(ENERGY_UUID)
            .name(ENERGY_NAME)
            .where(ENERGY_WHERE)
            .version(ENERGY_VERSION)
            .environment(ENERGY_ENVIRONMENT_SELECTED)
            .gateway(ENERGY_GATEWAY_SELECTED)
            .favourite(ENERGY_FAVOURITE)
            .build();

        when(energyService.findById(ENERGY_UUID)).thenReturn(Observable.just(energyModel));

        createWithIntent(ENERGY_UUID);

        assertEquals("invalid value", ENERGY_NAME, editTextEnergyName.getText().toString());
        assertEquals("invalid value", String.valueOf(ENERGY_WHERE), editTextEnergyWhere.getText().toString());

        assertEquals("invalid value", 1, spinnerEnergyVersion.getSelectedItemPosition());
        assertEquals("invalid prefix value", "*#18*7", textViewEnergyPrefix.getText().toString());
        assertEquals("invalid suffix value", "#0*X##", textViewEnergySuffix.getText().toString());

        assertEquals("invalid value", ENERGY_FAVOURITE, checkBoxDeviceFavourite.isChecked());

        EnvironmentModel environmentSelected = environments.get(spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", environmentSelected.getId(), ENERGY_ENVIRONMENT_SELECTED);

        GatewayModel gatewaySelected = gateways.get(spinnerDeviceGateway.getSelectedItemPosition());
        assertEquals("invalid value", ENERGY_GATEWAY_SELECTED, gatewaySelected.getUuid());
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalid() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(gatewayService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        expectRequired(editTextEnergyName);
        editTextEnergyName.setText("nameValue");

        expectRequired(editTextEnergyWhere);
        editTextEnergyWhere.setText("21");

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
        verify(energyService, never()).add(any(EnergyModel.class));
        verify(energyService, never()).update(any(EnergyModel.class));
        assertEquals("bad validation", validationRequired, view.getError());
    }

    private EnergyModel common_onMenuSave_valid(String uuidExtra) {
        String ENERGY_NAME = "myName";
        String ENERGY_GATEWAY_SELECTED = "uuid2";
        String ENERGY_WHERE = "08";
        EnergyManagement.Version ENERGY_VERSION = EnergyManagement.Version.MODEL_F523_A;
        Integer ENERGY_ENVIRONMENT_SELECTED = 101;
        boolean ENERGY_FAVOURITE = true;

        when(environmentService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newEnvironment(ENERGY_ENVIRONMENT_SELECTED, "env1"))));
        when(gatewayService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newGateway(ENERGY_GATEWAY_SELECTED, "2.2.2.2", 2))));

        String ENERGY_UUID = uuidExtra != null ? uuidExtra : "myNewUuid";
        when(energyService.add(any(EnergyModel.class))).thenReturn(Observable.just(ENERGY_UUID));
        when(energyService.update(any(EnergyModel.class))).thenReturn(Observable.just(null));

        createWithIntent(uuidExtra);

        editTextEnergyName.setText(String.valueOf(ENERGY_NAME));
        editTextEnergyWhere.setText(String.valueOf(ENERGY_WHERE));
        checkBoxDeviceFavourite.setChecked(ENERGY_FAVOURITE);

        spinnerEnergyVersion.setSelection(1);

        // for simplicity only 1 items
        spinnerDeviceEnvironment.setSelection(0);
        spinnerDeviceGateway.setSelection(0);

        activity.onOptionsItemSelected(new RoboMenuItem(R.id.action_device_save));

        EnergyModel energyModelMock = new EnergyModel();
        energyModelMock.setUuid(uuidExtra);
        energyModelMock.setName(ENERGY_NAME);
        energyModelMock.setWhere(ENERGY_WHERE);
        energyModelMock.setEnergyManagementVersion(ENERGY_VERSION);
        energyModelMock.setEnvironmentId(ENERGY_ENVIRONMENT_SELECTED);
        energyModelMock.setGatewayUuid(ENERGY_GATEWAY_SELECTED);
        energyModelMock.setFavourite(ENERGY_FAVOURITE);
        return energyModelMock;
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validAdd() {
        EnergyModel energyModelMock = common_onMenuSave_valid(null);

        verify(energyService, times(1)).add(EnergyModelMatcher.energyModelEq(energyModelMock));
        verify(energyService, never()).update(any(EnergyModel.class));
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validEdit() {
        when(energyService.findById(anyString())).thenReturn(Observable.<EnergyModel>empty());

        EnergyModel energyModelMock = common_onMenuSave_valid("anyUuid");

        verify(energyService, never()).add(any(EnergyModel.class));
        verify(energyService, times(1)).update(EnergyModelMatcher.energyModelEq(energyModelMock));
    }

}
