package com.github.openwebnet.view.profile;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.firestore.UserProfileModel;
import com.github.openwebnet.service.FirebaseService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.UtilityService;
import com.github.openwebnet.view.MainActivity;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentReference;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(ProfileActivity.class);
    private static final int MAX_PROFILE = 10;

    @BindView(R.id.recyclerViewProfile)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipeRefreshLayoutProfile)
    SwipeRefreshLayout swipeRefreshLayoutProfile;

    @BindView(R.id.speedDialProfile)
    SpeedDialView speedDialProfile;

    @BindString(R.string.validation_required)
    String labelValidationRequired;

    @Inject
    FirebaseService firebaseService;

    @Inject
    PreferenceService preferenceService;

    @Inject
    UtilityService utilityService;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UserProfileModel> profileItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ProfileAdapter(this, profileItems);
        mRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayoutProfile.setColorSchemeResources(R.color.primary, R.color.yellow_a400, R.color.accent);
        swipeRefreshLayoutProfile.setOnRefreshListener(this::refreshProfiles);

        initSpeedDial();
    }

    private void initSpeedDial() {
        // reset
        speedDialProfile.addActionItem(new SpeedDialActionItem.Builder(
                R.id.fab_profile_reset,
                R.drawable.delete
        ).setLabel(R.string.fab_profile_label_reset).create());

        // create
        speedDialProfile.addActionItem(new SpeedDialActionItem.Builder(
            R.id.fab_profile_create,
            R.drawable.account_plus
        ).setLabel(R.string.fab_profile_label_create).create());

        speedDialProfile.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.fab_profile_create:

                    int profileSize = mRecyclerView.getAdapter().getItemCount();
                    log.info("profileSize: {}", mRecyclerView.getAdapter().getItemCount());

                    // client side check
                    if (profileSize < MAX_PROFILE) {
                        showCreateDialog();
                    } else {
                        showSnackbar(R.string.error_profile_max);
                    }

                    speedDialProfile.close();
                    return true;
                case R.id.fab_profile_reset:

                    showConfirmationDialog(
                        R.string.dialog_profile_reset_title,
                        R.string.dialog_profile_reset_message,
                        this::resetProfile);

                    speedDialProfile.close();
                    return true;
                default:
                    break;
            }
            // keep open
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshProfiles();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile_logout:
                showConfirmationDialog(
                    R.string.dialog_profile_logout_title,
                    R.string.dialog_profile_logout_message,
                    this::logoutProfile);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    private void showConfirmationDialog(int titleStringId, int messageStringId, Action0 actionOk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setTitle(titleStringId)
            .setMessage(messageStringId)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(v -> {
                actionOk.call();
                dialog.dismiss();
            });
    }

    private void showEditDialog(int titleId, String name, Action1<String> editProfileAction) {
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_profile_edit, null);
        EditText editTextName = layout.findViewById(R.id.editTextDialogProfileEditName);
        editTextName.setText(name);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setView(layout)
            .setTitle(titleId)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(v -> {
                if (utilityService.isBlankText(editTextName)) {
                    editTextName.setError(labelValidationRequired);
                } else {
                    editProfileAction.call(utilityService.sanitizedText(editTextName));
                    dialog.dismiss();
                }
            });
    }

    private void showCreateDialog() {
        showEditDialog(
            R.string.dialog_profile_create_title,
            null,
            this::createProfile);
    }

    private void showRenameDialog(UserProfileModel profile) {
        showEditDialog(
            R.string.dialog_profile_rename_title,
            profile.getName(),
            name -> renameProfile(profile.getProfileRef(), name));
    }

    private void showShareDialog(DocumentReference profileRef) {
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_profile_share, null);
        EditText editTextEmailPrefix = layout.findViewById(R.id.editTextDialogProfileShareEmailPrefix);
        CheckBox checkBox = layout.findViewById(R.id.checkBoxDialogProfileShare);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setView(layout)
            .setTitle(R.string.dialog_profile_share_title)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setEnabled(false);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(v -> {
                if (utilityService.isBlankText(editTextEmailPrefix)) {
                    editTextEmailPrefix.setError(labelValidationRequired);
                } else {
                    shareProfile(profileRef, utilityService.sanitizedText(editTextEmailPrefix));
                    dialog.dismiss();
                }
            });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isChecked));
    }

    private void hideActions() {
        // hide everything
        mRecyclerView.setVisibility(View.INVISIBLE);
        speedDialProfile.hide();
        swipeRefreshLayoutProfile.setRefreshing(true);
    }

    private void updateProfiles(List<UserProfileModel> profiles) {
        profileItems.clear();
        profileItems.addAll(profiles);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayoutProfile.setRefreshing(false);
        speedDialProfile.show();
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private <T> void requestAction(Func0<Observable<T>> observableAction, Action1<T> onSuccess) {
        hideActions();

        if (utilityService.hasInternetAccess()) {
            observableAction.call()
                // better UX
                .delay(1 , TimeUnit.SECONDS)
                // max http timeout
                .timeout(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, error -> {
                    swipeRefreshLayoutProfile.setRefreshing(false);
                    log.error("requestAction: request failed", error);
                    showSnackbar(R.string.error_request);
                });
        } else {
            // show empty list
            updateProfiles(Lists.newArrayList());
            // hide
            speedDialProfile.hide();
            log.warn("requestAction: connection unavailable");
            showSnackbar(R.string.error_connection);
        }
    }

    private void refreshProfiles() {
        Func0<Observable<List<UserProfileModel>>> observableAction = () ->
            preferenceService.isFirstLogin() ?
                firebaseService.updateUser().flatMap(aVoid -> firebaseService.getUserProfiles()) :
                firebaseService.getUserProfiles();

        requestAction(observableAction, profiles -> {
            log.info("refreshProfiles: size={}", profiles.size());
            if (preferenceService.isFirstLogin()) {
                log.info("initFirstLogin");
                preferenceService.initFirstLogin();
            }
            updateProfiles(profiles);
        });
    }

    private void createProfile(String name) {
        requestAction(() -> firebaseService.addProfile(name)
                .flatMap(profileId -> firebaseService.getUserProfiles()), profiles -> {
            log.info("createProfile succeeded: refreshing");
            updateProfiles(profiles);
        });
    }

    private void renameProfile(DocumentReference profileRef, String name) {
        // TODO
    }

    private void resetProfile() {
        requestAction(() -> firebaseService.resetLocalProfile(), profileId -> {
            log.info("resetProfile succeeded: terminating");
            setResult(MainActivity.RESULT_CODE_PROFILE_RESET);
            finish();
        });
    }

    private void logoutProfile() {
        firebaseService.signOut(this, () -> {
            log.info("logoutProfile succeeded: terminating");
            finish();
        });
    }

    private void shareProfile(DocumentReference profileRef, String emailPrefix) {
        String email = String.format("%s%s", emailPrefix,
            utilityService.getString(R.string.dialog_profile_share_email_suffix));

        requestAction(() -> firebaseService.shareProfile(profileRef, email)
                .flatMap(profileId -> firebaseService.getUserProfiles()), profiles -> {
            log.info("shareProfile succeeded: refreshing");
            updateProfiles(profiles);
        });
    }


    private void showSnackbar(int stringId) {
        Snackbar.make(findViewById(android.R.id.content), utilityService.getString(stringId), Snackbar.LENGTH_LONG).show();
    }

    /**
     *
     */
    public static class OnUpdateProfilesEvent {

        private final List<UserProfileModel> profiles;

        public OnUpdateProfilesEvent(List<UserProfileModel> profiles) {
            this.profiles = profiles;
        }
    }

    // fired by ProfileAdapter
    @Subscribe
    public void onEvent(OnUpdateProfilesEvent event) {
        updateProfiles(event.profiles);
    }

    /**
     *
     */
    public static class OnShowConfirmationDialogEvent {

        private final int titleStringId;
        private final int messageStringId;
        private final Action0 actionOk;

        public OnShowConfirmationDialogEvent(int titleStringId, int messageStringId, Action0 actionOk) {
            this.titleStringId = titleStringId;
            this.messageStringId = messageStringId;
            this.actionOk = actionOk;
        }
    }

    // fired by ProfileAdapter
    @Subscribe
    public void onEvent(OnShowConfirmationDialogEvent event) {
        showConfirmationDialog(event.titleStringId, event.messageStringId, event.actionOk);
    }

    /**
     *
     */
    public static class OnShowEditDialogEvent {

        public enum Type {
            RENAME,
            SHARE
        }

        private final UserProfileModel profile;
        private final Type type;

        public OnShowEditDialogEvent(UserProfileModel profile, Type type) {
            this.profile = profile;
            this.type = type;
        }
    }

    // fired by ProfileAdapter
    @Subscribe
    public void onEvent(OnShowEditDialogEvent event) {
        switch (event.type) {
            case RENAME:
                showRenameDialog(event.profile);
                break;
            case SHARE:
                showShareDialog(event.profile.getProfileRef());
                break;
        }
    }

    /**
     *
     */
    public static class OnRequestActionEvent<T> {


        private final Func0<Observable<T>> observableAction;
        private final Action1<T> onSuccess;

        public OnRequestActionEvent(Func0<Observable<T>> observableAction, Action1<T> onSuccess) {
            this.observableAction = observableAction;
            this.onSuccess = onSuccess;
        }
    }

    // fired by ProfileAdapter
    @Subscribe
    public void onEvent(OnRequestActionEvent event) {
        requestAction(event.observableAction, event.onSuccess);
    }

}
