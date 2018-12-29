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
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.firestore.UserProfileModel;
import com.github.openwebnet.service.FirebaseService;
import com.github.openwebnet.service.UtilityService;
import com.github.openwebnet.view.MainActivity;
import com.google.common.collect.Lists;

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

    @BindString(R.string.validation_required)
    String labelValidationRequired;

    @Inject
    FirebaseService firebaseService;

    @Inject
    UtilityService utilityService;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UserProfileModel> profileItems = new ArrayList<>();
    private boolean actionBarMenuVisibility = false;

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
            case R.id.action_profile_create:
                int profileSize = mRecyclerView.getAdapter().getItemCount();
                log.info("profileSize: {}", mRecyclerView.getAdapter().getItemCount());

                // client side check
                if (profileSize < MAX_PROFILE) {
                    showCreateDialog();
                } else {
                    showSnackbar(R.string.error_profile_max);
                }
                return true;
            case R.id.action_profile_reset:
                showConfirmationDialog(
                    R.string.dialog_profile_reset_title,
                    R.string.dialog_profile_reset_message,
                    this::resetProfile);
                return true;
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.action_profile_group, actionBarMenuVisibility);
        return super.onPrepareOptionsMenu(menu);
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

    private void showCreateDialog() {
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_profile_create, null);
        EditText editTextName = layout.findViewById(R.id.editTextDialogProfileCreateName);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setView(layout)
            .setTitle(R.string.dialog_profile_create_title)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(v -> {
                if (utilityService.isBlankText(editTextName)) {
                    editTextName.setError(labelValidationRequired);
                } else {
                    createProfile(utilityService.sanitizedText(editTextName));
                    dialog.dismiss();
                }
            });
    }

    private void toggleActionBarMenu(boolean visibility) {
        actionBarMenuVisibility = visibility;
        invalidateOptionsMenu();
    }

    private void hideActions() {
        // hide everything
        mRecyclerView.setVisibility(View.INVISIBLE);
        toggleActionBarMenu(false);
        swipeRefreshLayoutProfile.setRefreshing(true);
    }

    private void updateProfiles(List<UserProfileModel> profiles) {
        profileItems.clear();
        profileItems.addAll(profiles);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayoutProfile.setRefreshing(false);
        toggleActionBarMenu(true);
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
            toggleActionBarMenu(false);
            log.warn("requestAction: connection unavailable");
            showSnackbar(R.string.error_connection);
        }
    }

    private void refreshProfiles() {
        requestAction(() -> firebaseService.getUserProfiles(), profiles -> {
            log.info("refreshProfiles: size={}", profiles.size());
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
