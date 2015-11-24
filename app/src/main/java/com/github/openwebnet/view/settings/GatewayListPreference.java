package com.github.openwebnet.view.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.github.openwebnet.R;

// TODO
public class GatewayListPreference extends ListPreference {

    public GatewayListPreference(Context context) {
        super(context);
    }

    public GatewayListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.dialog_gateway, null);

        builder.setTitle("title");
        builder.setMessage("message");
        builder.setView(layout);
        builder.setPositiveButton(R.string.button_add, null);
        builder.setNegativeButton(android.R.string.cancel, null);

        super.onPrepareDialogBuilder(builder);
    }
}
