package com.github.openwebnet.model.firestore;

import android.text.TextUtils;

import com.google.common.collect.Lists;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ProfileInfoModel {

    private String ownerId;

    private List<String> allowedUserIds;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date modifiedAt;

    private ProfileSnapshot snapshot;

    public ProfileInfoModel() {}

    private ProfileInfoModel(Builder builder) {
        this.ownerId = builder.ownerId;
        this.allowedUserIds = builder.allowedUserIds;
        this.createdAt = builder.createdAt;
        this.modifiedAt = builder.modifiedAt;
        this.snapshot = new ProfileSnapshot();
    }

    public static class Builder {

        private String ownerId;
        private List<String> allowedUserIds;
        private String name;
        private Date createdAt;
        private Date modifiedAt;

        private Builder(UserProfileModel userProfile) {
            this.name = userProfile.getName();
            this.createdAt = userProfile.getCreatedAt();
            this.modifiedAt = userProfile.getModifiedAt();
        }

        public Builder userId(String userId) {
            this.ownerId = userId;
            this.allowedUserIds = Lists.newArrayList(userId);
            return this;
        }

        public ProfileInfoModel build() {
            checkArgument(!TextUtils.isEmpty(ownerId), "ownerId is empty");
            checkArgument(allowedUserIds.size() == 1, "missing userId");
            checkNotNull(name, "name is null");

            return new ProfileInfoModel(this);
        }
    }

    public static Builder builder(UserProfileModel userProfile) {
        return new ProfileInfoModel.Builder(userProfile);
    }

    public String getOwnerId() {
        return ownerId;
    }

    public List<String> getAllowedUserIds() {
        return allowedUserIds;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public ProfileSnapshot getSnapshot() {
        return snapshot;
    }

}
