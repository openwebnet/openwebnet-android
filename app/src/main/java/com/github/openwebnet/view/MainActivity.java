package com.github.openwebnet.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.annimon.stream.Stream;
import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.iabutil.IabUtil;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.FirebaseService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.view.profile.ProfileActivity;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.github.openwebnet.view.NavigationViewItemSelectedListener.MENU_ENVIRONMENT_RANGE_MIN;
import static com.github.openwebnet.view.device.AbstractDeviceActivity.EXTRA_DEFAULT_ENVIRONMENT;
import static com.github.openwebnet.view.device.AbstractDeviceActivity.EXTRA_DEFAULT_GATEWAY;

public class MainActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);
    static final String STATE_TITLE = "com.github.openwebnet.view.MainActivity.STATE_TITLE";
    static final String STATE_FAB_MENU = "com.github.openwebnet.view.MainActivity.STATE_FAB_MENU";
    static final int REQUEST_CODE_SIGN_IN = 101;
    static final int REQUEST_CODE_PROFILE = 102;
    public static final int RESULT_CODE_PROFILE_RESET = 1001;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.floatingActionButtonMain)
    FloatingActionButton floatingActionButtonMain;

    @BindString(R.string.error_load_navigation_drawer)
    String errorLoadNavigationDrawer;

    @BindString(R.string.error_authentication)
    String errorAuthentication;

    @BindString(R.string.app_link)
    String appLinkGitHub;

    @Inject
    CommonService commonService;

    @Inject
    PreferenceService preferenceService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    FirebaseService firebaseService;

    int drawerMenuItemSelected;

    View navHeaderMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Injector.getApplicationComponent().inject(this);
        } catch (NullPointerException npe) {
            // https://github.com/openwebnet/openwebnet-android/issues/76
            log.error("applicationComponent is null: force initialize");
            Injector.initializeApplicationComponent((OpenWebNetApplication) getApplication());
            Injector.getApplicationComponent().inject(this);
        }

        ButterKnife.bind(this);
        IabUtil.newInstance(this).init();

        commonService.initApplication(this);
        initNavigationDrawer(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_TITLE, getSupportActionBar().getTitle().toString());
        outState.putBoolean(STATE_FAB_MENU, floatingActionButtonMain.isShown());
    }

    private void initNavigationDrawer(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        // profile new badge
        ImageView navProfileImageView = (ImageView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_profile));
        navProfileImageView.setImageResource(R.drawable.new_box);

        // issue with @OnClick(R.id.imageViewAppLink)
        // inflate manually in activity_main > NavigationView:headerLayout
        // https://code.google.com/p/android/issues/detail?id=190226
        navHeaderMain = navigationView.inflateHeaderView(R.layout.nav_header_main);
        initDrawerHeader();

        navigationView.setNavigationItemSelectedListener(new NavigationViewItemSelectedListener(this));

        // select favourite menu
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_favourite);
            navigationView.getMenu().performIdentifierAction(R.id.nav_favourite, Menu.NONE);
        } else {
            getSupportActionBar().setTitle(savedInstanceState.getString(STATE_TITLE));
            floatingActionButtonMain.setVisibility(
                savedInstanceState.getBoolean(STATE_FAB_MENU) ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void initDrawerHeader() {
        ImageView headerImageView = navHeaderMain.findViewById(R.id.imageViewHeader);

        if (firebaseService.isAuthenticated()) {
            log.debug("initHeader: valid firebase session");
            // update profile image
            String photoUrl = firebaseService.getUserPhotoUrl();
            if (!TextUtils.isEmpty(photoUrl)) {
                Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.github_circle)
                    .into(headerImageView);
            }
            headerImageView.setOnClickListener(null);
        } else {
            log.debug("initHeader: invalid firebase session");
            initOnClickLinkToGitHub(headerImageView);
        }
    }

    private void initOnClickLinkToGitHub(ImageView headerImageView) {
        headerImageView.setImageResource(R.drawable.github_circle);
        headerImageView.setOnClickListener(v -> startActivity(
            new Intent(Intent.ACTION_VIEW, Uri.parse(appLinkGitHub))
                .addCategory(Intent.CATEGORY_BROWSABLE)));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        EventBus.getDefault().post(new MainActivity
            .OnChangePreferenceDeviceDebugEvent(preferenceService.isDeviceDebugEnabled()));
        reloadMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    private void reloadMenu() {
        Menu menu = navigationView.getMenu();
        menu.removeGroup(R.id.nav_group_environment);
        environmentService.findAll().subscribe(
            environments -> addEnvironmentMenu(menu, environments),
            throwable -> showSnackbar(errorLoadNavigationDrawer));
    }

    private void addEnvironmentMenu(Menu menuGroup, List<EnvironmentModel> environments) {
        log.debug("reloadMenu: {}", environments);
        final AtomicInteger menuOrder = new AtomicInteger(MENU_ENVIRONMENT_RANGE_MIN);

        Stream.of(environments)
            .forEach(environment -> {
                menuGroup.add(R.id.nav_group_environment, environment.getId(),
                    menuOrder.getAndIncrement(), environment.getName());

                // edit menu
                MenuItem menuItem = MenuItemCompat.setActionView(menuGroup.findItem(environment.getId()),
                    R.layout.drawer_menu_environment);
                menuItem.getActionView().findViewById(R.id.imageViewDrawerMenuEnvironmentEdit)
                    .setOnClickListener(new NavigationViewClickListener(this, environment.getId()));
            });
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.floatingActionButtonMain)
    public void onClickFabMain(FloatingActionButton fab) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_DEFAULT_ENVIRONMENT, drawerMenuItemSelected);
        bundle.putString(EXTRA_DEFAULT_GATEWAY, commonService.getDefaultGateway());

        MainBottomSheetDialogFragment bottomSheetDialogFragment = new MainBottomSheetDialogFragment();
        bottomSheetDialogFragment.setArguments(bundle);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), "mainBottomSheetDialog");
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // refresh after logout
        initDrawerHeader();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IabUtil.getInstance().destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!IabUtil.getInstance().handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }

        // https://firebaseopensource.com/projects/firebase/firebaseui-android/auth/readme.md
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            //IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK && firebaseService.isAuthenticated()) {
                log.debug("onActivityResult: firebase login succeeded");
                initDrawerHeader();
                startActivityForResult(
                    new Intent(getBaseContext(), ProfileActivity.class),
                    REQUEST_CODE_PROFILE);
            } else {
                log.error("onActivityResult: firebase login failed");
                showSnackbar(errorAuthentication);
            }
        }

        if (requestCode == REQUEST_CODE_PROFILE) {
            if (resultCode == RESULT_CODE_PROFILE_RESET) {
                EventBus.getDefault().post(new NavigationViewClickListener.OnReloadDrawerEvent());
            }
        }
    }

    /**
     *
     */
    public static class OnChangeDrawerMenuEvent {

        private final int menuItemId;

        public OnChangeDrawerMenuEvent(Integer menuItemId) {
            this.menuItemId = menuItemId;
        }

        public int getMenuItemId() {
            return menuItemId;
        }
    }

    // fired from NavigationViewItemSelectedListener.onNavigationItemSelected
    @Subscribe
    public void onEvent(OnChangeDrawerMenuEvent event) {
        log.debug("EVENT OnChangeDrawerMenuEvent: id={}", event.getMenuItemId());
        //navigationView.getMenu().findItem(event.getMenuItemId()).setChecked(true);
        drawerMenuItemSelected = event.getMenuItemId();
    }

    /**
     *
     */
    public static class OnChangeFabVisibilityEvent {

        private final boolean visible;

        public OnChangeFabVisibilityEvent(boolean visible) {
            this.visible = visible;
        }

        public boolean isVisible() {
            return visible;
        }
    }

    // fired from DeviceListFragment.showLoader
    @Subscribe
    public void onEvent(OnChangeFabVisibilityEvent event) {
        floatingActionButtonMain.setVisibility(event.isVisible() ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     *
     */
    public static class OnChangePreferenceDeviceDebugEvent {

        private final boolean debug;

        public OnChangePreferenceDeviceDebugEvent(boolean debug) {
            this.debug = debug;
        }

        public boolean isDebug() {
            return debug;
        }
    }

    // fired from SettingsFragment.initDebug
    @Subscribe
    public void onEvent(OnChangePreferenceDeviceDebugEvent event) {
        toolbar.getMenu().findItem(R.id.action_settings).setVisible(event.isDebug());
    }

}
