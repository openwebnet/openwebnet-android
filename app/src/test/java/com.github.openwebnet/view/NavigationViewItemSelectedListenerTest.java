package com.github.openwebnet.view;

import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.ShadowAlertDialogSupport;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.view.device.DeviceListFragment;
import com.github.openwebnet.view.settings.SettingsFragment;
import com.google.common.collect.Lists;

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

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.ButterKnife;
import rx.Observable;

import static com.github.openwebnet.view.NavigationViewItemSelectedListener.MENU_FAVOURITE;
import static com.github.openwebnet.view.device.DeviceListFragment.ARG_ENVIRONMENT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*", "com.getbase.*"})
@PrepareForTest({Injector.class})
public class NavigationViewItemSelectedListenerTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    EnvironmentService environmentService;

    @BindString(R.string.app_name)
    String labelApplicationName;
    @BindString(R.string.drawer_menu_favourite)
    String labelDrawerMenuFavourite;
    @BindString(R.string.drawer_menu_add)
    String labelDrawerMenuAdd;
    @BindString(R.string.drawer_menu_settings)
    String labelDrawerMenuSettings;

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
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());

        activity = Robolectric.setupActivity(MainActivity.class);
        ButterKnife.bind(this, activity);
    }

    private MenuItem clickMenuItem(int menuId) {
        activity.navigationView.getMenu().performIdentifierAction(menuId, Menu.NONE);
        return activity.navigationView.getMenu().findItem(menuId);
    }

    @Test
    public void onNavigationItemSelected_shouldSelectFavourite() {
        setupActivity();
        MenuItem item = clickMenuItem(R.id.nav_favourite);
        assertEquals("wrong title", labelDrawerMenuFavourite, item.getTitle());

        assertEquals("invalid menu order", MENU_FAVOURITE, item.getOrder());
        assertEquals("wrong title", labelApplicationName, activity.getSupportActionBar().getTitle());
        assertFalse("should be hidden", activity.floatingActionsMenuMain.isShown());

        Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.content_frame);
        assertNotNull("null fragment", currentFragment);
        assertThat("invalid fragment", currentFragment, instanceOf(DeviceListFragment.class));

        int argumentEnvironment = currentFragment.getArguments().getInt(ARG_ENVIRONMENT);
        assertEquals("invalid fragment", MENU_FAVOURITE, argumentEnvironment);

        assertFalse("should be collapsed", activity.floatingActionsMenuMain.isExpanded());
        assertFalse("should be close", activity.drawerLayout.isDrawerOpen(GravityCompat.START));
    }

    @Test
    public void onNavigationItemSelected_shouldSelectAdd_showDialog() {
        setupActivity();
        MenuItem item = clickMenuItem(R.id.nav_add);
        assertEquals("wrong title", labelDrawerMenuAdd, item.getTitle());

        ShadowAlertDialogSupport shadowAlertDialog = ShadowAlertDialogSupport.getShadowAlertDialog();

        assertThat(activity.getString(R.string.dialog_add_environment_title),
                equalTo(shadowAlertDialog.getTitle()));

        View inflatedView = shadowAlertDialog.getInflatedView();
        assertNotNull("null layout", inflatedView);
    }

    @Test
    public void onNavigationItemSelected_shouldSelectAdd_clickInvalid() {
        setupActivity();
        clickMenuItem(R.id.nav_add);

        ShadowAlertDialogSupport shadowAlertDialog = ShadowAlertDialogSupport.getShadowAlertDialog();
        View inflatedView = shadowAlertDialog.getInflatedView();

        EditText name = (EditText) inflatedView.findViewById(R.id.editTextDialogEnvironmentName);

        assertNull("no error", name.getError());
        shadowAlertDialog.performButtonClick(AlertDialog.BUTTON_POSITIVE);
        assertThat(name.getError(), equalTo(activity.getString(R.string.validation_required)));
    }

    @Test
    public void onNavigationItemSelected_shouldSelectAdd_clickValid() {
        String NEW_ENVIRONMENT = "newEnvironment";
        int NEW_ENVIRONMENT_ID = 108;
        when(environmentService.add(NEW_ENVIRONMENT)).thenReturn(Observable.just(NEW_ENVIRONMENT_ID));

        setupActivity();
        clickMenuItem(R.id.nav_add);

        ShadowAlertDialogSupport shadowAlertDialog = ShadowAlertDialogSupport.getShadowAlertDialog();
        View inflatedView = shadowAlertDialog.getInflatedView();

        EditText name = (EditText) inflatedView.findViewById(R.id.editTextDialogEnvironmentName);
        name.setText(NEW_ENVIRONMENT);
        shadowAlertDialog.performButtonClick(AlertDialog.BUTTON_POSITIVE);
        verify(environmentService).add(NEW_ENVIRONMENT);
    }

    @Test
    public void onNavigationItemSelected_shouldSelectSettings() {
        setupActivity();
        MenuItem item = clickMenuItem(R.id.nav_settings);

        assertEquals("wrong title", labelDrawerMenuSettings, item.getTitle());
        assertFalse("should be hidden", activity.floatingActionsMenuMain.isShown());

        android.app.Fragment currentFragment = activity.getFragmentManager().findFragmentById(R.id.content_frame);
        assertNotNull("null fragment", currentFragment);
        assertThat("invalid fragment", currentFragment, instanceOf(SettingsFragment.class));
    }

    @Test
    @Ignore
    public void onNavigationItemSelected_shouldSelectChangelog() {
        setupActivity();

    }

    private EnvironmentModel newEnvironmentModel(Integer id, String name) {
        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(id);
        environment.setName(name);
        return environment;
    }

    @Test
    public void onNavigationItemSelected_shouldSelectDefault() {
        int MENU_ENVIRONMENT_POSITION = 102;
        int MENU_ENVIRONMENT_ID = 803;
        String MENU_ENVIRONMENT_NAME = "C-environment";

        List<EnvironmentModel> environments = Lists.newArrayList(
            newEnvironmentModel(801, "A-environment"),
            newEnvironmentModel(802, "B-environment"),
            newEnvironmentModel(MENU_ENVIRONMENT_ID, MENU_ENVIRONMENT_NAME),
            newEnvironmentModel(804, "D-environment"),
            newEnvironmentModel(805, "E-environment"));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));

        activity = Robolectric.setupActivity(MainActivity.class);
        ButterKnife.bind(this, activity);

        MenuItem item = clickMenuItem(MENU_ENVIRONMENT_ID);
        assertEquals("invalid menu order", MENU_ENVIRONMENT_POSITION, item.getOrder());
        assertEquals("wrong title", MENU_ENVIRONMENT_NAME, activity.getSupportActionBar().getTitle());
        assertFalse("should be hidden", activity.floatingActionsMenuMain.isShown());

        Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.content_frame);
        assertNotNull("null fragment", currentFragment);
        assertThat("invalid fragment", currentFragment, instanceOf(DeviceListFragment.class));

        int argumentEnvironment = currentFragment.getArguments().getInt(ARG_ENVIRONMENT);
        assertEquals("invalid fragment", MENU_ENVIRONMENT_ID, argumentEnvironment);

        assertFalse("should be collapsed", activity.floatingActionsMenuMain.isExpanded());
        assertFalse("should be close", activity.drawerLayout.isDrawerOpen(GravityCompat.START));
    }

}
