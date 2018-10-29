package com.github.openwebnet.service.impl;

import android.content.Context;
import android.content.Intent;

import com.firebase.ui.auth.AuthUI;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.firestore.UserModel;
import com.github.openwebnet.repository.FirestoreRepository;
import com.github.openwebnet.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action0;

import static com.google.common.base.Preconditions.checkArgument;

public class FirebaseServiceImpl implements FirebaseService {

    @Inject
    FirestoreRepository firestoreRepository;

    public FirebaseServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Boolean isAuthenticated() {
        return getCurrentUser() != null;
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
    public String getUserPhotoUrl() {
        return getUser().getPhotoUrl();
    }

    @Override
    public Observable<Void> updateUser() {
        return firestoreRepository.updateUser(getUser());
    }

    @Override
    public Observable<Void> addProfile(String name) {
        return firestoreRepository.addProfile(getUser(), name);
    }

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private UserModel getUser() {
        checkArgument(isAuthenticated(), "user not authenticated");
        FirebaseUser firebaseUser = getCurrentUser();
        Date createdAtDate = firebaseUser.getMetadata() != null ?
            new Date(firebaseUser.getMetadata().getCreationTimestamp()) : new Date();

        return new UserModel.Builder()
            .userId(firebaseUser.getUid())
            .email(firebaseUser.getEmail())
            .name(firebaseUser.getDisplayName())
            .phoneNumber(firebaseUser.getPhoneNumber())
            .photoUrl(firebaseUser.getPhotoUrl() == null ? null : firebaseUser.getPhotoUrl().toString())
            .createdAt(createdAtDate)
            .build();
    }

}
