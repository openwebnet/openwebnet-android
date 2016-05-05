package com.github.openwebnet.service;

import com.github.openwebnet.model.DeviceModel;

import java.util.List;

import rx.Observable;

public interface DeviceService extends DomoticService<DeviceModel> {

    Observable<List<DeviceModel>> requestByEnvironment(Integer id);

    Observable<List<DeviceModel>> requestFavourites();

    Observable<DeviceModel> sendRequest(DeviceModel device);

}
