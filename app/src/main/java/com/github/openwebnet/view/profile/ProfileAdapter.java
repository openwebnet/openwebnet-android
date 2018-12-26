package com.github.openwebnet.view.profile;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.firestore.UserProfileModel;
import com.github.openwebnet.service.FirebaseService;
import com.github.openwebnet.service.UtilityService;
import com.github.openwebnet.view.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * https://developer.android.com/guide/topics/ui/layout/recyclerview
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private static final Logger log = LoggerFactory.getLogger(ProfileAdapter.class);

    @Inject
    FirebaseService firebaseService;

    @Inject
    UtilityService utilityService;

    private final Activity mActivity;
    private List<UserProfileModel> mProfiles;

    public ProfileAdapter(Activity activity, List<UserProfileModel> profiles) {
        Injector.getApplicationComponent().inject(this);

        mActivity = activity;
        mProfiles = profiles;
    }

    /**
     *
     */
    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewProfileName)
        TextView textViewProfileName;

        @BindView(R.id.textViewProfileDate)
        TextView textViewProfileDate;

        @BindView(R.id.imageButtonProfileSwitch)
        ImageButton imageButtonProfileSwitch;

        @BindView(R.id.imageButtonProfileShare)
        ImageButton imageButtonProfileShare;

        @BindView(R.id.imageButtonProfileDelete)
        ImageButton imageButtonProfileDelete;

        public ProfileViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProfileViewHolder(LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.profile_item, viewGroup, false));
    }

    // TODO
    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder profileViewHolder, int i) {

        profileViewHolder.textViewProfileName.setText(
            mProfiles.get(i).getName());

        profileViewHolder.textViewProfileDate.setText(
            utilityService.formatDate(mProfiles.get(i).getCreatedAt()));

        // TODO check restore of each element and if it correctly delete old one
        // TODO refresh navbar
        profileViewHolder.imageButtonProfileSwitch.setOnClickListener(v -> {
            // TODO show confirmation dialog
            firebaseService
                .switchProfile(mProfiles.get(i).getProfileRef())
                .doOnError(error -> {
                    // TODO stringId
                    String message = "TODO profile switch error";
                    log.error("imageButtonProfileSwitch#onClick: {}", message, error);
                    EventBus.getDefault().post(new ProfileActivity.OnShowSnackbarEvent(message));
                })
                .subscribe(aVoid -> {
                    mActivity.setResult(MainActivity.RESULT_CODE_PROFILE_RESET);
                    mActivity.finish();
                });
        });

        profileViewHolder.imageButtonProfileShare.setOnClickListener(v ->
            log.info("TODO onClick: imageButtonProfileShare")
        );

        profileViewHolder.imageButtonProfileDelete.setOnClickListener(v -> {
            // TODO show confirmation dialog
            firebaseService
                .softDeleteProfile(mProfiles.get(i).getProfileRef())
                .doOnError(error -> {
                    // TODO stringId
                    String message = "TODO profile delete error";
                    log.error("imageButtonProfileDelete#onClick: {}", message, error);
                    EventBus.getDefault().post(new ProfileActivity.OnShowSnackbarEvent(message));
                })
                .subscribe(aVoid -> {
                    // TODO stringId
                    String message = "TODO profile delete success";
                    log.info("imageButtonProfileDelete#onClick: {}", message);
                    EventBus.getDefault().post(new ProfileActivity.OnRefreshProfilesEvent());
                    EventBus.getDefault().post(new ProfileActivity.OnShowSnackbarEvent(message));
                });
        });
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }

}
