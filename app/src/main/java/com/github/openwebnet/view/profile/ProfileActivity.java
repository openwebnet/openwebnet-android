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
import com.github.openwebnet.model.firestore.ProfileModel;
import com.github.openwebnet.service.FirebaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(ProfileActivity.class);

    @BindView(R.id.recyclerViewProfile)
    RecyclerView mRecyclerView;

    @Inject
    FirebaseService firebaseService;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ProfileAdapter(fakeProfiles());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile_create:
                log.info("onOptionsItemSelected: create");
                createProfile();
                return true;
            case R.id.action_profile_reset:
                log.info("onOptionsItemSelected: reset");
                return true;
            case R.id.action_profile_logout:
                log.info("onOptionsItemSelected: logout");
                firebaseService.signOut(this, () -> {
                    log.info("terminating ProfileActivity after logout");
                    finish();
                });
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

    private List<ProfileModel> fakeProfiles() {
        return Arrays.asList();
    }

    // TODO snackbar message
    // TODO dialog to ask name
    private void createProfile() {
        firebaseService.updateUser()
            .flatMap(aVoid -> firebaseService.addProfile("TODO"))
            .doOnError(error -> {
                log.error("createProfile failed", error);
                Snackbar.make(findViewById(android.R.id.content), "TODO error", Snackbar.LENGTH_LONG).show();
            })
            .subscribe(profileId -> log.info("createProfile succeeded: profileId={}", profileId));
    }
}
