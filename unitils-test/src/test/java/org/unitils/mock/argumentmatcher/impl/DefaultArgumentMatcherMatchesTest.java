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
public class DefaultArgumentMatcherMatchesTest {

    private DefaultArgumentMatcher defaultArgumentMatcher;


    @Test
    public void sameWhenSame() {
        List<String> list = asList("1", "2");
        defaultArgumentMatcher = new DefaultArgumentMatcher(list, null);

        MatchResult result = defaultArgumentMatcher.matches(list, list);
        assertEquals(SAME, result);
    }

    @Test
    public void matchWhenEqualAtInvocationTime() {
        defaultArgumentMatcher = new DefaultArgumentMatcher(null, asList("1", "2"));

        MatchResult result = defaultArgumentMatcher.matches(asList("1", "2", "3"), asList("1", "2"));
        assertEquals(MATCH, result);
    }

    @Test
    public void matchWhenEqualByReflectionAtInvocationTime() {
        defaultArgumentMatcher = new DefaultArgumentMatcher(null, new TestClass("222"));

        MatchResult result = defaultArgumentMatcher.matches(new TestClass("111"), new TestClass("222"));
        assertEquals(MATCH, result);
    }

    @Test
    public void noMatchWhenNotEqualAtInvocationTime() {
        defaultArgumentMatcher = new DefaultArgumentMatcher(null, "111");

        MatchResult result = defaultArgumentMatcher.matches("111", "222");
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void noMatchWhenNotEqualByReflectionAtInvocationTime() {
        defaultArgumentMatcher = new DefaultArgumentMatcher(new TestClass("xxx"), new TestClass("value"));

        MatchResult result = defaultArgumentMatcher.matches(new TestClass("xxx"), new TestClass("xxx"));
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void zeroCharacterIsNotIgnored() {
        defaultArgumentMatcher = new DefaultArgumentMatcher(null, (char) 0);

        MatchResult result = defaultArgumentMatcher.matches((char) 5, (char) 5);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void zeroIsNotIgnored() {
        defaultArgumentMatcher = new DefaultArgumentMatcher(null, 0);

        MatchResult result = defaultArgumentMatcher.matches(5, 5);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void falseIsNotIgnored() {
        defaultArgumentMatcher = new DefaultArgumentMatcher(false, false);

        MatchResult result = defaultArgumentMatcher.matches(true, true);
        assertEquals(NO_MATCH, result);
    }

    @Test
    public void nullIsIgnored() {
        defaultArgumentMatcher = new DefaultArgumentMatcher(null, null);

        MatchResult result = defaultArgumentMatcher.matches("value", "value");
        assertEquals(MATCH, result);
    }

    @Test
    public void orderIsIgnored() {
        defaultArgumentMatcher = new DefaultArgumentMatcher(null, asList("1", "2", "3"));

        MatchResult result = defaultArgumentMatcher.matches(asList("3", "2", "1"), asList("3", "2", "1"));
        assertEquals(MATCH, result);
    }

    @Test
    public void defaultsAreIgnored() {
        defaultArgumentMatcher = new DefaultArgumentMatcher(null, new TestClass(null));

        MatchResult result = defaultArgumentMatcher.matches(new TestClass("value"), new TestClass("value"));
        assertEquals(MATCH, result);
    }


    private static class TestClass {

        private String value;

        private TestClass(String value) {
            this.value = value;
        }
    }
}
