package com.github.openwebnet.view.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.text.TextUtils;

import com.github.openwebnet.R;

import de.cketti.library.changelog.ChangeLog;

import static com.github.openwebnet.view.settings.GatewayListPreference.PREF_DEFAULT_GATEWAY_VALUE;

public class SettingsFragment extends PreferenceFragment {

    public static final String PREF_KEY_ABOUT_CHANGELOG = "com.github.openwebnet_preferences.PREF_KEY_ABOUT_CHANGELOG";
    public static final String PREF_KEY_DEBUG_DEVICE = "com.github.openwebnet_preferences.PREF_KEY_DEBUG_DEVICE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        updatePreferenceSummary(getPreferenceScreen());
        initAbout();
    }

    private void updatePreferenceSummary(Preference preference) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
            for (int i=0; i<preferenceGroup.getPreferenceCount(); i++) {
                // recursive
                updatePreferenceSummary(preferenceGroup.getPreference(i));
            }
        } else if (preference instanceof GatewayListPreference) {
            updateGatewayListPreferenceSummary((GatewayListPreference) preference);
        }
    }

    private void updateGatewayListPreferenceSummary(GatewayListPreference preference) {
        String value = preference.getSharedPreferences().getString(PREF_DEFAULT_GATEWAY_VALUE, "");
        if (!TextUtils.isEmpty(value)) {
            preference.setSummary(value);
        }
    }

    private void initAbout() {
        getPreferenceScreen().findPreference(PREF_KEY_ABOUT_CHANGELOG)
            .setOnPreferenceClickListener(preference -> {
                new ChangeLog(preference.getContext()).getLogDialog().show();
                return true;
            });
    }
}
