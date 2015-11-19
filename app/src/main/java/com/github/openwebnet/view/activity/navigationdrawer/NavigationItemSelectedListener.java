package com.github.openwebnet.view.activity.navigationdrawer;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.openwebnet.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.Bind;

public class NavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

    private static final Logger log = LoggerFactory.getLogger(NavigationItemSelectedListener.class);

    private final Activity activity;
    private final DrawerLayout drawerLayout;

    public NavigationItemSelectedListener(Activity activity, DrawerLayout drawerLayout) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        log.debug("onNavigationItemSelected {}", id);

        if (id == R.id.nav_home) {
            showSnackbar("HOME");
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSnackbar(String message) {
        Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

}
