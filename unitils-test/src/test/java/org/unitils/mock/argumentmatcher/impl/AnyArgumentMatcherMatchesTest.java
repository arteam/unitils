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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.MATCH;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.NO_MATCH;

/**
 * @author Tim Ducheyne
 */
public class AnyArgumentMatcherMatchesTest {

    private AnyArgumentMatcher anyArgumentMatcher;


    @Test
    public void matchWhenSameType() {
        anyArgumentMatcher = new AnyArgumentMatcher(TestClass.class);

        MatchResult result = anyArgumentMatcher.matches(new TestClass(), null);
        assertEquals(MATCH, result);
    }

    @Test
    public void matchWhenSubType() {
        anyArgumentMatcher = new AnyArgumentMatcher(SuperClass.class);

        MatchResult result = anyArgumentMatcher.matches(new TestClass(), null);
        assertEquals(MATCH, result);
    }

    @Test
    public void noMatchWhenSuperType() {
        anyArgumentMatcher = new AnyArgumentMatcher(TestClass.class);

        MatchResult result = anyArgumentMatcher.matches(new SuperClass(), null);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void noMatchWhenNullType() {
        anyArgumentMatcher = new AnyArgumentMatcher(null);

        MatchResult result = anyArgumentMatcher.matches(new TestClass(), null);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void noMatchWhenNullArgument() {
        anyArgumentMatcher = new AnyArgumentMatcher(TestClass.class);

        MatchResult result = anyArgumentMatcher.matches(null, null);
        assertEquals(NO_MATCH, result);
    }


    public static class SuperClass {
    }

    public static class TestClass extends SuperClass {
    }
}
