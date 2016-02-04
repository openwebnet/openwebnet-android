package com.github.openwebnet.view;

import android.support.v7.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
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
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertController;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowDialog;

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
import static org.junit.Assert.assertThat;
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
    @BindString(R.string.drawer_menu_changelog)
    String labelDrawerMenuChangelog;

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
    public void onNavigationItemSelected_shouldSelectAdd() {
        setupActivity();
        MenuItem item = clickMenuItem(R.id.nav_add);
        assertEquals("wrong title", labelDrawerMenuAdd, item.getTitle());

        ShadowAlertDialogSupport shadowAlertDialog = ShadowAlertDialogSupport.getShadowAlertDialog();

        assertThat(activity.getString(R.string.dialog_add_environment_title),
            equalTo(shadowAlertDialog.getTitle()));

        View inflatedView = shadowAlertDialog.getInflatedView();
        EditText name = (EditText) inflatedView.findViewById(R.id.editTextDialogEnvironmentName);

        shadowAlertDialog.performButtonClick(AlertDialog.BUTTON_POSITIVE);


    }

    @Test
    @Ignore
    public void onNavigationItemSelected_shouldSelectSettings() {
        setupActivity();
        //assertEquals("wrong title", "xxx", item.getTitle());
    }

    @Test
    @Ignore
    public void onNavigationItemSelected_shouldSelectChangelog() {
        setupActivity();

    }

    @Test
    @Ignore
    public void onNavigationItemSelected_shouldSelectDefault() {
        setupActivity();

    }

}
