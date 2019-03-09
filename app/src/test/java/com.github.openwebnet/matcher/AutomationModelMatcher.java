package com.github.openwebnet.matcher;

import com.github.openwebnet.model.AutomationModel;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class AutomationModelMatcher extends ArgumentMatcher<AutomationModel> {

    private final AutomationModel expected;

    private AutomationModelMatcher(AutomationModel expected) {
        Preconditions.checkNotNull(expected);
        this.expected = expected;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument == null || !(argument instanceof AutomationModel)) {
            return false;
        }
        // ignore uuid
        AutomationModel actual = (AutomationModel) argument;
        return actual.getName().equals(expected.getName()) &&
            actual.getWhere().equals(expected.getWhere()) &&
            actual.getAutomationType().equals(expected.getAutomationType()) &&
            actual.getBus().equals(expected.getBus()) &&
            actual.getEnvironmentId().equals(expected.getEnvironmentId()) &&
            actual.getGatewayUuid().equals(expected.getGatewayUuid()) &&
            (actual.isFavourite() == expected.isFavourite());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expected == null ? "null" : automationModelValue(expected));
    }

    private String automationModelValue(AutomationModel automation) {
        Joiner joiner = Joiner.on("; ").skipNulls();
        return joiner.join(automation.getName(), automation.getWhere(),
            automation.getEnvironmentId(), automation.getGatewayUuid(),
            automation.isFavourite());
    }

    public static AutomationModel automationModelEq(AutomationModel expected) {
        return Matchers.argThat(new AutomationModelMatcher(expected));
    }
}