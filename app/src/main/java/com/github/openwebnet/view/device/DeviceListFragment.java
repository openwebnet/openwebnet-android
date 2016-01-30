package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.view.MainActivity;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;

import static com.github.openwebnet.view.NavigationViewItemSelectedListener.MENU_FAVOURITE;

public class DeviceListFragment extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(DeviceListFragment.class);
    private static final int GRID_COLUMNS = 2;

    public static final String ARG_ENVIRONMENT = "com.github.openwebnet.view.device.DeviceListFragment.ARG_ENVIRONMENT";

    @Bind(R.id.recyclerViewDeviceList)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipeRefreshLayoutDeviceList)
    SwipeRefreshLayout swipeRefreshLayoutDeviceList;

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
        View view = inflater.inflate(R.layout.fragment_list_device, container, false);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this, view);

        //mRecyclerView.setHasFixedSize(true);
        //mLayoutManager = new GridLayoutManager(getContext(), GRID_COLUMNS);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DeviceListAdapter(getActivity(), getArguments().getInt(ARG_ENVIRONMENT), domoticItems);
        mRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayoutDeviceList.setColorSchemeResources(R.color.primary, R.color.yellow, R.color.accent);
        swipeRefreshLayoutDeviceList.setOnRefreshListener(() ->
            EventBus.getDefault().post(new UpdateDeviceListEvent(getArguments().getInt(ARG_ENVIRONMENT))));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayoutDeviceList.post(() -> {
            swipeRefreshLayoutDeviceList.setRefreshing(true);
            EventBus.getDefault().post(new UpdateDeviceListEvent(getArguments().getInt(ARG_ENVIRONMENT)));
        });
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    /**
     *
     */
    public static class UpdateDeviceListEvent {

        private final int environmentId;

        public UpdateDeviceListEvent(int environmentId) {
            this.environmentId = environmentId;
        }

        public int getEnvironmentId() {
            return environmentId;
        }
    }

    @Subscribe
    public void onEvent(UpdateDeviceListEvent event) {
        initCards(event.getEnvironmentId());
    }

    public void initCards(int environmentId) {
        final boolean isFavouriteMenu = environmentId == MENU_FAVOURITE;
        showLoader(true, isFavouriteMenu);
        log.debug("initCards-isFavouriteMenu: {}", isFavouriteMenu);

        Observable<List<LightModel>> requestLights = isFavouriteMenu ? lightService.requestFavourites() :
            lightService.requestByEnvironment(environmentId);

        Observable<List<DeviceModel>> requestDevices = isFavouriteMenu ? deviceService.requestFavourites() :
            deviceService.requestByEnvironment(environmentId);

        Observable.zip(requestLights, requestDevices,
            (lights, devices) -> Lists.<DomoticModel>newArrayList(Iterables.concat(lights, devices)))
            .doOnError(throwable -> log.error("ERROR initCards", throwable))
            .subscribe(results -> {
                showLoader(false, isFavouriteMenu);
                domoticItems.clear();
                domoticItems.addAll(results);
                mAdapter.notifyDataSetChanged();
            });

    }

    private void showLoader(boolean refreshing, boolean isFavouriteMenu) {
        // mRecyclerView is null if user select another menu while is still loading
        if (mRecyclerView != null) {
            mRecyclerView.setVisibility(refreshing ? View.INVISIBLE : View.VISIBLE);
            if (!refreshing) {
                swipeRefreshLayoutDeviceList.setRefreshing(false);
            }
            if (!isFavouriteMenu) {
                // toggle visibility FloatingActionsMenu
                EventBus.getDefault().post(new MainActivity.OnChangeFabVisibilityEvent(!refreshing));
            }
        }
    }

}
