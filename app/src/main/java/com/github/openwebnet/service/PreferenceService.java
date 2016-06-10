package com.github.openwebnet.service;

import android.content.SharedPreferences;

import static com.github.niqdev.openwebnet.message.Heating.TemperatureScale;

public interface PreferenceService {

    boolean isFirstRun();

    void initFirstRun();

    String getDefaultGateway();

    TemperatureScale getDefaultTemperatureScale();

    boolean isDeviceDebugEnabled();

    SharedPreferences getSecurePreferences();

}
