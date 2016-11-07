package com.github.openwebnet.service;

import com.github.openwebnet.model.SoundModel;

import java.util.List;

import rx.Observable;

public interface SoundService extends DomoticService<SoundModel> {

    Observable<List<SoundModel>> requestByEnvironment(Integer id);

    Observable<List<SoundModel>> requestFavourites();

    Observable<SoundModel> turnOn(SoundModel sound);

    Observable<SoundModel> turnOff(SoundModel sound);

}