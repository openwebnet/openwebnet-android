package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.niqdev.openwebnet.message.SoundSystem;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.model.SoundModel;
import com.github.openwebnet.service.SoundService;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class SoundActivity extends AbstractDeviceActivity {

    private static final Logger log = LoggerFactory.getLogger(SoundActivity.class);

    @Inject
    SoundService soundService;

    @BindView(R.id.editTextSoundName)
    EditText editTextSoundName;

    @BindView(R.id.editTextSoundWhere)
    EditText editTextSoundWhere;

    @BindView(R.id.textViewSoundPrefix)
    TextView textViewSoundPrefix;

    @BindView(R.id.textViewSoundSuffix)
    TextView textViewSoundSuffix;

    @BindView(R.id.textViewSoundInfo)
    TextView textViewSoundInfo;

    @BindView(R.id.spinnerSoundSource)
    Spinner spinnerSoundSource;

    @BindView(R.id.spinnerSoundType)
    Spinner spinnerSoundType;

    @BindString(R.string.validation_bad_value)
    String validationBadValue;

    private SparseArray<SoundSystem.Type> soundTypeArray;

    private String soundUuid;

    private boolean initSoundTypeFirstTime = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        initSpinnerEnvironment();
        initSpinnerGateway();
        initSpinnerSoundType();
        initEditSound();

        // TODO not implemented
        spinnerSoundSource.setVisibility(View.GONE);
    }

    private void initSpinnerSoundType() {
        soundTypeArray = initSparseArray(Lists.newArrayList(
            SoundSystem.Type.AMPLIFIER_GENERAL,
            SoundSystem.Type.AMPLIFIER_GROUP,
            SoundSystem.Type.AMPLIFIER_P2P,
            SoundSystem.Type.SOURCE_GENERAL,
            SoundSystem.Type.SOURCE_P2P
        ));

        List<String> lightTypeLabels = Lists.newArrayList(
            getString(R.string.sound_label_amplifier_general),
            getString(R.string.sound_label_amplifier_group),
            getString(R.string.sound_label_amplifier_point_to_point),
            getString(R.string.sound_label_source_general),
            getString(R.string.sound_label_source_point_to_point)
        );
        initSpinnerAdapter(spinnerSoundType, lightTypeLabels);

        spinnerSoundType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initSoundType(soundTypeArray.get(spinnerSoundType.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void initSoundType(SoundSystem.Type type) {
        Action1<Integer> initView = visibility -> {
            editTextSoundWhere.setVisibility(visibility);
            textViewSoundSuffix.setVisibility(visibility);
            textViewSoundInfo.setVisibility(visibility);
        };

        Action1<String> initWhereValue = value -> {
            editTextSoundWhere.setError(null);

            // initialize all subsequent calls except the first time
            if (!initSoundTypeFirstTime) {
                editTextSoundWhere.setText(value);
            }
            // from now on every time reset editTextSoundWhere
            initSoundTypeFirstTime = false;
        };

        switch (type) {
            case AMPLIFIER_GENERAL:
                textViewSoundPrefix.setText(getString(R.string.sound_value_general_amplifier_stereo));
                initView.call(View.GONE);
                textViewSoundInfo.setVisibility(View.INVISIBLE);
                initWhereValue.call(SoundSystem.Type.AMPLIFIER_GENERAL_COMMAND);
                break;
            case AMPLIFIER_GROUP:
                textViewSoundPrefix.setText(getString(R.string.sound_prefix_stereo_group));
                textViewSoundInfo.setText(getString(R.string.sound_info_amplifier_group));
                initView.call(View.VISIBLE);
                initWhereValue.call("");
                break;
            case AMPLIFIER_P2P:
                textViewSoundPrefix.setText(getString(R.string.sound_prefix_stereo));
                textViewSoundInfo.setText(getString(R.string.sound_info_amplifier_point_to_point));
                initView.call(View.VISIBLE);
                initWhereValue.call("");
                break;
            case SOURCE_GENERAL:
                textViewSoundPrefix.setText(getString(R.string.sound_value_general_source_stereo));
                initView.call(View.GONE);
                textViewSoundInfo.setVisibility(View.INVISIBLE);
                initWhereValue.call(SoundSystem.Type.SOURCE_GENERAL_COMMAND);
                break;
            case SOURCE_P2P:
                textViewSoundPrefix.setText(getString(R.string.sound_prefix_stereo));
                textViewSoundInfo.setText(getString(R.string.sound_info_source_point_to_point));
                initView.call(View.VISIBLE);
                initWhereValue.call("");
                break;
            default: {
                throw new IllegalArgumentException("invalid sound type");
            }
        }
    }

    private void initEditSound() {
        soundUuid = getIntent().getStringExtra(RealmModel.FIELD_UUID);
        log.debug("initEditSound: {}", soundUuid);
        if (soundUuid != null) {
            soundService.findById(soundUuid).subscribe(sound -> {
                spinnerSoundType.setSelection(findSelectedItem(soundTypeArray).apply(sound.getSoundSystemType()));

                editTextSoundName.setText(String.valueOf(sound.getName()));
                editTextSoundWhere.setText(String.valueOf(sound.getWhere()));

                selectEnvironment(sound.getEnvironmentId());
                selectGateway(sound.getGatewayUuid());
                setFavourite(sound.isFavourite());
            });
        } else {
            // default must be AMPLIFIER_P2P
            spinnerSoundType.setSelection(2);
        }
    }

    protected SoundSystem.Type getSelectedSoundType() {
        return soundTypeArray.get(spinnerSoundType.getSelectedItemPosition());
    }

    @Override
    protected void onMenuSave() {
        log.debug("name: {}", editTextSoundName.getText());
        log.debug("where: {}", editTextSoundWhere.getText());
        log.debug("type: {}", getSelectedSoundType());
        log.debug("environment: {}", getSelectedEnvironment());
        log.debug("gateway: {}", getSelectedGateway());
        log.debug("favourite: {}", isFavourite());

        if (isValidSound()) {
            if (soundUuid == null) {
                soundService.add(parseSound()).subscribe(uuid -> finish());
            } else {
                soundService.update(parseSound())
                    .doOnCompleted(this::finish)
                    .subscribe();
            }
        }
    }

    private boolean isValidSound() {
        return isValidRequired(editTextSoundName) &&
            isValidRequired(editTextSoundWhere) &&
            isValidSoundType() &&
            isValidWhereRange() &&
            isValidDeviceEnvironment() &&
            isValidDeviceGateway();
    }

    protected boolean isValidSoundType() {
        return isValidRequired((TextView) spinnerSoundType.getSelectedView());
    }

    private boolean isValidWhereRange() {
        boolean isValid = SoundSystem.Type.isValid(
            getSelectedSoundType(),
            utilityService.sanitizedText(editTextSoundWhere)
        );
        if (!isValid) {
            editTextSoundWhere.setError(validationBadValue);
            editTextSoundWhere.requestFocus();
        }
        return isValid;
    }

    private SoundModel parseSound() {
        return (soundUuid == null ? SoundModel.addBuilder() : SoundModel.updateBuilder(soundUuid))
            .name(utilityService.sanitizedText(editTextSoundName))
            .where(editTextSoundWhere.getText().toString())
            // default
            .source(SoundSystem.Source.STEREO_CHANNEL)
            .type(getSelectedSoundType())
            .environment(getSelectedEnvironment().getId())
            .gateway(getSelectedGateway().getUuid())
            .favourite(isFavourite())
            .build();
    }
}
