package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class DeviceRepositoryImpl extends CommonRealmRepositoryImpl<DeviceModel>
        implements DeviceRepository {

    @Override
    protected Class<DeviceModel> getRealmModelClass() {
        return DeviceModel.class;
    }

    @Override
    public Observable<List<DeviceModel>> findByEnvironment(Integer id) {
        // TODO
        return Observable.just(new ArrayList<>());
    }
}
