package com.github.openwebnet.model.firestore;

import android.text.TextUtils;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ShareProfileRequest implements FirestoreModel<ShareProfileRequest> {

    private static final String FIELD_PROFILE_REF = "profileRef";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_MODIFIED_AT = "modifiedAt";
    private static final String FIELD_STATUS = "status";

    public enum Status {
        CREATED,
        // never used
        SUCCEEDED,
        FAILED
    }

    private DocumentReference profileRef;

    private String email;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date modifiedAt;

    private Status status;

    public ShareProfileRequest() {}

    private ShareProfileRequest(Builder builder) {
        this.profileRef = builder.profileRef;
        this.email = builder.email;
        this.createdAt = builder.createdAt;
        this.modifiedAt = builder.modifiedAt;
        this.status = builder.status;
    }

    public static class Builder {

        private DocumentReference profileRef;
        private String email;
        private Date createdAt;
        private Date modifiedAt;
        private Status status;

        private Builder() {
            Date timestamp = new Date();
            this.createdAt = timestamp;
            this.modifiedAt = timestamp;
            this.status = Status.CREATED;
        }

        public Builder profileRef(DocumentReference profileRef) {
            this.profileRef = profileRef;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public ShareProfileRequest build() {
            checkNotNull(profileRef, "profileRef is null");
            checkArgument(!TextUtils.isEmpty(email), "email is empty");
            return new ShareProfileRequest(this);
        }
    }

    public static Builder addBuilder() {
        return new ShareProfileRequest.Builder();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(FIELD_PROFILE_REF, getProfileRef());
        map.put(FIELD_EMAIL, getEmail());
        map.put(FIELD_CREATED_AT, getCreatedAt());
        map.put(FIELD_MODIFIED_AT, getModifiedAt());
        map.put(FIELD_STATUS, getStatus().name());
        return map;
    }

    @Override
    public ShareProfileRequest fromMap(Map<String, Object> map, ProfileVersionModel version) {
        ShareProfileRequest request = new ShareProfileRequest();
        request.profileRef = (DocumentReference) map.get(FIELD_PROFILE_REF);
        request.email = (String) map.get(FIELD_EMAIL);
        request.createdAt = ((Timestamp) map.get(FIELD_CREATED_AT)).toDate();
        request.modifiedAt = ((Timestamp) map.get(FIELD_MODIFIED_AT)).toDate();
        request.status = Status.valueOf((String) map.get(FIELD_STATUS));
        return request;
    }

    public DocumentReference getProfileRef() {
        return profileRef;
    }

    public String getEmail() {
        return email;
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

}
