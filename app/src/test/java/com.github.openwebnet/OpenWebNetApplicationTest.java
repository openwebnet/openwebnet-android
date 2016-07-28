package com.github.openwebnet;

public class OpenWebNetApplicationTest extends OpenWebNetApplication {

    @Override
    protected void initRealm() {
        // do nothing: databaseRealm.setup() throws java.lang.UnsatisfiedLinkError for testing
    }

}
