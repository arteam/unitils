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
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.NO_MATCH;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.SAME;

/**
 * @author Tim Ducheyne
 */
public class SameArgumentMatcherMatchesTest {

    private SameArgumentMatcher sameArgumentMatcher;


    @Test
    public void sameWhenSame() {
        List<String> list = asList("1", "2");
        sameArgumentMatcher = new SameArgumentMatcher(list);

        MatchResult result = sameArgumentMatcher.matches(list, null);
        assertEquals(SAME, result);
    }

    @Test
    public void noMatchWhenNotSame() {
        List<String> list = asList("1", "2");
        sameArgumentMatcher = new SameArgumentMatcher(list);

        MatchResult result = sameArgumentMatcher.matches(asList("1", "2"), null);
        assertEquals(NO_MATCH, result);
    }
}
