package com.github.openwebnet.service.impl;

import android.content.Context;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.KeyStoreService;
import com.google.common.io.BaseEncoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import javax.inject.Inject;

public class KeyStoreServiceImpl implements KeyStoreService {

    private static final Logger log = LoggerFactory.getLogger(KeyStoreService.class);

    @Inject
    Context mContext;

    public KeyStoreServiceImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    @Override
    public byte[] getKey() {
        // TODO
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        log.debug("key: {}", BaseEncoding.base16().lowerCase().encode(key));
        return key;
    }

    @Override
    public void writeKeyToFile(String fileName) throws IOException {
        File file = new File(mContext.getFilesDir(), fileName);
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(BaseEncoding.base16().lowerCase().encode(getKey()).getBytes());
        stream.close();
    }

}
