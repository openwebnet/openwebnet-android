package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.widget.EditText;

import com.github.niqdev.openwebnet.message.Automation;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.AutomationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AutomationActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(AutomationActivity.class);

    @Inject
    AutomationService automationService;

    @BindView(R.id.editTextAutomationName)
    EditText editTextAutomationName;

    @BindView(R.id.editTextAutomationWhere)
    EditText editTextAutomationWhere;

    private String automationUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        initEditAutomation();
    }

    private void initEditAutomation() {
        automationUuid = getIntent().getStringExtra(RealmModel.FIELD_UUID);
        log.debug("initEditAutomation: {}", automationUuid);
        if (automationUuid != null) {
            automationService.findById(automationUuid).subscribe(automation -> {
                editTextAutomationName.setText(String.valueOf(automation.getName()));
                editTextAutomationWhere.setText(String.valueOf(automation.getWhere()));

                selectEnvironment(automation.getEnvironmentId());
                selectGateway(automation.getGatewayUuid());
                setFavourite(automation.isFavourite());
            });
        }
    }

    @Override
    protected void onMenuSave() {
        log.debug("name: {}", editTextAutomationName.getText());
        log.debug("where: {}", editTextAutomationWhere.getText());
        log.debug("environment: {}", getSelectedEnvironment());
        log.debug("gateway: {}", getSelectedGateway());
        log.debug("favourite: {}", isFavourite());

        if (isValidAutomation()) {
            if (automationUuid == null) {
                automationService.add(parseAutomation()).subscribe(uuid -> finish());
            } else {
                automationService.update(parseAutomation())
                    .doOnCompleted(this::finish)
                    .subscribe();
            }
        }
    }

    private boolean isValidAutomation() {
        return isValidRequired(editTextAutomationName) &&
            isValidRequired(editTextAutomationWhere) &&
            isValidDeviceEnvironment() &&
            isValidDeviceGateway();
    }

    // TODO type + bus
    private AutomationModel parseAutomation() {
        return (automationUuid == null ? AutomationModel.addBuilder() : AutomationModel.updateBuilder(automationUuid))
            .name(utilityService.sanitizedText(editTextAutomationName))
            .where(editTextAutomationWhere.getText().toString())
            .type(Automation.Type.POINT)
            .bus(Automation.NO_BUS)
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite())
            .build();
    }

}
