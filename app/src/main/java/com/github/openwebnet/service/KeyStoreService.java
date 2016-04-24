package com.github.openwebnet.service;

import java.io.IOException;

public interface KeyStoreService {

    String getKey(String name);

    String getKey(String name, String defaultValue);

    void setKey(String name, String value);

    void writeKeyToFile(String fileName, String name) throws IOException;

}
