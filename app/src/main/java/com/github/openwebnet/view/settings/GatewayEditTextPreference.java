package com.github.openwebnet.view.settings;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.github.openwebnet.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayEditTextPreference extends EditTextPreference {

    private static final Logger log = LoggerFactory.getLogger(GatewayEditTextPreference.class);

    private View mDialogView;

    public GatewayEditTextPreference(Context context) {
        super(context);
    }

    public GatewayEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        mDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_gateway, null);
        return mDialogView;
    }

    @Override
    public void setText(String text) {
        // TODO persist to db
        log.debug("HOST: " + _getEditTextValue(R.id.editTextDialogGatewayHost));
        log.debug("PORT: " + _getEditTextValue(R.id.editTextDialogGatewayPort));

    }

    private String _getEditTextValue(int id) {
        return ((EditText) mDialogView.findViewById(id)).getText().toString();
    }
}
