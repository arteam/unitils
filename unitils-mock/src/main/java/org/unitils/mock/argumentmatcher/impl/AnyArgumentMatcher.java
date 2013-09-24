/*
 * Copyright 2013,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.mock.argumentmatcher.impl;

import org.unitils.mock.argumentmatcher.ArgumentMatcher;

import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.MATCH;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.NO_MATCH;

/**
 * A matcher for checking whether an argument value is of a certain type.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class AnyArgumentMatcher implements ArgumentMatcher {

    /* The expected type */
    protected Class<?> type;


    /**
     * Creates a matcher for the given expected type.
     *
     * @param type The expected type, not null
     */
    public AnyArgumentMatcher(Class<?> type) {
        this.type = type;
    }


    /**
     * Returns true if the given argument is of the expected type, false otherwise.
     *
     * @param argument                 The argument that were used by reference
     * @param argumentAtInvocationTime Copy of the argument, taken at the time that the invocation was performed
     * @return The match result, not null
     */
    public MatchResult matches(Object argument, Object argumentAtInvocationTime) {
        if (type != null && argument != null && type.isAssignableFrom(argument.getClass())) {
            return MATCH;
        }
        return NO_MATCH;
    }
}