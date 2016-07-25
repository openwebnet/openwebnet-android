package com.github.openwebnet.view.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.UtilityService;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class GatewayEditTextPreference extends EditTextPreference {

    private static final Logger log = LoggerFactory.getLogger(GatewayEditTextPreference.class);

    @Inject
    GatewayService gatewayService;

    @Inject
    UtilityService utilityService;

    private View mDialogView;
    private EditText mEditTextHost;
    private EditText mEditTextPort;
    private EditText mEditTextPassword;

    public GatewayEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected View onCreateDialogView() {
        mDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_gateway, null);
        mEditTextHost = (EditText) mDialogView.findViewById(R.id.editTextDialogGatewayHost);
        mEditTextPort = (EditText) mDialogView.findViewById(R.id.editTextDialogGatewayPort);
        mEditTextPassword = (EditText) mDialogView.findViewById(R.id.editTextDialogGatewayPassword);
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

    private boolean isValidHost() {
        if (utilityService.isBlankText(mEditTextHost)) {
            mEditTextHost.setError(utilityService.getString(R.string.validation_required));
            return false;
        }
        String host = utilityService.sanitizedText(mEditTextHost);
        if (!Patterns.IP_ADDRESS.matcher(host).matches() && !Patterns.DOMAIN_NAME.matcher(host).matches()) {
            mEditTextHost.setError(utilityService.getString(R.string.validation_host));
            return false;
        }
        return true;
    }

    private boolean isValidPort() {
        if (utilityService.isBlankText(mEditTextPort)) {
            mEditTextPort.setError(utilityService.getString(R.string.validation_required));
            return false;
        }
        Integer port = Integer.parseInt(utilityService.sanitizedText(mEditTextPort));
        if (port < 0 || port > 65535) {
            mEditTextPort.setError(utilityService.getString(R.string.validation_port));
            return false;
        }
        return true;
    }

    private void addGateway(Dialog dialog) {
        String host = utilityService.sanitizedText(mEditTextHost);
        Integer port = Integer.parseInt(utilityService.sanitizedText(mEditTextPort));
        String password = Strings.emptyToNull(utilityService.sanitizedText(mEditTextPassword));

        gatewayService.add(GatewayModel.newGateway(host, port, password))
            .subscribe(uuid -> {
                log.debug("NEW gateway: {}", uuid);
                dialog.dismiss();
            });
    }

}
