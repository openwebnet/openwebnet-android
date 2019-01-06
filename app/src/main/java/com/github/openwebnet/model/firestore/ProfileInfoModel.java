package com.github.openwebnet.model.firestore;

import android.os.Build;
import android.text.TextUtils;

import com.google.common.collect.Lists;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class ProfileInfoModel {

    private String ownerId;

    private List<String> userIds;

    private String name;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date modifiedAt;

    private String androidManufacturer;

    private String androidModel;

    private int androidVersionSdk;

    private String androidVersionRelease;

    public ProfileInfoModel() {}

    private ProfileInfoModel(Builder builder) {
        this.ownerId = builder.ownerId;
        this.userIds = builder.userIds;
        this.name = builder.name;
        this.createdAt = builder.createdAt;
        this.modifiedAt = builder.modifiedAt;
        this.androidManufacturer = builder.androidManufacturer;
        this.androidModel = builder.androidModel;
        this.androidVersionSdk = builder.androidVersionSdk;
        this.androidVersionRelease = builder.androidVersionRelease;
    }

    public static class Builder {

        private String ownerId;
        private List<String> userIds;
        private String name;
        private Date createdAt;
        private Date modifiedAt;
        private String androidManufacturer;
        private String androidModel;
        private int androidVersionSdk;
        private String androidVersionRelease;

        private Builder(UserProfileModel userProfile) {
            this.name = userProfile.getName();
            this.createdAt = userProfile.getCreatedAt();
            this.modifiedAt = userProfile.getModifiedAt();

            this.androidManufacturer = Build.MANUFACTURER;
            this.androidModel = Build.MODEL;
            this.androidVersionSdk = Build.VERSION.SDK_INT;
            this.androidVersionRelease = Build.VERSION.RELEASE;
        }

        public Builder userId(String userId) {
            this.ownerId = userId;
            this.userIds = Lists.newArrayList(userId);
            return this;
        }

        public ProfileInfoModel build() {
            checkArgument(!TextUtils.isEmpty(ownerId), "ownerId is empty");
            checkArgument(userIds.size() == 1, "missing userId");

            return new ProfileInfoModel(this);
        }
    }

    public static Builder builder(UserProfileModel userProfile) {
        return new ProfileInfoModel.Builder(userProfile);
    }

}
