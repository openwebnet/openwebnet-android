package com.github.openwebnet.view.profile;

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

    private List<UserProfileModel> mProfiles;

    public ProfileAdapter(List<UserProfileModel> profiles) {
        Injector.getApplicationComponent().inject(this);

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

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder profileViewHolder, int i) {
        // TODO
        profileViewHolder.textViewProfileName.setText(mProfiles.get(i).getName());
        // TODO format mProfiles.get(i).getCreatedAt()
        profileViewHolder.textViewProfileDate.setText("TODO date");

        profileViewHolder.imageButtonProfileSwitch.setOnClickListener(v ->
            log.info("TODO onClick: imageButtonProfileSwitch")
        );
        profileViewHolder.imageButtonProfileShare.setOnClickListener(v ->
            log.info("TODO onClick: imageButtonProfileShare")
        );
        profileViewHolder.imageButtonProfileDelete.setOnClickListener(v ->
            log.info("TODO onClick: imageButtonProfileDelete")
        );
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }

}
