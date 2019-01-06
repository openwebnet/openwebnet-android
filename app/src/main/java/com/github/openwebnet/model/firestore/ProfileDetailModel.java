package com.github.openwebnet.model.firestore;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.database.DatabaseRealmConfig;

public class ProfileDetailModel {

    private int appVersionCode;

    private String appVersionName;

    private int databaseFirestoreVersion;

    private int databaseRealmVersion;

    public ProfileDetailModel() {}

    private ProfileDetailModel(Builder builder) {
        this.appVersionCode = builder.appVersionCode;
        this.appVersionName = builder.appVersionName;
        this.databaseFirestoreVersion = builder.databaseFirestoreVersion;
        this.databaseRealmVersion = builder.databaseRealmVersion;
    }

    private static class Builder {

        private int appVersionCode;
        private String appVersionName;
        private int databaseFirestoreVersion;
        private int databaseRealmVersion;

        private Builder() {
            this.appVersionCode = BuildConfig.VERSION_CODE;
            this.appVersionName = BuildConfig.VERSION_NAME;
            this.databaseFirestoreVersion = FirestoreModel.DATABASE_VERSION;
            this.databaseRealmVersion = DatabaseRealmConfig.DATABASE_VERSION;
        }

        private ProfileDetailModel build() {
            return new ProfileDetailModel(this);
        }
    }

    public static ProfileDetailModel newInstance() {
        return new ProfileDetailModel.Builder().build();
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public int getDatabaseFirestoreVersion() {
        return databaseFirestoreVersion;
    }

    public int getDatabaseRealmVersion() {
        return databaseRealmVersion;
    }

}
