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
import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.LightService;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

// http://stackoverflow.com/questions/26666143/recyclerview-gridlayoutmanager-how-to-auto-detect-span-count
public class DeviceListFragment extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(DeviceListFragment.class);
    private static final int GRID_COLUMNS = 2;

    public static final String ARG_ENVIRONMENT = "com.github.openwebnet.view.device.DeviceListFragment.ARG_ENVIRONMENT";

    @Bind(R.id.recyclerViewDeviceList)
    RecyclerView mRecyclerView;

    @Inject
    LightService lightService;
    @Inject
    DeviceService deviceService;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private List<DomoticModel> domoticItems = new ArrayList<>();

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
        mAdapter = new DeviceListAdapter(domoticItems);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initCards();
    }

    // TODO init status
    private void initCards() {
        Integer environment = getArguments().getInt(ARG_ENVIRONMENT);
        Observable.zip(
            lightService.requestByEnvironment(environment),
            deviceService.findByEnvironment(environment),
            (lights, devices) -> Lists.<DomoticModel>newArrayList(Iterables.concat(lights, devices)))
            .doOnError(throwable -> log.error("ERROR initCards", throwable))
            .subscribe(results -> {
                domoticItems.clear();
                domoticItems.addAll(results);
                mAdapter.notifyDataSetChanged();
                log.debug("initCards environment={} domoticItems={}", environment, domoticItems);
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
