/*
 * Copyright Unitils.org
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

import org.unitils.mock.core.proxy.ProxyInvocation;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class BehaviorDefiningInvocations {

    protected boolean removeWhenUsed;
    protected List<BehaviorDefiningInvocation> behaviorDefiningInvocations = new ArrayList<BehaviorDefiningInvocation>();


    public BehaviorDefiningInvocations(boolean removeWhenUsed) {
        this.removeWhenUsed = removeWhenUsed;
    }


    public void addBehaviorDefiningInvocation(BehaviorDefiningInvocation behaviorDefiningInvocation) {
        behaviorDefiningInvocations.add(behaviorDefiningInvocation);
    }


    public void clear() {
        behaviorDefiningInvocations.clear();
    }


    /**
     * First we find all behavior defining invocations that have matching argument matchers. From these matching
     * invocations, we then count the nr of not-null (default) arguments. The result will be the
     * invocation with the lowest number of not-null arguments. If there are multiple invocations with the same
     * number, the first one is returned. E.g.
     *
     * myMethod(null, null);
     * myMethod("a", null);
     *
     * The second one will be returned if the given proxy invocation has the value "a" as first argument.
     *
     * @param proxyInvocation The actual invocation to match with, not null
     * @return The behavior defining invocation that matches best with the actual invocation, null if none found
     */
    public BehaviorDefiningInvocation getMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) {
        BehaviorDefiningInvocation bestMatchingBehaviorDefiningInvocation = null;
        int bestMatchingNrOfNotNullArguments = -1;

        for (BehaviorDefiningInvocation behaviorDefiningInvocation : behaviorDefiningInvocations) {
            if (!behaviorDefiningInvocation.matches(proxyInvocation)) {
                continue;
            }
            int nrOfNotNullArguments = behaviorDefiningInvocation.getNrOfNotNullArguments();
            if (nrOfNotNullArguments > bestMatchingNrOfNotNullArguments) {
                bestMatchingBehaviorDefiningInvocation = behaviorDefiningInvocation;
                bestMatchingNrOfNotNullArguments = nrOfNotNullArguments;
            }
        }
        if (removeWhenUsed && bestMatchingBehaviorDefiningInvocation != null) {
            behaviorDefiningInvocations.remove(bestMatchingBehaviorDefiningInvocation);
        }
        return bestMatchingBehaviorDefiningInvocation;
    }


}