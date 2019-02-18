package com.github.openwebnet.model.firestore;

import android.content.res.Resources;
import android.support.v4.os.ConfigurationCompat;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class UserModel {

    private String userId;

    private String email;

    private String name;

    private String phoneNumber;

    private String photoUrl;

    private String iso3Language;

    private String iso3Country;

    private String locale;

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
        this.iso3Language = builder.iso3Language;
        this.iso3Country = builder.iso3Country;
        this.locale = builder.locale;
        this.createdAt = builder.createdAt;
        this.modifiedAt = builder.modifiedAt;
    }

    private static class Builder {

        private String userId;
        private String email;
        private String name;
        private String phoneNumber;
        private String photoUrl;
        private String iso3Language;
        private String iso3Country;
        private String locale;
        private Date createdAt;
        private Date modifiedAt;

        private Builder(FirebaseUser firebaseUser) {
            this.userId = firebaseUser.getUid();
            this.email = firebaseUser.getEmail();
            this.name = firebaseUser.getDisplayName();
            this.phoneNumber = firebaseUser.getPhoneNumber();
            this.photoUrl = firebaseUser.getPhotoUrl() == null ? null : firebaseUser.getPhotoUrl().toString();

            // TODO https://stackoverflow.com/questions/4212320/get-the-current-language-in-device
            this.iso3Language = Locale.getDefault().getISO3Language();
            this.iso3Country = Locale.getDefault().getISO3Country();
            this.locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).toLanguageTags();

            this.createdAt = firebaseUser.getMetadata() != null ?
                new Date(firebaseUser.getMetadata().getCreationTimestamp()) : new Date();
            this.modifiedAt = new Date();
        }

        private UserModel build() {
            checkArgument(!TextUtils.isEmpty(userId), "userId is empty");
            checkArgument(!TextUtils.isEmpty(email), "email is empty");

            checkNotNull(createdAt, "modifiedAt is null");
            checkNotNull(modifiedAt, "modifiedAt is null");

            return new UserModel(this);
        }
    }

    public static UserModel newInstance(FirebaseUser firebaseUser) {
        return new Builder(firebaseUser).build();
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

    public String getIso3Language() {
        return iso3Language;
    }

    public String getIso3Country() {
        return iso3Country;
    }

    public String getLocale() {
        return locale;
    }

}
