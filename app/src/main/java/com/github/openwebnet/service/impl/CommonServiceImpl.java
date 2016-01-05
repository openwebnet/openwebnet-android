package com.github.openwebnet.service.impl;

import android.content.Context;

import com.github.niqdev.openwebnet.OpenWebNet;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.PreferenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    Context context;

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

    // TODO clean cache always when add/update gateway
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

    private String getString(int id) {
        return context.getResources().getString(id);
    }

}
