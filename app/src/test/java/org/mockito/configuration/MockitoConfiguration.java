package org.mockito.configuration;

public class MockitoConfiguration extends DefaultMockitoConfiguration {

    /*
     * There is a test error in LightActivityTest if we do not disable Objenesis cache.
     */
    @Override
    public boolean enableClassCache() {
        return false;
    }
}
