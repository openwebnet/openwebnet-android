package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.IpcamModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IpcamActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(IpcamActivity.class);

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
    }

    // TODO translate: activity_ipcam, bs_ipcam

    // TODO
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

    private void initEditIpcam() {
        // TODO
        initAuthentication();
    }

    private void initAuthentication() {
        // TODO
        switchIpcamAuthentication.setChecked(false);
        disableEditText(editTextIpcamUsername);
        disableEditText(editTextIpcamPassword);

        switchIpcamAuthentication.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableEditText(editTextIpcamUsername);
                editTextIpcamUsername.setInputType(InputType.TYPE_CLASS_TEXT);
                editTextIpcamUsername.requestFocus();
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

    private void initSpinnerStreamType() {
        // TODO EnumSet
        streamTypeArray = initSparseArray(Arrays.asList(IpcamModel.StreamType.MJPEG));
        initSpinnerAdapter(spinnerIpcamStreamType, Arrays.asList(IpcamModel.StreamType.MJPEG.name()));
    }

    @Override
    protected void onMenuSave() {

    }
}
