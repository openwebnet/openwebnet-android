package com.github.openwebnet.service;

import com.github.niqdev.openwebnet.OpenSession;
import com.github.niqdev.openwebnet.OpenWebNet;
import com.github.niqdev.openwebnet.message.OpenMessage;
import com.github.niqdev.openwebnet.message.SoundSystem;
import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.component.ApplicationComponent;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModule;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.SoundModel;
import com.github.openwebnet.repository.SoundRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
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

@RunWith(RobolectricTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"android.*"})
@PrepareForTest({Injector.class})
public class SoundServiceTest {

    private static final String GATEWAY_UUID = "myGateway";

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    SoundRepository soundRepository;

    @Inject
    SoundService soundService;

    @Inject
    CommonService commonService;

    @Singleton
    @Component(modules = {
        ApplicationContextModuleTest.class,
        DatabaseModuleTest.class,
        RepositoryModuleTest.class,
        DomoticModule.class
    })
    public interface SoundComponentTest extends ApplicationComponent {

        void inject(SoundServiceTest service);

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
        SoundComponentTest applicationComponentTest = DaggerSoundServiceTest_SoundComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .databaseModuleTest(new DatabaseModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .domoticModule(new DomoticModule())
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((SoundComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    @Test
    public void soundService_add() {
        String SOUND_UUID = "myUuid";
        SoundModel soundModel = new SoundModel();

        when(soundRepository.add(soundModel)).thenReturn(Observable.just(SOUND_UUID));

        TestSubscriber<String> tester = new TestSubscriber<>();
        soundService.add(soundModel).subscribe(tester);

        verify(soundRepository).add(soundModel);

        tester.assertValue(SOUND_UUID);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void soundService_update() {
        SoundModel soundModel = new SoundModel();

        when(soundRepository.update(soundModel)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        soundService.update(soundModel).subscribe(tester);

        verify(soundRepository).update(soundModel);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void soundService_delete() {
        String SOUND_UUID = "myUuid";

        when(soundRepository.delete(SOUND_UUID)).thenReturn(Observable.just(null));

        TestSubscriber<Void> tester = new TestSubscriber<>();
        soundService.delete(SOUND_UUID).subscribe(tester);

        verify(soundRepository).delete(SOUND_UUID);

        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void soundService_findById() {
        String SOUND_UUID = "myUuid";
        SoundModel soundModel = new SoundModel();
        soundModel.setUuid(SOUND_UUID);

        when(soundRepository.findById(SOUND_UUID)).thenReturn(Observable.just(soundModel));

        TestSubscriber<SoundModel> tester = new TestSubscriber<>();
        soundService.findById(SOUND_UUID).subscribe(tester);

        verify(soundRepository).findById(SOUND_UUID);

        tester.assertValue(soundModel);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void soundService_findByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<SoundModel> sounds = new ArrayList<>();

        when(soundRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(sounds));

        TestSubscriber<List<SoundModel>> tester = new TestSubscriber<>();
        soundService.findByEnvironment(ENVIRONMENT).subscribe(tester);

        verify(soundRepository).findByEnvironment(ENVIRONMENT);

        tester.assertValue(sounds);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void soundService_findFavourites() {
        List<SoundModel> sounds = new ArrayList<>();

        when(soundRepository.findFavourites()).thenReturn(Observable.just(sounds));

        TestSubscriber<List<SoundModel>> tester = new TestSubscriber<>();
        soundService.findFavourites().subscribe(tester);

        verify(soundRepository).findFavourites();

        tester.assertValue(sounds);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void soundService_requestByEnvironment() {
        Integer ENVIRONMENT = 108;
        List<SoundModel> sounds = new ArrayList<>();

        mockClient();
        when(soundRepository.findByEnvironment(ENVIRONMENT)).thenReturn(Observable.just(sounds));

        TestSubscriber<List<SoundModel>> tester = new TestSubscriber<>();
        soundService.requestByEnvironment(ENVIRONMENT).subscribe(tester);

        tester.assertValue(sounds);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void soundService_requestFavourites() {
        List<SoundModel> sounds = new ArrayList<>();

        mockClient();
        when(soundRepository.findFavourites()).thenReturn(Observable.just(sounds));

        TestSubscriber<List<SoundModel>> tester = new TestSubscriber<>();
        soundService.requestFavourites().subscribe(tester);

        tester.assertValue(sounds);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void soundService_turnOn() {
        SoundModel sound = mockSoundModel();
        mockClient();

        TestSubscriber<SoundModel> tester = new TestSubscriber<>();
        soundService.turnOn(sound).subscribe(tester);

        verify(commonService).findClient(GATEWAY_UUID);

        tester.assertValue(sound);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    @Test
    public void soundService_turnOff() {
        SoundModel sound = mockSoundModel();
        mockClient();

        TestSubscriber<SoundModel> tester = new TestSubscriber<>();
        soundService.turnOff(sound).subscribe(tester);

        verify(commonService).findClient(GATEWAY_UUID);

        tester.assertValue(sound);
        tester.assertCompleted();
        tester.assertNoErrors();
    }

    private SoundModel mockSoundModel() {
        return SoundModel.updateBuilder("uuid")
            .environment(108)
            .gateway(GATEWAY_UUID)
            .name("sound")
            .where("21")
            .source(SoundSystem.Source.STEREO_CHANNEL)
            .type(SoundSystem.Type.AMPLIFIER_P2P)
            .build();
    }

    private OpenWebNet mockClient() {
        String GATEWAY_HOST = "1.1.1.1";
        Integer GATEWAY_PORT = 1;

        GatewayModel gateway = new GatewayModel();
        gateway.setUuid(GATEWAY_UUID);
        gateway.setHost(GATEWAY_HOST);
        gateway.setPort(GATEWAY_PORT);

        OpenMessage request = () -> "REQUEST";
        OpenMessage response = () -> "RESPONSE";
        OpenSession session = OpenSession.newSession(request);
        session.addResponse(response);

        OpenWebNet client = OpenWebNet.newClient(OpenWebNet.gateway(GATEWAY_HOST, GATEWAY_PORT));
        OpenWebNet clientSpy = PowerMockito.mock(OpenWebNet.class, invocation -> Observable.just(client));

        when(clientSpy.send(request)).thenReturn(Observable.just(session));
        when(commonService.findClient(GATEWAY_UUID)).thenReturn(clientSpy);

        return clientSpy;
    }

}
