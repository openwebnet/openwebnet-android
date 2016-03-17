package com.github.openwebnet.view;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.view.device.AutomationActivity;
import com.github.openwebnet.view.device.DeviceActivity;
import com.github.openwebnet.view.device.LightActivity;
import com.google.common.collect.Lists;

import org.greenrobot.eventbus.EventBus;
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
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import rx.Observable;

import static com.github.openwebnet.view.device.AbstractDeviceActivity.EXTRA_DEFAULT_ENVIRONMENT;
import static com.github.openwebnet.view.device.AbstractDeviceActivity.EXTRA_DEFAULT_GATEWAY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*", "com.getbase.*"})
@PrepareForTest({Injector.class})
public class MainActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    CommonService commonService;
    @Inject
    EnvironmentService environmentService;

    @Bind(R.id.floatingActionButtonMain)
    FloatingActionButton floatingActionButtonMain;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @BindString(R.string.app_name)
    String labelAppName;

    MainActivity activity;

    @Before
    public void setup() {
        ApplicationComponentTest applicationComponentTest = DaggerApplicationComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .domoticModuleTest(new DomoticModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ApplicationComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    private void setupActivity() {
        // mock reloadMenu
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());

        activity = Robolectric.setupActivity(MainActivity.class);
        ButterKnife.bind(this, activity);
    }

    @Test
    public void shouldInitNavigationDrawerMenu() {
        List<EnvironmentModel> environments = Lists.newArrayList(
            newEnvironmentModel(801, "A-environment"),
            newEnvironmentModel(802, "B-environment"),
            newEnvironmentModel(803, "C-environment"),
            newEnvironmentModel(804, "D-environment"),
            newEnvironmentModel(805, "E-environment"));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));

        ButterKnife.bind(this, Robolectric.setupActivity(MainActivity.class));

        verify(commonService).initApplication();
        verify(environmentService).findAll();

        Menu menu = navigationView.getMenu();
        assertEquals("invalid menu title", "Favourites", menu.getItem(0).getTitle());
        assertEquals("invalid menu order", 10, menu.getItem(0).getOrder());

        // ordered by name (from repository)
        assertEquals("invalid menu title", "A-environment", menu.getItem(1).getTitle());
        assertEquals("invalid menu order", 100, menu.getItem(1).getOrder());
        assertEquals("invalid menu title", "B-environment", menu.getItem(2).getTitle());
        assertEquals("invalid menu order", 101, menu.getItem(2).getOrder());
        assertEquals("invalid menu title", "C-environment", menu.getItem(3).getTitle());
        assertEquals("invalid menu order", 102, menu.getItem(3).getOrder());
        assertEquals("invalid menu title", "D-environment", menu.getItem(4).getTitle());
        assertEquals("invalid menu order", 103, menu.getItem(4).getOrder());
        assertEquals("invalid menu title", "E-environment", menu.getItem(5).getTitle());
        assertEquals("invalid menu order", 104, menu.getItem(5).getOrder());

        assertEquals("invalid menu title", "Add environment", menu.getItem(6).getTitle());
        assertEquals("invalid menu order", 900, menu.getItem(6).getOrder());
        assertEquals("invalid menu title", "Settings", menu.getItem(7).getTitle());
        assertEquals("invalid menu order", 900, menu.getItem(7).getOrder());
    }

    private EnvironmentModel newEnvironmentModel(Integer id, String name) {
        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(id);
        environment.setName(name);
        return environment;
    }

    @Test
    public void shouldVerifyInstance_stateTitle() {
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());

        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller
            .create()
            .start()
            .resume()
            .visible()
            .get();
        ButterKnife.bind(this, activity);

        assertEquals("wrong title", labelAppName, activity.getSupportActionBar().getTitle());

        String CUSTOM_TITLE = "myNewTitle";
        activity.getSupportActionBar().setTitle(CUSTOM_TITLE);

        activity = controller
            .stop()
            .resume()
            .visible()
            .get();

        assertEquals("wrong title", CUSTOM_TITLE, activity.getSupportActionBar().getTitle());
    }

    @Test
    public void shouldVerifyInstance_stateFab() {
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());

        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        activity = controller
            .create()
            .start()
            .resume()
            .visible()
            .get();
        ButterKnife.bind(this, activity);

        activity.floatingActionButtonMain.setVisibility(View.VISIBLE);
        assertTrue("invalid state", activity.floatingActionButtonMain.isShown());

        activity = controller
            .stop()
            .resume()
            .visible()
            .get();

        assertTrue("invalid state", activity.floatingActionButtonMain.isShown());

        activity.floatingActionButtonMain.setVisibility(View.INVISIBLE);
        assertFalse("invalid state", activity.floatingActionButtonMain.isShown());

        activity = controller
            .stop()
            .resume()
            .visible()
            .get();

        assertFalse("invalid state", activity.floatingActionButtonMain.isShown());
    }

    @Ignore
    @Test
    public void clickingAddDevice_shouldStartDeviceActivity() {
        String DEFAULT_GATEWAY = "myGateway";
        int DEFAULT_ENVIRONMENT = 123;

        when(commonService.getDefaultGateway()).thenReturn(DEFAULT_GATEWAY);
        setupActivity();
        EventBus.getDefault().post(new MainActivity.OnChangeDrawerMenuEvent(DEFAULT_ENVIRONMENT));

        // TODO
        //floatingActionButtonAddDevice.performClick();
        Intent expectedIntent = new Intent(activity, DeviceActivity.class)
            .putExtra(EXTRA_DEFAULT_ENVIRONMENT, DEFAULT_ENVIRONMENT)
            .putExtra(EXTRA_DEFAULT_GATEWAY, DEFAULT_GATEWAY);

        assertThat(shadowOf(activity).getNextStartedActivity(), equalTo(expectedIntent));
    }

    @Ignore
    @Test
    public void clickingAddLight_shouldStartLightActivity() {
        String DEFAULT_GATEWAY = "myGateway";
        int DEFAULT_ENVIRONMENT = 123;

        when(commonService.getDefaultGateway()).thenReturn(DEFAULT_GATEWAY);
        setupActivity();
        EventBus.getDefault().post(new MainActivity.OnChangeDrawerMenuEvent(DEFAULT_ENVIRONMENT));

        // TODO
        //floatingActionButtonAddLight.performClick();
        Intent expectedIntent = new Intent(activity, LightActivity.class)
            .putExtra(EXTRA_DEFAULT_ENVIRONMENT, DEFAULT_ENVIRONMENT)
            .putExtra(EXTRA_DEFAULT_GATEWAY, DEFAULT_GATEWAY);

        assertThat(shadowOf(activity).getNextStartedActivity(), equalTo(expectedIntent));
    }

    @Ignore
    @Test
    public void clickingAddAutomation_shouldStartAutomationActivity() {
        String DEFAULT_GATEWAY = "myGateway";
        int DEFAULT_ENVIRONMENT = 123;

        when(commonService.getDefaultGateway()).thenReturn(DEFAULT_GATEWAY);
        setupActivity();
        EventBus.getDefault().post(new MainActivity.OnChangeDrawerMenuEvent(DEFAULT_ENVIRONMENT));

        // TODO
        //floatingActionButtonAddAutomation.performClick();
        Intent expectedIntent = new Intent(activity, AutomationActivity.class)
                .putExtra(EXTRA_DEFAULT_ENVIRONMENT, DEFAULT_ENVIRONMENT)
                .putExtra(EXTRA_DEFAULT_GATEWAY, DEFAULT_GATEWAY);

        assertThat(shadowOf(activity).getNextStartedActivity(), equalTo(expectedIntent));
    }

    @Test
    public void handleEvent_OnChangeDrawerMenuEvent() {
        int MENU_ID = 88;
        setupActivity();

        assertEquals("wrong menu selected", 0, activity.drawerMenuItemSelected);

        EventBus.getDefault().post(new MainActivity.OnChangeDrawerMenuEvent(MENU_ID));

        assertEquals("wrong menu selected", MENU_ID, activity.drawerMenuItemSelected);
    }

    @Test
    public void handleEvent_OnChangeFabVisibilityEvent() {
        setupActivity();

        EventBus.getDefault().post(new MainActivity.OnChangeFabVisibilityEvent(true));
        assertTrue("invalid state", activity.floatingActionButtonMain.isShown());

        EventBus.getDefault().post(new MainActivity.OnChangeFabVisibilityEvent(false));
        assertFalse("invalid state", activity.floatingActionButtonMain.isShown());
    }

    @Test
    public void handleEvent_OnChangePreferenceDeviceDebugEvent() {
        setupActivity();
        MenuItem menuDebug = activity.toolbar.getMenu().findItem(R.id.action_settings);

        assertFalse("invalid state", menuDebug.isVisible());

        EventBus.getDefault().post(new MainActivity.OnChangePreferenceDeviceDebugEvent(true));
        assertTrue("invalid state", menuDebug.isVisible());

        EventBus.getDefault().post(new MainActivity.OnChangePreferenceDeviceDebugEvent(false));
        assertFalse("invalid state", menuDebug.isVisible());
    }

}
