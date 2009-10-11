/*
 * Copyright 2008,  Unitils.org
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

/**
 * A matcher for checking whether an argument equals a given value.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class EqualsArgumentMatcher implements ArgumentMatcher {

    /* The excepted value */
    private final Object value;


    /**
     * Creates a matcher for the given value.
     *
     * @param value The expected value
     */
    public EqualsArgumentMatcher(Object value) {
        this.value = value;
    }


    /**
     * Returns true if the given object matches the expected argument, false otherwise.
     *
     * The argumentAtInvocationTime is a copy (deep clone) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference).
     *
     * @param argument                 The argument that were used by reference, not null
     * @param argumentAtInvocationTime Copy of the argument, taken at the time that the invocation was performed, not null
     * @return True when passed object matches, false otherwise.
     */
    public boolean matches(Object argument, Object argumentAtInvocationTime) {
        if (this.value == null) {
            return argument == null;
        }
        return this.value.equals(argument);
    }
}
