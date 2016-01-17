package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.repository.DatabaseRealm;
import com.github.openwebnet.repository.LightRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class LightRepositoryImpl extends CommonRealmRepositoryImpl<LightModel>
        implements LightRepository {

    private static final Logger log = LoggerFactory.getLogger(LightRepository.class);

    @Inject
    DatabaseRealm databaseRealm;

    public LightRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    protected Class<LightModel> getRealmModelClass() {
        return LightModel.class;
    }

    @Override
    public Observable<List<LightModel>> findByEnvironment(Integer id) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(databaseRealm.findCopyWhere(LightModel.class, LightModel.FIELD_ENVIRONMENT_ID, id));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("FIND_BY_ENVIRONMENT", e);
                subscriber.onError(e);
            }
        });
    }
}
