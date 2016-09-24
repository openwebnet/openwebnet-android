package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.EnergyService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EnergyActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(EnergyActivity.class);

    @Inject
    EnergyService energyService;

    @BindView(R.id.editTextEnergyName)
    EditText editTextEnergyName;

    @BindView(R.id.editTextEnergyWhere)
    EditText editTextEnergyWhere;

    @BindString(R.string.validation_bad_value)
    String validationBadValue;

    private String energyUuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        initSpinnerEnergyVersion();
        initEditEnergy();
    }

    private void initSpinnerEnergyVersion() {

    }

    private void initEditEnergy() {

    }

    @Override
    protected void onMenuSave() {

    }

}
