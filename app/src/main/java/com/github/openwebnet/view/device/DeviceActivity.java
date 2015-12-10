package com.github.openwebnet.view.device;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        OpenWebNetApplication.component(this).inject(this);
        ButterKnife.bind(this);
    }
}
