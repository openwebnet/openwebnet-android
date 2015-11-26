package com.github.openwebnet.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.openwebnet.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author niqdev
 */
public class DeviceListFragment extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(DeviceListFragment.class);

    public static final String ARG_ENVIRONMENT = "com.github.openwebnet.view.DeviceListFragment.ARG_ENVIRONMENT";

    @Bind(R.id.textViewDeviceList)
    TextView textViewDeviceList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.device_list_fragment, container, false);
        ButterKnife.bind(this, view);
        Integer environment = getArguments().getInt(ARG_ENVIRONMENT);
        log.debug("environment {}", environment);

        textViewDeviceList.setText("ENV: " + environment);

        return view;
    }
}
