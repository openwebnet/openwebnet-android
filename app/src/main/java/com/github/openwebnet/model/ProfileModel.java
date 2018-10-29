package com.github.openwebnet.model;

import android.text.TextUtils;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.database.DatabaseRealmConfig;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ProfileModel {

    private String name;

    @ServerTimestamp
    private Date timestampCreated;

    @ServerTimestamp
    private Date timestampUpdated;

    private String userId;

    private List<String> sharedUserIds;

    private boolean softDelete;

    private int databaseVersion;

    private int appVersionCode;

    private String appVersionName;

    // required by Firestore
    public ProfileModel() {}

    private ProfileModel(Builder builder) {
        this.name = builder.name;
        this.userId = builder.userId;
        this.sharedUserIds = builder.sharedUserIds;
        this.timestampCreated = builder.timestampCreated;
        this.timestampUpdated = builder.timestampUpdated;
        this.softDelete = builder.softDelete;
        this.databaseVersion = builder.databaseVersion;
        this.appVersionCode = builder.appVersionCode;
        this.appVersionName = builder.appVersionName;
    }

    public static class Builder {

        private String name;
        private String userId;
        private List<String> sharedUserIds;
        private Date timestampCreated;
        private Date timestampUpdated;
        private boolean softDelete;
        private int databaseVersion;
        private int appVersionCode;
        private String appVersionName;

        public Builder() {
            this.sharedUserIds = new ArrayList<>();
            this.timestampCreated = new Date();
            this.timestampUpdated = new Date();
            this.softDelete = false;
            this.databaseVersion = DatabaseRealmConfig.DATABASE_VERSION;
            this.appVersionCode = BuildConfig.VERSION_CODE;
            this.appVersionName = BuildConfig.VERSION_NAME;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder shareUserId(String sharedUserId) {
            checkArgument(!TextUtils.isEmpty(sharedUserId), "sharedUserId is empty");
            this.sharedUserIds.add(sharedUserId);
            return this;
        }

        public Builder timestampCreated(Date timestampCreated) {
            this.timestampCreated = timestampCreated;
            return this;
        }

        public Builder timestampUpdated(Date timestampUpdated) {
            this.timestampUpdated = timestampUpdated;
            return this;
        }

        public Builder softDelete(boolean softDelete) {
            this.softDelete = softDelete;
            return this;
        }

        public Builder databaseVersion(int databaseVersion) {
            this.databaseVersion = databaseVersion;
            return this;
        }

        public Builder appVersionCode(int appVersionCode) {
            this.appVersionCode = appVersionCode;
            return this;
        }

        public Builder databaseVersion(String appVersionName) {
            this.appVersionName = appVersionName;
            return this;
        }

        public ProfileModel build() {
            checkArgument(!TextUtils.isEmpty(name), "name is empty");
            checkArgument(!TextUtils.isEmpty(userId), "userId is empty");

            checkNotNull(timestampCreated, "timestampCreated is null");
            checkNotNull(timestampUpdated, "timestampUpdated is null");

            checkArgument(databaseVersion > 0, "databaseVersion is invalid");
            checkArgument(appVersionCode > 0, "appVersionCode is invalid");
            checkArgument(!TextUtils.isEmpty(appVersionName), "appVersionName is empty");

            return new ProfileModel(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(Date timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public Date getTimestampUpdated() {
        return timestampUpdated;
    }

    public void setTimestampUpdated(Date timestampUpdated) {
        this.timestampUpdated = timestampUpdated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getSharedUserIds() {
        return sharedUserIds;
    }

    public void setSharedUserIds(List<String> sharedUserIds) {
        this.sharedUserIds = sharedUserIds;
    }

    public boolean isSoftDelete() {
        return softDelete;
    }

    public void setSoftDelete(boolean softDelete) {
        this.softDelete = softDelete;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

}
