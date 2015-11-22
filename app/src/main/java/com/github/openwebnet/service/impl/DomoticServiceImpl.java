package com.github.openwebnet.service.impl;

import android.app.Application;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.R;
import com.github.openwebnet.model.DomoticEnvironment;
import com.github.openwebnet.repository.DomoticEnvironmentRepository;
import com.github.openwebnet.service.DomoticService;
import com.github.openwebnet.service.PreferenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author niqdev
 */
public class DomoticServiceImpl implements DomoticService {

    private static final Logger log = LoggerFactory.getLogger(DomoticServiceImpl.class);

    private final Application application;

    @Inject
    public DomoticServiceImpl(Application application) {
        OpenWebNetApplication.component(application).inject(this);
        this.application = application;
    }

    @Inject
    PreferenceService preferenceService;

    @Inject
    DomoticEnvironmentRepository environmentRepository;

    @Override
    public void initRepository() {
        if (preferenceService.isFirstRun()) {
            // TODO icon + refresh
            addEnvironment(getString(R.string.drawer_menu_example), "HOME_DESCRIPTION")
                .subscribe(id -> {
                    log.debug("initRepository with success");
                }, throwable -> {
                    log.error("initRepository", throwable);
                });
            preferenceService.initFirstRun();
        }
    }

    @Override
    public Observable<Integer> addEnvironment(String name, String description) {
        return environmentRepository.getNextId()
            .map(id -> {
                return DomoticEnvironment.newBuilder(id, name).description(description).build();
            })
            .flatMap(environment -> {
                return environmentRepository.add(environment);
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<DomoticEnvironment>> findAllEnvironment() {
        return environmentRepository.findAll();
    }

    private String getString(int id) {
        return application.getResources().getString(id);
    }
}
