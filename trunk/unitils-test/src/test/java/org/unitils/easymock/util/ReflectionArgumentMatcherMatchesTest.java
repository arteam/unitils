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
package org.unitils.easymock.util;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

/**
 * @author Tim Ducheyne
 */
public class ReflectionArgumentMatcherMatchesTest {

    private ReflectionArgumentMatcher<?> reflectionArgumentMatcher;


    @Test
    public void trueWhenEqual() {
        reflectionArgumentMatcher = new ReflectionArgumentMatcher<String>("value");

        boolean result = reflectionArgumentMatcher.matches("value");
        assertTrue(result);
    }

    @Test
    public void trueWhenLenientEqual() {
        reflectionArgumentMatcher = new ReflectionArgumentMatcher<List<String>>(asList("1", "2", "3"), LENIENT_ORDER, IGNORE_DEFAULTS);

        boolean result = reflectionArgumentMatcher.matches(asList("3", "2", "1"));
        assertTrue(result);
    }

    @Test
    public void falseWhenNotEqual() {
        reflectionArgumentMatcher = new ReflectionArgumentMatcher<String>("value");

        boolean result = reflectionArgumentMatcher.matches("xxx");
        assertFalse(result);
    }
}
