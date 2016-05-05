package com.github.openwebnet.service.impl;

import android.content.Context;
import android.net.ConnectivityManager;

import com.github.niqdev.openwebnet.OpenWebNet;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.PreferenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;

import static com.github.niqdev.openwebnet.OpenWebNet.gateway;
import static com.github.niqdev.openwebnet.OpenWebNet.newClient;

public class CommonServiceImpl implements CommonService {

    private static final Logger log = LoggerFactory.getLogger(CommonService.class);

    private HashMap<String, OpenWebNet> CLIENT_CACHE;

    @Inject
    PreferenceService preferenceService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    GatewayService gatewayService;

    @Inject
    Context mContext;

    public CommonServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public void initApplication() {
        if (preferenceService.isFirstRun()) {
            environmentService.add(getString(R.string.drawer_menu_example))
                .subscribe(id -> {
                    log.debug("initApplication with success");
                }, throwable -> {
                    log.error("initApplication", throwable);
                });
            preferenceService.initFirstRun();
        }
        CLIENT_CACHE = new HashMap<>();
    }

    // TODO clean cache always when add/update/delete gateway
    @Override
    public OpenWebNet findClient(String gatewayUuid) {
        if (!CLIENT_CACHE.containsKey(gatewayUuid)) {
            // blocking - same thread
            gatewayService.findById(gatewayUuid).subscribe(gatewayModel -> {
                OpenWebNet client = newClient(gateway(gatewayModel.getHost(), gatewayModel.getPort()));
                CLIENT_CACHE.put(gatewayUuid, client);
                log.info("new client cached: {}", gatewayUuid);
            });
        }
        return CLIENT_CACHE.get(gatewayUuid);
    }

    @Override
    public String getDefaultGateway() {
        return preferenceService.getDefaultGateway();
    }

    @Override
    public String getString(int id) {
        return mContext.getResources().getString(id);
    }

    @Override
    public boolean hasNetworkAccess() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public boolean hasInternetAccess() {
        // http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts
        Runtime runtime = Runtime.getRuntime();
        try {
            // ping google DNS
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException | InterruptedException e) {
            log.error("hasInternetAccess", e);
        }
        return false;
    }

}
