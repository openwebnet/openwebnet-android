package com.github.openwebnet.view.profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.ProfileModel;
import com.github.openwebnet.service.FirebaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.ZonedDateTime;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(ProfileActivity.class);

    public static final String EXTRA_PROFILE_EMAIL = "com.github.openwebnet.view.profile.EXTRA_PROFILE_EMAIL";
    public static final String EXTRA_PROFILE_NAME = "com.github.openwebnet.view.profile.EXTRA_PROFILE_NAME";

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

        String email = getIntent().getStringExtra(EXTRA_PROFILE_EMAIL);
        String name = getIntent().getStringExtra(EXTRA_PROFILE_NAME);
        log.info("ProfileActivity: email={} name={}", email, name);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ProfileAdapter(fakeProfiles());
        mRecyclerView.setAdapter(mAdapter);

        // TODO
        // https://firebaseopensource.com/projects/firebase/firebaseui-android/firestore/readme.md
        // { info: id(uuid), database-version, backup-name, date, share [user-id] | schema: light, automation,  ... }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile_create:
                log.info("onOptionsItemSelected: create");
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
        return Arrays.asList(
            new ProfileModel.Builder().name("Profile1").dateTime(ZonedDateTime.now()).build(),
            new ProfileModel.Builder().name("Profile2").dateTime(ZonedDateTime.now()).build(),
            new ProfileModel.Builder().name("Profile3").dateTime(ZonedDateTime.now()).build()
        );
    }

}
