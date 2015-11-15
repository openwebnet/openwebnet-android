package com.github.openwebnet.view.activity;

import android.app.Activity;
import android.os.Bundle;

import com.github.openwebnet.OpenWebNetApplication;

/**
 * @author niqdev
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((OpenWebNetApplication) getApplication()).getOpenWebNetComponent().inject(this);
    }

}
