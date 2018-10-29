package com.github.openwebnet.service.impl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.firebase.ui.auth.AuthUI;
import com.github.openwebnet.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import rx.functions.Action0;

public class FirebaseServiceImpl implements FirebaseService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseServiceImpl.class);

    private static final String FIRESTORE_COLLECTION_USERS = "users";
    private static final String FIRESTORE_COLLECTION_PROFILES = "profiles";

    @Override
    public Boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    @Override
    public String getUserId() {
        return getCurrentUser().getUid();
    }

    @Override
    public String getEmail() {
        return getCurrentUser().getEmail();
    }

    @Override
    public String getDisplayName() {
        return getCurrentUser().getDisplayName();
    }

    @Override
    public Uri getPhotoUrl() {
        return getCurrentUser().getPhotoUrl();
    }

    @Override
    public Map<String, Object> getUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("email", getEmail());
        user.put("name", getDisplayName());
        user.put("phoneNumber", getCurrentUser().getPhoneNumber());
        user.put("photoUrl", getPhotoUrl().toString());
        user.put("timestamp", FieldValue.serverTimestamp());
        return user;
    }

    @Override
    public Intent signIn() {
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))
            .build();
    }

    @Override
    public void signOut(Context context, Action0 action0) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener(task -> action0.call());
    }

    @Override
    public boolean updateUser() {
        try {
            getDb()
                .collection(FIRESTORE_COLLECTION_USERS)
                .document(getUserId())
                .set(getUser(), SetOptions.merge())
                .addOnSuccessListener(aVoid -> log.info("user updated with success"))
                .addOnFailureListener(e -> log.error("failed to update user", e));
            return true;
        } catch (Exception e) {
            log.error("updateUser error", e);
            return false;
        }
    }

    @Override
    public boolean addProfile() {
//        ProfileModel profile = new ProfileModel.Builder()
//            .name("myProfile1")
//            .ownerUserId(getUserId())
//            .build();

        Map<String, Object> profile = new HashMap<>();
        profile.put("name", "MyProfile");

        try {
            getDb()
                .collection(FIRESTORE_COLLECTION_USERS)
                .document(getUserId())
                .update(FIRESTORE_COLLECTION_PROFILES, profile)
                .addOnSuccessListener(aVoid -> log.info("profile added with success"))
                .addOnFailureListener(e -> log.error("failed to add profile", e));
            return true;
        } catch (Exception e) {
            log.error("updateUser error", e);
            return false;
        }
    }

    @Override
    public boolean shareProfile(String email) {
        return false;
    }

    @Override
    public boolean softDeleteProfile() {
        return false;
    }

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance();
    }

}
