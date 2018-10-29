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
    private Date createdAt;

    private String userId;

    private int databaseVersion;

    private int appVersionCode;

    private String appVersionName;

    // required by Firestore
    public ProfileModel() {}

    private ProfileModel(Builder builder) {
        this.name = builder.name;
        this.userId = builder.userId;
        this.createdAt = builder.createdAt;
        this.databaseVersion = builder.databaseVersion;
        this.appVersionCode = builder.appVersionCode;
        this.appVersionName = builder.appVersionName;
    }

    public static class Builder {

        private String name;
        private String userId;
        private Date createdAt;
        private int databaseVersion;
        private int appVersionCode;
        private String appVersionName;

        public Builder() {
            this.createdAt = new Date();
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

        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
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

            checkNotNull(createdAt, "createdAt is null");

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
