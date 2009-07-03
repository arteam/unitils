/*
 * Copyright 2006-2009,  Unitils.org
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

import org.unitils.mock.proxy.ProxyInvocation;

import java.util.ArrayList;
import java.util.List;

public class BehaviorDefinition {


    /* Mock behaviors that are removed once they have been matched */
    protected List<BehaviorDefiningInvocation> oneTimeMatchingbehaviorDefiningInvocations = new ArrayList<BehaviorDefiningInvocation>();

    /* Mock behaviors that can be matched and re-used for several invocation */
    protected List<BehaviorDefiningInvocation> alwaysMatchingbehaviorDefiningInvocations = new ArrayList<BehaviorDefiningInvocation>();


    public BehaviorDefiningInvocation getMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        BehaviorDefiningInvocation behaviorDefiningInvocation = getUnusedOneTimeMatchingBehaviorDefiningInvocation(proxyInvocation);
        if (behaviorDefiningInvocation == null) {
            behaviorDefiningInvocation = getAlwaysMatchingBehaviorDefiningInvocation(proxyInvocation);
            if (behaviorDefiningInvocation == null) {
                return null;
            }
        }
        behaviorDefiningInvocation.markAsUsed();
        return behaviorDefiningInvocation;
    }


    public void addOneTimeMatchingbehaviorDefiningInvocation(BehaviorDefiningInvocation behaviorDefiningInvocation) {
        oneTimeMatchingbehaviorDefiningInvocations.add(behaviorDefiningInvocation);
    }

    public void addAlwaysMatchingbehaviorDefiningInvocation(BehaviorDefiningInvocation behaviorDefiningInvocation) {
        alwaysMatchingbehaviorDefiningInvocations.add(behaviorDefiningInvocation);
    }


    public void reset() {
        oneTimeMatchingbehaviorDefiningInvocations.clear();
        alwaysMatchingbehaviorDefiningInvocations.clear();
    }


    protected BehaviorDefiningInvocation getUnusedOneTimeMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) {
        for (BehaviorDefiningInvocation behaviorDefiningInvocation : oneTimeMatchingbehaviorDefiningInvocations) {
            if (behaviorDefiningInvocation.isUsed()) {
                continue;
            }
            if (behaviorDefiningInvocation.matches(proxyInvocation)) {
                return behaviorDefiningInvocation;
            }
        }
        return null;
    }

    protected BehaviorDefiningInvocation getAlwaysMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) {
        for (BehaviorDefiningInvocation behaviorDefiningInvocation : alwaysMatchingbehaviorDefiningInvocations) {
            if (behaviorDefiningInvocation.matches(proxyInvocation)) {
                return behaviorDefiningInvocation;
            }
        }
        return null;
    }


}