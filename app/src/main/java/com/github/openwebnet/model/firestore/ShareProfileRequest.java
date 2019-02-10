package com.github.openwebnet.model.firestore;

import android.text.TextUtils;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ShareProfileRequest implements FirestoreModel<ShareProfileRequest> {

    private static final String FIELD_REQUEST_ID = "requestId";
    private static final String FIELD_PROFILE_REF = "profileRef";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_CREATED_AT = "createdAt";

    private UUID requestId;

    private DocumentReference profileRef;

    private String email;

    @ServerTimestamp
    private Date createdAt;

    public ShareProfileRequest() {}

    private ShareProfileRequest(Builder builder) {
        this.requestId = builder.requestId;
        this.profileRef = builder.profileRef;
        this.email = builder.email;
        this.createdAt = builder.createdAt;
    }

    public static class Builder {

        private final UUID requestId;
        private DocumentReference profileRef;
        private String email;
        private final Date createdAt;

        private Builder() {
            this.requestId = UUID.randomUUID();
            Date timestamp = new Date();
            this.createdAt = timestamp;
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
        map.put(FIELD_REQUEST_ID, getRequestId().toString());
        map.put(FIELD_PROFILE_REF, getProfileRef());
        map.put(FIELD_EMAIL, getEmail());
        map.put(FIELD_CREATED_AT, getCreatedAt());
        return map;
    }

    @Override
    public ShareProfileRequest fromMap(Map<String, Object> map, ProfileVersionModel version) {
        ShareProfileRequest request = new ShareProfileRequest();
        request.requestId = UUID.fromString((String) map.get(FIELD_REQUEST_ID));
        request.profileRef = (DocumentReference) map.get(FIELD_PROFILE_REF);
        request.email = (String) map.get(FIELD_EMAIL);
        request.createdAt = ((Timestamp) map.get(FIELD_CREATED_AT)).toDate();
        return request;
    }

    public UUID getRequestId() {
        return requestId;
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

}
