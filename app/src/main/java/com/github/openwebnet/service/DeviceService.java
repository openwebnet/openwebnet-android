package com.github.openwebnet.service;

import com.github.openwebnet.model.DeviceModel;

import java.util.List;

import rx.Observable;

public interface DeviceService {

    Observable<String> addDevice(DeviceModel.Builder device);

    Observable<List<DeviceModel>> findAllDevice();

}
