package com.github.openwebnet.matcher;

import com.github.openwebnet.model.TemperatureModel;
import com.google.common.base.Preconditions;

import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class TemperatureModelMatcher extends ArgumentMatcher<TemperatureModel> {

    private final TemperatureModel expected;

    private TemperatureModelMatcher(TemperatureModel expected) {
        Preconditions.checkNotNull(expected);
        this.expected = expected;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument == null || !(argument instanceof TemperatureModel)) {
            return false;
        }
        // ignore uuid
        TemperatureModel actual = (TemperatureModel) argument;
        return actual.getName().equals(expected.getName()) &&
            actual.getWhere().equals(expected.getWhere()) &&
            actual.getEnvironmentId().equals(expected.getEnvironmentId()) &&
            actual.getGatewayUuid().equals(expected.getGatewayUuid()) &&
            (actual.isFavourite() == expected.isFavourite());
    }

    public static TemperatureModel temperatureModelEq(TemperatureModel expected) {
        return Matchers.argThat(new TemperatureModelMatcher(expected));
    }
}