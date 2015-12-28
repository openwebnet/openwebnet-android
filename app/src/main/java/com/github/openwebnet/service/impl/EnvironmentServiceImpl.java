package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.repository.EnvironmentRepository;
import com.github.openwebnet.service.EnvironmentService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EnvironmentServiceImpl implements EnvironmentService {

    @Inject
    EnvironmentRepository environmentRepository;

    public EnvironmentServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<Integer> addEnvironment(String name) {
        return environmentRepository.getNextId()
            .map(id -> {
                EnvironmentModel environment = new EnvironmentModel();
                environment.setId(id);
                environment.setName(name);
                return environment;
            })
            .flatMap(environment -> environmentRepository.add(environment))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<EnvironmentModel>> findAllEnvironment() {
        return environmentRepository.findAll();
    }

}
