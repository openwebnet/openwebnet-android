package com.github.openwebnet.model.firestore;

import android.text.TextUtils;

import com.google.common.collect.Lists;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class UserProfileModel implements FirestoreModel<UserProfileModel> {

    public enum Status {
        ACTIVE,
        DELETED
    }

    private static final String FIELD_PROFILE_REF = "profileRef";
    private static final String FIELD_VERSION = "version";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_MODIFIED_AT = "modifiedAt";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_SHARED_FROM = "sharedFrom";
    private static final String FIELD_SHARED_TO = "sharedTo";

    private DocumentReference profileRef;

    // databaseFirestoreVersion
    private Integer version;

    private String name;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date modifiedAt;

    private Status status;

    private String sharedFrom;

    private List<String> sharedTo;

    public UserProfileModel() {}

    private UserProfileModel(Builder builder) {
        this.profileRef = builder.profileRef;
        this.version = builder.version;
        this.name = builder.name;
        this.createdAt = builder.createdAt;
        this.modifiedAt = builder.modifiedAt;
        this.status = builder.status;
        this.sharedFrom = builder.sharedFrom;
        this.sharedTo = builder.sharedTo;
    }

    public static class Builder {

        private DocumentReference profileRef;
        private Integer version;
        private String name;
        private Date createdAt;
        private Date modifiedAt;
        private Status status;
        private String sharedFrom = "";
        private List<String> sharedTo = Lists.newArrayList();

        private Builder() {
            this.version = FirestoreModel.DATABASE_VERSION;
            Date timestamp = new Date();
            this.createdAt = timestamp;
            this.modifiedAt = timestamp;
            this.status = Status.ACTIVE;
        }

        private Builder(Map<String, Object> map) {
            this.profileRef = (DocumentReference) map.get(FIELD_PROFILE_REF);
            this.version = toInt(map.get(FIELD_VERSION));
            this.name = (String) map.get(FIELD_NAME);
            this.createdAt = toDate(map.get(FIELD_CREATED_AT));
            this.modifiedAt = toDate(map.get(FIELD_MODIFIED_AT));
            this.status = Status.valueOf((String) map.get(FIELD_STATUS));
            this.sharedFrom = (String) map.get(FIELD_SHARED_FROM);
            this.sharedTo = (List<String>) map.get(FIELD_SHARED_TO);
        }

        // Integer is converted to Long from firestore
        private Integer toInt(Object i) {
            if (i == null) {
                return null;
            } else if (i instanceof Long) {
                return ((Long) i).intValue();
            } else if (i instanceof Integer) {
                return (Integer) i;
            }
            throw new IllegalArgumentException("invalid integer");
        }

        // Date is converted to Timestamp from firestore
        private Date toDate(Object timestamp) {
            if (timestamp == null) {
                return null;
            } else if (timestamp instanceof Timestamp) {
                return ((Timestamp) timestamp).toDate();
            } else if (timestamp instanceof Date) {
                return (Date) timestamp;
            }
            throw new IllegalArgumentException("invalid timestamp");
        }

        public Builder profileRef(DocumentReference profileRef) {
            this.profileRef = profileRef;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder modifiedAt(Date modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public UserProfileModel build() {
            checkNotNull(profileRef, "profileRef is null");
            checkArgument(version > 0, "invalid version");
            checkNotNull(name, "profileRef is null");
            checkNotNull(createdAt, "createdAt is null");
            checkNotNull(modifiedAt, "modifiedAt is null");
            checkNotNull(status, "status is null");
            checkNotNull(sharedFrom, "sharedFrom is null");
            checkNotNull(sharedTo, "sharedTo is null");

            return new UserProfileModel(this);
        }
    }

    public static Builder addBuilder() {
        return new UserProfileModel.Builder();
    }

    public static Builder getBuilder(Map<String, Object> map) {
        return new UserProfileModel.Builder(map);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_PROFILE_REF, getProfileRef());
        map.put(FIELD_VERSION, getVersion());
        map.put(FIELD_NAME, getName());
        map.put(FIELD_CREATED_AT, getCreatedAt());
        map.put(FIELD_MODIFIED_AT, getModifiedAt());
        map.put(FIELD_STATUS, getStatus().name());
        map.put(FIELD_SHARED_FROM, getSharedFrom());
        map.put(FIELD_SHARED_TO, getSharedTo());
        return map;
    }

    @Override
    public UserProfileModel fromMap(Map<String, Object> map, ProfileVersionModel version) {
        return new Builder(map).build();
    }

    public boolean isSharedFrom() {
        return TextUtils.isEmpty(sharedFrom);
    }

    public boolean isSharedTo() {
        return sharedTo.size() > 0;
    }

    public boolean isCompatibleVersion() {
        return version <= FirestoreModel.DATABASE_VERSION;
    }

    public DocumentReference getProfileRef() {
        return profileRef;
    }

    public Integer getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public Status getStatus() {
        return status;
    }

    public String getSharedFrom() {
        return sharedFrom;
    }

    public List<String> getSharedTo() {
        return sharedTo;
    }

}
