package com.github.openwebnet.view;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.UtilityService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class NavigationViewClickListener implements OnClickListener {

    private static final Logger log = LoggerFactory.getLogger(NavigationViewClickListener.class);

    @Inject
    EnvironmentService environmentService;

    @Inject
    UtilityService utilityService;

    @BindString(R.string.validation_required)
    String labelValidationRequired;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private final AppCompatActivity mActivity;
    private final int environmentId;
    private boolean shouldUpdateTitle;

    public NavigationViewClickListener(AppCompatActivity activity, int environmentId) {
        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this, activity);

        mActivity = activity;
        this.environmentId = environmentId;

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
                    if (utilityService.isBlankText(editTextName)) {
                        editTextName.setError(labelValidationRequired);
                    } else {
                        editEnvironment(utilityService.sanitizedText(editTextName));
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
                // refresh title only if is already open
                shouldUpdateTitle =  mActivity.getSupportActionBar().getTitle().equals(environment.getName());
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
                if (shouldUpdateTitle) {
                    mActivity.getSupportActionBar().setTitle(name);
                }
            })
            .subscribe(aVoid -> {}, throwable -> log.error("editEnvironment", throwable));
    }

    private void deleteEnvironment() {
        environmentService.delete(environmentId)
            .doOnCompleted(this::reloadDrawer)
            .subscribe(aVoid -> {}, throwable -> log.error("deleteEnvironment", throwable));
    }

    private void reloadDrawer() {
        // calls onPrepareOptionsMenu(): reload menu
        mActivity.invalidateOptionsMenu();
        mDrawerLayout.openDrawer(GravityCompat.START);
        // switch to the only environment that is always there i.e. "favourite"
        EventBus.getDefault().post(new NavigationViewItemSelectedListener.OnShowFavouriteEvent());
    }

    /**
     *
     */
    public static class OnReloadDrawerEvent {

        public OnReloadDrawerEvent() {}
    }

    // fired from MainActivity.onActivityResult
    @Subscribe
    public void onEvent(OnReloadDrawerEvent event) {
        reloadDrawer();
    }

}
