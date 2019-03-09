package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.niqdev.openwebnet.message.Automation;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.AutomationService;
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

public class AutomationActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(AutomationActivity.class);

    @Inject
    AutomationService automationService;

    @BindView(R.id.editTextAutomationName)
    EditText editTextAutomationName;

    @BindView(R.id.editTextAutomationWhere)
    EditText editTextAutomationWhere;

    @BindView(R.id.editTextAutomationBus)
    EditText editTextAutomationBus;

    @BindView(R.id.textViewAutomationPrefix)
    TextView textViewAutomationPrefix;

    @BindView(R.id.textViewAutomationSuffix)
    TextView textViewAutomationSuffix;

    @BindView(R.id.textViewAutomationPrefixBus)
    TextView textViewAutomationPrefixBus;

    @BindView(R.id.textViewAutomationInfo)
    TextView textViewAutomationInfo;

    @BindView(R.id.textViewAutomationInfoBus)
    TextView textViewAutomationInfoBus;

    @BindView(R.id.spinnerAutomationType)
    Spinner spinnerAutomationType;

    @BindString(R.string.validation_bad_value)
    String validationBadValue;

    private SparseArray<Automation.Type> automationTypeArray;

    private String automationUuid;

    private boolean initAutomationTypeFirstTime = true;
    private boolean initBusFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        initSpinnerAutomationType();
        initEditAutomation();
    }

    private void initSpinnerAutomationType() {
        automationTypeArray = initSparseArray(Lists.newArrayList(
            Automation.Type.GENERAL,
            Automation.Type.GENERAL_BUS,
            Automation.Type.AREA,
            Automation.Type.AREA_BUS,
            Automation.Type.GROUP,
            Automation.Type.GROUP_BUS,
            Automation.Type.POINT_TO_POINT,
            Automation.Type.POINT_TO_POINT_BUS
        ));

        List<String> automationTypeLabels = Lists.newArrayList(
            getString(R.string.device_label_general),
            getString(R.string.device_label_general_bus),
            getString(R.string.device_label_area),
            getString(R.string.device_label_area_bus),
            getString(R.string.device_label_group),
            getString(R.string.device_label_group_bus),
            getString(R.string.automation_label_point_to_point),
            getString(R.string.automation_label_point_to_point_bus)
        );
        initSpinnerAdapter(spinnerAutomationType, automationTypeLabels);

        spinnerAutomationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initAutomationType(automationTypeArray.get(spinnerAutomationType.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void initAutomationType(Automation.Type type) {
        Action1<Integer> initView = visibility -> {
            editTextAutomationWhere.setVisibility(visibility);
            textViewAutomationSuffix.setVisibility(visibility);
            textViewAutomationInfo.setVisibility(visibility);
        };

        Action1<String> initWhereValue = value -> {
            editTextAutomationWhere.setError(null);

            // initialize all subsequent calls except the first time
            if (!initAutomationTypeFirstTime) {
                editTextAutomationWhere.setText(value);
            }
            // from now on every time reset editTextAutomationWhere
            initAutomationTypeFirstTime = false;
        };

        Action1<Integer> initViewBus = visibility -> {
            editTextAutomationBus.setVisibility(visibility);
            textViewAutomationPrefixBus.setVisibility(visibility);
            textViewAutomationInfoBus.setVisibility(visibility);

            editTextAutomationBus.setError(null);

            // initialize all subsequent calls except the first time
            if (!initBusFirstTime) {
                editTextAutomationBus.setText(Automation.NO_BUS);
            }
            // from now on every time reset editTextAutomationBus
            initBusFirstTime = false;
        };

        Action0 hideBusView = () -> {
            initViewBus.call(View.GONE);
            textViewAutomationInfoBus.setVisibility(View.INVISIBLE);
            editTextAutomationBus.setText(Automation.NO_BUS);
        };

        switch (type) {
            case GENERAL:
                textViewAutomationPrefix.setText(getString(R.string.automation_value_general));
                initView.call(View.GONE);
                textViewAutomationInfo.setVisibility(View.INVISIBLE);
                initWhereValue.call(Automation.WHERE_GENERAL_VALUE);

                hideBusView.call();
                break;
            case GENERAL_BUS:
                textViewAutomationPrefix.setText(getString(R.string.automation_value_general_bus));
                initView.call(View.GONE);
                textViewAutomationInfo.setVisibility(View.INVISIBLE);
                initWhereValue.call(Automation.WHERE_GENERAL_VALUE);
                textViewAutomationSuffix.setVisibility(View.VISIBLE);

                initViewBus.call(View.VISIBLE);
                break;
            case AREA:
                textViewAutomationPrefix.setText(getString(R.string.automation_prefix_default));
                textViewAutomationInfo.setText(getString(R.string.automation_info_area));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                hideBusView.call();
                break;
            case AREA_BUS:
                textViewAutomationPrefix.setText(getString(R.string.automation_prefix_default));
                textViewAutomationInfo.setText(getString(R.string.automation_info_area));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                initViewBus.call(View.VISIBLE);
                break;
            case GROUP:
                textViewAutomationPrefix.setText(getString(R.string.automation_prefix_group));
                textViewAutomationInfo.setText(getString(R.string.automation_info_group));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                hideBusView.call();
                break;
            case GROUP_BUS:
                textViewAutomationPrefix.setText(getString(R.string.automation_prefix_group));
                textViewAutomationInfo.setText(getString(R.string.automation_info_group));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                initViewBus.call(View.VISIBLE);
                break;
            case POINT_TO_POINT:
                textViewAutomationPrefix.setText(getString(R.string.automation_prefix_default));
                textViewAutomationInfo.setText(getString(R.string.automation_info_point_to_point));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                hideBusView.call();
                break;
            case POINT_TO_POINT_BUS:
                textViewAutomationPrefix.setText(getString(R.string.automation_prefix_default));
                textViewAutomationInfo.setText(getString(R.string.automation_info_point_to_point));
                initView.call(View.VISIBLE);
                initWhereValue.call("");

                initViewBus.call(View.VISIBLE);
                break;
            default: {
                throw new IllegalArgumentException("invalid automation type");
            }
        }
    }

    private void initEditAutomation() {
        automationUuid = getIntent().getStringExtra(RealmModel.FIELD_UUID);
        log.debug("initEditAutomation: {}", automationUuid);
        if (automationUuid != null) {
            automationService.findById(automationUuid).subscribe(automation -> {
                spinnerAutomationType.setSelection(findSelectedItem(automationTypeArray).apply(automation.getAutomationType()));

                editTextAutomationName.setText(String.valueOf(automation.getName()));
                editTextAutomationWhere.setText(String.valueOf(automation.getWhere()));
                editTextAutomationBus.setText(String.valueOf(automation.getBus()));

                selectEnvironment(automation.getEnvironmentId());
                selectGateway(automation.getGatewayUuid());
                setFavourite(automation.isFavourite());
            });
        } else {
            // default must be "Point to Point"
            spinnerAutomationType.setSelection(6);
        }
    }

    protected Automation.Type getSelectedAutomationType() {
        return automationTypeArray.get(spinnerAutomationType.getSelectedItemPosition());
    }

    @Override
    protected void onMenuSave() {
        log.info("name: {}", editTextAutomationName.getText());
        log.info("where: {}", editTextAutomationWhere.getText());
        log.info("type: {}", getSelectedAutomationType());
        log.info("bus: {}", editTextAutomationBus.getText());
        log.info("environment: {}", getSelectedEnvironment());
        log.info("gateway: {}", getSelectedGateway());
        log.info("favourite: {}", isFavourite());

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
            isValidAutomationType() &&
            isValidWhereRange() &&
            isValidDeviceEnvironment() &&
            isValidDeviceGateway();
    }

    protected boolean isValidAutomationType() {
        return isValidRequired((TextView) spinnerAutomationType.getSelectedView());
    }

    private boolean isValidWhereRange() {
        boolean isValid = Automation.isValidRangeType(
            utilityService.sanitizedText(editTextAutomationWhere),
            getSelectedAutomationType(),
            utilityService.sanitizedText(editTextAutomationBus)
        );
        if (!isValid) {
            editTextAutomationWhere.setError(validationBadValue);
            editTextAutomationBus.setError(validationBadValue);
            editTextAutomationWhere.requestFocus();
            editTextAutomationBus.requestFocus();
        }
        return isValid;
    }

    private AutomationModel parseAutomation() {
        return (automationUuid == null ? AutomationModel.addBuilder() : AutomationModel.updateBuilder(automationUuid))
            .name(utilityService.sanitizedText(editTextAutomationName))
            .where(editTextAutomationWhere.getText().toString())
            .type(getSelectedAutomationType())
            .bus(editTextAutomationBus.getText().toString())
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite())
            .build();
    }

}
