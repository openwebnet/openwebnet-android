package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.EditText;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
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

    private String ScenarioUuid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        //initEditScenario();
    }

    @Override
    protected void onMenuSave() {
        
    }
    
}
