package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.ScenarioModel;
import com.github.openwebnet.model.SoundModel;
import com.github.openwebnet.model.TemperatureModel;
import com.github.openwebnet.repository.EnvironmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import rx.Observable;

import static com.google.common.base.Preconditions.checkState;

public class EnvironmentRepositoryImpl implements EnvironmentRepository {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentRepository.class);
    private static final Integer INITIAL_SEQ = 100;

    @Inject
    DatabaseRealm databaseRealm;

    public EnvironmentRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<Integer> getNextId() {
        return Observable.create(subscriber -> {
            try {
                Number maxId = databaseRealm.findMax(EnvironmentModel.class, EnvironmentModel.FIELD_ID);
                Integer lastId = maxId == null ? INITIAL_SEQ :
                    new AtomicInteger(maxId.intValue()).incrementAndGet();

                subscriber.onNext(lastId);
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-NEXT_ID", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Integer> add(EnvironmentModel environment) {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(databaseRealm.add(environment).getId());
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-ADD", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Void> update(EnvironmentModel environment) {
        return Observable.create(subscriber -> {
            try {
                databaseRealm.update(environment);
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-UPDATE", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<EnvironmentModel> findById(Integer id) {
        return Observable.create(subscriber -> {
            try {
                List<EnvironmentModel> models = databaseRealm
                    .findCopyWhere(EnvironmentModel.class, EnvironmentModel.FIELD_ID, id, null);
                checkState(models.size() == 1, "primary key violation: invalid id");
                subscriber.onNext(models.get(0));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-FIND_BY_ID", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<List<EnvironmentModel>> findAll() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(databaseRealm
                    .findSortedAscending(EnvironmentModel.class, EnvironmentModel.FIELD_NAME));
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-FIND_ALL", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Void> delete(Integer id) {
        return Observable.create(subscriber -> {
            try {
                // TODO use reflections? https://github.com/openwebnet/openwebnet-android/pull/29
                databaseRealm.delete(LightModel.class, DomoticModel.FIELD_ENVIRONMENT_ID, id);
                databaseRealm.delete(AutomationModel.class, DomoticModel.FIELD_ENVIRONMENT_ID, id);
                databaseRealm.delete(DeviceModel.class, DomoticModel.FIELD_ENVIRONMENT_ID, id);
                databaseRealm.delete(IpcamModel.class, DomoticModel.FIELD_ENVIRONMENT_ID, id);
                databaseRealm.delete(TemperatureModel.class, DomoticModel.FIELD_ENVIRONMENT_ID, id);
                databaseRealm.delete(ScenarioModel.class, DomoticModel.FIELD_ENVIRONMENT_ID, id);
                databaseRealm.delete(EnergyModel.class, DomoticModel.FIELD_ENVIRONMENT_ID, id);
                databaseRealm.delete(SoundModel.class, DomoticModel.FIELD_ENVIRONMENT_ID, id);

                databaseRealm.delete(EnvironmentModel.class, EnvironmentModel.FIELD_ID, id);
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("environment-DELETE", e);
                subscriber.onError(e);
            }
        });
    }
}
