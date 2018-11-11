package com.github.openwebnet.matcher;

import com.github.openwebnet.model.LightModel;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class LightModelMatcher extends ArgumentMatcher<LightModel> {

    private final LightModel expected;

    private LightModelMatcher(LightModel expected) {
        Preconditions.checkNotNull(expected);
        this.expected = expected;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument == null || !(argument instanceof LightModel)) {
            return false;
        }
        // ignore uuid
        LightModel actual = (LightModel) argument;
        return actual.getName().equals(expected.getName()) &&
            actual.getWhere().equals(expected.getWhere()) &&
            actual.getLightingType().equals(expected.getLightingType()) &&
            actual.getEnvironmentId().equals(expected.getEnvironmentId()) &&
            actual.getGatewayUuid().equals(expected.getGatewayUuid()) &&
            (actual.isFavourite() == expected.isFavourite());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expected == null ? "null" : lightModelValue(expected));
    }

    private String lightModelValue(LightModel light) {
        Joiner joiner = Joiner.on("; ").skipNulls();
        return joiner.join(light.getName(), light.getWhere(),
            light.getEnvironmentId(), light.getGatewayUuid(),
            light.isFavourite());
    }

    public static LightModel lightModelEq(LightModel expected) {
        return Matchers.argThat(new LightModelMatcher(expected));
    }
}