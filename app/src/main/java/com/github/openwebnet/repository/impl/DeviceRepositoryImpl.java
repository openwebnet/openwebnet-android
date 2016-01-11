package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.repository.DeviceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

import static com.github.openwebnet.model.DeviceModel.FIELD_ENVIRONMENT_ID;

public class DeviceRepositoryImpl extends CommonRealmRepositoryImpl<DeviceModel>
        implements DeviceRepository {

    private static final Logger log = LoggerFactory.getLogger(DeviceRepository.class);

    @Override
    protected Class<DeviceModel> getRealmModelClass() {
        return DeviceModel.class;
    }

    @Override
    public Observable<List<DeviceModel>> findByEnvironment(Integer id) {
        return Observable.create(subscriber -> {
            try {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<DeviceModel> models = realm
                    .where(DeviceModel.class).equalTo(FIELD_ENVIRONMENT_ID, id).findAll();

                subscriber.onNext(realm.copyFromRealm(models));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_BY_ENVIRONMENT", e);
                subscriber.onError(e);
            }
        });
    }
}
