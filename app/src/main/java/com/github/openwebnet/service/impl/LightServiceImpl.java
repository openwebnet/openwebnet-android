package com.github.openwebnet.service.impl;

import com.github.niqdev.openwebnet.message.Lighting;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.repository.LightRepository;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.LightService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LightServiceImpl implements LightService {

    private static final Logger log = LoggerFactory.getLogger(LightService.class);

    @Inject
    LightRepository lightRepository;

    @Inject
    CommonService commonService;

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
    public Observable<Void> update(LightModel light) {
        return lightRepository.update(light)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<LightModel>> findAll() {
        return lightRepository.findAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<LightModel>> findByEnvironment(Integer id) {
        return lightRepository.findByEnvironment(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<LightModel>> requestByEnvironment(Integer id) {
        // TODO improvement: group by gateway and for each gateway send all requests together
        return findByEnvironment(id)
            .flatMapIterable(lightModels -> lightModels)
            .flatMap(light -> commonService.findClient(light.getGatewayUuid())
                .send(Lighting.requestStatus(light.getWhere()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Lighting.handleStatus(
                    () -> light.setStatus(LightModel.Status.ON),
                    () -> light.setStatus(LightModel.Status.OFF)))
                .map(openSession -> Observable.just(light))
                .onErrorReturn(throwable -> {
                    log.warn("fail to request status for light={}", light.getUuid());
                    return Observable.just(light);
                }))
            .collect(() -> new ArrayList<>(), (lightModels, lightModelObservable) ->
                lightModelObservable.subscribe(lightModel -> lightModels.add(lightModel)));
    }

}
