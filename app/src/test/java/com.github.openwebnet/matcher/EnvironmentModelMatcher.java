package com.github.openwebnet.matcher;

import com.github.openwebnet.model.EnvironmentModel;
import com.google.common.base.Preconditions;

import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class EnvironmentModelMatcher extends ArgumentMatcher<EnvironmentModel> {

    private final EnvironmentModel expected;

    private EnvironmentModelMatcher(EnvironmentModel expected) {
        Preconditions.checkNotNull(expected);
        this.expected = expected;
    }

    @Override
    public boolean matches(Object argument) {
        if (argument == null || !(argument instanceof EnvironmentModel)) {
            return false;
        }
        EnvironmentModel actual = (EnvironmentModel) argument;
        return actual.getId().equals(expected.getId()) &&
            actual.getName().equals(expected.getName());
    }

    public static EnvironmentModel equals(EnvironmentModel expected) {
        return Matchers.argThat(new EnvironmentModelMatcher(expected));
    }
}