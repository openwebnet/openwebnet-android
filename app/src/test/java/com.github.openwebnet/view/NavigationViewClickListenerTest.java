package com.github.openwebnet.view;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.ShadowAlertDialogSupport;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.R;
import com.github.openwebnet.component.ApplicationComponentTest;
import com.github.openwebnet.component.DaggerApplicationComponentTest;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.component.module.ApplicationContextModuleTest;
import com.github.openwebnet.component.module.DatabaseModuleTest;
import com.github.openwebnet.component.module.DomoticModuleTest;
import com.github.openwebnet.component.module.RepositoryModuleTest;
import com.github.openwebnet.matcher.EnvironmentModelMatcher;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.service.EnvironmentService;
import com.github.openwebnet.service.UtilityService;
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

import butterknife.ButterKnife;
import rx.Observable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.robolectric.*", "android.*", "com.getbase.*"})
@PrepareForTest({Injector.class})
public class NavigationViewClickListenerTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Inject
    EnvironmentService environmentService;

    @Inject
    UtilityService utilityService;

    MainActivity activity;

    private int MENU_ENVIRONMENT_ID = 803;
    private String MENU_ENVIRONMENT_NAME = "C-environment";
    private EnvironmentModel environmentSelected = newEnvironmentModel(MENU_ENVIRONMENT_ID, MENU_ENVIRONMENT_NAME);

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

    private EnvironmentModel newEnvironmentModel(Integer id, String name) {
        EnvironmentModel environment = new EnvironmentModel();
        environment.setId(id);
        environment.setName(name);
        return environment;
    }

    private void showEditDialog() {
        List<EnvironmentModel> environments = Lists.newArrayList(
            newEnvironmentModel(801, "A-environment"),
            newEnvironmentModel(802, "B-environment"),
            environmentSelected,
            newEnvironmentModel(804, "D-environment"),
            newEnvironmentModel(805, "E-environment"));

        when(environmentService.findAll()).thenReturn(Observable.just(environments));
        when(environmentService.findById(MENU_ENVIRONMENT_ID)).thenReturn(Observable.just(environmentSelected));

        activity = Robolectric.setupActivity(MainActivity.class);
        ButterKnife.bind(this, activity);

        MenuItem itemSelected = activity.navigationView.getMenu().findItem(MENU_ENVIRONMENT_ID);
        View viewEdit = itemSelected.getActionView().findViewById(R.id.imageViewDrawerMenuEnvironmentEdit);
        viewEdit.performClick();
    }

    @Test
    public void onClick_shouldShowDialog() {
        showEditDialog();
        ShadowAlertDialogSupport shadowAlertDialog = ShadowAlertDialogSupport.getShadowAlertDialog();

        assertThat(activity.getString(R.string.dialog_edit_environment_title),
            equalTo(shadowAlertDialog.getTitle()));

        View inflatedView = shadowAlertDialog.getInflatedView();
        assertNotNull("null layout", inflatedView);

        EditText editTextName = (EditText) inflatedView.findViewById(R.id.editTextDialogEnvironmentName);
        assertEquals("invalid name", MENU_ENVIRONMENT_NAME, editTextName.getText().toString());
    }

    @Test
    public void onClick_isInvalid() {
        showEditDialog();
        ShadowAlertDialogSupport shadowAlertDialog = ShadowAlertDialogSupport.getShadowAlertDialog();
        View inflatedView = shadowAlertDialog.getInflatedView();
        EditText editTextName = (EditText) inflatedView.findViewById(R.id.editTextDialogEnvironmentName);
        when(utilityService.isBlankText(editTextName)).thenReturn(true);

        assertNull("no error", editTextName.getError());
        editTextName.setText("");
        shadowAlertDialog.performButtonClick(AlertDialog.BUTTON_POSITIVE);
        assertThat(editTextName.getError(), equalTo(activity.getString(R.string.validation_required)));

        verify(environmentService, never()).update(any(EnvironmentModel.class));
    }

    @Test
    public void onClick_shouldEdit() {
        String EDIT_ENVIRONMENT = "editEnvironment";
        environmentSelected.setName(EDIT_ENVIRONMENT);
        when(environmentService.update(EnvironmentModelMatcher.equals(environmentSelected))).thenReturn(Observable.just(null));

        showEditDialog();
        ShadowAlertDialogSupport shadowAlertDialog = ShadowAlertDialogSupport.getShadowAlertDialog();
        View inflatedView = shadowAlertDialog.getInflatedView();
        EditText editTextName = (EditText) inflatedView.findViewById(R.id.editTextDialogEnvironmentName);
        when(utilityService.isBlankText(editTextName)).thenReturn(false);
        when(utilityService.sanitizedText(editTextName)).thenReturn(EDIT_ENVIRONMENT);

        editTextName.setText(EDIT_ENVIRONMENT);
        shadowAlertDialog.performButtonClick(AlertDialog.BUTTON_POSITIVE);

        verify(environmentService).update(EnvironmentModelMatcher.equals(environmentSelected));
    }

}
