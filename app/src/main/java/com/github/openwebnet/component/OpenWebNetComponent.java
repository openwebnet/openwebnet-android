package com.github.openwebnet.component;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.component.module.OpenWebNetModule;
import com.github.openwebnet.component.module.RepositoryModule;
import com.github.openwebnet.service.impl.DomoticServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;
import com.github.openwebnet.view.MainActivity;
import com.github.openwebnet.view.NavigationItemListener;
import com.github.openwebnet.view.device.DeviceActivity;
import com.github.openwebnet.view.device.DeviceListFragment;
import com.github.openwebnet.view.device.LightActivity;
import com.github.openwebnet.view.settings.GatewayEditTextPreference;
import com.github.openwebnet.view.settings.GatewayListPreference;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {OpenWebNetModule.class, RepositoryModule.class})
public interface OpenWebNetComponent {

    void inject(OpenWebNetApplication application);
    void inject(MainActivity activity);
    void inject(DeviceActivity activity);
    void inject(LightActivity activity);
    void inject(DeviceListFragment fragment);

    // dagger needs concrete class
    void inject(PreferenceServiceImpl preferenceService);
    void inject(DomoticServiceImpl domoticService);
    void inject(NavigationItemListener listener);

    void inject(GatewayEditTextPreference editTextPreference);
    void inject(GatewayListPreference listPreference);

}
