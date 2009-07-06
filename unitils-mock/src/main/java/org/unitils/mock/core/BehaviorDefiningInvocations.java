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

import org.unitils.mock.core.proxy.ProxyInvocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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


    public BehaviorDefiningInvocation getMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) {
        Iterator<BehaviorDefiningInvocation> iterator = behaviorDefiningInvocations.iterator();
        while (iterator.hasNext()) {
            BehaviorDefiningInvocation behaviorDefiningInvocation = iterator.next();
            if (behaviorDefiningInvocation.matches(proxyInvocation)) {
                if (removeWhenUsed) {
                    iterator.remove();
                }
                return behaviorDefiningInvocation;
            }
        }
        return null;
    }


}