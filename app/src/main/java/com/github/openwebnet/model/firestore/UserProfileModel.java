package com.github.openwebnet.model.firestore;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class UserProfileModel implements FirestoreModel<UserProfileModel> {

    public enum Status {
        ACTIVE,
        DELETED
    }

    private static final String FIELD_PROFILE_REF = "profileRef";
    // TODO remove
    private static final String FIELD_PROFILE_USER_ID = "userId";
    private static final String FIELD_PROFILE_NAME = "name";
    private static final String FIELD_PROFILE_CREATED_AT = "createdAt";
    private static final String FIELD_PROFILE_STATUS = "status";
    // TODO add shared_to

    private DocumentReference profileRef;

    private String userId;

    private String name;

    @ServerTimestamp
    private Date createdAt;

    private Status status;

    public UserProfileModel() {}

    private UserProfileModel(Builder builder) {
        this.profileRef = builder.profileRef;
        this.userId = builder.userId;
        this.name = builder.name;
        this.createdAt = builder.createdAt;
        this.status = builder.status;
    }

    public static class Builder {

        private DocumentReference profileRef;
        private String name;
        private Date createdAt;
        private String userId;
        private Status status;

        public Builder(ProfileModel profile) {
            ProfileDetailModel details = profile.getDetails();
            this.userId = details.getUserId();
            this.name = details.getName();
            this.createdAt = details.getCreatedAt();
            this.status = Status.ACTIVE;
        }

        public Builder(Map<String, Object> map) {
            this.profileRef = (DocumentReference) map.get(FIELD_PROFILE_REF);
            this.userId = (String) map.get(FIELD_PROFILE_USER_ID);
            this.name = (String) map.get(FIELD_PROFILE_NAME);
            this.createdAt = ((Timestamp) map.get(FIELD_PROFILE_CREATED_AT)).toDate();
            this.status = Status.valueOf((String) map.get(FIELD_PROFILE_STATUS));
        }

        // copy: can't use Builder(userProfile.toMap())
        // because createdAt is casted to Timestamp from firestore
        public Builder(UserProfileModel userProfile) {
            this.profileRef = userProfile.getProfileRef();
            this.userId = userProfile.getUserId();
            this.name = userProfile.getName();
            this.createdAt = userProfile.getCreatedAt();
            this.status = userProfile.getStatus();
        }

        public Builder profileRef(DocumentReference profileRef) {
            this.profileRef = profileRef;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public UserProfileModel build() {
            checkNotNull(profileRef, "profileRef is null");
            checkNotNull(userId, "profileRef is null");
            checkNotNull(name, "profileRef is null");
            checkNotNull(createdAt, "createdAt is null");
            checkNotNull(status, "status is null");

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
        map.put(FIELD_PROFILE_STATUS, getStatus().name());
        return map;
    }

    @Override
    public UserProfileModel fromMap(Map<String, Object> map) {
        return new Builder(map).build();
    }

    public DocumentReference getProfileRef() {
        return profileRef;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

}
