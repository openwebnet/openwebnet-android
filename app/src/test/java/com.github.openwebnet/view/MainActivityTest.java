package com.github.openwebnet.view;

import android.app.Application;
import android.content.Intent;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.MockSupport;
import com.github.openwebnet.R;
import com.github.openwebnet.component.DaggerOpenWebNetComponent;
import com.github.openwebnet.component.OpenWebNetComponent;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.service.DomoticService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import rx.Observable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.robolectric.Shadows.shadowOf;

//https://bitbucket.org/hvisser/android-apt/issues/22/dagger-robolectric-gradle-apt-source-not
// https://stackoverflow.com/questions/26939340/how-do-you-override-a-module-dependency-in-a-unit-test-with-dagger-2-0?lq=1
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class})
public class MainActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    Realm mockRealm;
    DomoticService domoticServiceMock;

    @Before
    public void initialize() {
        mockRealm = MockSupport.mockRealm();
    }

    @Test
    public void clickingAddLight_shouldStartLightActivity() {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        activity.findViewById(R.id.floatingActionButtonAddLight).performClick();

        Intent expectedIntent = new Intent(activity, MainActivity.class);
        assertThat(shadowOf(activity).getNextStartedActivity(), equalTo(expectedIntent));
    }
}
