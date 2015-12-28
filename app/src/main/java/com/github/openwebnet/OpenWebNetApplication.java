package com.github.openwebnet;

import android.app.Application;

import com.github.openwebnet.component.Injector;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author niqdev
 */
public class OpenWebNetApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        Injector.initializeApplicationComponent(this);
    }
}
