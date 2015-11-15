package com.github.openwebnet.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.openwebnet.OpenWebNetApplication;

import butterknife.ButterKnife;
import io.realm.Realm;
import lombok.Getter;

/**
 * @author niqdev
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Getter
    protected Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO
        //((OpenWebNetApplication) getApplication()).getOpenWebNetComponent().inject(this);

        // TODO
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }
}
