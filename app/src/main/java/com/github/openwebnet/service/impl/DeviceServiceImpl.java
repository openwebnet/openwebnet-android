package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.repository.DeviceRepository;
import com.github.openwebnet.service.DeviceService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

// TODO
public class DeviceServiceImpl implements DeviceService {

    @Inject
    DeviceRepository deviceRepository;

    public DeviceServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<String> add(DeviceModel.Builder device) {
        return deviceRepository.add(device.build());
    }

    @Override
    public Observable<List<DeviceModel>> findAll() {
        return deviceRepository.findAll();
    }

    @Override
    public Observable<List<DeviceModel>> findByEnvironment(Integer id) {
        return deviceRepository.findByEnvironment(id);
    }

}
