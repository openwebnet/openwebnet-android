package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.model.ScenarioModel;
import com.github.openwebnet.service.ScenarioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScenarioActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(ScenarioActivity.class);

    @Inject
    ScenarioService scenarioService;

    @BindView(R.id.editTextScenarioName)
    EditText editTextScenarioName;

    @BindView(R.id.editTextScenarioWhere)
    EditText editTextScenarioWhere;

    private String scenarioUuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        initEditScenario();
    }

    private void initEditScenario() {
        scenarioUuid = getIntent().getStringExtra(RealmModel.FIELD_UUID);
        log.debug("initEditScenario: {}", scenarioUuid);
        if (scenarioUuid != null) {
            scenarioService.findById(scenarioUuid).subscribe(automation -> {
                editTextScenarioName.setText(String.valueOf(automation.getName()));
                editTextScenarioWhere.setText(String.valueOf(automation.getWhere()));

                selectEnvironment(automation.getEnvironmentId());
                selectGateway(automation.getGatewayUuid());
                setFavourite(automation.isFavourite());
            });
        }
    }

    @Override
    protected void onMenuSave() {
        log.debug("name: {}", editTextScenarioName.getText());
        log.debug("where: {}", editTextScenarioWhere.getText());
        log.debug("environment: {}", getSelectedEnvironment());
        log.debug("gateway: {}", getSelectedGateway());
        log.debug("favourite: {}", isFavourite());

        if (isValidScenario()) {
            if (scenarioUuid == null) {
                scenarioService.add(parseScenario()).subscribe(uuid -> finish());
            } else {
                scenarioService.update(parseScenario())
                    .doOnCompleted(this::finish)
                    .subscribe();
            }
        }
    }

    private boolean isValidScenario() {
        return isValidRequired(editTextScenarioName) &&
            isValidRequired(editTextScenarioWhere) &&
            isValidDeviceEnvironment() &&
            isValidDeviceGateway();
    }

    private ScenarioModel parseScenario() {
        return (scenarioUuid == null ? ScenarioModel.addBuilder() : ScenarioModel.updateBuilder(scenarioUuid))
            .name(utilityService.sanitizedText(editTextScenarioName))
            .where(editTextScenarioWhere.getText().toString())
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite())
            .build();
    }

}
