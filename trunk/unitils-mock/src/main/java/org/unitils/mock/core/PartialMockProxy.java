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

import org.unitils.mock.core.matching.MatchingInvocationBuilder;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.OriginalBehaviorInvokingMockBehavior;

public class PartialMockProxy<T> extends MockProxy<T> {

    /* The instance to invoke the behavior on, null for the proxied class */
    protected Object mockedInstance;


    public PartialMockProxy(String mockName, Class<T> mockedType, Object mockedInstance, BehaviorDefiningInvocations oneTimeMatchingBehaviorDefiningInvocations, BehaviorDefiningInvocations alwaysMatchingBehaviorDefiningInvocations, Scenario scenario, MatchingInvocationBuilder syntaxMonitor) {
        super(mockName, mockedType, oneTimeMatchingBehaviorDefiningInvocations, alwaysMatchingBehaviorDefiningInvocations, scenario, syntaxMonitor);
        this.mockedInstance = mockedInstance;
    }


    @Override
    protected MockBehavior getDefaultMockBehavior(ProxyInvocation proxyInvocation) {
        return new OriginalBehaviorInvokingMockBehavior(mockedInstance);
    }

}