package com.github.openwebnet.model.firestore;

import android.os.Build;
import android.text.TextUtils;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.database.DatabaseRealmConfig;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

public class ProfileDetailModel {

    private int profileVersion;

    private String userId;

    private String name;

    @ServerTimestamp
    private Date createdAt;

    private int databaseVersion;

    private int appVersionCode;

    private String appVersionName;

    private String androidManufacturer;

    private String androidModel;

    private int androidVersionSdk;

    private String androidVersionRelease;

    public ProfileDetailModel() {}

    private ProfileDetailModel(Builder builder) {
        this.profileVersion = builder.profileVersion;
        this.userId = builder.userId;
        this.name = builder.name;
        this.createdAt = builder.createdAt;
        this.databaseVersion = builder.databaseVersion;
        this.appVersionCode = builder.appVersionCode;
        this.appVersionName = builder.appVersionName;
        this.androidManufacturer = builder.androidManufacturer;
        this.androidModel = builder.androidModel;
        this.androidVersionSdk = builder.androidVersionSdk;
        this.androidVersionRelease = builder.androidVersionRelease;
    }

    public static class Builder {

        private int profileVersion;
        private String userId;
        private String name;
        private Date createdAt;
        private int databaseVersion;
        private int appVersionCode;
        private String appVersionName;
        private String androidManufacturer;
        private String androidModel;
        private int androidVersionSdk;
        private String androidVersionRelease;

        public Builder() {
            this.profileVersion = ProfileModel.PROFILE_VERSION;
            this.createdAt = new Date();
            this.databaseVersion = DatabaseRealmConfig.DATABASE_VERSION;
            this.appVersionCode = BuildConfig.VERSION_CODE;
            this.appVersionName = BuildConfig.VERSION_NAME;
            this.androidManufacturer = Build.MANUFACTURER;
            this.androidModel = Build.MODEL;
            this.androidVersionSdk = Build.VERSION.SDK_INT;
            this.androidVersionRelease = Build.VERSION.RELEASE;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public ProfileDetailModel build() {
            checkArgument(!TextUtils.isEmpty(userId), "userId is empty");
            checkArgument(!TextUtils.isEmpty(name), "name is empty");

            return new ProfileDetailModel(this);
        }
    }

    public int getProfileVersion() {
        return profileVersion;
    }

    public void setProfileVersion(int profileVersion) {
        this.profileVersion = profileVersion;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getAndroidManufacturer() {
        return androidManufacturer;
    }

    public void setAndroidManufacturer(String androidManufacturer) {
        this.androidManufacturer = androidManufacturer;
    }

    public String getAndroidModel() {
        return androidModel;
    }

    public void setAndroidModel(String androidModel) {
        this.androidModel = androidModel;
    }

    public int getAndroidVersionSdk() {
        return androidVersionSdk;
    }

    public void setAndroidVersionSdk(int androidVersionSdk) {
        this.androidVersionSdk = androidVersionSdk;
    }

    public String getAndroidVersionRelease() {
        return androidVersionRelease;
    }

    public void setAndroidVersionRelease(String androidVersionRelease) {
        this.androidVersionRelease = androidVersionRelease;
    }

}
