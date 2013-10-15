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

import org.unitils.mock.PartialMock;
import org.unitils.mock.annotation.MatchStatement;
import org.unitils.mock.core.matching.MatchingInvocationHandlerFactory;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.MockBehaviorFactory;

/**
 * Implementation of a PartialMock.
 * For a partial mock, if a method is called that is not mocked, the original behavior will be called.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class PartialMockObject<T> extends MockObject<T> implements PartialMock<T> {


    public PartialMockObject(String name, Class<T> type, T proxy, T matchingProxy, boolean chained, BehaviorDefiningInvocations behaviorDefiningInvocations, MatchingProxyInvocationHandler matchingProxyInvocationHandler, MockBehaviorFactory mockBehaviorFactory, MatchingInvocationHandlerFactory matchingInvocationHandlerFactory) {
        super(name, type, proxy, matchingProxy, chained, behaviorDefiningInvocations, matchingProxyInvocationHandler, mockBehaviorFactory, matchingInvocationHandlerFactory);
    }


    /**
     * Stubs out (removes) the behavior of the method when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.stub().method1();
     * <p/>
     * will not invoke the actual behavior of method1.
     * <p/>
     * If the method has a return type, a default value will be returned.
     * <p/>
     * Note: stubbed methods can still be asserted afterwards: e.g.
     * <p/>
     * mock.assertInvoked().method1();
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T stub() {
        MockBehavior mockBehavior = mockBehaviorFactory.createStubMockBehavior();
        return startBehaviorMatchingInvocation(mockBehavior, false);
    }
}