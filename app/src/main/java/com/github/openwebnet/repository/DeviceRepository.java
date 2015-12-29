package com.github.openwebnet.repository;

import com.github.openwebnet.model.DeviceModel;

import java.util.List;

import rx.Observable;

public interface DeviceRepository extends CommonRealmRepository<DeviceModel> {

    Observable<List<DeviceModel>> findByEnvironment(Integer id);

}
