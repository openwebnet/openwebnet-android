package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.TemperatureModel;
import com.github.openwebnet.repository.TemperatureRepository;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.TemperatureService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class TemperatureServiceImpl implements TemperatureService {

    private static final Logger log = LoggerFactory.getLogger(TemperatureService.class);

    @Inject
    TemperatureRepository temperatureRepository;

    @Inject
    CommonService commonService;

    public TemperatureServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }


    @Override
    public Observable<String> add(TemperatureModel temperature) {
        return temperatureRepository.add(temperature);
    }

    @Override
    public Observable<Void> update(TemperatureModel temperature) {
        return temperatureRepository.update(temperature);
    }

    @Override
    public Observable<Void> delete(String uuid) {
        return temperatureRepository.delete(uuid);
    }

    @Override
    public Observable<TemperatureModel> findById(String uuid) {
        return temperatureRepository.findById(uuid);
    }

    @Override
    public Observable<List<TemperatureModel>> findByEnvironment(Integer id) {
        return temperatureRepository.findByEnvironment(id);
    }

    @Override
    public Observable<List<TemperatureModel>> findFavourites() {
        return temperatureRepository.findFavourites();
    }

    @Override
    public Observable<List<TemperatureModel>> requestByEnvironment(Integer id) {
        return null;
    }

    @Override
    public Observable<List<TemperatureModel>> requestFavourites() {
        return null;
    }
}
