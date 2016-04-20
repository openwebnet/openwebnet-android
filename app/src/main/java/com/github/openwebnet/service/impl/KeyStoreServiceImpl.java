package com.github.openwebnet.service.impl;

import android.content.Context;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.service.KeyStoreService;
import com.google.common.io.BaseEncoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        log.debug("key: {}", BaseEncoding.base16().lowerCase().encode(key));
        return key;
    }

}
