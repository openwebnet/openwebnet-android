package com.github.openwebnet.service;

import android.content.Context;
import android.content.Intent;

import com.github.openwebnet.model.firestore.UserProfileModel;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import rx.Observable;
import rx.functions.Action0;

public interface FirebaseService {

    Boolean isAuthenticated();

    Intent signIn();

    void signOut(Context context, Action0 action0);

    String getUserPhotoUrl();

    Observable<Void> updateUser();

    Observable<String> addProfile(String name);

    Observable<List<UserProfileModel>> getProfiles();

    Observable<Void> switchProfile(DocumentReference profileRef);

    Observable<Void> renameProfile(DocumentReference profileRef, String name);

    Observable<Void> shareProfile(DocumentReference profileRef, String email);

    Observable<Void> deleteProfile(DocumentReference profileRef);

    Observable<Void> resetLocalProfile();

}
