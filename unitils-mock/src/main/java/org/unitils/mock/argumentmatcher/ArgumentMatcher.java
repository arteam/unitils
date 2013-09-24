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

/**
 * A matcher that can check whether a given argument matches certain criteria.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface ArgumentMatcher {

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
     * Returns true if the given object matches this object's expected argument, false otherwise.
     * <p/>
     * The argumentAtInvocationTime is a copy (deep clone) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference).
     *
     * @param argument                 The argument that was used by reference
     * @param argumentAtInvocationTime Copy of the argument, taken at the time that the invocation was performed
     * @return The match result, not null
     */
    MatchResult matches(Object argument, Object argumentAtInvocationTime);

}
