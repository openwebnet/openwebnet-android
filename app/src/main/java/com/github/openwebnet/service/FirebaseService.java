package com.github.openwebnet.service;

import android.net.Uri;

public interface FirebaseService {

    Boolean isAuthenticated();

    String getEmail();

    String getDisplayName();

    Uri getPhotoUrl();

}
