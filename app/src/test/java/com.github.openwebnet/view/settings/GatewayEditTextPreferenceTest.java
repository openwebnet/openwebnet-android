package com.github.openwebnet.view.settings;

import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.widget.EditText;
import android.widget.TextView;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.OpenWebNetApplicationTest;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.service.GatewayService;
import com.github.openwebnet.service.UtilityService;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(application = OpenWebNetApplicationTest.class, constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*"})
@PrepareForTest({Injector.class})
public class GatewayEditTextPreferenceTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    GatewayService gatewayService;

    @Inject
    UtilityService utilityService;

    private GatewayEditTextPreference gatewayEditTextPreference;

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

    private void setupDialog() {
        when(utilityService.isBlankText(any(TextView.class))).thenCallRealMethod();
        when(utilityService.sanitizedText(any(TextView.class))).thenCallRealMethod();

        SettingsFragment settingsFragment = new SettingsFragment();
        FragmentTestUtil.startVisibleFragment(settingsFragment);
        assertNotNull("fragment is null", settingsFragment);
        PreferenceScreen preferenceScreen = settingsFragment.getPreferenceScreen();
        initGatewayEditTextPreference(preferenceScreen);

        // TODO
        //gatewayEditTextPreference.onClick();
        assertTrue("should be visible", gatewayEditTextPreference.getDialog().isShowing());
    }

    // probably there is a better way!!
    private void initGatewayEditTextPreference(Preference preference) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
            for (int i=0; i<preferenceGroup.getPreferenceCount(); i++) {
                if (preferenceGroup.getPreference(i).getTitle().equals("Gateway")) {
                    PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceGroup.getPreference(i);
                    initGatewayEditTextPreference(preferenceCategory.getPreference(0));
                } else {
                    initGatewayEditTextPreference(preferenceGroup.getPreference(i));
                }
            }
        } else if (preference instanceof EditTextPreference) {
            // TODO wrong ClassCastException ?!?!
            gatewayEditTextPreference = (GatewayEditTextPreference) preference;
        }
    }

    @Ignore
    @Test
    public void testValidHost() {
        setupDialog();

        EditText mEditTextHost = (EditText) gatewayEditTextPreference.getDialog()
            .findViewById(R.id.editTextDialogGatewayHost);
        assertNotNull("should not be null", mEditTextHost);
    }

    @Ignore
    @Test
    public void testValidPort() {

    }

    @Ignore
    @Test
    public void testValidPassword() {

    }

}
