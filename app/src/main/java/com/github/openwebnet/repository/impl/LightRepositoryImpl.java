package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.repository.LightRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

import static com.github.openwebnet.model.LightModel.FIELD_ENVIRONMENT_ID;

public class LightRepositoryImpl extends CommonRealmRepositoryImpl<LightModel>
        implements LightRepository {

    private static final Logger log = LoggerFactory.getLogger(LightRepositoryImpl.class);

    @Override
    protected Class<LightModel> getRealmModelClass() {
        return LightModel.class;
    }

    @Override
    public Observable<List<LightModel>> findByEnvironment(Integer id) {
        return Observable.create(subscriber -> {
            try {
                RealmResults<LightModel> models = Realm.getDefaultInstance()
                    .where(LightModel.class).equalTo(FIELD_ENVIRONMENT_ID, id).findAll();
                subscriber.onNext(models);
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_BY_ENVIRONMENT", e);
                subscriber.onError(e);
            }
        });
    }
}
