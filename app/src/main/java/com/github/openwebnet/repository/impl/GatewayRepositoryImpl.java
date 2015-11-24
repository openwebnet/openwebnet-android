package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.GatewayRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.realm.Realm;
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
        log.debug("gateway-FIND_ALL");
        throw new UnsupportedOperationException("not implemented yet");
    }

}
