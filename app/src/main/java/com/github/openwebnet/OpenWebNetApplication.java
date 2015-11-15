package com.github.openwebnet;

import android.app.Application;

import com.github.openwebnet.component.OpenWebNetComponent;
import com.github.openwebnet.component.module.OpenWebNetModule;

/**
 * @author niqdev
 */
public class OpenWebNetApplication extends Application {

    private OpenWebNetComponent mOpenWebNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mOpenWebNetComponent = DaggerOpenWebNetComponent.builder()
            .openWebNetModule(new OpenWebNetModule()).build();
    }

    public OpenWebNetComponent getOpenWebNetComponent() {
        return mOpenWebNetComponent;
    }
}
