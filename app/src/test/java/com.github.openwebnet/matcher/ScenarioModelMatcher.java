package com.github.openwebnet.matcher;

import com.github.openwebnet.model.ScenarioModel;
import com.google.common.base.Preconditions;

import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class ScenarioModelMatcher extends ArgumentMatcher<ScenarioModel> {

    private final ScenarioModel expected;

    private ScenarioModelMatcher(ScenarioModel expected) {
        Preconditions.checkNotNull(expected);
        this.expected = expected;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument == null || !(argument instanceof ScenarioModel)) {
            return false;
        }
        // ignore uuid
        ScenarioModel actual = (ScenarioModel) argument;
        return actual.getName().equals(expected.getName()) &&
            actual.getWhere().equals(expected.getWhere()) &&
            actual.getEnvironmentId().equals(expected.getEnvironmentId()) &&
            actual.getGatewayUuid().equals(expected.getGatewayUuid()) &&
            (actual.isFavourite() == expected.isFavourite());
    }

    public static ScenarioModel scenarioModelEq(ScenarioModel expected) {
        return Matchers.argThat(new ScenarioModelMatcher(expected));
    }
}