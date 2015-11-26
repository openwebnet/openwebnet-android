package com.github.openwebnet.view.settings;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

import com.annimon.stream.Stream;
import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.service.DomoticService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.text.TextUtils.isEmpty;

public class GatewayListPreference extends ListPreference {

    private static final Logger log = LoggerFactory.getLogger(GatewayListPreference.class);

    public static final String PREF_DEFAULT_GATEWAY_KEY = "com.github.openwebnet.view.settings.DEFAULT_GATEWAY_KEY";
    public static final String PREF_DEFAULT_GATEWAY_VALUE = "com.github.openwebnet.view.settings.DEFAULT_GATEWAY_VALUE";

    @Inject
    DomoticService domoticService;

    public GatewayListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        OpenWebNetApplication.component(context).inject(this);
    }

    @Override
    protected View onCreateDialogView() {
        setKey(PREF_DEFAULT_GATEWAY_KEY);
        initEntries();
        return super.onCreateDialogView();
    }

    private void initEntries() {
        final List<String> entries = new ArrayList<>();
        final List<String> entryValues = new ArrayList<>();

        domoticService.findAllGateway()
            .subscribe(gateways -> {
                // split stream
                Stream.of(gateways).forEach(gateway -> {
                    entries.add(String.format("%s:%d", gateway.getHost(), gateway.getPort()));
                    entryValues.add(gateway.getUuid());
                });
                setEntries(listToCharSequence(entries));
                setEntryValues(listToCharSequence(entryValues));
            });
    }

    private CharSequence[] listToCharSequence(List<String> list) {
        return list.toArray(new CharSequence[list.size()]);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult && !isEmpty(getEntry()) && !isEmpty(getValue())) {
            setSummary(getEntry());
            getSharedPreferences()
                .edit()
                .putString(PREF_DEFAULT_GATEWAY_VALUE, getEntry().toString())
                .commit();
        }
    }
}
