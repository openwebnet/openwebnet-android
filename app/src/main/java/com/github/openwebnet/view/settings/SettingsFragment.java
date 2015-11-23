package com.github.openwebnet.view.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.github.openwebnet.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
