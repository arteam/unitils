package org.unitils.mock.example5;

import org.unitils.mock.annotation.ArgumentMatcher;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;

import static org.unitils.mock.core.proxy.StackTraceUtils.getInvocationLineNr;

// START SNIPPET: argumentMatcher
public class CustomArgumentMatchers {

    @ArgumentMatcher
    public static int lessThan(Integer lessThan) {
        LessThanArgumentMatcher argumentMatcher = new LessThanArgumentMatcher(lessThan);
        ArgumentMatcherRepository.getInstance().registerArgumentMatcher(argumentMatcher, getInvocationLineNr(CustomArgumentMatchers.class));
        return 0;
    }
}
// END SNIPPET: argumentMatcher
