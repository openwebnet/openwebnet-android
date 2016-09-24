package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.niqdev.openwebnet.message.EnergyManagement;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.EnergyService;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    @BindView(R.id.spinnerEnergyVersion)
    Spinner spinnerEnergyVersion;

    @BindView(R.id.textViewEnergyPrefix)
    TextView textViewEnergyPrefix;

    @BindView(R.id.textViewEnergySuffix)
    TextView textViewEnergySuffix;

    @BindString(R.string.validation_bad_value)
    String validationBadValue;

    private SparseArray<EnergyManagement.Version> energyVersionsArray;

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
        energyVersionsArray = initSparseArray(Lists.newArrayList(
            EnergyManagement.Version.MODEL_F523, EnergyManagement.Version.MODEL_F523_A
        ));

        List<String> energyVersionLabels = Lists.newArrayList(
            getString(R.string.energy_label_v1), getString(R.string.energy_label_v2)
        );
        initSpinnerAdapter(spinnerEnergyVersion, energyVersionLabels);

        spinnerEnergyVersion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (energyVersionsArray.get(spinnerEnergyVersion.getSelectedItemPosition())) {
                    case MODEL_F523: {
                        textViewEnergyPrefix.setText(getString(R.string.energy_prefix_v1));
                        textViewEnergySuffix.setText(getString(R.string.energy_suffix_v1));
                        break;
                    }
                    case MODEL_F523_A: {
                        textViewEnergyPrefix.setText(getString(R.string.energy_prefix_v2));
                        textViewEnergySuffix.setText(getString(R.string.energy_suffix_v2));
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("invalid energy version");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initEditEnergy() {

    }

    @Override
    protected void onMenuSave() {

    }

}
