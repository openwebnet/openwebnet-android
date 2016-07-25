package com.github.openwebnet.view.device;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.matcher.IpcamModelMatcher;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.IpcamService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.PreferenceService;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
public class IpcamActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @BindView(R.id.editTextIpcamName)
    EditText editTextIpcamName;

    @BindView(R.id.editTextIpcamUrl)
    EditText editTextIpcamUrl;

    @BindView(R.id.editTextIpcamUsername)
    EditText editTextIpcamUsername;

    @BindView(R.id.editTextIpcamPassword)
    EditText editTextIpcamPassword;

    @BindView(R.id.textViewIpcamUrlHelp)
    TextView textViewIpcamUrlHelp;

    @BindView(R.id.switchIpcamAuthentication)
    Switch switchIpcamAuthentication;

    @BindView(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @BindView(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @BindView(R.id.spinnerIpcamStreamType)
    Spinner spinnerIpcamStreamType;

    @BindView(R.id.checkBoxDeviceFavourite)
    CheckBox checkBoxDeviceFavourite;

    @BindString(R.string.label_none)
    String labelNone;

    @BindString(R.string.validation_required)
    String validationRequired;

    @BindString(R.string.validation_url)
    String validationUrl;

    @Inject
    IpcamService ipcamService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    GatewayService gatewayService;

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
            return mock(GatewayServiceImpl.class);
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

        @Provides
        @Singleton
        TemperatureService provideTemperatureService() {
            return new TemperatureServiceImpl();
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

    private EnvironmentModel newEnvironment(Integer id, String name) {
        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(id);
        environment.setName(name);
        return environment;
    }

    @Test
    public void shouldVerifyOnCreate_hideSpinnerGateway() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));
        verify(gatewayService, never()).findAll();

        createWithIntent(null);

        assertEquals("should not be visible", View.INVISIBLE, spinnerDeviceGateway.getVisibility());
    }

    @Test
    public void shouldVerifyOnCreate_initSpinnerStreamType() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        SpinnerAdapter adapterStreamType = spinnerIpcamStreamType.getAdapter();
        assertFalse("should not be empty", adapterStreamType.isEmpty());
        assertEquals("should not be empty", adapterStreamType.getCount(), 1);
        assertEquals("should verify first element", "MJPEG", adapterStreamType.getItem(0));
        assertEquals("should select default", 0, spinnerDeviceEnvironment.getSelectedItemPosition());
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
        List<EnvironmentModel> environments = Arrays.
            asList(newEnvironment(100, "env1"), newEnvironment(101, "env2"));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));

        createWithIntent(null);

        SpinnerAdapter adapterEnvironment = spinnerDeviceEnvironment.getAdapter();
        assertFalse("should not be empty", adapterEnvironment.isEmpty());
        assertEquals("should verify first element", "env1", adapterEnvironment.getItem(0));
        assertEquals("should verify second element", "env2", adapterEnvironment.getItem(1));
        assertEquals("should select default", 0, spinnerDeviceEnvironment.getSelectedItemPosition());
    }

    @Test
    public void shouldVerifyOnCreate_initAuthentication() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        assertFalse("should not be checked", switchIpcamAuthentication.isChecked());
        assertAuthenticationIsDisabled();

        switchIpcamAuthentication.setChecked(true);
        // username
        assertTrue("should be enabled", editTextIpcamUsername.isEnabled());
        assertTrue("should be focusable", editTextIpcamUsername.isFocusableInTouchMode());
        assertEquals("invalid input type", InputType.TYPE_CLASS_TEXT, editTextIpcamUsername.getInputType());
        assertTrue("should be empty", TextUtils.isEmpty(editTextIpcamUsername.getText()));
        assertTrue("should have focus", editTextIpcamUsername.hasFocus());
        // password
        assertTrue("should be enabled", editTextIpcamPassword.isEnabled());
        assertTrue("should be focusable", editTextIpcamPassword.isFocusableInTouchMode());
        assertEquals("invalid input type", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, editTextIpcamPassword.getInputType());
        assertTrue("should be empty", TextUtils.isEmpty(editTextIpcamPassword.getText()));
        assertFalse("should have not focus", editTextIpcamPassword.hasFocus());

        editTextIpcamUsername.setText("myUsername");
        editTextIpcamPassword.setText("myPassword");
        switchIpcamAuthentication.setChecked(false);
        assertAuthenticationIsDisabled();
    }

    private void assertAuthenticationIsDisabled() {
        // username
        assertFalse("should not be enabled", editTextIpcamUsername.isEnabled());
        assertFalse("should not be focusable", editTextIpcamUsername.isFocusable());
        assertEquals("invalid input type", InputType.TYPE_NULL, editTextIpcamUsername.getInputType());
        assertTrue("should be empty", TextUtils.isEmpty(editTextIpcamUsername.getText()));
        // password
        assertFalse("should not be enabled", editTextIpcamPassword.isEnabled());
        assertFalse("should not be focusable", editTextIpcamPassword.isFocusable());
        assertEquals("invalid input type", InputType.TYPE_NULL, editTextIpcamPassword.getInputType());
        assertTrue("should be empty", TextUtils.isEmpty(editTextIpcamPassword.getText()));
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithoutUuid() {
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());

        createWithIntent(null);

        verify(mock(IpcamServiceImpl.class), never()).findById(anyString());

        assertTrue("should be empty", TextUtils.isEmpty(editTextIpcamName.getText()));
        assertTrue("should be empty", TextUtils.isEmpty(editTextIpcamUrl.getText()));

        assertFalse("should not be checked", switchIpcamAuthentication.isChecked());
        assertTrue("should be empty", TextUtils.isEmpty(editTextIpcamUsername.getText()));
        assertTrue("should be empty", TextUtils.isEmpty(editTextIpcamPassword.getText()));

        assertEquals("invalid value", -1, spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", 0, spinnerIpcamStreamType.getSelectedItemPosition());
        assertFalse("should not be checked", checkBoxDeviceFavourite.isChecked());
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithUuid_withoutAuthentication() {
        String IPCAM_UUID = "myUuid";
        String IPCAM_NAME = "myName";
        String IPCAM_URL = "myUrl";
        IpcamModel.StreamType IPCAM_STREAM_TYPE = IpcamModel.StreamType.MJPEG;
        Integer IPCAM_ENVIRONMENT_SELECTED = 108;
        boolean IPCAM_FAVOURITE = true;

        List<EnvironmentModel> environments = Arrays.
            asList(newEnvironment(100, "env1"), newEnvironment(IPCAM_ENVIRONMENT_SELECTED, "env8"));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));

        IpcamModel ipcamModel = IpcamModel.updateBuilder(IPCAM_UUID)
            .name(IPCAM_NAME)
            .url(IPCAM_URL)
            .streamType(IPCAM_STREAM_TYPE)
            .environment(IPCAM_ENVIRONMENT_SELECTED)
            .favourite(IPCAM_FAVOURITE)
            .build();

        when(ipcamService.findById(IPCAM_UUID)).thenReturn(Observable.just(ipcamModel));

        createWithIntent(IPCAM_UUID);

        assertEquals("invalid value", IPCAM_NAME, editTextIpcamName.getText().toString());
        assertEquals("invalid value", IPCAM_URL, editTextIpcamUrl.getText().toString());

        assertFalse("should not be checked", switchIpcamAuthentication.isChecked());

        SpinnerAdapter adapterStreamType = spinnerIpcamStreamType.getAdapter();
        assertFalse("should not be empty", adapterStreamType.isEmpty());
        assertEquals("should not be empty", adapterStreamType.getCount(), 1);
        assertEquals("should verify first element", IPCAM_STREAM_TYPE.name(), adapterStreamType.getItem(0));

        EnvironmentModel environmentSelected = environments.get(spinnerDeviceEnvironment.getSelectedItemPosition());
        assertEquals("invalid value", environmentSelected.getId(), IPCAM_ENVIRONMENT_SELECTED);

        assertEquals("invalid value", IPCAM_FAVOURITE, checkBoxDeviceFavourite.isChecked());
    }

    @Test
    public void shouldVerifyOnCreate_initEditWithUuid_withAuthentication() {
        when(environmentService.findAll()).thenReturn(Observable.just(Arrays.asList(newEnvironment(100, "env1"))));

        String IPCAM_UUID = "myUuidEdit";
        String IPCAM_USERNAME = "myUsername";
        String IPCAM_PASSWORD = "myPassword";

        IpcamModel ipcamModel = IpcamModel.updateBuilder(IPCAM_UUID)
            .name("myName")
            .url("myUrl")
            .username(IPCAM_USERNAME)
            .password(IPCAM_PASSWORD)
            .streamType(IpcamModel.StreamType.MJPEG)
            .environment(100)
            .build();

        when(ipcamService.findById(IPCAM_UUID)).thenReturn(Observable.just(ipcamModel));

        createWithIntent(IPCAM_UUID);

        assertTrue("should be checked", switchIpcamAuthentication.isChecked());
        assertEquals("invalid value", IPCAM_USERNAME, editTextIpcamUsername.getText().toString());
        assertEquals("invalid value", IPCAM_PASSWORD, editTextIpcamPassword.getText().toString());
    }

    private void expectViewError(TextView view, String error) {
        activity.onOptionsItemSelected(new RoboMenuItem(R.id.action_device_save));
        verify(ipcamService, never()).add(any(IpcamModel.class));
        verify(ipcamService, never()).update(any(IpcamModel.class));
        assertEquals("should be required", error, view.getError());
        // fix error:
        // The specified child already has a parent. You must call removeView() on the child's parent first.
        if (view.getParent() != null && view instanceof EditText) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalid() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        expectViewError(editTextIpcamName, validationRequired);
        editTextIpcamName.setText("ipcamName");

        expectViewError(editTextIpcamUrl, validationRequired);
        editTextIpcamUrl.setText("invalidUrl");
        expectViewError(editTextIpcamUrl, validationUrl);
        editTextIpcamUrl.setText("http://XXX");

        expectViewError((TextView) spinnerDeviceEnvironment.getSelectedView(), validationRequired);
        ArrayAdapter<String> adapterEnvironment = new ArrayAdapter<>(activity,
            android.R.layout.simple_spinner_dropdown_item, Arrays.asList("ENVIRONMENT"));
        spinnerDeviceEnvironment.setAdapter(adapterEnvironment);

        // now is valid
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_invalidAuthentication() {
        when(environmentService.findAll()).thenReturn(Observable.just(new ArrayList<>()));

        createWithIntent(null);

        editTextIpcamName.setText("ipcamName");
        editTextIpcamUrl.setText("http://XXX");
        switchIpcamAuthentication.setChecked(true);

        expectViewError(editTextIpcamUsername, validationRequired);
        editTextIpcamUsername.setText("ipcamUsername");

        expectViewError(editTextIpcamPassword, validationRequired);
        editTextIpcamPassword.setText("ipcamPassword");

        // now is valid
    }

    private IpcamModel common_onMenuSave_valid(String uuidExtra) {
        String IPCAM_NAME = "myName";
        String IPCAM_URL = "http://myUrl";
        String IPCAM_USERNAME = "myUsername";
        String IPCAM_PASSWORD = "myPassword";
        Integer IPCAM_ENVIRONMENT_SELECTED = 108;
        boolean IPCAM_FAVOURITE = true;

        when(environmentService.findAll()).thenReturn(Observable.
            just(Arrays.asList(newEnvironment(IPCAM_ENVIRONMENT_SELECTED, "env1"))));

        String IPCAM_UUID = uuidExtra != null ? uuidExtra : "myNewUuid";
        when(ipcamService.add(any(IpcamModel.class))).thenReturn(Observable.just(IPCAM_UUID));
        when(ipcamService.update(any(IpcamModel.class))).thenReturn(Observable.just(null));

        createWithIntent(uuidExtra);

        editTextIpcamName.setText(IPCAM_NAME);
        editTextIpcamUrl.setText(IPCAM_URL);
        switchIpcamAuthentication.setChecked(true);
        editTextIpcamUsername.setText(IPCAM_USERNAME);
        editTextIpcamPassword.setText(IPCAM_PASSWORD);
        spinnerIpcamStreamType.setSelection(0);
        spinnerDeviceEnvironment.setSelection(0);
        checkBoxDeviceFavourite.setChecked(IPCAM_FAVOURITE);

        activity.onOptionsItemSelected(new RoboMenuItem(R.id.action_device_save));

        IpcamModel ipcamModel = IpcamModel.updateBuilder(IPCAM_UUID)
            .name(IPCAM_NAME)
            .url(IPCAM_URL)
            .username(IPCAM_USERNAME)
            .password(IPCAM_PASSWORD)
            .streamType(IpcamModel.StreamType.MJPEG)
            .environment(IPCAM_ENVIRONMENT_SELECTED)
            .favourite(IPCAM_FAVOURITE)
            .build();
        return ipcamModel;
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validAdd() {
        IpcamModel ipcamMock = common_onMenuSave_valid(null);

        verify(ipcamService, times(1)).add(IpcamModelMatcher.ipcamModelEq(ipcamMock));
        verify(ipcamService, never()).update(any(IpcamModel.class));
    }

    @Test
    public void shouldVerifyOnCreate_onMenuSave_validEdit() {
        when(ipcamService.findById(anyString())).thenReturn(Observable.<IpcamModel>empty());

        IpcamModel ipcamMock = common_onMenuSave_valid("anyUuid");

        verify(ipcamService, never()).add(any(IpcamModel.class));
        verify(ipcamService, times(1)).update(IpcamModelMatcher.ipcamModelEq(ipcamMock));
    }
}
