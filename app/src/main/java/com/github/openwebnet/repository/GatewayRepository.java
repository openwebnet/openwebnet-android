package com.github.openwebnet.repository;

import com.github.openwebnet.model.GatewayModel;

import java.util.List;

import rx.Observable;

public interface GatewayRepository {

    Observable<String> add(GatewayModel gateway);

    Observable<List<GatewayModel>> findAll();

}
