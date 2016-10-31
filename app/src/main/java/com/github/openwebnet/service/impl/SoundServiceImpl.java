package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.SoundModel;
import com.github.openwebnet.repository.SoundRepository;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.SoundService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

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
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Observable<List<SoundModel>> requestFavourites() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Observable<SoundModel> turnOn(SoundModel sound) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Observable<SoundModel> turnOff(SoundModel sound) {
        throw new UnsupportedOperationException("TODO");
    }

}
