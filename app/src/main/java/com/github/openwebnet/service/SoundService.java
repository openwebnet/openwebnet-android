package com.github.openwebnet.service;

import com.github.openwebnet.model.SoundModel;

import java.util.List;

import rx.Observable;

public interface SoundService extends DomoticService<SoundModel> {

    Observable<List<SoundModel>> requestByEnvironment(Integer id);

    Observable<List<SoundModel>> requestFavourites();

    Observable<SoundModel> turnOn(SoundModel sound);

    Observable<SoundModel> turnOff(SoundModel sound);

    Observable<SoundModel> volumeUp(SoundModel sound);

    Observable<SoundModel> volumeDown(SoundModel sound);

    Observable<SoundModel> stationUp(SoundModel sound);

    Observable<SoundModel> stationDown(SoundModel sound);

}