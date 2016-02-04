package android.support.v7.app;

import android.view.View;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.ReflectionHelpers;

import java.lang.reflect.Field;

import static org.robolectric.Shadows.shadowOf;

/**
 * Issue with {@link android.support.v7.app.AlertDialog}
 * ShadowAlertDialog#getLatestAlertDialog supports only {@link android.app.AlertDialog}
 */
public class ShadowAlertDialogSupport {

    private final AlertController mAlert;

    private ShadowAlertDialogSupport(AlertController mAlert) {
        this.mAlert = mAlert;
    }

    public static ShadowAlertDialogSupport getShadowAlertDialog(){
        ShadowDialog dialog = shadowOf(RuntimeEnvironment.application).getLatestDialog();
        AlertDialog alertDialog = getPrivateField(dialog, "realDialog", AlertDialog.class);
        AlertController alert = ReflectionHelpers.getField(alertDialog, "mAlert");
        return new ShadowAlertDialogSupport(alert);
    }

    private static <T> T getPrivateField(Object o, String name, Class<T> type) {
        try {
            Field fieldObj = o.getClass().getDeclaredField(name);
            fieldObj.setAccessible(true);
            T field = (T) fieldObj.get(o);
            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }


    public CharSequence getTitle() {
        return getPrivateField(mAlert, "mTitle", CharSequence.class);
    }

    public void performButtonClick(int whichButton) {
        mAlert.getButton(whichButton).performClick();
    }

    public View getInflatedView() {
        return getPrivateField(mAlert, "mView", View.class);
    }

}
