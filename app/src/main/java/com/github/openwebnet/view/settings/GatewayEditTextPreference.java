package com.github.openwebnet.view.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.R;
import com.github.openwebnet.service.DomoticService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class GatewayEditTextPreference extends EditTextPreference {

    private static final Logger log = LoggerFactory.getLogger(GatewayEditTextPreference.class);

    @Inject
    DomoticService domoticService;

    private View mDialogView;
    private EditText mEditTextHost;
    private EditText mEditTextPort;

    public GatewayEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        OpenWebNetApplication.component(context).inject(this);
    }

    @Override
    protected View onCreateDialogView() {
        mDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_gateway, null);
        mEditTextHost = (EditText) mDialogView.findViewById(R.id.editTextDialogGatewayHost);
        mEditTextPort = (EditText) mDialogView.findViewById(R.id.editTextDialogGatewayPort);
        return mDialogView;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        // override handler
        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(view -> {
                if (isValidHost() && isValidPort()) {
                    addGateway(dialog);
                }
            });
    }

    private String getString(int id) {
        return getContext().getResources().getString(id);
    }

    private boolean isValidHost() {
        if (TextUtils.isEmpty(mEditTextHost.getText())) {
            mEditTextHost.setError(getString(R.string.validation_required));
            return false;
        }
        if (!Patterns.IP_ADDRESS.matcher(mEditTextHost.getText().toString()).matches()) {
            mEditTextHost.setError(getString(R.string.validation_host));
            return false;
        }
        return true;
    }

    private boolean isValidPort() {
        if (TextUtils.isEmpty(mEditTextPort.getText())) {
            mEditTextPort.setError(getString(R.string.validation_required));
            return false;
        }
        Integer port = Integer.parseInt(mEditTextPort.getText().toString());
        if (port < 0 || port > 65535) {
            mEditTextPort.setError(getString(R.string.validation_port));
            return false;
        }
        return true;
    }

    private void addGateway(Dialog dialog) {
        String host = mEditTextHost.getText().toString();
        Integer port = Integer.parseInt(mEditTextPort.getText().toString());
        domoticService.addGateway(host, port)
            .subscribe(uuid -> {
                log.debug("addGateway: {}", uuid);
                dialog.dismiss();
            });
    }

}
