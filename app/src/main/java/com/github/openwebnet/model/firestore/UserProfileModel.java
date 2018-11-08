package com.github.openwebnet.model.firestore;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class UserProfileModel implements FirestoreModel {

    private static final String FIELD_PROFILE_REF = "profileRef";
    private static final String FIELD_PROFILE_USER_ID = "userId";
    private static final String FIELD_PROFILE_NAME = "name";
    private static final String FIELD_PROFILE_CREATED_AT = "createdAt";

    private DocumentReference profileRef;

    private String userId;

    private String name;

    @ServerTimestamp
    private Date createdAt;

    public UserProfileModel() {}

    private UserProfileModel(Builder builder) {
        this.profileRef = builder.profileRef;
        this.userId = builder.userId;
        this.name = builder.name;
        this.createdAt = builder.createdAt;
    }

    public static class Builder {

        private DocumentReference profileRef;
        private String name;
        private Date createdAt;
        private String userId;

        public Builder(ProfileModel profile) {
            ProfileDetailModel details = profile.getDetails();
            this.userId = details.getUserId();
            this.name = details.getName();
            this.createdAt = details.getCreatedAt();
        }

        public Builder profileRef(DocumentReference profileRef) {
            this.profileRef = profileRef;
            return this;
        }

        public UserProfileModel build() {
            checkNotNull(profileRef, "profileRef is null");
            checkNotNull(userId, "profileRef is null");
            checkNotNull(name, "profileRef is null");
            checkNotNull(createdAt, "createdAt is null");

            return new UserProfileModel(this);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_PROFILE_REF, getProfileRef());
        map.put(FIELD_PROFILE_USER_ID, getUserId());
        map.put(FIELD_PROFILE_NAME, getName());
        map.put(FIELD_PROFILE_CREATED_AT, getCreatedAt());
        return map;
    }

    public DocumentReference getProfileRef() {
        return profileRef;
    }

    public void setProfileRef(DocumentReference profileRef) {
        this.profileRef = profileRef;
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

}
