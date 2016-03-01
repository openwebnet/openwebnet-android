package com.github.openwebnet.repository;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.LightModel;

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
public class AutomationRepositoryTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    AutomationRepository automationRepository;

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
    public void automationRepository_findByEnvironment() {
        Integer ENVIRONMENT = 108;

        AutomationModel automation1 = AutomationModel.updateBuilder("uuid1")
            .environment(ENVIRONMENT)
            .gateway("gateway")
            .name("automation1")
            .where("10")
            .build();

        AutomationModel automation2 = AutomationModel.updateBuilder("uuid2")
            .environment(ENVIRONMENT)
            .gateway("gateway")
            .name("automation2")
            .where("11")
            .build();

        List<AutomationModel> automationModels = Arrays.asList(automation1, automation2);

        when(databaseRealm.findCopyWhere(AutomationModel.class, DomoticModel.FIELD_ENVIRONMENT_ID,
            ENVIRONMENT, DomoticModel.FIELD_NAME)).thenReturn(automationModels);

        TestSubscriber<List<AutomationModel>> tester = new TestSubscriber<>();
        automationRepository.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(databaseRealm).findCopyWhere(AutomationModel.class, DomoticModel.FIELD_ENVIRONMENT_ID,
            ENVIRONMENT, DomoticModel.FIELD_NAME);

        tester.assertValue(automationModels);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void automationRepository_findFavourites() {
        AutomationModel automation = AutomationModel.updateBuilder("uuid2")
            .environment(108)
            .gateway("gateway")
            .name("automation2")
            .where("11")
            .favourite(true)
            .build();

        List<AutomationModel> automationModels = Arrays.asList(automation);

        when(databaseRealm.findCopyWhere(AutomationModel.class, DomoticModel.FIELD_FAVOURITE,
            true, DomoticModel.FIELD_NAME)).thenReturn(automationModels);

        TestSubscriber<List<AutomationModel>> tester = new TestSubscriber<>();
        automationRepository.findFavourites().subscribe(tester);

        verify(databaseRealm).findCopyWhere(AutomationModel.class, DomoticModel.FIELD_FAVOURITE,
            true, DomoticModel.FIELD_NAME);

        tester.assertValue(automationModels);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

}
