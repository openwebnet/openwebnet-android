package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.repository.DatabaseRealm;
import com.github.openwebnet.repository.DeviceRepository;

import javax.inject.Inject;

public class DeviceRepositoryImpl extends DomoticRepositoryImpl<DeviceModel>
        implements DeviceRepository {

    @Inject
    DatabaseRealm databaseRealm;

    public DeviceRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<DeviceModel> getRealmModelClass() {
        return DeviceModel.class;
    }

}
