package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.ButterKnife;

public class SoundActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(SoundActivity.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        initEditSound();
    }

    private void initEditSound() {
        // TODO
    }

    @Override
    protected void onMenuSave() {

    }
}
