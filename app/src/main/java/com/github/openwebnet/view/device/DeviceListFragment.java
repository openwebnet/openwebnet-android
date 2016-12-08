package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.ScenarioModel;
import com.github.openwebnet.model.SoundModel;
import com.github.openwebnet.model.TemperatureModel;
import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnergyService;
import com.github.openwebnet.service.IpcamService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.ScenarioService;
import com.github.openwebnet.service.SoundService;
import com.github.openwebnet.service.TemperatureService;
import com.github.openwebnet.view.MainActivity;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;

import static com.github.openwebnet.view.NavigationViewItemSelectedListener.MENU_FAVOURITE;

public class DeviceListFragment extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(DeviceListFragment.class);

    public static final String ARG_ENVIRONMENT = "com.github.openwebnet.view.device.DeviceListFragment.ARG_ENVIRONMENT";

    @BindView(R.id.recyclerViewDeviceList)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipeRefreshLayoutDeviceList)
    SwipeRefreshLayout swipeRefreshLayoutDeviceList;

    @Inject
    IpcamService ipcamService;

    @Inject
    TemperatureService temperatureService;

    @Inject
    LightService lightService;

    @Inject
    AutomationService automationService;

    @Inject
    DeviceService deviceService;

    @Inject
    ScenarioService scenarioService;

    @Inject
    EnergyService energyService;

    @Inject
    SoundService soundService;

    private Unbinder unbinder;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private List<DomoticModel> domoticItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_device, container, false);

        Injector.getApplicationComponent().inject(this);
        unbinder = ButterKnife.bind(this, view);

        mLayoutManager = new GridLayoutManager(getContext(), calculateGridColumns());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DeviceListAdapter(getActivity(), getArguments().getInt(ARG_ENVIRONMENT), domoticItems);
        mRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayoutDeviceList.setColorSchemeResources(R.color.primary, R.color.yellow_a400, R.color.accent);
        swipeRefreshLayoutDeviceList.setOnRefreshListener(() ->
            EventBus.getDefault().post(new UpdateDeviceListEvent(getArguments().getInt(ARG_ENVIRONMENT))));

        return view;
    }

    private int calculateGridColumns() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        int columns = Math.round(dpWidth / 300);
        return columns;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayoutDeviceList.post(() ->
            EventBus.getDefault().post(new UpdateDeviceListEvent(getArguments().getInt(ARG_ENVIRONMENT))));
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
        swipeRefreshLayoutDeviceList.setRefreshing(true);
        initCards(event.getEnvironmentId());
    }

    public void initCards(int environmentId) {
        final boolean isFavouriteMenu = environmentId == MENU_FAVOURITE;
        showLoader(true, isFavouriteMenu);
        log.debug("initCards-isFavouriteMenu: {}", isFavouriteMenu);

        Observable<List<IpcamModel>> findIpcams = isFavouriteMenu ? ipcamService.findFavourites() :
            ipcamService.findByEnvironment(environmentId);

        Observable<List<TemperatureModel>> requestTemperatures = isFavouriteMenu ? temperatureService.requestFavourites() :
            temperatureService.requestByEnvironment(environmentId);

        Observable<List<EnergyModel>> requestEnergies = isFavouriteMenu ? energyService.requestFavourites() :
            energyService.requestByEnvironment(environmentId);

        Observable<List<LightModel>> requestLights = isFavouriteMenu ? lightService.requestFavourites() :
            lightService.requestByEnvironment(environmentId);

        Observable<List<AutomationModel>> requestAutomations = isFavouriteMenu ? automationService.requestFavourites() :
            automationService.requestByEnvironment(environmentId);

        Observable<List<ScenarioModel>> requestScenarios = isFavouriteMenu ? scenarioService.requestFavourites() :
            scenarioService.requestByEnvironment(environmentId);

        Observable<List<SoundModel>> requestSounds = isFavouriteMenu ? soundService.requestFavourites() :
            soundService.requestByEnvironment(environmentId);

        Observable<List<DeviceModel>> requestDevices = isFavouriteMenu ? deviceService.requestFavourites() :
            deviceService.requestByEnvironment(environmentId);

        Observable.zip(findIpcams, requestTemperatures, requestEnergies, requestLights, requestAutomations, requestScenarios, requestSounds, requestDevices,
            (ipcams, temperatures, energies, lights, automations, scenarios, sounds, devices) ->
                Lists.<DomoticModel>newArrayList(Iterables.concat(ipcams, temperatures, energies, lights, automations, scenarios, sounds, devices)))
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
