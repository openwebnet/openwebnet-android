package com.github.openwebnet.view.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.text.TextUtils;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.github.openwebnet.R;
import com.github.openwebnet.view.MainActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import static com.github.openwebnet.view.settings.GatewayListPreference.PREF_DEFAULT_GATEWAY_VALUE;

public class SettingsFragment extends PreferenceFragment {

    public static final String PREF_KEY_DEBUG_DEVICE = "com.github.openwebnet_preferences.PREF_KEY_DEBUG_DEVICE";
    public static final String PREF_KEY_TEMPERATURE = "com.github.openwebnet_preferences.PREF_KEY_TEMPERATURE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        updatePreferenceSummary(getPreferenceScreen());
        initTemperatureChange();
        initDebug();
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
        } else if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            preference.setSummary(listPreference.getEntry());
        }
    }

    private void updateGatewayListPreferenceSummary(GatewayListPreference preference) {
        String value = preference.getSharedPreferences().getString(PREF_DEFAULT_GATEWAY_VALUE, "");
        if (!TextUtils.isEmpty(value)) {
            preference.setSummary(value);
        }
    }

    private void initTemperatureChange() {
        getPreferenceScreen().findPreference(PREF_KEY_TEMPERATURE)
            .setOnPreferenceChangeListener((preference, newValue) -> {

                List<String> keys = Arrays.asList(getResources().getStringArray(R.array.temperature_scale_keys));
                Optional<String> label = Stream.of(keys)
                    .filter(newValue::equals)
                    .map(keys::indexOf)
                    .map(index -> getResources().getStringArray(R.array.temperature_scale_values)[index])
                    .findFirst();

                preference.setSummary(label.get());
                return true;
            });
    }

    private void initDebug() {
        getPreferenceScreen().findPreference(PREF_KEY_DEBUG_DEVICE)
            .setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new MainActivity
                    .OnChangePreferenceDeviceDebugEvent((Boolean) newValue));
                return true;
            });
    }

}
