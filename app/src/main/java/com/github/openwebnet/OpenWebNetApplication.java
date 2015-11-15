package com.github.openwebnet;

import android.app.Application;

import com.github.openwebnet.component.DaggerOpenWebNetComponent;
import com.github.openwebnet.component.OpenWebNetComponent;
import com.github.openwebnet.component.module.OpenWebNetModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import lombok.Getter;

/**
 * @author niqdev
 */
public class OpenWebNetApplication extends Application {

    @Getter
    private OpenWebNetComponent openWebNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        openWebNetComponent = DaggerOpenWebNetComponent.builder()
            .openWebNetModule(new OpenWebNetModule()).build();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

}
