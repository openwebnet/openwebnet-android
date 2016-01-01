package com.github.openwebnet.service;

import com.github.niqdev.openwebnet.OpenWebNet;

public interface CommonService {

    void initRepository();

    OpenWebNet findClient(String gatewayUuid);

}
