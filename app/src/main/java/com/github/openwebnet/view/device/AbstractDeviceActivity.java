package com.github.openwebnet.view.device;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.openwebnet.R;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;

public abstract class AbstractDeviceActivity extends AppCompatActivity {

    @Bind(R.id.spinnerDeviceEnvironment)
    Spinner spinnerDeviceEnvironment;

    @Bind(R.id.spinnerDeviceGateway)
    Spinner spinnerDeviceGateway;

    @Bind(R.id.checkBoxDeviceFavourite)
    CheckBox checkBoxDeviceFavourite;

    @BindString(R.string.validation_required)
    String validationRequired;

    @BindString(R.string.label_none)
    String labelNone;

    @Inject
    EnvironmentService environmentService;

    @Inject
    GatewayService gatewayService;

    private SparseArray<EnvironmentModel> environmentArray;
    private SparseArray<GatewayModel> gatewayArray;

    /**
     *
     */
    protected abstract void onMenuSave();

    protected void initSpinnerEnvironment() {
        environmentService.findAllEnvironment().subscribe(environments -> {
            environmentArray = initSparseArray(environments);

            List<String> environmentValues = Stream.of(environments)
                .map(environment -> environment.getName()).collect(Collectors.toList());

            initEmptyList(environmentValues);
            initSpinnerAdapter(spinnerDeviceEnvironment, environmentValues);
        });
    }

    protected void initSpinnerGateway() {
        gatewayService.findAllGateway().subscribe(gateways -> {
            gatewayArray = initSparseArray(gateways);

            List<String> gatewayValues = Stream.of(gateways)
                .map(gateway -> String.format("%s:%d", gateway.getHost(),
                    gateway.getPort())).collect(Collectors.toList());

            initEmptyList(gatewayValues);
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

    private void initEmptyList(List<String> values) {
        if (values.isEmpty()) {
            values.add(labelNone);
        }
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

    protected boolean isValidDeviceEnvironment() {
        return isValidRequired((TextView) spinnerDeviceEnvironment.getSelectedView());
    }

    protected boolean isValidDeviceGateway() {
        return isValidRequired((TextView) spinnerDeviceGateway.getSelectedView());
    }

    protected boolean isValidRequired(TextView view) {
        if (view != null && (TextUtils.isEmpty(view.getText()) || view.getText().equals(labelNone))) {
            view.setError(validationRequired);
            return false;
        }
        return true;
    }

    protected boolean isFavourite() {
        return checkBoxDeviceFavourite.isChecked();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_device_save:
                onMenuSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_save, menu);
        return true;
    }

}
