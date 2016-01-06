package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.repository.DeviceRepository;
import com.github.openwebnet.service.DeviceService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class DeviceServiceImpl implements DeviceService {

    @Inject
    DeviceRepository deviceRepository;

    public DeviceServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }


    @Override
    public Observable<String> add(DeviceModel device) {
        return deviceRepository.add(device);
    }

    @Override
    public Observable<Void> update(DeviceModel device) {
        return deviceRepository.update(device);
    }

    @Override
    public Observable<Void> delete(String uuid) {
        return deviceRepository.delete(uuid);
    }

    @Override
    public Observable<DeviceModel> findById(String uuid) {
        return deviceRepository.findById(uuid);
    }

    @Override
    public Observable<List<DeviceModel>> findByEnvironment(Integer id) {
        return deviceRepository.findByEnvironment(id);
    }
}
