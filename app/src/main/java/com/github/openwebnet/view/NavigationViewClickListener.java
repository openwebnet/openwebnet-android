package com.github.openwebnet.view;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.view.device.DeviceListFragment;
import com.github.openwebnet.view.device.DeviceListFragment.UpdateDeviceListEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import rx.functions.Action1;

import static com.github.openwebnet.view.NavigationViewItemSelectedListener.MENU_FAVOURITE;

public class NavigationViewClickListener implements OnClickListener {

    private static final Logger log = LoggerFactory.getLogger(NavigationViewClickListener.class);

    @Inject
    EnvironmentService environmentService;

    @BindString(R.string.validation_required)
    String labelValidationRequired;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private final Activity mActivity;
    private final int environmentId;

    public NavigationViewClickListener(Activity activity, int environmentId) {
        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this, activity);

        mActivity = activity;
        this.environmentId = environmentId;
    }

    @Override
    public void onClick(View v) {
        log.debug("onClick: {}", environmentId);
        showDialogEditEnvironment();
    }

    private void showDialogEditEnvironment() {
        View layout = LayoutInflater.from(mActivity).inflate(R.layout.dialog_environment, null);
        EditText editTextName = (EditText) layout.findViewById(R.id.editTextDialogEnvironmentName);

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
            .setView(layout)
            .setTitle(R.string.dialog_edit_environment_title)
            .setPositiveButton(R.string.button_edit, null)
            .setNegativeButton(R.string.button_delete, null)
            .setNeutralButton(android.R.string.cancel, null);

        Action1<EnvironmentModel> showDialogEnvironment = environmentSelected -> {
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    if (TextUtils.isEmpty(editTextName.getText())) {
                        editTextName.setError(labelValidationRequired);
                    } else {
                        editEnvironment(editTextName.getText().toString());
                        dialog.dismiss();
                    }
                });
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener(v -> {
                    deleteEnvironment();
                    dialog.dismiss();
                });
        };

        environmentService.findById(environmentId)
            .subscribe(environment -> {
                EventBus.getDefault().post(new MainActivity.OnChangeDrawerMenuEvent(environmentId));
                mDrawerLayout.closeDrawer(GravityCompat.START);
                editTextName.setText(environment.getName());
                showDialogEnvironment.call(environment);
            });
    }

    private void editEnvironment(String name) {
        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(environmentId);
        environment.setName(name);

        environmentService.update(environment)
            .doOnCompleted(() -> {
                // calls onPrepareOptionsMenu(): reload menu
                mActivity.invalidateOptionsMenu();
                mDrawerLayout.openDrawer(GravityCompat.START);
            })
            .subscribe(aVoid -> {}, throwable -> log.error("editEnvironment", throwable));
    }

    private void deleteEnvironment() {
        environmentService.delete(environmentId)
            .doOnCompleted(() -> {
                // calls onPrepareOptionsMenu(): reload menu
                mActivity.invalidateOptionsMenu();
                mDrawerLayout.openDrawer(GravityCompat.START);
                // Switch to the only environment that is always there : the "favourite" environment.
                EventBus.getDefault().post(new UpdateDeviceListEvent(MENU_FAVOURITE));
            })
            .subscribe(aVoid -> {}, throwable -> log.error("deleteEnvironment", throwable));
    }
}
