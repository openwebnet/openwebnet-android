package com.github.openwebnet.service.impl;

import com.github.niqdev.openwebnet.OpenSession;
import com.github.niqdev.openwebnet.message.Heating;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.TemperatureModel;
import com.github.openwebnet.repository.TemperatureRepository;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.TemperatureService;
import com.github.openwebnet.service.UtilityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class TemperatureServiceImpl implements TemperatureService {

    private static final Logger log = LoggerFactory.getLogger(TemperatureService.class);

    @Inject
    TemperatureRepository temperatureRepository;

    @Inject
    CommonService commonService;

    @Inject
    PreferenceService preferenceService;

    @Inject
    UtilityService utilityService;

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
        return findByEnvironment(id)
            .flatMapIterable(temperatureModels -> temperatureModels)
            .flatMap(requestTemperature())
            .collect(ArrayList::new, List::add);
    }

    @Override
    public Observable<List<TemperatureModel>> requestFavourites() {
        return findFavourites()
            .flatMapIterable(temperatureModels -> temperatureModels)
            .flatMap(requestTemperature())
            .collect(ArrayList::new, List::add);
    }

    private Func1<TemperatureModel, Observable<TemperatureModel>> requestTemperature() {

        final Func2<String, Heating.TemperatureScale, Heating> request = Heating::requestTemperature;

        final Func2<OpenSession, TemperatureModel, TemperatureModel> handler = (openSession, temperature) -> {
            Heating.handleTemperature(
                value -> temperature.setValue(String.valueOf(value)),
                () -> temperature.setValue(null))
            .call(openSession);
            return temperature;
        };

        return temperature -> commonService.findClient(temperature.getGatewayUuid())
            .send(request.call(temperature.getWhere(), preferenceService.getDefaultTemperatureScale()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(openSession -> handler.call(openSession, temperature))
            .onErrorReturn(throwable -> {
                log.warn("temperature={} | failing request={}", temperature.getUuid(), Heating.requestTemperature(temperature.getWhere()));
                // unreadable temperature
                return temperature;
            });

    }
}
