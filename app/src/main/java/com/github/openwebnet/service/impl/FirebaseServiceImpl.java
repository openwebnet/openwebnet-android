package com.github.openwebnet.service.impl;

import android.content.Context;
import android.content.Intent;

import com.firebase.ui.auth.AuthUI;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.firestore.UserModel;
import com.github.openwebnet.model.firestore.UserProfileModel;
import com.github.openwebnet.repository.FirestoreRepository;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.FirebaseService;
import com.github.openwebnet.service.UtilityService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action0;

import static com.google.common.base.Preconditions.checkArgument;

public class FirebaseServiceImpl implements FirebaseService {

    @Inject
    FirestoreRepository firestoreRepository;

    @Inject
    EnvironmentService environmentService;

    @Inject
    UtilityService utilityService;

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
            .setTosAndPrivacyPolicyUrls(
                utilityService.getString(R.string.url_terms_conditions),
                utilityService.getString(R.string.url_privacy_policy))
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
    public Observable<String> addProfile(String name) {
        return firestoreRepository.updateUser(getUser())
            .flatMap(aVoid -> firestoreRepository.addProfile(getUser(), name));
    }

    @Override
    public Observable<List<UserProfileModel>> getUserProfiles() {
        return firestoreRepository.getUserProfiles(getUser().getUserId());
    }

    @Override
    public Observable<Void> switchProfile(DocumentReference profileRef) {
        return safeDeleteLocalProfile()
            .flatMap(aVoid -> firestoreRepository.getProfile(profileRef))
            .single()
            .flatMap(profileModel -> firestoreRepository.applyProfile(profileModel))
            .map(counts -> null);
    }

    // TODO rename
    @Override
    public Observable<Void> softDeleteProfile(DocumentReference profileRef) {
        return firestoreRepository.deleteUserProfile(getUser().getUserId(), profileRef);
    }

    @Override
    public Observable<Void> shareProfile(DocumentReference profileRef, String email) {
        return firestoreRepository.shareProfile(getUser().getUserId(), profileRef, email);
    }

    @Override
    public Observable<Void> resetLocalProfile() {
        return safeDeleteLocalProfile()
            .flatMap(aVoid -> addDefaultEnvironment());
    }

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private UserModel getUser() {
        checkArgument(isAuthenticated(), "user not authenticated");
        return UserModel.newInstance(getCurrentUser());
    }

    private Observable<Void> addDefaultEnvironment() {
        return environmentService
            .add(utilityService.getString(R.string.drawer_menu_example))
            .map(id -> null);
    }

    private Observable<Void> safeDeleteLocalProfile() {
        // make sure at least one environment exists
        // otherwise if empty it will stop the flow
        return addDefaultEnvironment()
            .flatMap(aVoid -> firestoreRepository.deleteLocalProfile())
            // make sure to consume only once
            .last();
    }

}
