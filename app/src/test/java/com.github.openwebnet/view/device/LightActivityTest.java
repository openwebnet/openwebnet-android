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
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.matcher.LightModelMatcher;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.LightModel;
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
import org.robolectric.fakes.RoboMenuItem;
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
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class LightActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Bind(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @Bind(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @Bind(R.id.checkBoxDeviceFavourite)
    CheckBox checkBoxDeviceFavourite;

    @Bind(R.id.editTextLightName)
    EditText editTextLightName;

    @Bind(R.id.editTextLightWhere)
    EditText editTextLightWhere;

    @Bind(R.id.checkBoxLightDimmer)
    CheckBox checkBoxLightDimmer;

    @BindString(R.string.validation_required)
    String validationRequired;

    @BindString(R.string.label_none)
    String labelNone;

    @Inject
    LightService lightService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    GatewayService gatewayService;

    private ActivityController<LightActivity> controller;
    private LightActivity activity;

    @Singleton
    @Component(modules = {LightActivityModuleTest.class, DatabaseModuleTest.class, RepositoryModuleTest.class})
    public interface LightActivityComponentTest extends ApplicationComponent {

        void inject(LightActivityTest activity);

    }

    @Module
    public class LightActivityModuleTest {

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
            return mock(LightServiceImpl.class);
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

    }

    @Before
    public void setupDagger() {
        LightActivityComponentTest applicationComponentTest = DaggerLightActivityTest_LightActivityComponentTest.builder()
            .lightActivityModuleTest(new LightActivityModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((LightActivityComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    private void createWithIntent(String uuidExtra) {
        controller = Robolectric.buildActivity(LightActivity.class);

        Intent intent = new Intent(RuntimeEnvironment.application, LightActivity.class);
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
    public void shouldVerifyOnCreate_initSpinner_noResult() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(gatewayService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        SpinnerAdapter adapterEnvironment = spinnerDeviceEnvironment.getAdapter();
        assertFalse("should not be empty", adapterEnvironment.isEmpty());
        assertEquals("should verify first element", labelNone, adapterEnvironment.getItem(0));

        SpinnerAdapter adapterGateway = spinnerDeviceGateway.getAdapter();
        assertFalse("should not be empty", adapterGateway.isEmpty());
        assertEquals("should verify first element", labelNone, adapterGateway.getItem(0));
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

        verify(mock(LightServiceImpl.class), never()).findById(anyString());

        assertEquals("invalid value", "", editTextLightName.getText().toString());
        assertEquals("invalid value", "", editTextLightWhere.getText().toString());

        assertEquals("invalid value", false, checkBoxLightDimmer.isChecked());
        assertEquals("invalid value", false, checkBoxDeviceFavourite.isChecked());

        assertEquals("invalid value", -1, spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", -1, spinnerDeviceGateway.getSelectedItemPosition());
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithUuid() {
        String LIGHT_UUID = "myUuid";
        String LIGHT_NAME = "myName";
        String LIGHT_GATEWAY_SELECTED = "uuid2";
        String LIGHT_WHERE = "08";
        Integer LIGHT_ENVIRONMENT_SELECTED = 108;
        boolean LIGHT_DIMMER = true;
        boolean LIGHT_FAVOURITE = true;

        List<EnvironmentModel> environments = Arrays.
            asList(newEnvironment(100, "env1"), newEnvironment(LIGHT_ENVIRONMENT_SELECTED, "env8"));

        List<GatewayModel> gateways = Arrays.
            asList(newGateway("uuid1", "1.1.1.1", 1), newGateway(LIGHT_GATEWAY_SELECTED, "2.2.2.2", 2));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));
        when(gatewayService.findAll()).thenReturn(Observable.just(gateways));

        LightModel lightModel = LightModel.updateBuilder(LIGHT_UUID)
            .name(LIGHT_NAME)
            .where(LIGHT_WHERE)
            .dimmer(LIGHT_DIMMER)
            .environment(LIGHT_ENVIRONMENT_SELECTED)
            .gateway(LIGHT_GATEWAY_SELECTED)
            .favourite(LIGHT_FAVOURITE)
            .build();

        when(lightService.findById(LIGHT_UUID)).thenReturn(Observable.just(lightModel));

        createWithIntent(LIGHT_UUID);

        assertEquals("invalid value", LIGHT_NAME, editTextLightName.getText().toString());
        assertEquals("invalid value", String.valueOf(LIGHT_WHERE), editTextLightWhere.getText().toString());

        assertEquals("invalid value", LIGHT_DIMMER, checkBoxLightDimmer.isChecked());
        assertEquals("invalid value", LIGHT_FAVOURITE, checkBoxDeviceFavourite.isChecked());

        EnvironmentModel environmentSelected = environments.get(spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", environmentSelected.getId(), LIGHT_ENVIRONMENT_SELECTED);

        GatewayModel gatewaySelected = gateways.get(spinnerDeviceGateway.getSelectedItemPosition());
        assertEquals("invalid value", LIGHT_GATEWAY_SELECTED, gatewaySelected.getUuid());
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalid() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        when(gatewayService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        expectRequired(editTextLightName);
        editTextLightName.setText("nameValue");

        expectRequired(editTextLightWhere);
        editTextLightWhere.setText("21");

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
        verify(lightService, never()).add(any(LightModel.class));
        verify(lightService, never()).update(any(LightModel.class));
        assertEquals("bad validation", validationRequired, view.getError());
        //assertTrue("bad focus", view.hasFocus());
    }

    private LightModel common_onMenuSave_valid(String uuidExtra) {
        String LIGHT_NAME = "myName";
        String LIGHT_GATEWAY_SELECTED = "uuid2";
        String LIGHT_WHERE = "08";
        Integer LIGHT_ENVIRONMENT_SELECTED = 101;
        boolean LIGHT_DIMMER = true;
        boolean LIGHT_FAVOURITE = true;

        when(environmentService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newEnvironment(LIGHT_ENVIRONMENT_SELECTED, "env1"))));
        when(gatewayService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newGateway(LIGHT_GATEWAY_SELECTED, "2.2.2.2", 2))));

        String LIGHT_UUID = uuidExtra != null ? uuidExtra : "myNewUuid";
        when(lightService.add(any(LightModel.class))).thenReturn(Observable.just(LIGHT_UUID));
        when(lightService.update(any(LightModel.class))).thenReturn(Observable.just(null));

        createWithIntent(uuidExtra);

        editTextLightName.setText(String.valueOf(LIGHT_NAME));
        editTextLightWhere.setText(String.valueOf(LIGHT_WHERE));

        checkBoxLightDimmer.setChecked(LIGHT_DIMMER);
        checkBoxDeviceFavourite.setChecked(LIGHT_FAVOURITE);

        // for simplicity only 1 items
        spinnerDeviceEnvironment.setSelection(0);
        spinnerDeviceGateway.setSelection(0);

        activity.onOptionsItemSelected(new RoboMenuItem(R.id.action_device_save));

        LightModel lightMock = new LightModel();
        lightMock.setUuid(uuidExtra);
        lightMock.setName(LIGHT_NAME);
        lightMock.setWhere(LIGHT_WHERE);
        lightMock.setDimmer(LIGHT_DIMMER);
        lightMock.setEnvironmentId(LIGHT_ENVIRONMENT_SELECTED);
        lightMock.setGatewayUuid(LIGHT_GATEWAY_SELECTED);
        lightMock.setFavourite(LIGHT_FAVOURITE);
        return lightMock;
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validAdd() {
        LightModel lightMock = common_onMenuSave_valid(null);

        verify(lightService, times(1)).add(LightModelMatcher.lightModelEq(lightMock));
        verify(lightService, never()).update(any(LightModel.class));
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validEdit() {
        when(lightService.findById(anyString())).thenReturn(Observable.<LightModel>empty());

        LightModel lightMock = common_onMenuSave_valid("anyUuid");

        verify(lightService, never()).add(any(LightModel.class));
        verify(lightService, times(1)).update(LightModelMatcher.lightModelEq(lightMock));
    }

    @After
    public void tearDown() {
        controller.pause().stop().destroy();
    }

}
