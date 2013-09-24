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
import org.unitils.reflectionassert.ReflectionComparator;

import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.*;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

/**
 * A matcher for checking whether an argument equals a given value. This matchers uses reference comparison if the
 * expected and actual arguments refer to the same object. Otherwise, lenient reflection comparison is used (This means
 * the actual order of collections will be ignored and only fields that have a non default value will be compared)
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultArgumentMatcher implements ArgumentMatcher {

    /* The original value passed to the argument matcher */
    protected Object value;
    /* Copy of the original value */
    protected Object valueAtInvocationTime;


    /**
     * Creates a matcher for the given value. The original value is stored and a copy of the value is taken so that it
     * can be compared even when the value itself was modified later-on.
     *
     * @param value The expected value
     */
    public DefaultArgumentMatcher(Object value, Object valueAtInvocationTime) {
        this.value = value;
        this.valueAtInvocationTime = valueAtInvocationTime;
    }


    /**
     * Returns true if the given object matches the expected argument, false otherwise. If the given argument refers to
     * the same object as the original value, true is returned. If the given argument is another object than the original
     * value, lenient reflection comparison is used to compare the values. This means that the actual order of collections
     * will be ignored and only fields that have a non default value will be compared.
     *
     * @param argument                 The argument that was used by reference
     * @param argumentAtInvocationTime Copy of the argument, taken at the time that the invocation was performed
     * @return The match result, not null
     */
    public MatchResult matches(Object argument, Object argumentAtInvocationTime) {
        if (value == argument) {
            return SAME;
        }
        ReflectionComparator reflectionComparator;
        if (valueAtInvocationTime instanceof Character || valueAtInvocationTime instanceof Number || valueAtInvocationTime instanceof Boolean) {
            reflectionComparator = createRefectionComparator();
        } else {
            reflectionComparator = createRefectionComparator(LENIENT_ORDER, IGNORE_DEFAULTS);
        }
        if (reflectionComparator.isEqual(valueAtInvocationTime, argumentAtInvocationTime)) {
            return MATCH;
        }
        return NO_MATCH;
    }
}
