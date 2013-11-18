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
import org.unitils.mock.core.proxy.Argument;

import static org.junit.Assert.assertEquals;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.MATCH;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.NO_MATCH;

/**
 * @author Tim Ducheyne
 */
public class AnyArgumentMatcherMatchesTest {

    private AnyArgumentMatcher<TestClass> anyArgumentMatcher;


    @Test
    public void matchWhenSameType() {
        Argument<TestClass> argument = new Argument<TestClass>(new TestClass(), null, TestClass.class);
        anyArgumentMatcher = new AnyArgumentMatcher<TestClass>(TestClass.class);

        MatchResult result = anyArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void matchWhenSubType() {
        Argument<SuperClass> argument = new Argument<SuperClass>(new TestClass(), null, TestClass.class);
        AnyArgumentMatcher<SuperClass> anyArgumentMatcher = new AnyArgumentMatcher<SuperClass>(SuperClass.class);

        MatchResult result = anyArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void noMatchWhenSuperType() {
        Argument<SuperClass> argument = new Argument<SuperClass>(new SuperClass(), null, SuperClass.class);
        AnyArgumentMatcher<SuperClass> anyArgumentMatcher = new AnyArgumentMatcher<SuperClass>(TestClass.class);

        MatchResult result = anyArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void noMatchWhenNullType() {
        Argument<TestClass> argument = new Argument<TestClass>(new TestClass(), null, TestClass.class);
        anyArgumentMatcher = new AnyArgumentMatcher<TestClass>(null);

        MatchResult result = anyArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void noMatchWhenNullArgument() {
        Argument<TestClass> argument = new Argument<TestClass>(null, null, TestClass.class);
        anyArgumentMatcher = new AnyArgumentMatcher<TestClass>(TestClass.class);

        MatchResult result = anyArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }


    public static class SuperClass {
    }

    public static class TestClass extends SuperClass {
    }
}
