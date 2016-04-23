package com.github.openwebnet.service;

import java.io.IOException;

/*
 * @see https://github.com/googlesamples/android-BasicAndroidKeyStore
 */
public interface KeyStoreService {

    byte[] getKey();

    /**
     * Dump the key 128-character string in hexadecimal format to file
     * Note: use only for development abd debugging purpose
     *
     * @param fileName
     * @throws IOException
     */
    void writeKeyToFile(String fileName) throws IOException;

}
