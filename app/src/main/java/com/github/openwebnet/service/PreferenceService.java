package com.github.openwebnet.service;

import android.content.SharedPreferences;

public interface PreferenceService {

    boolean isFirstRun();

    void initFirstRun();

    String getDefaultGateway();

    boolean isDeviceDebugEnabled();

    SharedPreferences getSecurePreferences();

}
