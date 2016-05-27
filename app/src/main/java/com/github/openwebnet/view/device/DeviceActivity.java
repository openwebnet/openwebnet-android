package com.github.openwebnet.view.device;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.PreferenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(DeviceActivity.class);

    @Inject
    DeviceService deviceService;

    @Inject
    PreferenceService preferenceService;

    @BindView(R.id.editTextDeviceName)
    EditText editTextDeviceName;

    @BindView(R.id.editTextDeviceRequest)
    EditText editTextDeviceRequest;

    @BindView(R.id.editTextDeviceResponse)
    EditText editTextDeviceResponse;

    @BindView(R.id.checkBoxDeviceRunOnLoad)
    CheckBox checkBoxDeviceRunOnLoad;

    @BindView(R.id.checkBoxDeviceConfirm)
    CheckBox checkBoxDeviceConfirm;

    @BindView(R.id.checkBoxDeviceAccept)
    CheckBox checkBoxDeviceAccept;

    @BindView(R.id.textViewDevicePasteResponse)
    TextView textViewDevicePasteResponse;

    @BindView(R.id.imageButtonDevicePasteResponse)
    ImageButton imageButtonDevicePasteResponse;

    @BindString(R.string.device_debug_label)
    String labelDeviceDebug;

    @BindString(R.string.device_debug_label_invalid)
    String labelDeviceDebugInvalid;

    private String deviceUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        initEditDevice();
        initAcceptDisclaimer();
        initPasteRespone();
    }

    private void initEditDevice() {
        deviceUuid = getIntent().getStringExtra(RealmModel.FIELD_UUID);
        log.debug("initEditDevice: {}", deviceUuid);
        if (deviceUuid != null) {
            deviceService.findById(deviceUuid).subscribe(device -> {
                editTextDeviceName.setText(String.valueOf(device.getName()));
                editTextDeviceRequest.setText(String.valueOf(device.getRequest()));
                editTextDeviceResponse.setText(String.valueOf(device.getResponse()));

                selectEnvironment(device.getEnvironmentId());
                selectGateway(device.getGatewayUuid());
                setFavourite(device.isFavourite());

                checkBoxDeviceRunOnLoad.setChecked(device.isRunOnLoad());
                checkBoxDeviceConfirm.setChecked(device.isShowConfirmation());
            });
        }
    }

    private void initAcceptDisclaimer() {
        checkBoxDeviceAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxDeviceAccept.clearFocus();
                checkBoxDeviceAccept.setError(null);
            }
        });
    }

    private void initPasteRespone() {
        textViewDevicePasteResponse.setVisibility(View.INVISIBLE);
        imageButtonDevicePasteResponse.setVisibility(View.INVISIBLE);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (preferenceService.isDeviceDebugEnabled() && clipboard.hasPrimaryClip()
            && labelDeviceDebug.equals(clipboard.getPrimaryClipDescription().getLabel())
            && clipboard.getPrimaryClip().getItemAt(0) != null) {

            textViewDevicePasteResponse.setVisibility(View.VISIBLE);
            imageButtonDevicePasteResponse.setVisibility(View.VISIBLE);

            imageButtonDevicePasteResponse.setOnClickListener(v -> {
                if (labelDeviceDebug.equals(clipboard.getPrimaryClipDescription().getLabel())) {
                    editTextDeviceResponse.setText(clipboard.getPrimaryClip().getItemAt(0).getText());
                } else {
                    Toast.makeText(this, labelDeviceDebugInvalid, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onMenuSave() {
        log.debug("name: {}", editTextDeviceName.getText());
        log.debug("request: {}", editTextDeviceRequest.getText());
        log.debug("response: {}", editTextDeviceResponse.getText());
        log.debug("environment: {}", getSelectedEnvironment());
        log.debug("gateway: {}", getSelectedGateway());
        log.debug("favourite: {}", isFavourite());
        log.debug("runOnLoad: {}", checkBoxDeviceRunOnLoad.isChecked());
        log.debug("confirm: {}", checkBoxDeviceConfirm.isChecked());
        log.debug("accept: {}", checkBoxDeviceAccept.isChecked());

        if (isValidDevice()) {
            if (deviceUuid == null) {
                deviceService.add(parseDevice()).subscribe(uuid -> finish());
            } else {
                deviceService.update(parseDevice())
                    .doOnCompleted(() -> finish())
                    .subscribe();
            }
        }
    }

    private boolean isValidDevice() {
        if (!checkBoxDeviceAccept.isChecked()) {
            checkBoxDeviceAccept.setError(validationRequired);
            checkBoxDeviceAccept.requestFocus();
        } else {
            checkBoxDeviceAccept.setError(null);
        }
        return checkBoxDeviceAccept.isChecked() &&
            isValidRequired(editTextDeviceName) &&
            isValidRequired(editTextDeviceRequest) &&
            isValidRequired(editTextDeviceResponse) &&
            isValidDeviceEnvironment() &&
            isValidDeviceGateway();
    }

    private DeviceModel parseDevice() {
        return (deviceUuid == null ? DeviceModel.addBuilder() : DeviceModel.updateBuilder(deviceUuid))
            .name(utilityService.sanitizedText(editTextDeviceName))
            .request(editTextDeviceRequest.getText().toString())
            .response(editTextDeviceResponse.getText().toString())
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite())
            .runOnLoad(checkBoxDeviceRunOnLoad.isChecked())
            .showConfirmation(checkBoxDeviceConfirm.isChecked())
            .build();
    }

}
