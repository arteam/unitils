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
package org.unitils.mock.mockbehavior.impl;

import org.unitils.mock.Mock;
import org.unitils.mock.core.BehaviorDefiningInvocation;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

/**
 * @author Tim Ducheyne
 */
public class ChainedMockBehavior implements MockBehavior {

    protected Mock<?> mock;
    protected BehaviorDefiningInvocation behaviorDefiningInvocation;
    protected MockBehavior originalMockBehavior;


    public ChainedMockBehavior(Mock<?> mock, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        this.mock = mock;
        this.behaviorDefiningInvocation = behaviorDefiningInvocation;
        this.originalMockBehavior = behaviorDefiningInvocation.getMockBehavior();
    }


    /**
     * Installs the chain by letting the mock return an intermediary mock that will perform the original behavior
     */
    public void installChain() {
        behaviorDefiningInvocation.setMockBehavior(new ValueReturningMockBehavior(mock.getMock()));
    }

    public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
        return originalMockBehavior.execute(proxyInvocation);
    }
}