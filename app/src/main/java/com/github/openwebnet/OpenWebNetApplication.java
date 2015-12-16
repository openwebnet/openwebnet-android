package com.github.openwebnet;

import android.app.Application;
import android.content.Context;

import com.github.openwebnet.component.DaggerOpenWebNetComponent;
import com.github.openwebnet.component.OpenWebNetComponent;
import com.github.openwebnet.component.module.OpenWebNetModule;
import com.github.openwebnet.component.module.RepositoryModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author niqdev
 */
public class OpenWebNetApplication extends Application {

    private OpenWebNetComponent openWebNetComponent;

    public static OpenWebNetComponent component(Context context) {
        return ((OpenWebNetApplication) context.getApplicationContext()).getOpenWebNetComponent();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        openWebNetComponent = DaggerOpenWebNetComponent.builder()
            .openWebNetModule(new OpenWebNetModule(this))
            .repositoryModule(new RepositoryModule())
            .build();

        OpenWebNetApplication.component(this).inject(this);
    }

    public OpenWebNetComponent getOpenWebNetComponent() {
        return openWebNetComponent;
    }
}
