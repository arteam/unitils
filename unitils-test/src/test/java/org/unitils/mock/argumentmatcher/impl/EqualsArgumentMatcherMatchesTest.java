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
public class EqualsArgumentMatcherMatchesTest {


    @Test
    public void matchWhenEquals() {
        Argument<List> argument = new Argument<List>(asList("1", "2"), null, List.class);
        EqualsArgumentMatcher<List> equalsArgumentMatcher = new EqualsArgumentMatcher<List>(asList("1", "2"));

        MatchResult result = equalsArgumentMatcher.matches(argument);
        assertEquals(MATCH, result);
    }

    @Test
    public void noMatchWhenNotEquals() {
        Argument<List> argument = new Argument<List>(asList("1", "2", "3"), null, List.class);
        EqualsArgumentMatcher<List> equalsArgumentMatcher = new EqualsArgumentMatcher<List>(asList("1", "2"));

        MatchResult result = equalsArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void sameWhenBothNull() {
        Argument<String> argument = new Argument<String>(null, null, String.class);
        EqualsArgumentMatcher<String> equalsArgumentMatcher = new EqualsArgumentMatcher<String>(null);

        MatchResult result = equalsArgumentMatcher.matches(argument);
        assertEquals(SAME, result);
    }

    @Test
    public void sameWhenSameValue() {
        List<String> list = asList("1", "2");
        Argument<List> argument = new Argument<List>(list, null, List.class);
        EqualsArgumentMatcher<List> equalsArgumentMatcher = new EqualsArgumentMatcher<List>(list);

        MatchResult result = equalsArgumentMatcher.matches(argument);
        assertEquals(SAME, result);
    }

    @Test
    public void noMatchWhenNullExpected() {
        Argument<String> argument = new Argument<String>("value", null, String.class);
        EqualsArgumentMatcher<String> equalsArgumentMatcher = new EqualsArgumentMatcher<String>(null);

        MatchResult result = equalsArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void noMatchWhenNullArgument() {
        Argument<String> argument = new Argument<String>(null, null, String.class);
        EqualsArgumentMatcher<String> equalsArgumentMatcher = new EqualsArgumentMatcher<String>("value");

        MatchResult result = equalsArgumentMatcher.matches(argument);
        assertEquals(NO_MATCH, result);
    }
}
