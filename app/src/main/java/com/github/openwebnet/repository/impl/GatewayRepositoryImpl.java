package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.GatewayRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

public class GatewayRepositoryImpl implements GatewayRepository {

    private static final Logger log = LoggerFactory.getLogger(GatewayRepository.class);

    @Override
    public Observable<String> add(GatewayModel gateway) {
        return Observable.create(subscriber -> {
            try {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealm(gateway);
                realm.commitTransaction();

                subscriber.onNext(gateway.getUuid());
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("gateway-ADD", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<List<GatewayModel>> findAll() {
        return Observable.create(subscriber -> {
            try {
                RealmResults<GatewayModel> gateways =
                    Realm.getDefaultInstance().where(GatewayModel.class).findAll();
                subscriber.onNext(gateways);
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("gateway-FIND_ALL", e);
                subscriber.onError(e);
            }
        });
    }

}
