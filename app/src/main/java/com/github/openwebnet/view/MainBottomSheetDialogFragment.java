package com.github.openwebnet.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.github.openwebnet.R;
import com.github.openwebnet.view.device.AutomationActivity;
import com.github.openwebnet.view.device.DeviceActivity;
import com.github.openwebnet.view.device.IpcamActivity;
import com.github.openwebnet.view.device.LightActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.openwebnet.view.device.AbstractDeviceActivity.EXTRA_DEFAULT_ENVIRONMENT;
import static com.github.openwebnet.view.device.AbstractDeviceActivity.EXTRA_DEFAULT_GATEWAY;

public class MainBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private static final Logger log = LoggerFactory.getLogger(MainBottomSheetDialogFragment.class);

    @BindView(R.id.gridViewBottomSheet)
    GridView gridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_main, container, false);
        ButterKnife.bind(this, view);

        BottomSheetDialogAdapter mAdapter = new BottomSheetDialogAdapter(this.getContext());
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(onItemClickListener());
        return view;
    }

    private AdapterView.OnItemClickListener onItemClickListener() {
        return (parent, view, position, id) -> {
            switch ((int) id) {
                case R.id.bs_add_light:
                    actionNewIntent(LightActivity.class);
                    break;
                case R.id.bs_add_automation:
                    actionNewIntent(AutomationActivity.class);
                    break;
                case R.id.bs_add_heating:
                    log.debug("TODO heating");
                    break;
                case R.id.bs_add_device:
                    actionNewIntent(DeviceActivity.class);
                    break;
                case R.id.bs_add_ipcam:
                    actionNewIntent(IpcamActivity.class);
                    break;
                default:
                    log.warn("invalid menu");
                    break;
            }
        };
    }

    private <T> void actionNewIntent(Class<T> clazz) {
        this.dismiss();

        Intent intentNew = new Intent(this.getContext(), clazz)
            .putExtra(EXTRA_DEFAULT_ENVIRONMENT, getArguments().getInt(EXTRA_DEFAULT_ENVIRONMENT))
            .putExtra(EXTRA_DEFAULT_GATEWAY, getArguments().getString(EXTRA_DEFAULT_GATEWAY));
        startActivity(intentNew);
    }
}
