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

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.MATCH;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.NO_MATCH;

/**
 * @author Tim Ducheyne
 */
public class LenEqArgumentMatcherMatchesTest {

    @Test
    public void matchWhenEqual() {
        Argument<List> argument = new Argument<List>(asList("1", "2", "3"), asList("1", "2"), List.class);
        LenEqArgumentMatcher<List> lenEqArgumentMatcher = new LenEqArgumentMatcher<List>(asList("1", "2"));

        MatchResult result = lenEqArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void matchWhenEqualByReflection() {
        Argument<TestClass> argument = new Argument<TestClass>(new TestClass("value"), new TestClass("value"), TestClass.class);
        LenEqArgumentMatcher<TestClass> lenEqArgumentMatcher = new LenEqArgumentMatcher<TestClass>(new TestClass("value"));

        MatchResult result = lenEqArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void noMatchWhenNotEqual() {
        Argument<String> argument = new Argument<String>("222", "222", String.class);
        LenEqArgumentMatcher<String> lenEqArgumentMatcher = new LenEqArgumentMatcher<String>("111");

        MatchResult result = lenEqArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void noMatchWhenNotEqualByReflection() {
        Argument<TestClass> argument = new Argument<TestClass>(new TestClass("xxx"), new TestClass("xxx"), TestClass.class);
        LenEqArgumentMatcher<TestClass> lenEqArgumentMatcher = new LenEqArgumentMatcher<TestClass>(new TestClass("value"));

        MatchResult result = lenEqArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void zeroCharacterIsNotIgnored() {
        Argument<Character> argument = new Argument<Character>((char) 5, (char) 5, Character.class);
        LenEqArgumentMatcher<Character> lenEqArgumentMatcher = new LenEqArgumentMatcher<Character>((char) 0);

        MatchResult result = lenEqArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void zeroIsNotIgnored() {
        Argument<Integer> argument = new Argument<Integer>(5, 5, Integer.class);
        LenEqArgumentMatcher<Integer> lenEqArgumentMatcher = new LenEqArgumentMatcher<Integer>(0);

        MatchResult result = lenEqArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void falseIsNotIgnored() {
        Argument<Boolean> argument = new Argument<Boolean>(true, true, Boolean.class);
        LenEqArgumentMatcher<Boolean> lenEqArgumentMatcher = new LenEqArgumentMatcher<Boolean>(false);

        MatchResult result = lenEqArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void nullIsIgnored() {
        Argument<String> argument = new Argument<String>("value", "value", String.class);
        LenEqArgumentMatcher<String> lenEqArgumentMatcher = new LenEqArgumentMatcher<String>(null);

        MatchResult result = lenEqArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void orderIsIgnored() {
        Argument<List> argument = new Argument<List>(asList("3", "2", "1"), asList("3", "2", "1"), List.class);
        LenEqArgumentMatcher<List> lenEqArgumentMatcher = new LenEqArgumentMatcher<List>(asList("1", "2", "3"));

        MatchResult result = lenEqArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void defaultsAreIgnored() {
        Argument<TestClass> argument = new Argument<TestClass>(new TestClass("value"), new TestClass("value"), TestClass.class);
        LenEqArgumentMatcher<TestClass> lenEqArgumentMatcher = new LenEqArgumentMatcher<TestClass>(new TestClass(null));

        MatchResult result = lenEqArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }


    private static class TestClass {

        private String value;

        private TestClass(String value) {
            this.value = value;
        }
    }
}
