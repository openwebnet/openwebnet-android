package com.github.openwebnet.view.profile;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.firestore.UserProfileModel;
import com.github.openwebnet.service.FirebaseService;
import com.github.openwebnet.service.UtilityService;
import com.github.openwebnet.view.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * - add swipe to refresh
 * - menu item for each card
 * - FAB create/reset/logout
 * >>> https://github.com/leinardi/FloatingActionButtonSpeedDial
 * https://github.com/Clans/FloatingActionButton
 * https://github.com/futuresimple/android-floating-action-button
 * https://github.com/makovkastar/FloatingActionButton
 * https://github.com/wangjiegulu/RapidFloatingActionButton
 */
public class ProfileActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(ProfileActivity.class);

    @BindView(R.id.recyclerViewProfile)
    RecyclerView mRecyclerView;

    @Inject
    FirebaseService firebaseService;

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
                createProfile();
                return true;
            case R.id.action_profile_reset:
                resetProfile();
                return true;
            case R.id.action_profile_logout:
                logoutProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    // TODO check internet connection
    // TODO toggle loader
    private void refreshProfiles() {
        firebaseService.getUserProfiles()
            // TODO message
            .doOnError(error -> showError(error, "refreshProfiles failed"))
            .subscribe(results -> {
                log.info("refreshProfiles: size={}", results.size());
                profileItems.clear();
                profileItems.addAll(results);
                mAdapter.notifyDataSetChanged();
            });
    }

    // TODO dialog to ask name
    private void createProfile() {
        firebaseService.updateUser()
            .flatMap(aVoid -> firebaseService.addProfile("TODO"))
            // TODO message
            .doOnError(error -> showError(error, "createProfile failed"))
            .subscribe(profileId -> {
                log.info("createProfile succeeded: profileId={}", profileId);
                refreshProfiles();
                showSnackbar("TODO profile create success");
            });
    }

    // TODO dialog
    private void resetProfile() {
        firebaseService.resetLocalProfile()
            // TODO message
            .doOnError(error -> showError(error, "resetProfile failed"))
            .subscribe(profileId -> {
                log.info("terminating ProfileActivity after reset");
                // TODO show snackbar success
                setResult(MainActivity.RESULT_CODE_PROFILE_RESET);
                finish();
            });
    }

    // TODO hide
    private void logoutProfile() {
        firebaseService.signOut(this, () -> {
            log.info("terminating ProfileActivity after logout");
            finish();
        });
    }

    private void showError(Throwable error, String message) {
        log.error("showError: {}", message, error);
        showSnackbar(message);
    }

    // TODO int stringId
    // TODO toast ???
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    /**
     *
     */
    public static class OnRefreshProfilesEvent {

        public OnRefreshProfilesEvent() {}
    }

    // fired by ProfileAdapter
    @Subscribe
    public void onEvent(OnRefreshProfilesEvent event) {
        refreshProfiles();
    }

    /**
     *
     */
    public static class OnShowSnackbarEvent {

        private final String message;

        public OnShowSnackbarEvent(String message) {
            this.message = message;
        }
    }

    // fired by ProfileAdapter
    @Subscribe
    public void onEvent(OnShowSnackbarEvent event) {
        showSnackbar(event.message);
    }

}
