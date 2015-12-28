package com.github.openwebnet.service;

import com.github.openwebnet.model.GatewayModel;

import java.util.List;

import rx.Observable;

public interface GatewayService {

    Observable<String> addGateway(String host, Integer port);

    Observable<List<GatewayModel>> findAllGateway();

}
