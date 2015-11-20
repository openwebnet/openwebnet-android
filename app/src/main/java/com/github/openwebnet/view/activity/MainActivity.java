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

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
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

    @Inject
    DomoticService domoticService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenWebNetApplication.component(this).inject(this);
        ButterKnife.bind(this);

        domoticService.initRepository();
        initActionBar();
        initNavigationDrawer();
        // TODO pull to refresh
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationItemSelectedListener(this, drawerLayout));

        domoticService.findAllEnvironment()
            .subscribe(
                environments -> { addAllMenu(environments); },
                throwable -> { showSnackbar("Error init navigation drawer"); });
    }

    private void addAllMenu(List<DomoticEnvironment> environments) {
        log.debug("findAll: {}", environments);
        Menu menu = navigationView.getMenu();
        for (DomoticEnvironment environment: environments) {
            menu.add(R.id.nav_group_environment, environment.getId(), Menu.NONE, environment.getName());
        }
    }

    @OnClick(R.id.fab)
    public void floatingActionButtonClick(View view) {
        domoticService.addEnvironment("name_TODO", "description_TODO")
            .subscribe(
                id -> { showSnackbar("success: " + id); },
                throwable -> { showSnackbar("error ADD"); });
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
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
    protected void onDestroy() {
        super.onDestroy();
        // TODO ?
        Realm.getDefaultInstance().close();
    }
}
