package com.github.openwebnet.service;

import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.LightModel;

import java.util.List;

import rx.Observable;

public interface DomoticService {

    void initRepository();

    Observable<Integer> addEnvironment(String name);

    Observable<List<EnvironmentModel>> findAllEnvironment();

    Observable<String> addGateway(String host, Integer port);

    Observable<List<GatewayModel>> findAllGateway();

    Observable<String> addLight(LightModel.Builder light);

    Observable<List<LightModel>> findAllLight();

    Observable<String> addDevice(DeviceModel.Builder device);

    Observable<List<DeviceModel>> findAllDevice();

    Observable<List<LightModel>> findLightByEnvironment(Integer id);

}
