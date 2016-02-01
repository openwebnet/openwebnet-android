package com.github.openwebnet.repository;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.DeviceModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PrepareForTest({Injector.class})
public class DeviceRepositoryTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    DeviceRepository deviceRepository;

    @Inject
    DatabaseRealm databaseRealm;

    @Before
    public void setupDagger() {
        ApplicationComponentTest applicationComponentTest = DaggerApplicationComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .domoticModuleTest(new DomoticModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(false))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ApplicationComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void deviceRepository_findByEnvironment() {
        Integer ENVIRONMENT = 108;

        DeviceModel device1 = DeviceModel.updateBuilder("uuid1")
            .environment(ENVIRONMENT)
            .gateway("gateway")
            .name("device1")
            .request("*#1")
            .response("*#1")
            .build();

        List<DeviceModel> devices = Arrays.asList(device1);

        when(databaseRealm.findCopyWhere(DeviceModel.class, DeviceModel.FIELD_ENVIRONMENT_ID, ENVIRONMENT)).thenReturn(devices);

        TestSubscriber<List<DeviceModel>> tester = new TestSubscriber<>();
        deviceRepository.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(databaseRealm).findCopyWhere(DeviceModel.class, DeviceModel.FIELD_ENVIRONMENT_ID, ENVIRONMENT);

        tester.assertValue(devices);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void deviceRepository_findFavourites() {
        DeviceModel device1 = DeviceModel.updateBuilder("uuid1")
            .environment(108)
            .gateway("gateway")
            .name("device1")
            .request("*#1")
            .response("*#1")
            .favourite(true)
            .build();

        List<DeviceModel> devices = Arrays.asList(device1);

        when(databaseRealm.findCopyWhere(DeviceModel.class, DeviceModel.FIELD_FAVOURITE, true)).thenReturn(devices);

        TestSubscriber<List<DeviceModel>> tester = new TestSubscriber<>();
        deviceRepository.findFavourites().subscribe(tester);

        verify(databaseRealm).findCopyWhere(DeviceModel.class, DeviceModel.FIELD_FAVOURITE, true);

        tester.assertValue(devices);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

}
