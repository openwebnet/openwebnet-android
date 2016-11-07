package com.github.openwebnet.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.GridView;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.view.device.AbstractDeviceActivity;
import com.github.openwebnet.view.device.AutomationActivity;
import com.github.openwebnet.view.device.DeviceActivity;
import com.github.openwebnet.view.device.EnergyActivity;
import com.github.openwebnet.view.device.IpcamActivity;
import com.github.openwebnet.view.device.LightActivity;
import com.github.openwebnet.view.device.ScenarioActivity;
import com.github.openwebnet.view.device.SoundActivity;
import com.github.openwebnet.view.device.TemperatureActivity;

import org.greenrobot.eventbus.EventBus;
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
import org.robolectric.annotation.Config;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static com.github.openwebnet.view.device.AbstractDeviceActivity.EXTRA_DEFAULT_ENVIRONMENT;
import static com.github.openwebnet.view.device.AbstractDeviceActivity.EXTRA_DEFAULT_GATEWAY;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class MainBottomSheetDialogFragmentTest {

    private static final String DEFAULT_GATEWAY = "myGateway";
    private static final int DEFAULT_ENVIRONMENT = 123;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    CommonService commonService;

    @Inject
    EnvironmentService environmentService;

    @BindView(R.id.floatingActionButtonMain)
    FloatingActionButton floatingActionButtonMain;

    MainActivity activity;
    MainBottomSheetDialogFragment mainBottomSheetDialog;

    @Before
    public void setup() {
        ApplicationComponentTest applicationComponentTest = DaggerApplicationComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModuleTest(new DomoticModuleTest())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ApplicationComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    private void setupFragment() {
        when(commonService.getDefaultGateway()).thenReturn(DEFAULT_GATEWAY);
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());

        activity = Robolectric.setupActivity(MainActivity.class);
        ButterKnife.bind(this, activity);

        EventBus.getDefault().post(new MainActivity.OnChangeDrawerMenuEvent(DEFAULT_ENVIRONMENT));

        // show bottom sheet dialog
        floatingActionButtonMain.performClick();

        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("mainBottomSheetDialog");
        assertNotNull("should not be null", fragment);
        assertTrue("invalid instance type", fragment instanceof MainBottomSheetDialogFragment);
        mainBottomSheetDialog = (MainBottomSheetDialogFragment) fragment;
    }

    private void expectStartActivity(int position, int title, Class<? extends AbstractDeviceActivity> newActivity) {
        GridView gridView = mainBottomSheetDialog.gridView;

        assertTrue("invalid instance type", gridView.getAdapter() instanceof BottomSheetDialogAdapter);
        BottomSheetDialogAdapter adapter = (BottomSheetDialogAdapter) gridView.getAdapter();
        MenuItem item = (MenuItem) adapter.getItem(position);
        assertEquals("invalid title", activity.getResources().getString(title),
            item.getTitle().toString().replace("\\n", " "));

        gridView.performItemClick(item.getActionView(), position, gridView.getAdapter().getItemId(position));

        Intent expectedIntent = new Intent(activity, newActivity)
            .putExtra(EXTRA_DEFAULT_ENVIRONMENT, DEFAULT_ENVIRONMENT)
            .putExtra(EXTRA_DEFAULT_GATEWAY, DEFAULT_GATEWAY);

        assertThat(shadowOf(activity).getNextStartedActivity(), equalTo(expectedIntent));
    }

    @Test
    public void onItemClick_shouldStartLightActivity() {
        setupFragment();
        expectStartActivity(0, R.string.activity_light, LightActivity.class);
    }

    @Test
    public void onItemClick_shouldStartAutomationActivity() {
        setupFragment();
        expectStartActivity(1, R.string.activity_automation, AutomationActivity.class);
    }

    @Test
    public void onItemClick_shouldStartTemperatureActivity() {
        setupFragment();
        expectStartActivity(2, R.string.activity_temperature, TemperatureActivity.class);
    }

    @Test
    public void onItemClick_shouldStartSoundActivity() {
        setupFragment();
        expectStartActivity(3, R.string.activity_sound, SoundActivity.class);
    }

    @Test
    public void onItemClick_shouldStartEnergyActivity() {
        setupFragment();
        expectStartActivity(4, R.string.activity_energy, EnergyActivity.class);
    }

    @Test
    public void onItemClick_shouldStartScenarioActivity() {
        setupFragment();
        expectStartActivity(5, R.string.activity_scenario, ScenarioActivity.class);
    }

    @Test
    public void onItemClick_shouldStartDeviceActivity() {
        setupFragment();
        expectStartActivity(6, R.string.activity_device, DeviceActivity.class);
    }

    @Test
    public void onItemClick_shouldStartIpcamActivity() {
        setupFragment();
        expectStartActivity(7, R.string.activity_ipcam, IpcamActivity.class);
    }

}
