package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.LightService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LightActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(LightActivity.class);

    @Inject
    LightService lightService;

    @BindView(R.id.editTextLightName)
    EditText editTextLightName;

    @BindView(R.id.editTextLightWhere)
    EditText editTextLightWhere;

    @BindView(R.id.checkBoxLightDimmer)
    CheckBox checkBoxLightDimmer;

    private String lightUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        initEditLight();
    }

    private void initEditLight() {
        lightUuid = getIntent().getStringExtra(RealmModel.FIELD_UUID);
        log.debug("initEditLight: {}", lightUuid);
        if (lightUuid != null) {
            lightService.findById(lightUuid).subscribe(light -> {
                editTextLightName.setText(String.valueOf(light.getName()));
                editTextLightWhere.setText(String.valueOf(light.getWhere()));

                checkBoxLightDimmer.setChecked(light.isDimmer());
                selectEnvironment(light.getEnvironmentId());
                selectGateway(light.getGatewayUuid());
                setFavourite(light.isFavourite());
            });
        }
    }

    @Override
    protected void onMenuSave() {
        log.debug("name: {}", editTextLightName.getText());
        log.debug("where: {}", editTextLightWhere.getText());
        log.debug("dimmer: {}", checkBoxLightDimmer.isChecked());
        log.debug("environment: {}", getSelectedEnvironment());
        log.debug("gateway: {}", getSelectedGateway());
        log.debug("favourite: {}", isFavourite());

        if (isValidLight()) {
            if (lightUuid == null) {
                lightService.add(parseLight()).subscribe(uuid -> finish());
            } else {
                lightService.update(parseLight())
                    .doOnCompleted(() -> finish())
                    .subscribe();
            }
        }
    }

    private boolean isValidLight() {
        return isValidRequired(editTextLightName) &&
            isValidRequired(editTextLightWhere) &&
            isValidDeviceEnvironment() &&
            isValidDeviceGateway();
    }

    private LightModel parseLight() {
        return (lightUuid == null ? LightModel.addBuilder() : LightModel.updateBuilder(lightUuid))
            .name(utilityService.sanitizedText(editTextLightName))
            .where(editTextLightWhere.getText().toString())
            .dimmer(checkBoxLightDimmer.isChecked())
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite())
            .build();
    }

}
