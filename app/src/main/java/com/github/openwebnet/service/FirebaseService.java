package com.github.openwebnet.service;

import android.content.Context;
import android.content.Intent;

import rx.Observable;
import rx.functions.Action0;

public interface FirebaseService {

    Boolean isAuthenticated();

    Intent signIn();

    void signOut(Context context, Action0 action0);

    String getUserPhotoUrl();

    Observable<Void> updateUser();

    Observable<Void> addProfile(String name);

}
