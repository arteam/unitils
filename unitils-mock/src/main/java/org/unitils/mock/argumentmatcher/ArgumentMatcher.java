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
package org.unitils.mock.argumentmatcher;

import org.unitils.mock.core.proxy.Argument;

/**
 * A matcher that can check whether a given argument matches certain criteria.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public abstract class ArgumentMatcher<T> {

    public static enum MatchResult {

        NO_MATCH(0),
        MATCH(1),
        SAME(2);

        protected int score;

        MatchResult(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }
    }

    /**
     * Returns NO_MATCH if the given argument does not match,
     * SAME if it is an exact match (e.g. same instance),
     * MATCH if it is a match (e.g. not null).
     *
     * @param argument The argument to match, not null
     * @return The match result, not null
     */
    public abstract MatchResult matches(Argument<T> argument);

    /**
     * Hook method. Called when the given argument resulted in the best match.
     *
     * @param argument The argument that was matched, not null
     */
    public void matched(Argument<T> argument) {
        // ignore by default
    }
}
