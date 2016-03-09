package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.repository.EnvironmentRepository;
import com.github.openwebnet.service.EnvironmentService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class EnvironmentServiceImpl implements EnvironmentService {

    @Inject
    EnvironmentRepository environmentRepository;

    public EnvironmentServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<Integer> add(String name) {
        return environmentRepository.getNextId()
            .map(id -> {
                EnvironmentModel environment = new EnvironmentModel();
                environment.setId(id);
                environment.setName(name);
                return environment;
            })
            .flatMap(environment -> environmentRepository.add(environment));
    }

    @Override
    public Observable<Void> update(EnvironmentModel environment) {
        return environmentRepository.update(environment);
    }

    @Override
    public Observable<List<EnvironmentModel>> findAll() {
        return environmentRepository.findAll();
    }

    @Override
    public Observable<EnvironmentModel> findById(Integer id) {
        return environmentRepository.findById(id);
    }

    @Override
    public Observable<Void> delete(Integer id) {
        return environmentRepository.delete(id);
    }
}
