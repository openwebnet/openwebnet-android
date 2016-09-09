package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.ScenarioModel;
import com.github.openwebnet.repository.ScenerioRepository;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.ScenarioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class ScenarioServiceImpl implements ScenarioService {

    private static final Logger log = LoggerFactory.getLogger(ScenarioService.class);

    @Inject
    ScenerioRepository scenerioRepository;

    @Inject
    CommonService commonService;

    public ScenarioServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<String> add(ScenarioModel scenario) {
        return scenerioRepository.add(scenario);
    }

    @Override
    public Observable<Void> update(ScenarioModel scenario) {
        return scenerioRepository.update(scenario);
    }

    @Override
    public Observable<Void> delete(String uuid) {
        return scenerioRepository.delete(uuid);
    }

    @Override
    public Observable<ScenarioModel> findById(String uuid) {
        return scenerioRepository.findById(uuid);
    }

    @Override
    public Observable<List<ScenarioModel>> findByEnvironment(Integer id) {
        return scenerioRepository.findByEnvironment(id);
    }

    @Override
    public Observable<List<ScenarioModel>> findFavourites() {
        return scenerioRepository.findFavourites();
    }
}
