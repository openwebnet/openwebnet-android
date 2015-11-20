package com.github.openwebnet.service.impl;

import android.app.Application;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.model.DomoticEnvironment;
import com.github.openwebnet.repository.DomoticEnvironmentRepository;
import com.github.openwebnet.service.DomoticService;
import com.github.openwebnet.service.PreferenceService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author niqdev
 */
public class DomoticServiceImpl implements DomoticService {

    @Inject
    public DomoticServiceImpl(Application application) {
        OpenWebNetApplication.component(application).inject(this);
    }

    @Inject
    PreferenceService preferenceService;

    @Inject
    DomoticEnvironmentRepository environmentRepository;

    @Override
    public void initRepository() {
        if (preferenceService.isFirstRun()) {
            // TODO
            addEnvironment("HOME_NAME", "HOME_DESCRIPTION");
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
}
