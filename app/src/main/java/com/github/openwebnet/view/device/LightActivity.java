package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.niqdev.openwebnet.message.Lighting;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.LightService;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class LightActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(LightActivity.class);

    @Inject
    LightService lightService;

    @BindView(R.id.editTextLightName)
    EditText editTextLightName;

    @BindView(R.id.editTextLightWhere)
    EditText editTextLightWhere;

    @BindView(R.id.textViewLightPrefix)
    TextView textViewLightPrefix;

    @BindView(R.id.textViewLightSuffix)
    TextView textViewLightSuffix;

    @BindView(R.id.textViewLightInfo)
    TextView textViewLightInfo;

    @BindView(R.id.checkBoxLightDimmer)
    CheckBox checkBoxLightDimmer;

    @BindView(R.id.spinnerLightType)
    Spinner spinnerLightType;

    @BindString(R.string.validation_bad_value)
    String validationBadValue;

    private SparseArray<Lighting.Type> lightTypeArray;

    private String lightUuid;

    /*
     * unfortunately there is no better way to prevent
     * editTextLightWhere to be initialized to ""
     * the first time when initEditLight is invoked
     */
    protected boolean initLightTypeFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        initSpinnerLightType();
        initEditLight();
    }

    private void initSpinnerLightType() {
        lightTypeArray = initSparseArray(Lists.newArrayList(
            Lighting.Type.GENERAL, Lighting.Type.AREA,
            Lighting.Type.GROUP, Lighting.Type.POINT_TO_POINT
        ));

        List<String> lightTypeLabels = Lists.newArrayList(
            getString(R.string.light_label_general), getString(R.string.light_label_area),
            getString(R.string.light_label_group), getString(R.string.light_label_point_to_point)
        );
        initSpinnerAdapter(spinnerLightType, lightTypeLabels);

        spinnerLightType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initLightType(lightTypeArray.get(spinnerLightType.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void initLightType(Lighting.Type type) {
        Action1<Integer> initView = visibility -> {
            editTextLightWhere.setVisibility(visibility);
            textViewLightSuffix.setVisibility(visibility);
            textViewLightInfo.setVisibility(visibility);
        };

        Action1<String> initWhereValue = value -> {
            editTextLightWhere.setError(null);

            // initialize all subsequent calls except the first time
            if (!initLightTypeFirstTime) {
                editTextLightWhere.setText(value);
            }
            // from now on every time reset editTextLightWhere
            initLightTypeFirstTime = false;
        };

        switch (type) {
            case GENERAL:
                textViewLightPrefix.setText(getString(R.string.light_value_general));
                initView.call(View.GONE);
                initWhereValue.call(Lighting.WHERE_GENERAL_VALUE);
                break;
            case AREA:
                textViewLightPrefix.setText(getString(R.string.light_prefix_default));
                textViewLightInfo.setText(getString(R.string.light_info_area));
                initView.call(View.VISIBLE);
                initWhereValue.call("");
                break;
            case GROUP:
                textViewLightPrefix.setText(getString(R.string.light_prefix_group));
                textViewLightInfo.setText(getString(R.string.light_info_group));
                initView.call(View.VISIBLE);
                initWhereValue.call("");
                break;
            case POINT_TO_POINT:
                textViewLightPrefix.setText(getString(R.string.light_prefix_default));
                textViewLightInfo.setText(getString(R.string.light_info_point_to_point));
                initView.call(View.VISIBLE);
                initWhereValue.call("");
                break;
            default: {
                throw new IllegalArgumentException("invalid lighting type");
            }
        }
    }

    private void initEditLight() {
        lightUuid = getIntent().getStringExtra(RealmModel.FIELD_UUID);
        log.debug("initEditLight: {}", lightUuid);
        if (lightUuid != null) {
            lightService.findById(lightUuid).subscribe(light -> {
                spinnerLightType.setSelection(findSelectedItem(lightTypeArray).apply(light.getLightingType()));

                editTextLightName.setText(String.valueOf(light.getName()));
                editTextLightWhere.setText(String.valueOf(light.getWhere()));

                checkBoxLightDimmer.setChecked(light.isDimmer());
                selectEnvironment(light.getEnvironmentId());
                selectGateway(light.getGatewayUuid());
                setFavourite(light.isFavourite());
            });
        } else {
            // default must be "Point to Point"
            spinnerLightType.setSelection(3);
        }
    }

    protected Lighting.Type getSelectedLightType() {
        return lightTypeArray.get(spinnerLightType.getSelectedItemPosition());
    }

    @Override
    protected void onMenuSave() {
        log.debug("name: {}", editTextLightName.getText());
        log.debug("where: {}", editTextLightWhere.getText());
        log.debug("dimmer: {}", checkBoxLightDimmer.isChecked());
        log.debug("type: {}", getSelectedLightType());
        log.debug("environment: {}", getSelectedEnvironment());
        log.debug("gateway: {}", getSelectedGateway());
        log.debug("favourite: {}", isFavourite());

        if (isValidLight()) {
            if (lightUuid == null) {
                lightService.add(parseLight()).subscribe(uuid -> finish());
            } else {
                lightService.update(parseLight())
                    .doOnCompleted(this::finish)
                    .subscribe();
            }
        }
    }

    private boolean isValidLight() {
        return isValidRequired(editTextLightName) &&
            isValidRequired(editTextLightWhere) &&
            isValidLightType() &&
            isValidWhereRange() &&
            isValidDeviceEnvironment() &&
            isValidDeviceGateway();
    }

    protected boolean isValidLightType() {
        return isValidRequired((TextView) spinnerLightType.getSelectedView());
    }

    private boolean isValidWhereRange() {
        boolean isValid = Lighting.isValidRangeType(
            utilityService.sanitizedText(editTextLightWhere),
            getSelectedLightType()
        );
        if (!isValid) {
            editTextLightWhere.setError(validationBadValue);
            editTextLightWhere.requestFocus();
        }
        return isValid;
    }

    private LightModel parseLight() {
        return (lightUuid == null ? LightModel.addBuilder() : LightModel.updateBuilder(lightUuid))
            .name(utilityService.sanitizedText(editTextLightName))
            .where(editTextLightWhere.getText().toString())
            .dimmer(checkBoxLightDimmer.isChecked())
            .type(getSelectedLightType())
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite())
            .build();
    }

}
