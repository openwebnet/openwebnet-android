package com.github.openwebnet;

import com.github.openwebnet.component.DaggerOpenWebNetComponentTest;
import com.github.openwebnet.component.OpenWebNetComponent;
import com.github.openwebnet.component.OpenWebNetComponentTest;
import com.github.openwebnet.component.module.OpenWebNetModuleTest;

public class OpenWebNetApplicationTest extends OpenWebNetApplication {

    private OpenWebNetComponentTest openWebNetComponentTest;

    @Override
    public void onCreate() {
        super.onCreate();

        openWebNetComponentTest = DaggerOpenWebNetComponentTest.builder()
                .openWebNetModuleTest(new OpenWebNetModuleTest(this)).build();

        OpenWebNetApplication.component(this).inject(this);
    }

    @Override
    public OpenWebNetComponent getOpenWebNetComponent() {
        return openWebNetComponentTest;
    }
}
