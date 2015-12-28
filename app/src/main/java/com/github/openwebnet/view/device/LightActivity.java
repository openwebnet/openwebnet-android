package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.service.LightService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LightActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(LightActivity.class);

    @Inject
    LightService lightService;

    @Bind(R.id.editTextLightName)
    EditText editTextLightName;

    @Bind(R.id.editTextLightWhere)
    EditText editTextLightWhere;

    @Bind(R.id.checkBoxLightDimmer)
    CheckBox checkBoxLightDimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
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
            lightService.addLight(lightBuilder()).subscribe(uuid -> finish());
        }
    }

    private boolean isValidLight() {
        return isValidRequired(editTextLightName) && isValidRequired(editTextLightWhere) &&
            isValidDeviceEnvironment() && isValidDeviceGateway();
    }

    private LightModel.Builder lightBuilder() {
        return LightModel.newBuilder()
            .name(editTextLightName.getText().toString())
            .where(Integer.parseInt(editTextLightWhere.getText().toString()))
            .dimmer(checkBoxLightDimmer.isChecked())
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite());
    }
}
