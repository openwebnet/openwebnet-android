package com.github.openwebnet.service;

import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;

import java.util.List;

import rx.Observable;

public interface DomoticService {

    void initRepository();

    Observable<Integer> addEnvironment(String name);

    Observable<List<EnvironmentModel>> findAllEnvironment();

    Observable<String> addGateway(String host, Integer port);

    Observable<List<GatewayModel>> findAllGateway();
}
