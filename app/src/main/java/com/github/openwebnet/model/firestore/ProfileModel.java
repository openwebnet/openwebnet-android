package com.github.openwebnet.model.firestore;

import android.text.TextUtils;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.database.DatabaseRealmConfig;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ProfileModel {

    private String name;

    @ServerTimestamp
    private Date timestampCreated;

    @ServerTimestamp
    private Date timestampUpdated;

    private String userId;

    private int databaseVersion;

    private int appVersionCode;

    private String appVersionName;

    // required by Firestore
    public ProfileModel() {}

    private ProfileModel(Builder builder) {
        this.name = builder.name;
        this.userId = builder.userId;
        this.timestampCreated = builder.timestampCreated;
        this.timestampUpdated = builder.timestampUpdated;
        this.databaseVersion = builder.databaseVersion;
        this.appVersionCode = builder.appVersionCode;
        this.appVersionName = builder.appVersionName;
    }

    public static class Builder {

        private String name;
        private String userId;
        private Date timestampCreated;
        private Date timestampUpdated;
        private int databaseVersion;
        private int appVersionCode;
        private String appVersionName;

        public Builder() {
            this.timestampCreated = new Date();
            this.timestampUpdated = new Date();
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

        public Builder timestampCreated(Date timestampCreated) {
            this.timestampCreated = timestampCreated;
            return this;
        }

        public Builder timestampUpdated(Date timestampUpdated) {
            this.timestampUpdated = timestampUpdated;
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
