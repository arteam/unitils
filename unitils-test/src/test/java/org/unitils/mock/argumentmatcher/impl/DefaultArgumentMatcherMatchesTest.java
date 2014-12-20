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
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.*;

/**
 * @author Tim Ducheyne
 */
public class DefaultArgumentMatcherMatchesTest {


    @Test
    public void sameWhenSame() {
        List<String> list = asList("1", "2");
        Argument<List> argument = new Argument<List>(list, null, List.class);
        DefaultArgumentMatcher<List> defaultArgumentMatcher = new DefaultArgumentMatcher<List>(list, null);

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(SAME, result);
    }

    @Test
    public void matchWhenEqualAtInvocationTime() {
        Argument<List> argument = new Argument<List>(asList("1", "2", "3"), asList("1", "2"), List.class);
        DefaultArgumentMatcher<List> defaultArgumentMatcher = new DefaultArgumentMatcher<List>(null, asList("1", "2"));

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void matchWhenEqualByReflectionAtInvocationTime() {
        Argument<TestClass> argument = new Argument<TestClass>(new TestClass("111"), new TestClass("222"), TestClass.class);
        DefaultArgumentMatcher<TestClass> defaultArgumentMatcher = new DefaultArgumentMatcher<TestClass>(null, new TestClass("222"));

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void noMatchWhenNotEqualAtInvocationTime() {
        Argument<String> argument = new Argument<String>("111", "222", String.class);
        DefaultArgumentMatcher<String> defaultArgumentMatcher = new DefaultArgumentMatcher<String>(null, "111");

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void noMatchWhenNotEqualByReflectionAtInvocationTime() {
        Argument<TestClass> argument = new Argument<TestClass>(new TestClass("xxx"), new TestClass("xxx"), TestClass.class);
        DefaultArgumentMatcher<TestClass> defaultArgumentMatcher = new DefaultArgumentMatcher<TestClass>(new TestClass("xxx"), new TestClass("value"));

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void zeroCharacterIsNotIgnored() {
        Argument<Character> argument = new Argument<Character>((char) 5, (char) 5, Character.class);
        DefaultArgumentMatcher<Character> defaultArgumentMatcher = new DefaultArgumentMatcher<Character>(null, (char) 0);

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void zeroIsNotIgnored() {
        Argument<Integer> argument = new Argument<Integer>(5, 5, Integer.class);
        DefaultArgumentMatcher<Integer> defaultArgumentMatcher = new DefaultArgumentMatcher<Integer>(null, 0);

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void falseIsNotIgnored() {
        Argument<Boolean> argument = new Argument<Boolean>(true, true, Boolean.class);
        DefaultArgumentMatcher<Boolean> defaultArgumentMatcher = new DefaultArgumentMatcher<Boolean>(false, false);

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void nullIsIgnored() {
        Argument<String> argument = new Argument<String>("value", "value", String.class);
        DefaultArgumentMatcher<String> defaultArgumentMatcher = new DefaultArgumentMatcher<String>(null, null);

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void orderIsIgnored() {
        Argument<List> argument = new Argument<List>(asList("3", "2", "1"), asList("3", "2", "1"), List.class);
        DefaultArgumentMatcher<List> defaultArgumentMatcher = new DefaultArgumentMatcher<List>(null, asList("1", "2", "3"));

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void defaultsAreIgnored() {
        Argument<TestClass> argument = new Argument<TestClass>(new TestClass("value"), new TestClass("value"), TestClass.class);
        DefaultArgumentMatcher<TestClass> defaultArgumentMatcher = new DefaultArgumentMatcher<TestClass>(null, new TestClass(null));

        MatchResult result = defaultArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }


    private static class TestClass {

        private String value;

        private TestClass(String value) {
            this.value = value;
        }
    }
}
