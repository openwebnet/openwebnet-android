package com.github.openwebnet.service.impl;

import android.content.Context;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.PreferenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class CommonServiceImpl implements CommonService {

    private static final Logger log = LoggerFactory.getLogger(CommonService.class);

    @Inject
    PreferenceService preferenceService;

    @Inject
    EnvironmentService environmentService;

    @Inject
    Context context;

    public CommonServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public void initRepository() {
        if (preferenceService.isFirstRun()) {
            environmentService.addEnvironment(getString(R.string.drawer_menu_example))
                .subscribe(id -> {
                    log.debug("initRepository with success");
                }, throwable -> {
                    log.error("initRepository", throwable);
                });
            preferenceService.initFirstRun();
        }
    }

    private String getString(int id) {
        return context.getResources().getString(id);
    }

}
