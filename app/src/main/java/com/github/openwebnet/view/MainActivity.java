package com.github.openwebnet.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Stream;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.view.device.DeviceActivity;
import com.github.openwebnet.view.device.LightActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

import static com.github.openwebnet.view.NavigationViewItemSelectedListener.MENU_ENVIRONMENT_RANGE_MIN;
import static com.github.openwebnet.view.device.AbstractDeviceActivity.EXTRA_DEFAULT_ENVIRONMENT;
import static com.github.openwebnet.view.device.AbstractDeviceActivity.EXTRA_DEFAULT_GATEWAY;

public class MainActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.floatingActionsMenuMain)
    FloatingActionsMenu floatingActionsMenuMain;

    @BindString(R.string.error_load_navigation_drawer)
    String errorLoadNavigationDrawer;

    @Inject
    CommonService commonService;
    @Inject
    EnvironmentService environmentService;

    private int drawerMenuItemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        commonService.initApplication();
        initNavigationDrawer();
        // TODO pull to refresh
    }

    private void initNavigationDrawer() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationViewItemSelectedListener(this));

        // select favourite menu
        navigationView.setCheckedItem(R.id.nav_favourite);
        navigationView.getMenu().performIdentifierAction(R.id.nav_favourite, Menu.NONE);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
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

    @OnClick(R.id.floatingActionButtonAddDevice)
    public void onClickAddDevice(FloatingActionButton fab) {
        actionNewIntent(DeviceActivity.class);
    }

    @OnClick(R.id.floatingActionButtonAddLight)
    public void onClickAddLight(FloatingActionButton fab) {
        actionNewIntent(LightActivity.class);
    }

    private <T> void actionNewIntent(Class<T> clazz) {
        // TODO show background opaque layer
        floatingActionsMenuMain.collapse();

        Intent intentNew = new Intent(this, clazz)
            .putExtra(EXTRA_DEFAULT_ENVIRONMENT, drawerMenuItemSelected)
            .putExtra(EXTRA_DEFAULT_GATEWAY, commonService.getDefaultGateway());
        startActivity(intentNew);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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
}
