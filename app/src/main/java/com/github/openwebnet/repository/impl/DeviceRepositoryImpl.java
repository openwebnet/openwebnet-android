package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.repository.DeviceRepository;

public class DeviceRepositoryImpl extends CommonRealmRepositoryImpl<DeviceModel>
        implements DeviceRepository {

    @Override
    protected Class<DeviceModel> getRealmModelClass() {
        return DeviceModel.class;
    }
}
