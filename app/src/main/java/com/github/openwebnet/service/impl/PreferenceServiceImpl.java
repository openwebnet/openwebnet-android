package com.github.openwebnet.service.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.github.niqdev.openwebnet.message.Heating.TemperatureScale;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.PreferenceService;
import com.securepreferences.SecurePreferences;

import javax.inject.Inject;

import static com.github.niqdev.openwebnet.message.Heating.TemperatureScale.CELSIUS;
import static com.github.openwebnet.view.settings.GatewayListPreference.PREF_DEFAULT_GATEWAY_KEY;
import static com.github.openwebnet.view.settings.SettingsFragment.PREF_KEY_DEBUG_DEVICE;
import static com.github.openwebnet.view.settings.SettingsFragment.PREF_KEY_TEMPERATURE;

public class PreferenceServiceImpl implements PreferenceService {

    public static final String PREFERENCE_DEFAULT = "com.github.openwebnet_preferences";
    public static final String PREFERENCE_MAIN = "com.github.openwebnet.MAIN";
    public static final String KEY_FIRST_RUN = "com.github.openwebnet.MAIN.FIRST_RUN";
    public static final String KEY_FIRST_LOGIN = "com.github.openwebnet.MAIN.FIRST_LOGIN";
    public static final String KEY_APP_VERSION = "com.github.openwebnet.MAIN.APP_VERSION";

    private static final String PREFERENCE_SECURE = "com.github.openwebnet.secure_preferences";
    private static final String PREFERENCE_SECURE_PWD = "NO_PWD";

    private SharedPreferences securePreferences;

    @Inject
    Context mContext;

    public PreferenceServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public boolean isFirstRun() {
        return getMainSharedPreferences().getBoolean(KEY_FIRST_RUN, true);
    }

    @Override
    public void initFirstRun() {
        getMainSharedPreferences().edit().putBoolean(KEY_FIRST_RUN, false).apply();
    }

    @Override
    public boolean isNewVersion() {
        return getMainSharedPreferences().getInt(KEY_APP_VERSION, 0) != Build.VERSION.SDK_INT;
    }

    @Override
    public void initVersion() {
        getMainSharedPreferences().edit().putInt(KEY_APP_VERSION, Build.VERSION.SDK_INT).apply();
    }

    @Override
    public boolean isFirstLogin() {
        return getMainSharedPreferences().getBoolean(KEY_FIRST_LOGIN, true);
    }

    @Override
    public void initFirstLogin() {
        getMainSharedPreferences().edit().putBoolean(KEY_FIRST_LOGIN, false).apply();
    }

    @Override
    public String getDefaultGateway() {
        return getDefaultSharedPreferences().getString(PREF_DEFAULT_GATEWAY_KEY, null);
    }

    @Override
    public TemperatureScale getDefaultTemperatureScale() {
        String temperatureScale = getDefaultSharedPreferences().getString(PREF_KEY_TEMPERATURE, CELSIUS.name());
        return TemperatureScale.valueOf(temperatureScale);
    }

    @Override
    public boolean isDeviceDebugEnabled() {
        return getDefaultSharedPreferences().getBoolean(PREF_KEY_DEBUG_DEVICE, false);
    }

    @Override
    public SharedPreferences getSecurePreferences() {
        if (securePreferences == null) {
            // https://github.com/scottyab/secure-preferences/commit/f7a6e8b1a0961d1b96d848ef996c92ed732a0324
            // ISSUE breaking change from v0.1.4 to v0.1.6
            // revert order for retro-compatibility from [salt, sharedPrefFilename] (correct) to [sharedPrefFilename, salt] (wrong)
            this.securePreferences = new SecurePreferences(mContext, PREFERENCE_SECURE_PWD, PREFERENCE_SECURE, null, 10000);
        }
        return securePreferences;
    }

    private SharedPreferences getMainSharedPreferences() {
        return mContext.getSharedPreferences(PREFERENCE_MAIN, Context.MODE_PRIVATE);
    }

    private SharedPreferences getDefaultSharedPreferences() {
        return mContext.getSharedPreferences(PREFERENCE_DEFAULT, Context.MODE_PRIVATE);
    }

}
