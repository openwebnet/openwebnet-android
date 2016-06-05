package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.TemperatureService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TemperatureActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(TemperatureActivity.class);

    @Inject
    TemperatureService temperatureService;

    @BindView(R.id.editTextTemperatureName)
    EditText editTextTemperatureName;

    @BindView(R.id.editTextTemperatureWhere)
    EditText editTextTemperatureWhere;

    private String temperatureUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        initEditTemperature();
    }

    private void initEditTemperature() {

    }

    @Override
    protected void onMenuSave() {

    }
}
