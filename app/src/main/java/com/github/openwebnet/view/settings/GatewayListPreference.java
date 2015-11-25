package com.github.openwebnet.view.settings;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

import com.annimon.stream.Stream;
import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.service.DomoticService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GatewayListPreference extends ListPreference {

    private static final Logger log = LoggerFactory.getLogger(GatewayListPreference.class);

    public static final String PREF_DEFAULT_GATEWAY = "com.github.openwebnet.view.settings.DEFAULT_GATEWAY";

    @Inject
    DomoticService domoticService;

    public GatewayListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        OpenWebNetApplication.component(context).inject(this);
    }

    @Override
    protected View onCreateDialogView() {
        setKey(PREF_DEFAULT_GATEWAY);
        setSummary(getSharedPreferences().getString(PREF_DEFAULT_GATEWAY, "TODO"));
        initEntries();
        return super.onCreateDialogView();
    }

    private void initEntries() {
        final List<String> entryValues = new ArrayList<>();
        final List<String> entries = new ArrayList<>();

        domoticService.findAllGateway()
            .subscribe(gateways -> {
                Stream.of(gateways).forEach(gateway -> {
                    entries.add(gateway.getUuid());
                    entryValues.add(String.format("%s:%d", gateway.getHost(), gateway.getPort()));
                });
                setEntries(listToCharSequence(entries));
                setEntryValues(listToCharSequence(entryValues));
            });
    }

    private CharSequence[] listToCharSequence(List<String> list) {
        return list.toArray(new CharSequence[list.size()]);
    }

}
