package com.github.openwebnet.service.impl;

import android.content.Context;
import android.content.SharedPreferences;

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
            this.securePreferences = new SecurePreferences(mContext, PREFERENCE_SECURE_PWD, PREFERENCE_SECURE);
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
