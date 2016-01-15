package com.github.openwebnet;

import android.app.Application;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.repository.DatabaseRealm;

import javax.inject.Inject;

/**
 * @author niqdev
 */
public class OpenWebNetApplication extends Application {

    @Inject
    DatabaseRealm databaseRealm;

    @Override
    public void onCreate() {
        super.onCreate();

        Injector.initializeApplicationComponent(this);
        Injector.getApplicationComponent().inject(this);
        databaseRealm.setup();
    }
}
