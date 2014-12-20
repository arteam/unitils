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
package org.unitils.mock.core;

import org.unitils.mock.mockbehavior.MockBehavior;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class BehaviorDefiningInvocation extends MatchingInvocation {

    /* The behavior to execute */
    protected MockBehavior mockBehavior;
    /* When true, the behavior will only match once */
    protected boolean oneTimeMatch;


    /**
     * Creates a behavior defining invocation for the given prosy invocation.
     * <p/>
     * The argumentsAtInvocationTime should be copies (deep clones) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference).
     *
     * @param matchingInvocation The matching invocation, not null
     * @param mockBehavior       The behavior to execute, not null
     * @param oneTimeMatch       When true, the behavior will only match once
     */
    public BehaviorDefiningInvocation(MatchingInvocation matchingInvocation, MockBehavior mockBehavior, boolean oneTimeMatch) {
        super(matchingInvocation, matchingInvocation.argumentMatchers);
        this.mockBehavior = mockBehavior;
        this.oneTimeMatch = oneTimeMatch;
    }


    /**
     * @return The behavior to execute, not null
     */
    public MockBehavior getMockBehavior() {
        return mockBehavior;
    }

    /**
     * @param mockBehavior The behavior to execute, not null
     */
    public void setMockBehavior(MockBehavior mockBehavior) {
        this.mockBehavior = mockBehavior;
    }

    public boolean isOneTimeMatch() {
        return oneTimeMatch;
    }
}