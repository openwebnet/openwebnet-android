package com.github.openwebnet.view.device;

import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.github.niqdev.openwebnet.message.SoundSystem;
import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.matcher.SoundModelMatcher;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.model.SoundModel;
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
import static org.junit.Assert.assertTrue;
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
public class SoundActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @BindView(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @BindView(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @BindView(R.id.checkBoxDeviceFavourite)
    CheckBox checkBoxDeviceFavourite;

    @BindView(R.id.editTextSoundName)
    EditText editTextSoundName;

    @BindView(R.id.editTextSoundWhere)
    EditText editTextSoundWhere;

    @BindView(R.id.spinnerSoundSource)
    Spinner spinnerSoundSource;

    @BindView(R.id.spinnerSoundType)
    Spinner spinnerSoundType;

    @BindString(R.string.validation_required)
    String validationRequired;

    @BindString(R.string.label_none)
    String labelNone;

    @BindString(R.string.label_missing_gateway)
    String labelMissingGateway;

    @Inject
    SoundService soundService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    GatewayService gatewayService;

    private ActivityController<SoundActivity> controller;
    private SoundActivity activity;

    @Singleton
    @Component(modules = {SoundActivityModuleTest.class, DatabaseModuleTest.class, RepositoryModuleTest.class})
    public interface SoundActivityComponentTest extends ApplicationComponent {

        void inject(SoundActivityTest activity);

    }

    @Module
    public class SoundActivityModuleTest {

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
            return mock(SoundServiceImpl.class);
        }

    }

    @Before
    public void setupDagger() {
        SoundActivityComponentTest applicationComponentTest = DaggerSoundActivityTest_SoundActivityComponentTest.builder()
            .soundActivityModuleTest(new SoundActivityModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((SoundActivityComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
    }

    private void createWithIntent(String uuidExtra) {
        Intent intent = new Intent(RuntimeEnvironment.application, SoundActivity.class);
        intent.putExtra(RealmModel.FIELD_UUID, uuidExtra);

        controller = Robolectric.buildActivity(SoundActivity.class, intent);

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

        verifyInitSpinnerSoundType();
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

        verifyInitSpinnerSoundType();
    }

    private void verifyInitSpinnerSoundType() {
        SpinnerAdapter adapterSoundType = spinnerSoundType.getAdapter();
        assertFalse("should not be empty", adapterSoundType.isEmpty());
        assertTrue("should contains 4 item", adapterSoundType.getCount() == 5);
        assertEquals("invalid item", activity.getString(R.string.sound_label_amplifier_general), adapterSoundType.getItem(0));
        assertEquals("invalid item", activity.getString(R.string.sound_label_amplifier_group), adapterSoundType.getItem(1));
        assertEquals("invalid item", activity.getString(R.string.sound_label_amplifier_point_to_point), adapterSoundType.getItem(2));
        assertEquals("invalid item", activity.getString(R.string.sound_label_source_general), adapterSoundType.getItem(3));
        assertEquals("invalid item", activity.getString(R.string.sound_label_source_point_to_point), adapterSoundType.getItem(4));
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithoutUuid() {
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());
        when(gatewayService.findAll()).thenReturn(Observable.<List<GatewayModel>>empty());

        createWithIntent(null);

        verify(mock(SoundServiceImpl.class), never()).findById(anyString());

        assertEquals("invalid value", "", editTextSoundName.getText().toString());
        assertEquals("invalid value", "", editTextSoundWhere.getText().toString());
        
        assertEquals("invalid value", false, checkBoxDeviceFavourite.isChecked());

        assertEquals("invalid value", -1, spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", -1, spinnerDeviceGateway.getSelectedItemPosition());
        assertEquals("invalid value", -1, spinnerSoundSource.getSelectedItemPosition());
        assertEquals("invalid value", 2, spinnerSoundType.getSelectedItemPosition());
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithUuid() {
        String SOUND_UUID = "myUuid";
        String SOUND_NAME = "myName";
        String SOUND_GATEWAY_SELECTED = "uuid2";
        String SOUND_WHERE = "08";
        SoundSystem.Source SOUND_SOURCE = SoundSystem.Source.STEREO_CHANNEL;
        SoundSystem.Type SOUND_TYPE = SoundSystem.Type.AMPLIFIER_P2P;
        Integer SOUND_ENVIRONMENT_SELECTED = 108;
        boolean SOUND_FAVOURITE = true;

        List<EnvironmentModel> environments = Arrays.
            asList(newEnvironment(100, "env1"), newEnvironment(SOUND_ENVIRONMENT_SELECTED, "env8"));

        List<GatewayModel> gateways = Arrays.
            asList(newGateway("uuid1", "1.1.1.1", 1), newGateway(SOUND_GATEWAY_SELECTED, "2.2.2.2", 2));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));
        when(gatewayService.findAll()).thenReturn(Observable.just(gateways));

        SoundModel soundModel = SoundModel.updateBuilder(SOUND_UUID)
            .name(SOUND_NAME)
            .where(SOUND_WHERE)
            .source(SOUND_SOURCE)
            .type(SOUND_TYPE)
            .environment(SOUND_ENVIRONMENT_SELECTED)
            .gateway(SOUND_GATEWAY_SELECTED)
            .favourite(SOUND_FAVOURITE)
            .build();

        when(soundService.findById(SOUND_UUID)).thenReturn(Observable.just(soundModel));

        createWithIntent(SOUND_UUID);

        assertEquals("invalid value", SOUND_NAME, editTextSoundName.getText().toString());

        assertEquals("invalid value", SOUND_FAVOURITE, checkBoxDeviceFavourite.isChecked());

        EnvironmentModel environmentSelected = environments.get(spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", environmentSelected.getId(), SOUND_ENVIRONMENT_SELECTED);

        GatewayModel gatewaySelected = gateways.get(spinnerDeviceGateway.getSelectedItemPosition());
        assertEquals("invalid value", SOUND_GATEWAY_SELECTED, gatewaySelected.getUuid());

        assertEquals("invalid value", SOUND_TYPE, activity.getSelectedSoundType());
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalid() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(gatewayService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        expectRequired(editTextSoundName);
        editTextSoundName.setText("nameValue");

        expectRequired(editTextSoundWhere);
        editTextSoundWhere.setText("21");

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
        verify(soundService, never()).add(any(SoundModel.class));
        verify(soundService, never()).update(any(SoundModel.class));
        assertEquals("bad validation", validationRequired, view.getError());
        //assertTrue("bad focus", view.hasFocus());
    }

    private SoundModel common_onMenuSave_valid(String uuidExtra) {
        String SOUND_NAME = "myName";
        String SOUND_GATEWAY_SELECTED = "uuid2";
        String SOUND_WHERE = "08";
        SoundSystem.Source SOUND_SOURCE = SoundSystem.Source.STEREO_CHANNEL;
        SoundSystem.Type SOUND_TYPE = SoundSystem.Type.AMPLIFIER_P2P;
        Integer SOUND_ENVIRONMENT_SELECTED = 101;
        boolean SOUND_FAVOURITE = true;

        when(environmentService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newEnvironment(SOUND_ENVIRONMENT_SELECTED, "env1"))));
        when(gatewayService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newGateway(SOUND_GATEWAY_SELECTED, "2.2.2.2", 2))));

        String SOUND_UUID = uuidExtra != null ? uuidExtra : "myNewUuid";
        when(soundService.add(any(SoundModel.class))).thenReturn(Observable.just(SOUND_UUID));
        when(soundService.update(any(SoundModel.class))).thenReturn(Observable.just(null));

        createWithIntent(uuidExtra);

        checkBoxDeviceFavourite.setChecked(SOUND_FAVOURITE);

        // for simplicity only 1 items
        spinnerDeviceEnvironment.setSelection(0);
        spinnerDeviceGateway.setSelection(0);
        spinnerSoundType.setSelection(2);

        editTextSoundName.setText(String.valueOf(SOUND_NAME));
        editTextSoundWhere.setText(String.valueOf(SOUND_WHERE));

        activity.onOptionsItemSelected(new RoboMenuItem(R.id.action_device_save));

        SoundModel soundMock = new SoundModel();
        soundMock.setUuid(uuidExtra);
        soundMock.setName(SOUND_NAME);
        soundMock.setWhere(SOUND_WHERE);
        soundMock.setSoundSystemSource(SOUND_SOURCE);
        soundMock.setSoundSystemType(SOUND_TYPE);
        soundMock.setEnvironmentId(SOUND_ENVIRONMENT_SELECTED);
        soundMock.setGatewayUuid(SOUND_GATEWAY_SELECTED);
        soundMock.setFavourite(SOUND_FAVOURITE);
        return soundMock;
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validAdd() {
        SoundModel soundMock = common_onMenuSave_valid(null);

        verify(soundService, times(1)).add(SoundModelMatcher.soundModelEq(soundMock));
        verify(soundService, never()).update(any(SoundModel.class));
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validEdit() {
        when(soundService.findById(anyString())).thenReturn(Observable.<SoundModel>empty());

        SoundModel soundMock = common_onMenuSave_valid("anyUuid");

        verify(soundService, never()).add(any(SoundModel.class));
        verify(soundService, times(1)).update(SoundModelMatcher.soundModelEq(soundMock));
    }

}
