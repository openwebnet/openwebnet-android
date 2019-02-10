package com.github.openwebnet.service;

import android.support.v7.app.AppCompatActivity;

import com.github.niqdev.openwebnet.OpenWebNet;

public interface CommonService {

    void initApplication(AppCompatActivity activity);

    OpenWebNet findClient(String gatewayUuid);

    String getDefaultGateway();

}
