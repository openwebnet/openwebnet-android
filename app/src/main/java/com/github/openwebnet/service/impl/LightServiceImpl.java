package com.github.openwebnet.service.impl;

import com.github.niqdev.openwebnet.OpenSession;
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
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.github.openwebnet.model.LightModel.Status.OFF;
import static com.github.openwebnet.model.LightModel.Status.ON;

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
    public Observable<List<LightModel>> findFavourites() {
        return lightRepository.findFavourites();
    }

    private Func1<String, Lighting> requestStatus = where -> Lighting.requestStatus(where);

    private Func2<OpenSession, LightModel, LightModel> handleStatus = (openSession, light) -> {
        Lighting.handleStatus(() -> light.setStatus(ON), () -> light.setStatus(OFF)).call(openSession);
        return light;
    };

    @Override
    public Observable<List<LightModel>> requestByEnvironment(Integer id) {
        return findByEnvironment(id)
            .flatMapIterable(lightModels -> lightModels)
            .flatMap(requestLight(requestStatus, handleStatus))
            .collect(() -> new ArrayList<>(),
                (lightModels, lightModel) -> lightModels.add(lightModel));
    }

    @Override
    public Observable<List<LightModel>> requestFavourites() {
        return findFavourites()
            .flatMapIterable(lightModels -> lightModels)
            .flatMap(requestLight(requestStatus, handleStatus))
            .collect(() -> new ArrayList<>(),
                (lightModels, lightModel) -> lightModels.add(lightModel));
    }

    private Func2<OpenSession, LightModel, LightModel> handleResponse(LightModel.Status status) {
        return (openSession, light) -> {
            Lighting.handleResponse(() ->
                light.setStatus(status), () -> light.setStatus(null)).call(openSession);
            return light;
        };
    }

    @Override
    public Observable<LightModel> turnOn(LightModel light) {
        Func1<String, Lighting> requestTurnOn = where -> Lighting.requestTurnOn(where);

        return Observable.just(light).flatMap(requestLight(requestTurnOn, handleResponse(ON)));
    }

    @Override
    public Observable<LightModel> turnOff(LightModel light) {
        Func1<String, Lighting> requestTurnOff = where -> Lighting.requestTurnOff(where);

        return Observable.just(light).flatMap(requestLight(requestTurnOff, handleResponse(OFF)));
    }

    private Func1<LightModel, Observable<LightModel>> requestLight(
        Func1<String, Lighting> request, Func2<OpenSession, LightModel, LightModel> handler) {
        // TODO improvement: group by gateway and for each gateway send all requests together
        return light -> commonService.findClient(light.getGatewayUuid())
            .send(request.call(light.getWhere()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(openSession -> handler.call(openSession, light))
            .onErrorReturn(throwable -> {
                log.warn("light={} | failing request={}", light.getUuid(), request.call(light.getWhere()).getValue());
                // unreadable status
                return light;
            });
    }

}
