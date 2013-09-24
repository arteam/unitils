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

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.*;

/**
 * @author Tim Ducheyne
 */
public class EqualsArgumentMatcherMatchesTest {

    private EqualsArgumentMatcher equalsArgumentMatcher;


    @Test
    public void matchWhenEquals() {
        equalsArgumentMatcher = new EqualsArgumentMatcher(asList("1", "2"));

        MatchResult result = equalsArgumentMatcher.matches(asList("1", "2"), null);
        assertEquals(MATCH, result);
    }

    @Test
    public void noMatchWhenNotEquals() {
        equalsArgumentMatcher = new EqualsArgumentMatcher(asList("1", "2"));

        MatchResult result = equalsArgumentMatcher.matches(asList("1", "2", "3"), null);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void sameWhenBothNull() {
        equalsArgumentMatcher = new EqualsArgumentMatcher(null);

        MatchResult result = equalsArgumentMatcher.matches(null, null);
        assertEquals(SAME, result);
    }

    @Test
    public void sameWhenSameValue() {
        List<String> list = asList("1", "2");
        equalsArgumentMatcher = new EqualsArgumentMatcher(list);

        MatchResult result = equalsArgumentMatcher.matches(list, null);
        assertEquals(SAME, result);
    }

    @Test
    public void noMatchWhenNullExpected() {
        equalsArgumentMatcher = new EqualsArgumentMatcher(null);

        MatchResult result = equalsArgumentMatcher.matches("value", null);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void noMatchWhenNullArgument() {
        equalsArgumentMatcher = new EqualsArgumentMatcher("value");

        MatchResult result = equalsArgumentMatcher.matches(null, null);
        assertEquals(NO_MATCH, result);
    }
}
