package com.github.openwebnet.service.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.repository.GatewayRepository;
import com.github.openwebnet.service.GatewayService;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class GatewayServiceImpl implements GatewayService {

    @Inject
    GatewayRepository gatewayRepository;

    public GatewayServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public Observable<String> add(GatewayModel gateway) {
        return gatewayRepository.add(gateway);
    }

    @Override
    public Observable<List<GatewayModel>> findAll() {
        return gatewayRepository.findAll();
    }

    @Override
    public Observable<GatewayModel> findById(String uuid) {
        return gatewayRepository.findById(uuid);
    }

}
