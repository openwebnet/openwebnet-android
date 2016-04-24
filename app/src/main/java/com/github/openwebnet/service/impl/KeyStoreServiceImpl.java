package com.github.openwebnet.service.impl;

import android.content.Context;
import android.text.TextUtils;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.KeyStoreService;
import com.q42.qlassified.Qlassified;
import com.q42.qlassified.Storage.QlassifiedSharedPreferencesService;
import com.q42.qlassified.Storage.QlassifiedStorageService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

public class KeyStoreServiceImpl implements KeyStoreService {

    private static final String KEY_STORE_PREFERENCES = "key_store_preferences";

    @Inject
    Context mContext;

    public KeyStoreServiceImpl() {
        Injector.getApplicationComponent().inject(this);
        Qlassified.Service.start(mContext);
        QlassifiedStorageService storageService = new QlassifiedSharedPreferencesService(mContext, KEY_STORE_PREFERENCES);
        Qlassified.Service.setStorageService(storageService);
    }

    @Override
    public String getKey(String name) {
        return Qlassified.Service.getString(name);
    }

    @Override
    public String getKey(String name, String defaultValue) {
        String value = Qlassified.Service.getString(name);
        return TextUtils.isEmpty(value) ? defaultValue : value;
    }

    @Override
    public void setKey(String name, String value) {
        Qlassified.Service.put(name, value);
    }

    @Override
    public void writeKeyToFile(String fileName, String name) throws IOException {
        File file = new File(mContext.getFilesDir(), fileName);
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(getKey(name, "").getBytes());
        stream.close();
    }

}
