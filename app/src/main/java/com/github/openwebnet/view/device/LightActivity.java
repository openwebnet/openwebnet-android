package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
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
import rx.functions.Action0;
import rx.functions.Action1;

public class LightActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(LightActivity.class);

    @Inject
    LightService lightService;

    @BindView(R.id.editTextLightName)
    EditText editTextLightName;

    @BindView(R.id.editTextLightWhere)
    EditText editTextLightWhere;

    @BindView(R.id.editTextLightBus)
    EditText editTextLightBus;

    @BindView(R.id.textViewLightPrefix)
    TextView textViewLightPrefix;

    @BindView(R.id.textViewLightSuffix)
    TextView textViewLightSuffix;

    @BindView(R.id.textViewLightPrefixBus)
    TextView textViewLightPrefixBus;

    @BindView(R.id.textViewLightInfo)
    TextView textViewLightInfo;

    @BindView(R.id.textViewLightInfoBus)
    TextView textViewLightInfoBus;

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
    private boolean initLightTypeFirstTime = true;
    private boolean initBusFirstTime = true;

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
            Lighting.Type.GENERAL,
            Lighting.Type.GENERAL_BUS,
            Lighting.Type.AREA,
            Lighting.Type.AREA_BUS,
            Lighting.Type.GROUP,
            Lighting.Type.GROUP_BUS,
            Lighting.Type.POINT_TO_POINT,
            Lighting.Type.POINT_TO_POINT_BUS
        ));

        List<String> lightTypeLabels = Lists.newArrayList(
            getString(R.string.light_label_general),
            getString(R.string.light_label_general_bus),
            getString(R.string.light_label_area),
            getString(R.string.light_label_area_bus),
            getString(R.string.light_label_group),
            getString(R.string.light_label_group_bus),
            getString(R.string.light_label_point_to_point),
            getString(R.string.light_label_point_to_point_bus)
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

        Action1<Integer> initViewBus = visibility -> {
            editTextLightBus.setVisibility(visibility);
            textViewLightPrefixBus.setVisibility(visibility);
            textViewLightInfoBus.setVisibility(visibility);

            editTextLightBus.setError(null);

            // initialize all subsequent calls except the first time
            if (!initBusFirstTime) {
                editTextLightBus.setText(Lighting.NO_BUS);
            }
            // from now on every time reset editTextLightBus
            initBusFirstTime = false;
        };

        Action0 hideBusView = () -> {
            initViewBus.call(View.GONE);
            textViewLightInfoBus.setVisibility(View.INVISIBLE);
            editTextLightBus.setText(Lighting.NO_BUS);
        };

        switch (type) {
            case GENERAL:
                textViewLightPrefix.setText(getString(R.string.light_value_general));
                initView.call(View.GONE);
                textViewLightInfo.setVisibility(View.INVISIBLE);
                initWhereValue.call(Lighting.WHERE_GENERAL_VALUE);

                hideBusView.call();
                break;
            case GENERAL_BUS:
                textViewLightPrefix.setText(getString(R.string.light_value_general_bus));
                initView.call(View.GONE);
                textViewLightInfo.setVisibility(View.INVISIBLE);
                initWhereValue.call(Lighting.WHERE_GENERAL_VALUE);
                textViewLightSuffix.setVisibility(View.VISIBLE);

                initViewBus.call(View.VISIBLE);
                break;
            case AREA:
                textViewLightPrefix.setText(getString(R.string.light_prefix_default));
                textViewLightInfo.setText(getString(R.string.light_info_area));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                hideBusView.call();
                break;
            case AREA_BUS:
                textViewLightPrefix.setText(getString(R.string.light_prefix_default));
                textViewLightInfo.setText(getString(R.string.light_info_area));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                initViewBus.call(View.VISIBLE);
                break;
            case GROUP:
                textViewLightPrefix.setText(getString(R.string.light_prefix_group));
                textViewLightInfo.setText(getString(R.string.light_info_group));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                hideBusView.call();
                break;
            case GROUP_BUS:
                textViewLightPrefix.setText(getString(R.string.light_prefix_group));
                textViewLightInfo.setText(getString(R.string.light_info_group));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                initViewBus.call(View.VISIBLE);
                break;
            case POINT_TO_POINT:
                textViewLightPrefix.setText(getString(R.string.light_prefix_default));
                textViewLightInfo.setText(getString(R.string.light_info_point_to_point));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                hideBusView.call();
                break;
            case POINT_TO_POINT_BUS:
                textViewLightPrefix.setText(getString(R.string.light_prefix_default));
                textViewLightInfo.setText(getString(R.string.light_info_point_to_point));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                initViewBus.call(View.VISIBLE);
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
                editTextLightBus.setText(String.valueOf(light.getBus()));

                selectEnvironment(light.getEnvironmentId());
                selectGateway(light.getGatewayUuid());
                setFavourite(light.isFavourite());
            });
        } else {
            // default must be "Point to Point"
            spinnerLightType.setSelection(6);
        }
    }

    protected Lighting.Type getSelectedLightType() {
        return lightTypeArray.get(spinnerLightType.getSelectedItemPosition());
    }

    @Override
    protected void onMenuSave() {
        log.info("name: {}", editTextLightName.getText());
        log.info("where: {}", editTextLightWhere.getText());
        log.info("type: {}", getSelectedLightType());
        log.info("bus: {}", editTextLightBus.getText());
        log.info("environment: {}", getSelectedEnvironment());
        log.info("gateway: {}", getSelectedGateway());
        log.info("favourite: {}", isFavourite());

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
            getSelectedLightType(),
            utilityService.sanitizedText(editTextLightBus)
        );
        if (!isValid) {
            editTextLightWhere.setError(validationBadValue);
            editTextLightBus.setError(validationBadValue);
            editTextLightWhere.requestFocus();
            editTextLightBus.requestFocus();
        }
        return isValid;
    }

    private LightModel parseLight() {
        return (lightUuid == null ? LightModel.addBuilder() : LightModel.updateBuilder(lightUuid))
            .name(utilityService.sanitizedText(editTextLightName))
            .where(editTextLightWhere.getText().toString())
            .type(getSelectedLightType())
            .bus(editTextLightBus.getText().toString())
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite())
            .build();
    }

}
