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
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.model.RealmModel;
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

    protected enum EnergyGroup {
        VERSION_1,
        VERSION_2
    }

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

    private SparseArray<EnergyGroup> energyVersionsArray;

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
            EnergyGroup.VERSION_1, EnergyGroup.VERSION_2
        ));

        List<String> energyVersionLabels = Lists.newArrayList(
            getString(R.string.energy_label_v1), getString(R.string.energy_label_v2)
        );
        initSpinnerAdapter(spinnerEnergyVersion, energyVersionLabels);

        spinnerEnergyVersion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initEnergyGroup(energyVersionsArray.get(spinnerEnergyVersion.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void initEnergyGroup(EnergyGroup group) {
        switch (group) {
            case VERSION_1: {
                textViewEnergyPrefix.setText(getString(R.string.energy_prefix_v1));
                textViewEnergySuffix.setText(getString(R.string.energy_suffix_v1));
                break;
            }
            case VERSION_2: {
                textViewEnergyPrefix.setText(getString(R.string.energy_prefix_v2));
                textViewEnergySuffix.setText(getString(R.string.energy_suffix_v2));
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid energy group");
            }
        }
    }

    private void initEditEnergy() {
        energyUuid = getIntent().getStringExtra(RealmModel.FIELD_UUID);
        log.debug("initEditEnergy: {}", energyUuid);
        if (energyUuid != null) {
            energyService.findById(energyUuid).subscribe(energy -> {
                editTextEnergyName.setText(String.valueOf(energy.getName()));
                editTextEnergyWhere.setText(String.valueOf(energy.getWhere()));

                selectEnergyVersion(energy.getEnergyManagementVersion());
                selectEnvironment(energy.getEnvironmentId());
                selectGateway(energy.getGatewayUuid());
                setFavourite(energy.isFavourite());
            });
        }
    }

    private void selectEnergyVersion(EnergyManagement.Version energyManagementVersion) {
        switch (energyManagementVersion) {
            case MODEL_F520: case MODEL_F523: case MODEL_3522:
                initEnergyGroup(EnergyGroup.VERSION_1);
                spinnerEnergyVersion.setSelection(findSelectedItem(energyVersionsArray).apply(EnergyGroup.VERSION_1));
                break;
            case MODEL_F522_A: case MODEL_F523_A:
                initEnergyGroup(EnergyGroup.VERSION_2);
                spinnerEnergyVersion.setSelection(findSelectedItem(energyVersionsArray).apply(EnergyGroup.VERSION_2));
                break;
            default:
                throw new IllegalArgumentException("invalid energy version");
        }
    }

    // use just one model for each version
    private EnergyManagement.Version getSelectedEnergyVersion() {
        switch (energyVersionsArray.get(spinnerEnergyVersion.getSelectedItemPosition())) {
            case VERSION_1:
                return EnergyManagement.Version.MODEL_F523;
            case VERSION_2:
                return EnergyManagement.Version.MODEL_F523_A;
            default:
                throw new IllegalArgumentException("invalid energy version");
        }
    }

    @Override
    protected void onMenuSave() {
        log.debug("name: {}", editTextEnergyName.getText());
        log.debug("where: {}", editTextEnergyWhere.getText());
        log.debug("version: {}", getSelectedEnergyVersion());
        log.debug("environment: {}", getSelectedEnvironment());
        log.debug("gateway: {}", getSelectedGateway());
        log.debug("favourite: {}", isFavourite());

        if (isValidEnergy()) {
            if (energyUuid == null) {
                energyService.add(parseEnergy()).subscribe(uuid -> finish());
            } else {
                energyService.update(parseEnergy())
                    .doOnCompleted(this::finish)
                    .subscribe();
            }
        }
    }

    private boolean isValidEnergy() {
        return isValidRequired(editTextEnergyName) &&
            isValidRequired(editTextEnergyWhere) &&
            isValidWhereRange() &&
            isValidEnergyVersion() &&
            isValidDeviceEnvironment() &&
            isValidDeviceGateway();
    }

    private boolean isValidWhereRange() {
        int where = Integer.parseInt(utilityService.sanitizedText(editTextEnergyWhere));
        if (where < 1 || where > 255) {
            editTextEnergyWhere.setError(validationBadValue);
            editTextEnergyWhere.requestFocus();
            return false;
        }
        return true;
    }

    protected boolean isValidEnergyVersion() {
        return isValidRequired((TextView) spinnerEnergyVersion.getSelectedView());
    }

    private EnergyModel parseEnergy() {
        return (energyUuid == null ? EnergyModel.addBuilder() : EnergyModel.updateBuilder(energyUuid))
            .name(utilityService.sanitizedText(editTextEnergyName))
            .where(editTextEnergyWhere.getText().toString())
            .version(getSelectedEnergyVersion())
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite())
            .build();
    }

}
