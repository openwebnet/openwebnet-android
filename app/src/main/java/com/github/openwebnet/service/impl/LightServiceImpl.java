package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.repository.LightRepository;
import com.github.openwebnet.service.LightService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LightServiceImpl implements LightService {

    @Inject
    LightRepository lightRepository;

    public LightServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<String> add(LightModel.Builder light) {
        return lightRepository.add(light.build())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<LightModel>> findAll() {
        return lightRepository.findAll();
    }

    @Override
    public Observable<List<LightModel>> findByEnvironment(Integer id) {
        return lightRepository.findByEnvironment(id);
    }

    @Override
    public Observable<Void> update(LightModel light) {
        return lightRepository.update(light);
    }

}
