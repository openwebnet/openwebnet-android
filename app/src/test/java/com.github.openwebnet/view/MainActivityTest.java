package com.github.openwebnet.view;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.view.Menu;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.view.device.DeviceActivity;
import com.github.openwebnet.view.device.LightActivity;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "com.getbase.*"})
@PrepareForTest({Injector.class})
public class MainActivityTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    CommonService commonService;
    @Inject
    EnvironmentService environmentService;

    @Bind(R.id.floatingActionButtonAddDevice)
    FloatingActionButton floatingActionButtonAddDevice;
    @Bind(R.id.floatingActionButtonAddLight)
    FloatingActionButton floatingActionButtonAddLight;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    MainActivity activity;

    @Before
    public void setup() {
        ApplicationComponentTest applicationComponentTest = DaggerApplicationComponentTest.builder()
            .applicationContextModuleTest(new ApplicationContextModuleTest())
            .domoticModuleTest(new DomoticModuleTest())
            .repositoryModuleTest(new RepositoryModuleTest(true))
            .build();

        PowerMockito.mockStatic(Injector.class);
        PowerMockito.when(Injector.getApplicationComponent()).thenReturn(applicationComponentTest);

        ((ApplicationComponentTest) Injector.getApplicationComponent()).inject(this);
    }

    private void setupActivity() {
        // mock reloadMenu
        when(environmentService.findAll()).thenReturn(Observable.<List<EnvironmentModel>>empty());

        activity = Robolectric.setupActivity(MainActivity.class);
        ButterKnife.bind(this, activity);
    }

    @Test
    public void shouldInitNavigationDrawerMenu() {
        EnvironmentModel environment1 = new EnvironmentModel();
        environment1.setId(100);
        environment1.setName("environment1");
        EnvironmentModel environment2 = new EnvironmentModel();
        environment2.setId(101);
        environment2.setName("environment2");
        List<EnvironmentModel> environments = Lists.newArrayList(environment1, environment2);

        when(environmentService.findAll()).thenReturn(Observable.just(environments));

        ButterKnife.bind(this, Robolectric.setupActivity(MainActivity.class));

        verify(commonService).initApplication();
        verify(environmentService).findAll();

        Menu menu = navigationView.getMenu();
        assertEquals("invalid menu title", "Favourite", menu.getItem(0).getTitle());
        assertEquals("invalid menu order", 10, menu.getItem(0).getOrder());
        assertEquals("invalid menu title", "environment1", menu.getItem(1).getTitle());
        assertEquals("invalid menu order", 100, menu.getItem(1).getOrder());
        assertEquals("invalid menu title", "environment2", menu.getItem(2).getTitle());
        assertEquals("invalid menu order", 101, menu.getItem(2).getOrder());
        assertEquals("invalid menu title", "Add environment", menu.getItem(3).getTitle());
        assertEquals("invalid menu order", 900, menu.getItem(3).getOrder());
        assertEquals("invalid menu title", "Settings", menu.getItem(4).getTitle());
        assertEquals("invalid menu order", 900, menu.getItem(4).getOrder());
    }

    @Test
    public void clickingAddLight_shouldStartDeviceActivity() {
        setupActivity();

        floatingActionButtonAddDevice.performClick();
        Intent expectedIntent = new Intent(activity, DeviceActivity.class);
        assertThat(shadowOf(activity).getNextStartedActivity(), equalTo(expectedIntent));
    }

    @Test
    public void clickingAddLight_shouldStartLightActivity() {
        setupActivity();

        floatingActionButtonAddLight.performClick();
        Intent expectedIntent = new Intent(activity, LightActivity.class);
        assertThat(shadowOf(activity).getNextStartedActivity(), equalTo(expectedIntent));
    }

}
