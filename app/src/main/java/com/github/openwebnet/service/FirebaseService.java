package com.github.openwebnet.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import rx.functions.Action0;

public interface FirebaseService {

    Boolean isAuthenticated();

    String getEmail();

    String getDisplayName();

    Uri getPhotoUrl();

    Intent signIn();

    void signOut(Context context, Action0 action0);

}
