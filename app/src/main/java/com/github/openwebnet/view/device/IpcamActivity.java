package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.view.View;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.ButterKnife;

public class IpcamActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(IpcamActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam);getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        hideSpinnerGateway();
        initSpinnerEnvironment();
        initEditIpcam();
    }

    // TODO translate: activity_ipcam, bs_ipcam

    // TODO
    private void hideSpinnerGateway() {
        spinnerDeviceGateway.setVisibility(View.INVISIBLE);
        //spinnerDeviceEnvironment.setPadding();
    }

    private void initEditIpcam() {
        // TODO
    }

    @Override
    protected void onMenuSave() {

    }
}
