package com.github.openwebnet.model.firestore;

import android.text.TextUtils;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class UserModel {

    private String userId;

    private String email;

    private String name;

    private String phoneNumber;

    private String photoUrl;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date modifiedAt;

    public UserModel() {}

    private UserModel(Builder builder) {
        this.userId = builder.userId;
        this.email = builder.email;
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.photoUrl = builder.photoUrl;
        this.createdAt = builder.createdAt;
        this.modifiedAt = builder.modifiedAt;
    }

    public static class Builder {

        private String userId;
        private String email;
        private String name;
        private String phoneNumber;
        private String photoUrl;
        private Date createdAt;
        private Date modifiedAt;

        public Builder() {
            this.modifiedAt = new Date();
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder photoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder modifiedAt(Date modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public UserModel build() {
            checkArgument(!TextUtils.isEmpty(userId), "userId is empty");
            checkArgument(!TextUtils.isEmpty(email), "email is empty");

            checkNotNull(createdAt, "modifiedAt is null");
            checkNotNull(modifiedAt, "modifiedAt is null");

            return new UserModel(this);
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

}
