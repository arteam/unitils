/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.mock;

import org.unitils.mock.core.*;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.ExceptionThrowingMockBehavior;
import org.unitils.mock.mockbehavior.impl.ValueReturningMockBehavior;
import org.unitils.mock.core.InvocationMatcherBuilder;
import static org.unitils.mock.util.ProxyUtil.createMockObjectProxy;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockBehaviorDefiner<T> {


    private MockObject<T> mockObject;

    private InvocationMatcherBuilder invocationMatcherBuilder;


    public MockBehaviorDefiner(MockObject<T> mockObject, InvocationMatcherBuilder invocationMatcherBuilder) {
        this.mockObject = mockObject;
        this.invocationMatcherBuilder = invocationMatcherBuilder;
    }


    public T returns(Object returnValue) {
        MockBehavior mockBehavior = new ValueReturningMockBehavior(returnValue);
        return createOneTimeMatchingMockBehaviorProxy(mockBehavior);
    }


    public T raises(Throwable exception) {
        MockBehavior mockBehavior = new ExceptionThrowingMockBehavior(exception);
        return createOneTimeMatchingMockBehaviorProxy(mockBehavior);
    }


    public T performs(MockBehavior mockBehavior) {
        return createOneTimeMatchingMockBehaviorProxy(mockBehavior);
    }


    public T alwaysReturns(Object returnValue) {
        MockBehavior mockBehavior = new ValueReturningMockBehavior(returnValue);
        return createAlwaysMatchingMockBehaviorProxy(mockBehavior);
    }


    public T alwaysRaises(Throwable exception) {
        MockBehavior mockBehavior = new ExceptionThrowingMockBehavior(exception);
        return createAlwaysMatchingMockBehaviorProxy(mockBehavior);
    }


    public T alwaysPerforms(MockBehavior mockBehavior) {
        return createAlwaysMatchingMockBehaviorProxy(mockBehavior);
    }


    protected T createOneTimeMatchingMockBehaviorProxy(MockBehavior mockBehavior) {
        return createMockObjectProxy(mockObject, new MockBehaviorInvocationHandler(mockBehavior, false));
    }


    protected T createAlwaysMatchingMockBehaviorProxy(MockBehavior mockBehavior) {
        return createMockObjectProxy(mockObject, new MockBehaviorInvocationHandler(mockBehavior, true));
    }


    protected class MockBehaviorInvocationHandler implements InvocationHandler {

        private MockBehavior mockBehavior;

        private boolean alwaysMatching;


        public MockBehaviorInvocationHandler(MockBehavior mockBehavior, boolean alwaysMatching) {
            this.mockBehavior = mockBehavior;
            this.alwaysMatching = alwaysMatching;
        }

        public Object handleInvocation(Invocation invocation) throws Throwable {
            InvocationMatcher invocationMatcher = invocationMatcherBuilder.createInvocationMatcher(invocation);

            if (alwaysMatching) {
                mockObject.registerAlwaysMatchingMockBehavior(invocationMatcher, mockBehavior);
            } else {
                mockObject.registerOneTimeMatchingMockBehavior(invocationMatcher, mockBehavior);
            }
            return null;
        }
    }


}
