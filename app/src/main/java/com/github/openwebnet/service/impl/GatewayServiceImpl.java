package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.GatewayRepository;
import com.github.openwebnet.service.GatewayService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GatewayServiceImpl implements GatewayService {

    @Inject
    GatewayRepository gatewayRepository;

    public GatewayServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<String> add(String host, Integer port) {
        return gatewayRepository.add(GatewayModel.newGateway(host, port))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<GatewayModel>> findAll() {
        return gatewayRepository.findAll();
    }

}
