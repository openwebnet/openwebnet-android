package com.github.openwebnet.model;

import android.text.TextUtils;

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

    private String ownerUserId;

    private List<String> shareUserIds;

    private boolean softDelete;

    private int databaseVersion;

    // required by Firestore
    public ProfileModel() {}

    private ProfileModel(Builder builder) {
        this.name = builder.name;
        this.ownerUserId = builder.ownerUserId;
        this.shareUserIds = builder.shareUserIds;
        this.timestampCreated = builder.timestampCreated;
        this.timestampUpdated = builder.timestampUpdated;
    }

    public static class Builder {

        private String name;
        private String ownerUserId;
        private List<String> shareUserIds;
        private Date timestampCreated;
        private Date timestampUpdated;
        private boolean softDelete;
        private int databaseVersion;

        public Builder() {
            this.shareUserIds = new ArrayList<>();
            this.timestampCreated = new Date();
            this.timestampUpdated = new Date();
            this.softDelete = false;
            this.databaseVersion = DatabaseRealmConfig.DATABASE_VERSION;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder ownerUserId(String ownerUserId) {
            this.ownerUserId = ownerUserId;
            return this;
        }

        public Builder shareUserId(String shareUserId) {
            checkArgument(!TextUtils.isEmpty(shareUserId), "shareUserId is empty");
            this.shareUserIds.add(shareUserId);
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

        public ProfileModel build() {
            checkArgument(!TextUtils.isEmpty(name), "name is empty");
            checkArgument(!TextUtils.isEmpty(ownerUserId), "ownerUserId is empty");

            checkNotNull(timestampCreated, "timestampCreated is null");
            checkNotNull(timestampUpdated, "timestampUpdated is null");

            checkArgument(databaseVersion > 0, "databaseVersion is invalid");

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

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public List<String> getShareUserIds() {
        return shareUserIds;
    }

    public void setShareUserIds(List<String> shareUserIds) {
        this.shareUserIds = shareUserIds;
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

}
