package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.ButterKnife;

public class LightActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(LightActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        OpenWebNetApplication.component(this).inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
    }

    @Override
    protected void onMenuSave() {
        log.debug("environment: {}", getSelectedEnvironment());
        log.debug("gateway: {}", getSelectedGateway());
    }
}
