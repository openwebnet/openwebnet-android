package com.github.openwebnet.view.activity;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.github.openwebnet.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        int id = item.getItemId();
        log.debug("onNavigationItemSelected {}", id);

        // TODO
        // save preference first time
        // first time create index menu
        // first time create home entry
        //public int getNextKey() {    realm.where(Child_pages.class).maximumInt("id_cp") + 1; }
        // .max("id").intValue()

        if (id == R.id.nav_home) {
            showDialogAddEnvironment();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSnackbar(String message) {
        Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    private void showDialogAddEnvironment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
            .setView(R.layout.dialog_environment)
            .setTitle(R.string.dialog_add_environment_title)
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                log.debug("dialog OK");
            })
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                log.debug("dialog CANCEL");
            });
        builder.show();
    }

}
