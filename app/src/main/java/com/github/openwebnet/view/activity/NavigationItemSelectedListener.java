package com.github.openwebnet.view.activity;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.R;
import com.github.openwebnet.service.DomoticService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class NavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

    private static final Logger log = LoggerFactory.getLogger(NavigationItemSelectedListener.class);

    @Inject
    DomoticService domoticService;

    private final Activity activity;
    private final DrawerLayout drawerLayout;

    @Inject
    public NavigationItemSelectedListener(Activity activity, DrawerLayout drawerLayout) {
        OpenWebNetApplication.component(activity).inject(this);
        this.activity = activity;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        log.debug("onNavigationItemSelected {}", id);
        
        switch (id) {
            case R.id.nav_add:
                showDialogAddEnvironment();
                break;
            case R.id.nav_settings:
                log.debug("nav_settings");
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSnackbar(String message) {
        Snackbar.make(activity.findViewById(R.id.fab), message, Snackbar.LENGTH_LONG).show();
    }

    private void showDialogAddEnvironment() {
        View layout = LayoutInflater.from(activity).inflate(R.layout.dialog_environment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
            .setView(layout)
            .setTitle(R.string.dialog_add_environment_title)
            .setPositiveButton(R.string.dialog_add_environment_btn, null)
            .setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(v -> {
                EditText name = (EditText) layout.findViewById(R.id.editTextDialogEnvironmentName);
                if (TextUtils.isEmpty(name.getText())) {
                    name.setError(getString(R.string.validation_required));
                } else {
                    addEnvironment(name.getText().toString());
                    dialog.dismiss();
                }
            });
    }

    private void addEnvironment(String name) {
        domoticService.addEnvironment(name)
            .subscribe(id -> {
                // calls onPrepareOptionsMenu(): reload menu
                activity.invalidateOptionsMenu();
                drawerLayout.openDrawer(GravityCompat.START);
            },
            throwable -> {
                // TODO string resource
                showSnackbar(getString(R.string.error_add_environment));
            });
    }

    private String getString(int id) {
        return activity.getResources().getString(id);
    }

}
