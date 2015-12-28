package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.DeviceModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceListFragment extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(DeviceListFragment.class);
    private static final int GRID_COLUMNS = 2;

    public static final String ARG_ENVIRONMENT = "com.github.openwebnet.view.device.DeviceListFragment.ARG_ENVIRONMENT";

    @Bind(R.id.recyclerViewDeviceList)
    RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_list_fragment, container, false);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this, view);

        //mRecyclerView.setHasFixedSize(true);

        //mLayoutManager = new GridLayoutManager(getContext(), GRID_COLUMNS);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // TODO
        Integer environment = getArguments().getInt(ARG_ENVIRONMENT);

//        domoticService.findLightByEnvironment(environment)
//            .subscribe(lightModels -> );

        log.debug("environment {}", environment);

        List<DeviceModel> devices = new ArrayList<>();
        /*
        devices.add(DeviceModel.newBuilder().environment(100).gateway("uuid0").name("name0").raw("command0").build());
        devices.add(DeviceModel.newBuilder().environment(101).gateway("uuid1").name("name1").raw("command1").build());
        devices.add(DeviceModel.newBuilder().environment(102).gateway("uuid2").name("name2").raw("command2").build());
        devices.add(DeviceModel.newBuilder().environment(103).gateway("uuid3").name("name3").raw("command3").build());
        devices.add(DeviceModel.newBuilder().environment(104).gateway("uuid4").name("name4").raw("command4").build());
        devices.add(DeviceModel.newBuilder().environment(105).gateway("uuid5").name("name5").raw("command5").build());
        */

        //mAdapter = new DeviceListAdapter(devices);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
