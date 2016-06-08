package com.github.openwebnet.service;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModule;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.repository.IpcamRepository;

import org.junit.After;
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
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.plugins.RxJavaTestPlugins;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"android.*"})
@PrepareForTest({Injector.class})
public class IpcamServiceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    IpcamRepository ipcamRepository;

    @Inject
    IpcamService ipcamService;

    @Inject
    CommonService commonService;

    @Singleton
    @Component(modules = {
        ApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface IpcamComponentTest extends ApplicationComponent {

        void inject(IpcamServiceTest service);

    }

    @Before
    public void setupRxJava() {
        RxJavaTestPlugins.immediateAndroidSchedulers();
    }

    @After
    public void tearDownRxJava() {
        RxJavaTestPlugins.resetPlugins();
    }

    @Before
    public void setupDagger() {
        IpcamComponentTest applicationComponentTest = DaggerIpcamServiceTest_IpcamComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((IpcamComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void ipcamService_add() {
        String IPCAM_UUID = "myUuid";
        IpcamModel ipcamModel = new IpcamModel();

        when(ipcamRepository.add(ipcamModel)).thenReturn(Observable.just(IPCAM_UUID));

        TestSubscriber<String> tester = new TestSubscriber<>();
        ipcamService.add(ipcamModel).subscribe(tester);

        verify(ipcamRepository).add(ipcamModel);

        tester.assertValue(IPCAM_UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void ipcamService_update() {
        IpcamModel ipcamModel = new IpcamModel();

        when(ipcamRepository.update(ipcamModel)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        ipcamService.update(ipcamModel).subscribe(tester);

        verify(ipcamRepository).update(ipcamModel);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void ipcamService_delete() {
        String IPCAM_UUID = "myUuid";

        when(ipcamRepository.delete(IPCAM_UUID)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        ipcamService.delete(IPCAM_UUID).subscribe(tester);

        verify(ipcamRepository).delete(IPCAM_UUID);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void ipcamService_findById() {
        String IPCAM_UUID = "myUuid";
        IpcamModel ipcamModel = new IpcamModel();
        ipcamModel.setUuid(IPCAM_UUID);

        when(ipcamRepository.findById(IPCAM_UUID)).thenReturn(Observable.just(ipcamModel));

        TestSubscriber<IpcamModel> tester = new TestSubscriber<>();
        ipcamService.findById(IPCAM_UUID).subscribe(tester);

        verify(ipcamRepository).findById(IPCAM_UUID);

        tester.assertValue(ipcamModel);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void ipcamService_findByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<IpcamModel> ipcams = new ArrayList<>();

        when(ipcamRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(ipcams));

        TestSubscriber<List<IpcamModel>> tester = new TestSubscriber<>();
        ipcamService.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(ipcamRepository).findByEnvironment(ENVIRONMENT);

        tester.assertValue(ipcams);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void ipcamService_findFavourites() {
        List<IpcamModel> ipcams = new ArrayList<>();

        when(ipcamRepository.findFavourites()).thenReturn(Observable.just(ipcams));

        TestSubscriber<List<IpcamModel>> tester = new TestSubscriber<>();
        ipcamService.findFavourites().subscribe(tester);

        verify(ipcamRepository).findFavourites();

        tester.assertValue(ipcams);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

}
