package com.github.openwebnet.view.profile;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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

        @BindView(R.id.imageButtonProfileCardMenu)
        ImageButton imageButtonProfileCardMenu;

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

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder profileViewHolder, int i) {

        profileViewHolder.textViewProfileName.setText(
            mProfiles.get(i).getName());

        profileViewHolder.textViewProfileDate.setText(
            utilityService.formatDate(mProfiles.get(i).getCreatedAt()));

        profileViewHolder.imageButtonProfileCardMenu.setOnClickListener(v ->
            showProfileCardMenu(v, mProfiles.get(i)));
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }

    private void showProfileCardMenu(View view, UserProfileModel profile) {
        PopupMenu popupMenu = new PopupMenu(mActivity, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_profile_card, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            log.debug("PROFILE CARD MENU selected [id={}]", id);
            switch (id) {
                case R.id.action_profile_card_switch:
                    EventBus.getDefault().post(new ProfileActivity.OnShowConfirmationDialogEvent(
                        R.string.dialog_profile_switch_title,
                        R.string.dialog_profile_switch_message,
                        () -> switchProfile(profile)));
                    break;
                case R.id.action_profile_card_share: shareProfile(profile);
                    // TODO
                    break;
                case R.id.action_profile_card_delete:
                    EventBus.getDefault().post(new ProfileActivity.OnShowConfirmationDialogEvent(
                        R.string.dialog_profile_delete_title,
                        R.string.dialog_profile_delete_message,
                        () -> deleteProfile(profile)));
                    break;
            }
            return true;
        });
    }

    private void switchProfile(UserProfileModel profile) {
        EventBus.getDefault().post(new ProfileActivity.OnRequestActionEvent<>(
            () -> firebaseService.switchProfile(profile.getProfileRef()), aVoid -> {
            log.info("switchProfile succeeded: terminating");
            mActivity.setResult(MainActivity.RESULT_CODE_PROFILE_RESET);
            mActivity.finish();
        }));
    }

    private void shareProfile(UserProfileModel profile) {
        log.info("TODO onClick: imageButtonProfileShare");
    }

    private void deleteProfile(UserProfileModel profile) {
        EventBus.getDefault().post(new ProfileActivity.OnRequestActionEvent<>(
            () -> firebaseService.softDeleteProfile(profile.getProfileRef())
                .flatMap(profileId -> firebaseService.getUserProfiles()), profiles -> {
            log.info("deleteProfile succeeded: refreshing");
            EventBus.getDefault().post(new ProfileActivity.OnUpdateProfilesEvent(profiles));
        }));
    }

}
