package com.github.openwebnet;

import com.github.openwebnet.component.Injector;

public class OpenWebNetApplicationTest extends OpenWebNetApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.initializeApplicationComponent(this);
    }
}
