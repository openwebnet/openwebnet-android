package com.github.openwebnet.service;

import com.github.openwebnet.model.DeviceModel;

import java.util.List;

import rx.Observable;

public interface DeviceService {

    Observable<String> add(DeviceModel device);

    Observable<Void> update(DeviceModel device);

    Observable<Void> delete(String uuid);

    Observable<DeviceModel> findById(String uuid);

    Observable<List<DeviceModel>> findByEnvironment(Integer id);

    Observable<List<DeviceModel>> findFavourites();

    /* operate on background threads */

    Observable<DeviceModel> sendRequest(DeviceModel device);

}
