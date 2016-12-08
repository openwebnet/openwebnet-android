package com.github.openwebnet.service.impl;

import com.github.niqdev.openwebnet.OpenSession;
import com.github.niqdev.openwebnet.message.SoundSystem;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.SoundModel;
import com.github.openwebnet.repository.SoundRepository;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.SoundService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

import static com.github.openwebnet.model.SoundModel.Status.OFF;
import static com.github.openwebnet.model.SoundModel.Status.ON;

public class SoundServiceImpl implements SoundService {

    private static final Logger log = LoggerFactory.getLogger(SoundService.class);

    @Inject
    SoundRepository soundRepository;

    @Inject
    CommonService commonService;

    public SoundServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<String> add(SoundModel sound) {
        return soundRepository.add(sound);
    }

    @Override
    public Observable<Void> update(SoundModel sound) {
        return soundRepository.update(sound);
    }

    @Override
    public Observable<Void> delete(String uuid) {
        return soundRepository.delete(uuid);
    }

    @Override
    public Observable<SoundModel> findById(String uuid) {
        return soundRepository.findById(uuid);
    }

    @Override
    public Observable<List<SoundModel>> findByEnvironment(Integer id) {
        return soundRepository.findByEnvironment(id);
    }

    @Override
    public Observable<List<SoundModel>> findFavourites() {
        return soundRepository.findFavourites();
    }

    @Override
    public Observable<List<SoundModel>> requestByEnvironment(Integer id) {
        return findByEnvironment(id)
            .flatMapIterable(soundModels -> soundModels)
            .flatMap(requestSound(SoundSystem::requestStatus, handleStatus))
            .collect(ArrayList::new, List::add);
    }

    @Override
    public Observable<List<SoundModel>> requestFavourites() {
        return findFavourites()
            .flatMapIterable(soundModels -> soundModels)
            .flatMap(requestSound(SoundSystem::requestStatus, handleStatus))
            .collect(ArrayList::new, List::add);
    }

    @Override
    public Observable<SoundModel> turnOn(SoundModel sound) {
        return Observable.just(sound).flatMap(requestSound(SoundSystem::requestTurnOn, handleResponse(ON)));
    }

    @Override
    public Observable<SoundModel> turnOff(SoundModel sound) {
        return Observable.just(sound).flatMap(requestSound(SoundSystem::requestTurnOff, handleResponse(OFF)));
    }

    @Override
    public Observable<SoundModel> volumeUp(SoundModel sound) {
        // use current status
        return Observable.just(sound)
            .flatMap(requestSound(SoundSystem::requestVolumeUp, handleResponse(sound.getStatus())));
    }

    @Override
    public Observable<SoundModel> volumeDown(SoundModel sound) {
        // use current status
        return Observable.just(sound)
            .flatMap(requestSound(SoundSystem::requestVolumeDown, handleResponse(sound.getStatus())));
    }

    @Override
    public Observable<SoundModel> stationUp(SoundModel sound) {
        // use current status
        return Observable.just(sound)
            .flatMap(requestSound(SoundSystem::requestStationUp, handleResponse(sound.getStatus())));
    }

    @Override
    public Observable<SoundModel> stationDown(SoundModel sound) {
        // use current status
        return Observable.just(sound)
            .flatMap(requestSound(SoundSystem::requestStationDown, handleResponse(sound.getStatus())));
    }

    private Func2<OpenSession, SoundModel, SoundModel> handleStatus = (openSession, sound) -> {
        SoundSystem.handleStatus(() -> sound.setStatus(ON), () -> sound.setStatus(OFF)).call(openSession);
        return sound;
    };

    private Func2<OpenSession, SoundModel, SoundModel> handleResponse(SoundModel.Status status) {
        return (openSession, sound) -> {
            SoundSystem.handleResponse(() -> sound.setStatus(status), () -> sound.setStatus(null)).call(openSession);
            return sound;
        };
    }

    private Func1<SoundModel, Observable<SoundModel>> requestSound(
        Func2<String, SoundSystem.Type, SoundSystem> request, Func2<OpenSession, SoundModel, SoundModel> handler) {

        return sound -> commonService.findClient(sound.getGatewayUuid())
            .send(request.call(sound.getWhere(), sound.getSoundSystemType()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(openSession -> handler.call(openSession, sound))
            .onErrorReturn(throwable -> {
                log.warn("sound={} | failing request={}", sound.getUuid(),
                    request.call(sound.getWhere(), sound.getSoundSystemType()).getValue());
                // unreadable status
                return sound;
            });
    }

    // with source: turnOn/turnOff
    private Func1<SoundModel, Observable<SoundModel>> requestSound(
        Func3<String, SoundSystem.Type, SoundSystem.Source, SoundSystem> request,
        Func2<OpenSession, SoundModel, SoundModel> handler) {

        return sound -> commonService.findClient(sound.getGatewayUuid())
            .send(request.call(sound.getWhere(), sound.getSoundSystemType(), sound.getSoundSystemSource()))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map(openSession -> handler.call(openSession, sound))
            .onErrorReturn(throwable -> {
                log.warn("sound={} | failing request={}", sound.getUuid(),
                    request.call(sound.getWhere(), sound.getSoundSystemType(), sound.getSoundSystemSource()).getValue());
                // unreadable status
                return sound;
            });
    }

}