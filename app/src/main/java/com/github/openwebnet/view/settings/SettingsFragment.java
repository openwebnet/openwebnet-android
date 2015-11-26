package com.github.openwebnet.view.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.text.TextUtils;

import com.github.openwebnet.R;

import static com.github.openwebnet.view.settings.GatewayListPreference.PREF_DEFAULT_GATEWAY_VALUE;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        updatePreferenceSummary(getPreferenceScreen());
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
}
