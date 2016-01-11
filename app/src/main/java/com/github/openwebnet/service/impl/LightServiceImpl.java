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
    public Observable<String> add(LightModel light) {
        return lightRepository.add(light);
    }

    @Override
    public Observable<Void> update(LightModel light) {
        return lightRepository.update(light);
    }

    @Override
    public Observable<Void> delete(String uuid) {
        return lightRepository.delete(uuid);
    }

    @Override
    public Observable<LightModel> findById(String uuid) {
        return lightRepository.findById(uuid);
    }

    @Override
    public Observable<List<LightModel>> findByEnvironment(Integer id) {
        return lightRepository.findByEnvironment(id);
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
                    // unreadable status
                    return Observable.just(light);
                }))
            .collect(() -> new ArrayList<>(), (lightModels, lightModelObservable) ->
                lightModelObservable.subscribe(lightModel -> lightModels.add(lightModel)));
    }

    @Override
    public Observable<LightModel> turnOn(LightModel light) {
        return commonService.findClient(light.getGatewayUuid())
            .send(Lighting.requestTurnOn(light.getWhere()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(Lighting.handleResponse(
                () -> light.setStatus(LightModel.Status.ON),
                () -> light.setStatus(null)))
            .map(openSession -> light)
            .onErrorReturn(throwable -> {
                log.error("fail to turnOn light={}", light.getUuid());
                light.setStatus(null);
                return light;
            });
    }

    @Override
    public Observable<LightModel> turnOff(LightModel light) {
        return commonService.findClient(light.getGatewayUuid())
            .send(Lighting.requestTurnOff(light.getWhere()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(Lighting.handleResponse(
                () -> light.setStatus(LightModel.Status.OFF),
                () -> light.setStatus(null)))
            .map(openSession -> light)
            .onErrorReturn(throwable -> {
                log.error("fail to turnOff light={}", light.getUuid());
                light.setStatus(null);
                return light;
            });
    }

}
