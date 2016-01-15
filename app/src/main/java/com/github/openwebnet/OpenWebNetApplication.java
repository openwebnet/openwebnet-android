package com.github.openwebnet;

import android.app.Application;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.repository.DatabaseHelper;

import javax.inject.Inject;

/**
 * @author niqdev
 */
public class OpenWebNetApplication extends Application {

    @Inject
    DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        Injector.initializeApplicationComponent(this);
        Injector.getApplicationComponent().inject(this);
        databaseHelper.setup();
    }
}
