package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.model.TemperatureModel;
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
        temperatureUuid = getIntent().getStringExtra(RealmModel.FIELD_UUID);
        log.debug("initEditTemperature: {}", temperatureUuid);
        if (temperatureUuid != null) {
            temperatureService.findById(temperatureUuid).subscribe(temperature -> {
                editTextTemperatureName.setText(String.valueOf(temperature.getName()));
                editTextTemperatureWhere.setText(String.valueOf(temperature.getWhere()));

                selectEnvironment(temperature.getEnvironmentId());
                selectGateway(temperature.getGatewayUuid());
                setFavourite(temperature.isFavourite());
            });
        }
    }

    @Override
    protected void onMenuSave() {
        log.debug("name: {}", editTextTemperatureName.getText());
        log.debug("where: {}", editTextTemperatureWhere.getText());
        log.debug("environment: {}", getSelectedEnvironment());
        log.debug("gateway: {}", getSelectedGateway());
        log.debug("favourite: {}", isFavourite());

        if (isValidTemperature()) {
            if (temperatureUuid == null) {
                temperatureService.add(parseTemperature()).subscribe(uuid -> finish());
            } else {
                temperatureService.update(parseTemperature())
                    .doOnCompleted(this::finish)
                    .subscribe();
            }
        }
    }

    private boolean isValidTemperature() {
        return isValidRequired(editTextTemperatureName) &&
            isValidRequired(editTextTemperatureWhere) &&
            isValidDeviceEnvironment() &&
            isValidDeviceGateway();
    }

    private TemperatureModel parseTemperature() {
        return (temperatureUuid == null ? TemperatureModel.addBuilder() : TemperatureModel.updateBuilder(temperatureUuid))
            .name(utilityService.sanitizedText(editTextTemperatureName))
            .where(editTextTemperatureWhere.getText().toString())
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite())
            .build();
    }

}
