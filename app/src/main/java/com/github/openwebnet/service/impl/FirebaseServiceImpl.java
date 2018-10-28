package com.github.openwebnet.service.impl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.firebase.ui.auth.AuthUI;
import com.github.openwebnet.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import rx.functions.Action0;

public class FirebaseServiceImpl implements FirebaseService {

    @Override
    public Boolean isAuthenticated() {
        return getCurrentUser() != null;
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

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
