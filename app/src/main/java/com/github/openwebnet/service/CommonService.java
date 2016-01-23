package com.github.openwebnet.service;

import com.github.niqdev.openwebnet.OpenWebNet;

public interface CommonService {

    void initApplication();

    OpenWebNet findClient(String gatewayUuid);

    String getDefaultGateway();

    String getString(int id);

}
