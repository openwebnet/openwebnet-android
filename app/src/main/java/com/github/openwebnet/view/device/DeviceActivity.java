package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.R;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.service.DomoticService;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

// TODO
/*
actionMessage: *CHI*COSA*DOVE##
statusMessage: *#CHI*DOVE##

Messaggi di Comando / Stato
        *CHI*COSA*DOVE##
Messaggi di Richiesta Stato
*#CHI*DOVE##
Messaggi di Richiesta/Lettura/Scrittura Grandezza
Richiesta:
*#CHI*DOVE*GRANDEZZA##
Lettura:
*#CHI*DOVE*GRANDEZZA*VALORE1*...*VALOREn##
Scrittura:
*#CHI*DOVE*#GRANDEZZA*VALORE1*...*VALOREn##
Messaggi di Acknowledge
ACK:
*#*1##
NACK:
*#*0##
*/
public class DeviceActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(DeviceActivity.class);

    @Bind(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @Bind(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @Inject
    DomoticService domoticService;

    private SparseArray<EnvironmentModel> environmentArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        OpenWebNetApplication.component(this).inject(this);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        domoticService.findAllEnvironment().subscribe(environments -> {

            environmentArray = initSparseArray(environments);

            List<String> environmentValues = Stream.of(environments)
                .map(environment -> environment.getName()).collect(Collectors.toList());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, environmentValues);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDeviceEnvironment.setAdapter(adapter);

            // TODO
            EnvironmentModel environment = environmentArray.get(spinnerDeviceEnvironment.getSelectedItemPosition());
            log.debug("environment: {}", environment);
        });
    }

    private <T> SparseArray<T> initSparseArray(List<T> items) {
        SparseArray<T> array = new SparseArray<>();
        for (int index = 0; index < items.size(); index ++) {
            array.put(index++, items.get(index));
        }
        return array;
    }
}
