package com.github.openwebnet.repository;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.database.DatabaseRealm;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.model.SampleModel;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import rx.observers.TestSubscriber;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"com.noveogroup.android.*"})
@PrepareForTest({Injector.class})
public class CommonRealmRepositoryTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    SampleRepository sampleRepository;

    @Inject
    DatabaseRealm databaseRealm;

    @Before
    public void setupDagger() {
        ApplicationComponentTest applicationComponentTest = DaggerApplicationComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(false))
            .domoticModuleTest(new DomoticModuleTest())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ApplicationComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void commonRealmRepository_add() {
        String UUID = "modelUuid";

        SampleModel model = new SampleModel();
        model.setUuid(UUID);

        when(databaseRealm.add(model)).thenReturn(model);

        TestSubscriber<String> tester = new TestSubscriber<>();
        sampleRepository.add(model).subscribe(tester);

        verify(databaseRealm).add(model);

        tester.assertValue(UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void commonRealmRepository_update() {
        SampleModel model = new SampleModel();
        model.setUuid("modelUuid");

        when(databaseRealm.update(model)).thenReturn(model);

        TestSubscriber<Void> tester = new TestSubscriber<>();
        sampleRepository.update(model).subscribe(tester);

        verify(databaseRealm).update(model);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void commonRealmRepository_delete() {
        String UUID = "modelUuid";

        doNothing().when(databaseRealm).delete(SampleModel.class, RealmModel.FIELD_UUID, UUID);

        TestSubscriber<Void> tester = new TestSubscriber<>();
        sampleRepository.delete(UUID).subscribe(tester);

        verify(databaseRealm).delete(SampleModel.class, RealmModel.FIELD_UUID, UUID);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void commonRealmRepository_findById() {
        String UUID = "modelUuid";

        SampleModel model = new SampleModel();
        model.setUuid(UUID);

        when(databaseRealm.findWhere(SampleModel.class, RealmModel.FIELD_UUID, UUID))
            .thenReturn(Arrays.asList(model));

        TestSubscriber<SampleModel> tester = new TestSubscriber<>();
        sampleRepository.findById(UUID).subscribe(tester);

        verify(databaseRealm).findWhere(SampleModel.class, RealmModel.FIELD_UUID, UUID);

        tester.assertValue(model);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void commonRealmRepository_findById_errorNoResult() {
        String UUID = "modelUuid";

        when(databaseRealm.findWhere(SampleModel.class, RealmModel.FIELD_UUID, UUID))
            .thenReturn(new ArrayList<>());

        TestSubscriber<SampleModel> tester = new TestSubscriber<>();
        sampleRepository.findById(UUID).subscribe(tester);

        verify(databaseRealm).findWhere(SampleModel.class, RealmModel.FIELD_UUID, UUID);

        tester.assertNotCompleted();
        tester.assertError(IllegalStateException.class);
    }

    @Test
    public void commonRealmRepository_findById_errorDuplicate() {
        String UUID = "modelUuid";

        SampleModel model1 = new SampleModel();
        model1.setUuid("uuid1");
        SampleModel model2 = new SampleModel();
        model2.setUuid("uuid2");
        List<SampleModel> models = Arrays.asList(model1, model2);

        when(databaseRealm.findWhere(SampleModel.class, RealmModel.FIELD_UUID, UUID))
                .thenReturn(models);

        TestSubscriber<SampleModel> tester = new TestSubscriber<>();
        sampleRepository.findById(UUID).subscribe(tester);

        verify(databaseRealm).findWhere(SampleModel.class, RealmModel.FIELD_UUID, UUID);

        tester.assertNotCompleted();
        tester.assertError(IllegalStateException.class);
    }

    @Test
    public void commonRealmRepository_findAll() {
        SampleModel model1 = new SampleModel();
        model1.setUuid("uuid1");
        SampleModel model2 = new SampleModel();
        model2.setUuid("uuid2");
        List<SampleModel> models = Arrays.asList(model1, model2);

        when(databaseRealm.find(SampleModel.class)).thenReturn(models);

        TestSubscriber<List<SampleModel>> tester = new TestSubscriber<>();
        sampleRepository.findAll().subscribe(tester);

        verify(databaseRealm).find(SampleModel.class);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

}
