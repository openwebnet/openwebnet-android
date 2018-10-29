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
    private Date timestampUpdated;

    public UserModel() {}

    private UserModel(Builder builder) {
        this.userId = builder.userId;
        this.email = builder.email;
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.photoUrl = builder.photoUrl;
        this.timestampUpdated = builder.timestampUpdated;
    }

    public static class Builder {

        private String userId;
        private String email;
        private String name;
        private String phoneNumber;
        private String photoUrl;
        private Date timestampUpdated;

        public Builder() {
            this.timestampUpdated = new Date();
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

        public Builder timestampUpdated(Date timestampUpdated) {
            this.timestampUpdated = timestampUpdated;
            return this;
        }

        public UserModel build() {
            checkArgument(!TextUtils.isEmpty(userId), "userId is empty");
            checkArgument(!TextUtils.isEmpty(email), "email is empty");
            checkNotNull(timestampUpdated, "timestampUpdated is null");

            return new UserModel(this);
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Date getTimestampUpdated() {
        return timestampUpdated;
    }

    public void setTimestampUpdated(Date timestampUpdated) {
        this.timestampUpdated = timestampUpdated;
    }

}
