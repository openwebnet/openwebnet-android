package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.service.IpcamService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

// TODO translate
public class IpcamActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(IpcamActivity.class);

    @Inject
    IpcamService ipcamService;

    @Bind(R.id.editTextIpcamName)
    EditText editTextIpcamName;

    @Bind(R.id.editTextIpcamUrl)
    EditText editTextIpcamUrl;

    @Bind(R.id.editTextIpcamUsername)
    EditText editTextIpcamUsername;

    @Bind(R.id.editTextIpcamPassword)
    EditText editTextIpcamPassword;

    @Bind(R.id.textViewIpcamUrlHelp)
    TextView textViewIpcamUrlHelp;

    @Bind(R.id.switchIpcamAuthentication)
    Switch switchIpcamAuthentication;

    @Bind(R.id.spinnerIpcamStreamType)
    Spinner spinnerIpcamStreamType;

    @BindString(R.string.validation_url)
    String validationUrl;

    private SparseArray<IpcamModel.StreamType> streamTypeArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        textViewIpcamUrlHelp.setMovementMethod(LinkMovementMethod.getInstance());
        hideSpinnerGateway();
        initSpinnerEnvironment();
        initSpinnerStreamType();
        initEditIpcam();
        initAuthentication();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void hideSpinnerGateway() {
        spinnerDeviceGateway.setVisibility(View.INVISIBLE);
        ViewGroup.MarginLayoutParams layoutParamsSpinnerGateway = (ViewGroup.MarginLayoutParams) spinnerDeviceGateway.getLayoutParams();
        layoutParamsSpinnerGateway.bottomMargin = dp2px(0);
        spinnerDeviceGateway.setLayoutParams(layoutParamsSpinnerGateway);

        ViewGroup.MarginLayoutParams layoutParamsSpinnerEnvironment = (ViewGroup.MarginLayoutParams) spinnerDeviceEnvironment.getLayoutParams();
        layoutParamsSpinnerEnvironment.bottomMargin = dp2px(0);
        spinnerDeviceEnvironment.setLayoutParams(layoutParamsSpinnerEnvironment);
    }

    private void initSpinnerStreamType() {
        List<IpcamModel.StreamType> streamTypes = IpcamModel.StreamType.toList();
        streamTypeArray = initSparseArray(streamTypes);

        List<String> streamTypeValues = Stream.of(streamTypes)
            .map(streamType -> getString(streamType.getLabelId())).collect(Collectors.toList());
        initSpinnerAdapter(spinnerIpcamStreamType, streamTypeValues);
    }

    private IpcamModel.StreamType getSelectedStreamType() {
        return streamTypeArray.get(spinnerIpcamStreamType.getSelectedItemPosition());
    }

    private void initEditIpcam() {
        // TODO
    }

    private void initAuthentication() {
        // TODO
        switchIpcamAuthentication.setChecked(false);
        disableEditText(editTextIpcamUsername);
        disableEditText(editTextIpcamPassword);

        switchIpcamAuthentication.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // enable username
                enableEditText(editTextIpcamUsername);
                editTextIpcamUsername.setInputType(InputType.TYPE_CLASS_TEXT);
                editTextIpcamUsername.requestFocus();
                // enable password
                enableEditText(editTextIpcamPassword);
                editTextIpcamPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                disableEditText(editTextIpcamUsername);
                disableEditText(editTextIpcamPassword);
            }
        });
    }

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
        editText.setFocusable(false);
        editText.setInputType(InputType.TYPE_NULL);
        editText.setText(null);
    }

    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
        editText.setFocusableInTouchMode(true);
    }

    @Override
    protected void onMenuSave() {
        log.debug("name: {}", editTextIpcamName.getText());
        log.debug("url: {}", editTextIpcamUrl.getText());
        log.debug("hasAuthentication: {}", switchIpcamAuthentication.isChecked());
        log.debug("username: {}", editTextIpcamUsername.getText());
        log.debug("password: {}", editTextIpcamPassword.getText());
        log.debug("streamType: {}", getSelectedStreamType());
        log.debug("environment: {}", getSelectedEnvironment());
        log.debug("favourite: {}", isFavourite());

        if (isValidIpcam()) {
            log.debug("VALID");
            //ipcamService.add(parseIpcam()).subscribe(uuid -> finish());
        }
    }

    private boolean isValidIpcam() {
        // TODO url validation ?
        // TODO hasAuthentication: username/password
        // TODO isValidStreamType
        return isValidRequired(editTextIpcamName) &&
            isValidRequired(editTextIpcamUrl) &&
            isValidUrl(editTextIpcamUrl) &&
            (!switchIpcamAuthentication.isChecked() ||
                isValidRequired(editTextIpcamUsername) && isValidRequired(editTextIpcamPassword)) &&
            isValidDeviceEnvironment();
    }

    protected boolean isValidUrl(TextView view) {
        if (view != null && !URLUtil.isValidUrl(editTextIpcamUrl.getText().toString())) {
            view.setError(validationUrl);
            view.requestFocus();
            return false;
        }
        return true;
    }

    // TODO
    private IpcamModel parseIpcam() {
        return null;
    }

}
