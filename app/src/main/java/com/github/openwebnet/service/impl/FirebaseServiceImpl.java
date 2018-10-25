package com.github.openwebnet.service.impl;

import android.net.Uri;

import com.github.openwebnet.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
