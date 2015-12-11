package com.github.openwebnet.view.device;

import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.openwebnet.R;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.service.DomoticService;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class AbstractDeviceActivity extends AppCompatActivity {

    @Bind(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @Bind(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @Inject
    DomoticService domoticService;

    private SparseArray<EnvironmentModel> environmentArray;
    private SparseArray<GatewayModel> gatewayArray;

    protected void initSpinnerEnvironment() {
        domoticService.findAllEnvironment().subscribe(environments -> {
            environmentArray = initSparseArray(environments);

            List<String> environmentValues = Stream.of(environments)
                .map(environment -> environment.getName()).collect(Collectors.toList());

            initSpinnerAdapter(spinnerDeviceEnvironment, environmentValues);
        });
    }

    protected void initSpinnerGateway() {
        domoticService.findAllGateway().subscribe(gateways -> {
            gatewayArray = initSparseArray(gateways);

            List<String> gatewayValues = Stream.of(gateways)
                .map(gateway -> String.format("%s:%d", gateway.getHost(),
                    gateway.getPort())).collect(Collectors.toList());

            initSpinnerAdapter(spinnerDeviceGateway, gatewayValues);
        });
    }

    private <T> SparseArray<T> initSparseArray(List<T> items) {
        SparseArray<T> array = new SparseArray<>();
        for (int index = 0; index < items.size(); index++) {
            array.put(index, items.get(index));
        }
        return array;
    }

    private void initSpinnerAdapter(Spinner spinner, List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_dropdown_item, values);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    protected EnvironmentModel getSelectedEnvironment() {
        return environmentArray.get(spinnerDeviceEnvironment.getSelectedItemPosition());
    }

    protected GatewayModel getSelectedGateway() {
        return gatewayArray.get(spinnerDeviceGateway.getSelectedItemPosition());
    }

}
