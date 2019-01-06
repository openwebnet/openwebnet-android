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
public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_TYPE = 100;

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

    /**
     *
     */
    static class EmptyViewHolder extends RecyclerView.ViewHolder {

        static final int VIEW_TYPE = -1;

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mProfiles.isEmpty()) {
            return EmptyViewHolder.VIEW_TYPE;
        } else {
            return ProfileViewHolder.VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ProfileViewHolder.VIEW_TYPE:
                return new ProfileViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_item, parent, false));
            case EmptyViewHolder.VIEW_TYPE:
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_empty, parent, false));
            default:
                throw new IllegalStateException("invalid view type");
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case ProfileViewHolder.VIEW_TYPE:
                initProfileViewHolder((ProfileViewHolder) viewHolder, position);
                break;
            case EmptyViewHolder.VIEW_TYPE:
                break;
            default:
                throw new IllegalStateException("invalid view type");
        }
    }

    // TODO disable apply if appVersion is higher than current version
    private void initProfileViewHolder(ProfileViewHolder profileViewHolder, int position) {
        profileViewHolder.textViewProfileName.setText(
            mProfiles.get(position).getName());

        profileViewHolder.textViewProfileDate.setText(
            utilityService.formatDate(mProfiles.get(position).getCreatedAt()));

        profileViewHolder.imageButtonProfileCardMenu.setOnClickListener(v ->
            showProfileCardMenu(v, mProfiles.get(position)));
    }

    @Override
    public int getItemCount() {
        // to show empty list
        return mProfiles.size() > 0 ? mProfiles.size() : 1;
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
                case R.id.action_profile_card_share:
                    EventBus.getDefault().post(new ProfileActivity.OnShowShareDialogEvent(profile.getProfileRef()));
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

    private void deleteProfile(UserProfileModel profile) {
        EventBus.getDefault().post(new ProfileActivity.OnRequestActionEvent<>(
            () -> firebaseService.deleteUserProfile(profile.getProfileRef())
                .flatMap(profileId -> firebaseService.getUserProfiles()), profiles -> {
            log.info("deleteProfile succeeded: refreshing");
            EventBus.getDefault().post(new ProfileActivity.OnUpdateProfilesEvent(profiles));
        }));
    }

}
