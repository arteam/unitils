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

import org.unitils.mock.core.proxy.ProxyInvocation;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class BehaviorDefiningInvocations {

    /* Mock behaviors that are removed once they have been matched */
    protected List<BehaviorDefiningInvocation> behaviorDefiningInvocations = new ArrayList<BehaviorDefiningInvocation>();


    public List<BehaviorDefiningInvocation> getBehaviorDefiningInvocations() {
        return behaviorDefiningInvocations;
    }

    public void addBehaviorDefiningInvocation(BehaviorDefiningInvocation behaviorDefiningInvocation) {
        behaviorDefiningInvocations.add(behaviorDefiningInvocation);
    }

    public void reset() {
        behaviorDefiningInvocations.clear();
    }

    /**
     * First we find all behavior defining invocations that have matching argument matchers and take the one with the highest
     * matching score (identity match scores higher than an equals match). If there are 2 invocations with the same score,
     * we take the invocation with the lowest nr of not-null (default) arguments.  E.g.
     * <p/>
     * myMethod(null, null);
     * myMethod("a", null);
     * <p/>
     * The second one will be returned if the given proxy invocation has the value "a" as first argument.
     * If both have the same nr of not-null arguments, the last one is returned for always matching behavior.
     * This way you can still override behavior that was set up before.
     * For one time matching behavior we always return the first match.
     * E.g.
     * <p/>
     * myMock.returns(1).myMethod();
     * myMock.returns(2).myMethod();
     * <p/>
     * Will always return 2 since the behavior is overridden (last one wins)
     * <p/>
     * myMock.onceReturns(1).myMethod();
     * myMock.onceReturns(2).myMethod();
     * <p/>
     * Will first return 1 and then return 2 (order is kept)
     *
     * @param proxyInvocation The actual invocation to match with, not null
     * @return The behavior defining invocation that matches best with the actual invocation, null if none found
     */
    public BehaviorDefiningInvocation getMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) {
        BehaviorDefiningInvocation bestMatchingBehaviorDefiningInvocation = null;
        int bestMatchingScore = -1;

        int count = behaviorDefiningInvocations.size();
        for (int i = count - 1; i >= 0; i--) {
            BehaviorDefiningInvocation behaviorDefiningInvocation = behaviorDefiningInvocations.get(i);
            int matchingScore = behaviorDefiningInvocation.matches(proxyInvocation);
            if (matchingScore == -1) {
                // no match
                continue;
            }
            if (matchingScore < bestMatchingScore) {
                // there is a better match
                continue;
            }
            if (matchingScore > bestMatchingScore) {
                // better match
                bestMatchingScore = matchingScore;
                bestMatchingBehaviorDefiningInvocation = behaviorDefiningInvocation;
                continue;
            }
            if (matchingScore == bestMatchingScore) {
                // same score, nr of not-null values determines the best match
                int nrOfNotNullArguments = behaviorDefiningInvocation.getNrOfNotNullArguments();
                int bestMatchingNrOfNotNullArguments = bestMatchingBehaviorDefiningInvocation.getNrOfNotNullArguments();
                if (nrOfNotNullArguments > bestMatchingNrOfNotNullArguments) {
                    bestMatchingBehaviorDefiningInvocation = behaviorDefiningInvocation;
                    continue;
                }
                // same score, one time matcher wins over always matcher + first one time matcher wins
                if (nrOfNotNullArguments == bestMatchingNrOfNotNullArguments) {
                    if (behaviorDefiningInvocation.isOneTimeMatch()) {
                        bestMatchingBehaviorDefiningInvocation = behaviorDefiningInvocation;
                    }
                }
            }
        }
        if (bestMatchingBehaviorDefiningInvocation != null) {
            bestMatchingBehaviorDefiningInvocation.matched(proxyInvocation);
            if (bestMatchingBehaviorDefiningInvocation.isOneTimeMatch()) {
                behaviorDefiningInvocations.remove(bestMatchingBehaviorDefiningInvocation);
            }
        }
        return bestMatchingBehaviorDefiningInvocation;
    }
}