package com.github.openwebnet.service.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.view.settings.GatewayListPreference;
import com.github.openwebnet.view.settings.SettingsFragment;
import com.securepreferences.SecurePreferences;

import javax.inject.Inject;

public class PreferenceServiceImpl implements PreferenceService {

    private static final String PREFERENCE_DEFAULT = "com.github.openwebnet_preferences";
    private static final String PREFERENCE_MAIN = "com.github.openwebnet.MAIN";
    private static final String KEY_FIRST_RUN = "com.github.openwebnet.MAIN.FIRST_RUN";

    private static final String PREFERENCE_SECURE = "secure_preferences";
    private static final String PREFERENCE_SECURE_PWD = "NO_PWD";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences securePreferences;

    @Inject
    Context mContext;

    public PreferenceServiceImpl() {
        Injector.getApplicationComponent().inject(this);
        this.sharedPreferences = mContext.getSharedPreferences(PREFERENCE_MAIN, Context.MODE_PRIVATE);
        this.securePreferences = new SecurePreferences(mContext, PREFERENCE_SECURE_PWD, PREFERENCE_SECURE);
    }

    @Override
    public boolean isFirstRun() {
        return sharedPreferences.getBoolean(KEY_FIRST_RUN, true);
    }

    @Override
    public void initFirstRun() {
        sharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply();
    }

    @Override
    public String getDefaultGateway() {
        return mContext.getSharedPreferences(PREFERENCE_DEFAULT, Context.MODE_PRIVATE)
            .getString(GatewayListPreference.PREF_DEFAULT_GATEWAY_KEY, null);
    }

    @Override
    public boolean isDeviceDebugEnabled() {
        return mContext.getSharedPreferences(PREFERENCE_DEFAULT, Context.MODE_PRIVATE)
            .getBoolean(SettingsFragment.PREF_KEY_DEBUG_DEVICE, false);
    }

    @Override
    public SharedPreferences getSecurePreferences() {
        return securePreferences;
    }

}
