package org.unitils.mock.example5;

import org.unitils.mock.argumentmatcher.ArgumentMatcher;

import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.MATCH;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.NO_MATCH;

// START SNIPPET: argumentMatcher
public class LessThanArgumentMatcher implements ArgumentMatcher {

    private Integer lessThan;

    public LessThanArgumentMatcher(Integer lessThan) {
        this.lessThan = lessThan;
    }

    public MatchResult matches(Object argument, Object argumentAtInvocationTime) {
        Integer argumentAsInt = (Integer) argument;
        if (argumentAsInt != null && lessThan.compareTo(argumentAsInt) > 0) {
            return MATCH;
        }
        return NO_MATCH;
    }
}
// END SNIPPET: argumentMatcher