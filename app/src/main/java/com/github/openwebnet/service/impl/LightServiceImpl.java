package com.github.openwebnet.service.impl;

import com.github.niqdev.openwebnet.OpenWebNet;
import com.github.niqdev.openwebnet.message.Lighting;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.repository.LightRepository;
import com.github.openwebnet.service.LightService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
        // TODO group by gateway and for each gateway create a new client

        // findClientByGateway
        OpenWebNet client = OpenWebNet.newClient(OpenWebNet.gateway("10.0.2.2", 20000));

        return findByEnvironment(id)
            .flatMapIterable(lightModels -> lightModels)
            .flatMap(lightModel -> client.send(Lighting.requestStatus(lightModel.getWhere()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Lighting.handleStatus(
                    () -> lightModel.setStatus(LightModel.Status.ON),
                    () -> lightModel.setStatus(LightModel.Status.OFF)))
                .map(openSession -> Observable.just(lightModel)))
            .collect(() -> new ArrayList<>(), (lightModels, lightModelObservable) ->
                lightModelObservable.subscribe(lightModel -> lightModels.add(lightModel)));
    }

}
