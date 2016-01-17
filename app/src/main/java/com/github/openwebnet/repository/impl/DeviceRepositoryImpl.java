package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.repository.DatabaseRealm;
import com.github.openwebnet.repository.DeviceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class DeviceRepositoryImpl extends CommonRealmRepositoryImpl<DeviceModel>
        implements DeviceRepository {

    private static final Logger log = LoggerFactory.getLogger(DeviceRepository.class);

    @Inject
    DatabaseRealm databaseRealm;

    public DeviceRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<DeviceModel> getRealmModelClass() {
        return DeviceModel.class;
    }

    @Override
    public Observable<List<DeviceModel>> findByEnvironment(Integer id) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(databaseRealm
                    .findCopyWhere(DeviceModel.class, DeviceModel.FIELD_ENVIRONMENT_ID, id));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_BY_ENVIRONMENT", e);
                subscriber.onError(e);
            }
        });
    }
}
