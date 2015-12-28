package com.github.openwebnet.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.annimon.stream.Stream;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.view.device.DeviceActivity;
import com.github.openwebnet.view.device.LightActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(MainActivity.class);

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @BindString(R.string.error_load_navigation_drawer)
    String errorLoadNavigationDrawer;

    @Inject
    CommonService commonService;
    @Inject
    EnvironmentService environmentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        commonService.initRepository();
        initNavigationDrawer();
        // TODO pull to refresh
    }

    private void initNavigationDrawer() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(
            new NavigationItemListener(this, drawerLayout));
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
        environmentService.findAllEnvironment().subscribe(
            environments -> {
                log.debug("reloadMenu: {}", environments);
                // TODO orderBy name
                Stream.of(environments).forEach(environment -> {
                    menu.add(R.id.nav_group_environment, environment.getId(),
                        environment.getId(), environment.getName());
                });
            },
            throwable -> {
                showSnackbar(errorLoadNavigationDrawer);
            });
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.floatingActionButtonAddDevice)
    public void onClickAddDevice(FloatingActionButton fab) {
        startActivity(new Intent(this, DeviceActivity.class));
    }

    @OnClick(R.id.floatingActionButtonAddLight)
    public void onClickAddLight(FloatingActionButton fab) {
        startActivity(new Intent(this, LightActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO ?
        Realm.getDefaultInstance().close();
    }
}
