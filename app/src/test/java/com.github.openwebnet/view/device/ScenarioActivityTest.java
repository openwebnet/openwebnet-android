package com.github.openwebnet.view.device;

import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.github.openwebnet.matcher.ScenarioModelMatcher;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.model.ScenarioModel;
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
import static org.junit.Assert.assertFalse;
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
public class ScenarioActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @BindView(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @BindView(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @BindView(R.id.checkBoxDeviceFavourite)
    CheckBox checkBoxDeviceFavourite;

    @BindView(R.id.editTextScenarioName)
    EditText editTextScenarioName;

    @BindView(R.id.editTextScenarioWhere)
    EditText editTextScenarioWhere;

    @BindString(R.string.validation_required)
    String validationRequired;

    @BindString(R.string.label_none)
    String labelNone;

    @BindString(R.string.label_missing_gateway)
    String labelMissingGateway;

    @Inject
    ScenarioService scenarioService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    GatewayService gatewayService;

    private ActivityController<ScenarioActivity> controller;
    private ScenarioActivity activity;

    @Singleton
    @Component(modules = {ScenarioActivityModuleTest.class, DatabaseModuleTest.class, RepositoryModuleTest.class})
    public interface ScenarioActivityComponentTest extends ApplicationComponent {

        void inject(ScenarioActivityTest activity);

    }

    @Module
    public class ScenarioActivityModuleTest {

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
            return mock(ScenarioServiceImpl.class);
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
        ScenarioActivityComponentTest applicationComponentTest = DaggerScenarioActivityTest_ScenarioActivityComponentTest.builder()
            .scenarioActivityModuleTest(new ScenarioActivityModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ScenarioActivityComponentTest) Injector.getApplicationComponent()).inject(this);
    }
    
    @After
    public void tearDown() {
        controller.pause().stop().destroy();
    }

    private void createWithIntent(String uuidExtra) {
        Intent intent = new Intent(RuntimeEnvironment.application, ScenarioActivity.class);
        intent.putExtra(RealmModel.FIELD_UUID, uuidExtra);

        controller = Robolectric.buildActivity(ScenarioActivity.class, intent);

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
    public void shouldVerifyOnCreate_initSpinner_noResult() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(gatewayService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        SpinnerAdapter adapterEnvironment = spinnerDeviceEnvironment.getAdapter();
        assertFalse("should not be empty", adapterEnvironment.isEmpty());
        assertEquals("should verify first element", labelNone, adapterEnvironment.getItem(0));

        SpinnerAdapter adapterGateway = spinnerDeviceGateway.getAdapter();
        assertFalse("should not be empty", adapterGateway.isEmpty());
        assertEquals("should verify first element", labelMissingGateway, adapterGateway.getItem(0));
    }

    @Test
    public void shouldVerifyOnCreate_initSpinner() {
        List<EnvironmentModel> environments = Arrays.
            asList(newEnvironment(100, "env1"), newEnvironment(101, "env2"));

        List<GatewayModel> gateways = Arrays.
            asList(newGateway("uuid1", "1.1.1.1", 1), newGateway("uuid2", "2.2.2.2", 2));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));
        when(gatewayService.findAll()).thenReturn(Observable.just(gateways));

        createWithIntent(null);

        SpinnerAdapter adapterEnvironment = spinnerDeviceEnvironment.getAdapter();
        assertFalse("should not be empty", adapterEnvironment.isEmpty());
        assertEquals("should verify first element", "env1", adapterEnvironment.getItem(0));
        assertEquals("should verify second element", "env2", adapterEnvironment.getItem(1));
        assertEquals("should select default", 0, spinnerDeviceEnvironment.getSelectedItemPosition());

        SpinnerAdapter adapterGateway = spinnerDeviceGateway.getAdapter();
        assertFalse("should not be empty", adapterGateway.isEmpty());
        assertEquals("should verify first element", "1.1.1.1:1", adapterGateway.getItem(0));
        assertEquals("should verify second element", "2.2.2.2:2", adapterGateway.getItem(1));
        assertEquals("should select default", 0, spinnerDeviceGateway.getSelectedItemPosition());
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithoutUuid() {
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());
        when(gatewayService.findAll()).thenReturn(Observable.<List<GatewayModel>>empty());

        createWithIntent(null);

        verify(mock(ScenarioServiceImpl.class), never()).findById(anyString());

        assertEquals("invalid value", "", editTextScenarioName.getText().toString());
        assertEquals("invalid value", "", editTextScenarioWhere.getText().toString());
        
        assertEquals("invalid value", false, checkBoxDeviceFavourite.isChecked());
        assertEquals("invalid value", -1, spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", -1, spinnerDeviceGateway.getSelectedItemPosition());
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithUuid() {
        String SCENARIO_UUID = "myUuid";
        String SCENARIO_NAME = "myName";
        String SCENARIO_GATEWAY_SELECTED = "uuid2";
        String SCENARIO_WHERE = "08";
        Integer SCENARIO_ENVIRONMENT_SELECTED = 108;
        boolean SCENARIO_FAVOURITE = true;

        List<EnvironmentModel> environments = Arrays.
            asList(newEnvironment(100, "env1"), newEnvironment(SCENARIO_ENVIRONMENT_SELECTED, "env8"));

        List<GatewayModel> gateways = Arrays.
            asList(newGateway("uuid1", "1.1.1.1", 1), newGateway(SCENARIO_GATEWAY_SELECTED, "2.2.2.2", 2));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));
        when(gatewayService.findAll()).thenReturn(Observable.just(gateways));

        ScenarioModel scenarioModel = ScenarioModel.updateBuilder(SCENARIO_UUID)
            .name(SCENARIO_NAME)
            .where(SCENARIO_WHERE)
            .environment(SCENARIO_ENVIRONMENT_SELECTED)
            .gateway(SCENARIO_GATEWAY_SELECTED)
            .favourite(SCENARIO_FAVOURITE)
            .build();

        when(scenarioService.findById(SCENARIO_UUID)).thenReturn(Observable.just(scenarioModel));

        createWithIntent(SCENARIO_UUID);

        assertEquals("invalid value", SCENARIO_NAME, editTextScenarioName.getText().toString());
        assertEquals("invalid value", String.valueOf(SCENARIO_WHERE), editTextScenarioWhere.getText().toString());

        assertEquals("invalid value", SCENARIO_FAVOURITE, checkBoxDeviceFavourite.isChecked());

        EnvironmentModel environmentSelected = environments.get(spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", environmentSelected.getId(), SCENARIO_ENVIRONMENT_SELECTED);

        GatewayModel gatewaySelected = gateways.get(spinnerDeviceGateway.getSelectedItemPosition());
        assertEquals("invalid value", SCENARIO_GATEWAY_SELECTED, gatewaySelected.getUuid());
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalid() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(gatewayService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        expectRequired(editTextScenarioName);
        editTextScenarioName.setText("nameValue");

        expectRequired(editTextScenarioWhere);
        editTextScenarioWhere.setText("31");

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
        verify(scenarioService, never()).add(any(ScenarioModel.class));
        verify(scenarioService, never()).update(any(ScenarioModel.class));
        assertEquals("bad validation", validationRequired, view.getError());
        //assertTrue("bad focus", view.hasFocus());
    }

    private ScenarioModel common_onMenuSave_valid(String uuidExtra) {
        String SCENARIO_NAME = "myName";
        String SCENARIO_GATEWAY_SELECTED = "uuid2";
        String SCENARIO_WHERE = "08";
        Integer SCENARIO_ENVIRONMENT_SELECTED = 101;
        boolean SCENARIO_FAVOURITE = true;

        when(environmentService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newEnvironment(SCENARIO_ENVIRONMENT_SELECTED, "env1"))));
        when(gatewayService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newGateway(SCENARIO_GATEWAY_SELECTED, "2.2.2.2", 2))));

        String SCENARIO_UUID = uuidExtra != null ? uuidExtra : "myNewUuid";
        when(scenarioService.add(any(ScenarioModel.class))).thenReturn(Observable.just(SCENARIO_UUID));
        when(scenarioService.update(any(ScenarioModel.class))).thenReturn(Observable.just(null));

        createWithIntent(uuidExtra);

        editTextScenarioName.setText(String.valueOf(SCENARIO_NAME));
        editTextScenarioWhere.setText(String.valueOf(SCENARIO_WHERE));

        checkBoxDeviceFavourite.setChecked(SCENARIO_FAVOURITE);

        // for simplicity only 1 items
        spinnerDeviceEnvironment.setSelection(0);
        spinnerDeviceGateway.setSelection(0);

        activity.onOptionsItemSelected(new RoboMenuItem(R.id.action_device_save));

        ScenarioModel scenarioModel = new ScenarioModel();
        scenarioModel.setUuid(uuidExtra);
        scenarioModel.setName(SCENARIO_NAME);
        scenarioModel.setWhere(SCENARIO_WHERE);
        scenarioModel.setEnvironmentId(SCENARIO_ENVIRONMENT_SELECTED);
        scenarioModel.setGatewayUuid(SCENARIO_GATEWAY_SELECTED);
        scenarioModel.setFavourite(SCENARIO_FAVOURITE);
        return scenarioModel;
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validAdd() {
        ScenarioModel scenarioMock = common_onMenuSave_valid(null);

        verify(scenarioService, times(1)).add(ScenarioModelMatcher.scenarioModelEq(scenarioMock));
        verify(scenarioService, never()).update(any(ScenarioModel.class));
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validEdit() {
        when(scenarioService.findById(anyString())).thenReturn(Observable.<ScenarioModel>empty());

        ScenarioModel scenarioMock = common_onMenuSave_valid("anyUuid");

        verify(scenarioService, never()).add(any(ScenarioModel.class));
        verify(scenarioService, times(1)).update(ScenarioModelMatcher.scenarioModelEq(scenarioMock));
    }

}
