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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.MATCH;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.NO_MATCH;

/**
 * @author Tim Ducheyne
 */
public class RefEqArgumentMatcherMatchesTest {

    private RefEqArgumentMatcher refEqArgumentMatcher;


    @Test
    public void matchWhenEqual() {
        refEqArgumentMatcher = new RefEqArgumentMatcher(asList("1", "2"));

        MatchResult result = refEqArgumentMatcher.matches(asList("1", "2", "3"), asList("1", "2"));
        assertEquals(MATCH, result);
    }

    @Test
    public void matchWhenEqualByReflection() {
        refEqArgumentMatcher = new RefEqArgumentMatcher(new TestClass("value"));

        MatchResult result = refEqArgumentMatcher.matches(new TestClass("value"), new TestClass("value"));
        assertEquals(MATCH, result);
    }

    @Test
    public void noMatchWhenNotEqual() {
        refEqArgumentMatcher = new RefEqArgumentMatcher("111");

        MatchResult result = refEqArgumentMatcher.matches("222", "222");
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void noMatchWhenNotEqualByReflection() {
        refEqArgumentMatcher = new RefEqArgumentMatcher(new TestClass("value"));

        MatchResult result = refEqArgumentMatcher.matches(new TestClass("xxx"), new TestClass("xxx"));
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void zeroCharacterIsNotIgnored() {
        refEqArgumentMatcher = new RefEqArgumentMatcher((char) 0);

        MatchResult result = refEqArgumentMatcher.matches((char) 5, (char) 5);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void zeroIsNotIgnored() {
        refEqArgumentMatcher = new RefEqArgumentMatcher(0);

        MatchResult result = refEqArgumentMatcher.matches(5, 5);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void falseIsNotIgnored() {
        refEqArgumentMatcher = new RefEqArgumentMatcher(false);

        MatchResult result = refEqArgumentMatcher.matches(true, true);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void nullIsNotIgnored() {
        refEqArgumentMatcher = new RefEqArgumentMatcher(null);

        MatchResult result = refEqArgumentMatcher.matches("value", "value");
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void orderIsNotIgnored() {
        refEqArgumentMatcher = new RefEqArgumentMatcher(asList("1", "2", "3"));

        MatchResult result = refEqArgumentMatcher.matches(asList("3", "2", "1"), asList("3", "2", "1"));
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void defaultsAreNotIgnored() {
        refEqArgumentMatcher = new RefEqArgumentMatcher(new TestClass(null));

        MatchResult result = refEqArgumentMatcher.matches(new TestClass("value"), new TestClass("value"));
        assertEquals(NO_MATCH, result);
    }


    private static class TestClass {

        private String value;

        private TestClass(String value) {
            this.value = value;
        }
    }
}
