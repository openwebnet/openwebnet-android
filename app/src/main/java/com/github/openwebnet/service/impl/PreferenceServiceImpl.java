package com.github.openwebnet.service.impl;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.github.openwebnet.service.PreferenceService;

/**
 * @author niqdev
 */
public class PreferenceServiceImpl implements PreferenceService {

    private static final String FILE_NAME = "com.github.openwebnet.PREFERENCE";

    private final Application application;
    private final SharedPreferences sharedPreferences;

    public PreferenceServiceImpl(Application application) {
        this.application = application;
        this.sharedPreferences = application.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean isFirstRun() {
        return false;
    }

    @Override
    public void initFirstRun() {

    }
}
