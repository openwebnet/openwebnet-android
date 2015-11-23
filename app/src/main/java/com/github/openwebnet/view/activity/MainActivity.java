package com.github.openwebnet.view.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.R;
import com.github.openwebnet.model.DomoticEnvironment;
import com.github.openwebnet.service.DomoticService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

// TODO test
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
    DomoticService domoticService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenWebNetApplication.component(this).inject(this);
        ButterKnife.bind(this);

        domoticService.initRepository();
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
            new NavigationItemSelectedListener(this, drawerLayout));
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
        domoticService.findAllEnvironment().subscribe(
            environments -> {
                log.debug("reloadMenu: {}", environments);
                for (DomoticEnvironment environment : environments) {
                    menu.add(R.id.nav_group_environment, environment.getId(), Menu.NONE, environment.getName());
                }
            },
            throwable -> {
                showSnackbar(errorLoadNavigationDrawer);
            });
    }

    @OnClick(R.id.fab)
    public void floatingActionButtonClick(View view) {
        // TODO
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO ?
        Realm.getDefaultInstance().close();
    }
}
