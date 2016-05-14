package com.github.openwebnet.component;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.component.module.ApplicationContextModule;
import com.github.openwebnet.component.module.DatabaseModule;
import com.github.openwebnet.component.module.DomoticModule;
import com.github.openwebnet.component.module.RepositoryModule;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.database.DatabaseRealmConfig;
import com.github.openwebnet.repository.impl.AutomationRepositoryImpl;
import com.github.openwebnet.repository.impl.DeviceRepositoryImpl;
import com.github.openwebnet.repository.impl.EnvironmentRepositoryImpl;
import com.github.openwebnet.repository.impl.GatewayRepositoryImpl;
import com.github.openwebnet.repository.impl.IpcamRepositoryImpl;
import com.github.openwebnet.repository.impl.LightRepositoryImpl;
import com.github.openwebnet.service.impl.AutomationServiceImpl;
import com.github.openwebnet.service.impl.CommonServiceImpl;
import com.github.openwebnet.service.impl.DeviceServiceImpl;
import com.github.openwebnet.service.impl.EnvironmentServiceImpl;
import com.github.openwebnet.service.impl.GatewayServiceImpl;
import com.github.openwebnet.service.impl.IpcamServiceImpl;
import com.github.openwebnet.service.impl.LightServiceImpl;
import com.github.openwebnet.service.impl.PreferenceServiceImpl;
import com.github.openwebnet.service.impl.UtilityServiceImpl;
import com.github.openwebnet.view.MainActivity;
import com.github.openwebnet.view.NavigationViewClickListener;
import com.github.openwebnet.view.NavigationViewItemSelectedListener;
import com.github.openwebnet.view.device.AutomationActivity;
import com.github.openwebnet.view.device.DeviceActivity;
import com.github.openwebnet.view.device.DeviceListAdapter;
import com.github.openwebnet.view.device.DeviceListFragment;
import com.github.openwebnet.view.device.IpcamActivity;
import com.github.openwebnet.view.device.IpcamStreamActivity;
import com.github.openwebnet.view.device.LightActivity;
import com.github.openwebnet.view.settings.GatewayEditTextPreference;
import com.github.openwebnet.view.settings.GatewayListPreference;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    ApplicationContextModule.class,
    DatabaseModule.class,
    RepositoryModule.class,
    DomoticModule.class
})
public interface ApplicationComponent {

    // view
    void inject(OpenWebNetApplication application);
    void inject(MainActivity activity);
    void inject(DeviceActivity activity);
    void inject(LightActivity activity);
    void inject(AutomationActivity activity);
    void inject(IpcamActivity activity);
    void inject(IpcamStreamActivity activity);
    void inject(DeviceListFragment fragment);

    void inject(NavigationViewItemSelectedListener listener);
    void inject(NavigationViewClickListener listener);
    void inject(GatewayEditTextPreference editTextPreference);
    void inject(GatewayListPreference listPreference);
    void inject(DeviceListAdapter deviceListAdapter);

    // service
    void inject(PreferenceServiceImpl preferenceService);
    void inject(CommonServiceImpl commonService);
    void inject(UtilityServiceImpl utilityService);

    // domotic service
    void inject(GatewayServiceImpl gatewayService);
    void inject(EnvironmentServiceImpl environmentService);
    void inject(DeviceServiceImpl deviceService);
    void inject(LightServiceImpl lightService);
    void inject(AutomationServiceImpl automationService);
    void inject(IpcamServiceImpl ipcamService);

    // database
    void inject(DatabaseRealm databaseRealm);
    void inject(DatabaseRealmConfig databaseRealmConfig);

    // repository
    void inject(GatewayRepositoryImpl repository);
    void inject(EnvironmentRepositoryImpl repository);
    void inject(DeviceRepositoryImpl repository);
    void inject(LightRepositoryImpl repository);
    void inject(AutomationRepositoryImpl repository);
    void inject(IpcamRepositoryImpl repository);

}
