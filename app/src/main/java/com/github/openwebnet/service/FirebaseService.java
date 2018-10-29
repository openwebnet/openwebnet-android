package com.github.openwebnet.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.Map;

import rx.functions.Action0;

public interface FirebaseService {

    Boolean isAuthenticated();

    String getUserId();

    String getEmail();

    String getDisplayName();

    Uri getPhotoUrl();

    Map<String, Object> getUser();

    Intent signIn();

    void signOut(Context context, Action0 action0);

    boolean updateUser();

    boolean addProfile();

    boolean shareProfile(String email);

    boolean softDeleteProfile();

}
