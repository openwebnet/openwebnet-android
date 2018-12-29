package com.github.openwebnet.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.iabutil.DonationDialogFragment;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.FirebaseService;
import com.github.openwebnet.service.UtilityService;
import com.github.openwebnet.view.device.DeviceListFragment;
import com.github.openwebnet.view.profile.ProfileActivity;
import com.github.openwebnet.view.settings.SettingsFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.openwebnet.view.MainActivity.REQUEST_CODE_PROFILE;
import static com.github.openwebnet.view.device.DeviceListFragment.ARG_ENVIRONMENT;
import static com.google.common.base.Preconditions.checkArgument;

public class NavigationViewItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

    private static final Logger log = LoggerFactory.getLogger(NavigationViewItemSelectedListener.class);

    // @see menu/activity_main_drawer.xml
    public static final int MENU_FAVOURITE = 10;
    public static final int MENU_ENVIRONMENT_RANGE_MIN = 100;
    public static final int MENU_ENVIRONMENT_RANGE_MAX = 899;

    @Inject
    EnvironmentService environmentService;

    @Inject
    UtilityService utilityService;

    @Inject
    FirebaseService firebaseService;

    @BindView(R.id.floatingActionButtonMain)
    FloatingActionButton floatingActionButtonMain;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindString(R.string.app_name)
    String labelApplicationName;

    @BindString(R.string.activity_settings)
    String labelSettings;

    @BindString(R.string.validation_required)
    String labelValidationRequired;

    private final AppCompatActivity mActivity;

    public NavigationViewItemSelectedListener(AppCompatActivity activity) {
        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this, activity);

        this.mActivity = activity;

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        EventBus.getDefault().post(new MainActivity.OnChangeDrawerMenuEvent(id));
        log.debug("MENU selected [id={}]", id);

        switch (id) {
            case R.id.nav_favourite:
                checkArgument(item.getOrder() == MENU_FAVOURITE, "invalid favourite menu id");
                showFavourite();
                break;
            case R.id.nav_add:
                showDialogAddEnvironment();
                break;
            case R.id.nav_profile:
                showProfile();
                break;
            case R.id.nav_settings:
                showSettings();
                break;
            case R.id.nav_donation:
                DonationDialogFragment.show(mActivity);
                break;
            case R.id.nav_changelog:
                ChangeLogDialogFragment.show(mActivity);
                break;
            default:
                checkArgument(id >= MENU_ENVIRONMENT_RANGE_MIN
                    || id <= MENU_ENVIRONMENT_RANGE_MAX, "invalid environment menu id");

                mActivity.getSupportActionBar().setTitle(item.getTitle());
                floatingActionButtonMain.setVisibility(View.VISIBLE);
                showDeviceList(id);
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDeviceList(int id) {
        toggleScrollActionBar();
        removeFragment();

        Fragment fragment = mActivity.getSupportFragmentManager()
            .findFragmentById(R.id.content_frame);

        if (fragment == null || fragment.getArguments().getInt(ARG_ENVIRONMENT) != id) {
            fragment = new DeviceListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_ENVIRONMENT, id);
            fragment.setArguments(args);
        }

        mActivity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();
    }

    private void showFavourite() {
        mActivity.getSupportActionBar().setTitle(labelApplicationName);
        floatingActionButtonMain.setVisibility(View.INVISIBLE);
        showDeviceList(MENU_FAVOURITE);
    }

    private void showDialogAddEnvironment() {
        View layout = LayoutInflater.from(mActivity).inflate(R.layout.dialog_environment, null);

        AlertDialog dialog = new AlertDialog.Builder(mActivity)
            .setView(layout)
            .setTitle(R.string.dialog_add_environment_title)
            .setPositiveButton(R.string.button_add, null)
            .setNeutralButton(android.R.string.cancel, null)
            .create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(v -> {
                EditText name = layout.findViewById(R.id.editTextDialogEnvironmentName);
                if (utilityService.isBlankText(name)) {
                    name.setError(labelValidationRequired);
                } else {
                    addEnvironment(utilityService.sanitizedText(name));
                    dialog.dismiss();
                }
            });
    }

    private void addEnvironment(String name) {
        environmentService.add(name)
            .subscribe(id -> {
                // calls onPrepareOptionsMenu(): reload menu
                mActivity.invalidateOptionsMenu();
                mDrawerLayout.openDrawer(GravityCompat.START);
            });
    }

    private void showProfile() {
        if (firebaseService.isAuthenticated()) {
            log.debug("showProfile: valid profile");
            mActivity.startActivityForResult(
                new Intent(mActivity, ProfileActivity.class),
                REQUEST_CODE_PROFILE);
        } else {
            log.info("showProfile: init login");
            mActivity.startActivityForResult(
                firebaseService.signIn(),
                MainActivity.REQUEST_CODE_SIGN_IN);
        }
    }

    private void showSettings() {
        disableScrollActionBar();
        mActivity.getSupportActionBar().setTitle(labelSettings);
        floatingActionButtonMain.setVisibility(View.INVISIBLE);
        removeCompactFragment();

        // refactor with android.support.v4 when will be stable
        mActivity.getFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, new SettingsFragment())
            .commit();
    }

    private void removeFragment() {
        android.app.Fragment fragment = mActivity.getFragmentManager()
            .findFragmentById(R.id.content_frame);
        if (fragment != null) {
            mActivity.getFragmentManager()
                .beginTransaction()
                .remove(fragment).commit();
        }
    }

    private void removeCompactFragment() {
        Fragment compactFragment = mActivity.getSupportFragmentManager()
            .findFragmentById(R.id.content_frame);
        if (compactFragment != null) {
            mActivity.getSupportFragmentManager()
                .beginTransaction()
                .remove(compactFragment).commit();
        }
    }

    private void toggleScrollActionBar() {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
            | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
    }

    private void disableScrollActionBar() {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
    }

    /**
     *
     */
    public static class OnShowFavouriteEvent {

        public OnShowFavouriteEvent() {}
    }

    // fired by NavigationViewClickListener.reloadDrawer
    @Subscribe
    public void onEvent(OnShowFavouriteEvent event) {
        showFavourite();
    }

}
