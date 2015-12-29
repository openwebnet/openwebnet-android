package com.github.openwebnet.service;

import com.github.openwebnet.model.DeviceModel;

import java.util.List;

import rx.Observable;

public interface DeviceService {

    Observable<String> add(DeviceModel.Builder device);

    Observable<List<DeviceModel>> findAll();

    Observable<List<DeviceModel>> findByEnvironment(Integer id);

}
