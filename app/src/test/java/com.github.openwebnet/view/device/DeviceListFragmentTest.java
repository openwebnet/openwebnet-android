package com.github.openwebnet.view.device;

import android.os.Bundle;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.TemperatureModel;
import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.IpcamService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.TemperatureService;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

import static com.github.openwebnet.view.NavigationViewItemSelectedListener.MENU_FAVOURITE;
import static com.github.openwebnet.view.device.DeviceListFragment.ARG_ENVIRONMENT;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class DeviceListFragmentTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    EnvironmentService environmentService;

    @Inject
    IpcamService ipcamService;

    @Inject
    TemperatureService temperatureService;

    @Inject
    LightService lightService;

    @Inject
    AutomationService automationService;

    @Inject
    DeviceService deviceService;

    @Before
    public void setup() {
        ApplicationComponentTest applicationComponentTest = DaggerApplicationComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModuleTest(new DomoticModuleTest())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ApplicationComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    private void setupFragment(int environment) {
        List<EnvironmentModel> environments = Lists.newArrayList(
            newEnvironmentModel(123, "A-environment"),
            newEnvironmentModel(environment, "B-environment"),
            newEnvironmentModel(10, "C-environment"),
            newEnvironmentModel(789, "D-environment"));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));

        DeviceListFragment fragment = new DeviceListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ENVIRONMENT, environment);
        fragment.setArguments(args);
        SupportFragmentTestUtil.startFragment(fragment);
        assertNotNull("should not be null", fragment);
    }

    private EnvironmentModel newEnvironmentModel(Integer id, String name) {
        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(id);
        environment.setName(name);
        return environment;
    }

    @Test
    public void onUpdateDeviceListEvent_initCards() {
        int ENVIRONMENT = 456;
        when(ipcamService.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.<List<IpcamModel>>empty());
        when(temperatureService.requestByEnvironment(ENVIRONMENT)).thenReturn(Observable.<List<TemperatureModel>>empty());
        when(lightService.requestByEnvironment(ENVIRONMENT)).thenReturn(Observable.<List<LightModel>>empty());
        when(automationService.requestByEnvironment(ENVIRONMENT)).thenReturn(Observable.<List<AutomationModel>>empty());
        when(deviceService.requestByEnvironment(ENVIRONMENT)).thenReturn(Observable.<List<DeviceModel>>empty());

        setupFragment(ENVIRONMENT);

        verify(ipcamService).findByEnvironment(ENVIRONMENT);
        verify(temperatureService).requestByEnvironment(ENVIRONMENT);
        verify(lightService).requestByEnvironment(ENVIRONMENT);
        verify(automationService).requestByEnvironment(ENVIRONMENT);
        verify(deviceService).requestByEnvironment(ENVIRONMENT);
    }

    @Test
    public void onUpdateDeviceListEvent_initFavouriteCards() {
        when(ipcamService.findFavourites()).thenReturn(Observable.<List<IpcamModel>>empty());
        when(temperatureService.requestFavourites()).thenReturn(Observable.<List<TemperatureModel>>empty());
        when(lightService.requestFavourites()).thenReturn(Observable.<List<LightModel>>empty());
        when(automationService.requestFavourites()).thenReturn(Observable.<List<AutomationModel>>empty());
        when(deviceService.requestFavourites()).thenReturn(Observable.<List<DeviceModel>>empty());

        setupFragment(MENU_FAVOURITE);

        verify(ipcamService).findFavourites();
        verify(temperatureService).requestFavourites();
        verify(lightService).requestFavourites();
        verify(automationService).requestFavourites();
        verify(deviceService).requestFavourites();
    }

}
