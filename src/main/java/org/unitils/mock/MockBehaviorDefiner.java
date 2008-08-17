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

import org.unitils.mock.action.Action;
import org.unitils.mock.invocationhandler.impl.AlwaysMatchingMockBehaviorInvocationHandler;
import org.unitils.mock.invocationhandler.InvocationHandler;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.invocationhandler.impl.OneTimeMatchingMockBehaviorInvocationHandler;
import org.unitils.mock.syntax.MockBehaviorBuilder;
import static org.unitils.mock.util.ProxyUtil.createMockObjectProxy;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockBehaviorDefiner<T> {

    private MockBehaviorBuilder mockBehaviorBuilder = MockBehaviorBuilder.getInstance();

    private MockObject<T> mockObject;


    public MockBehaviorDefiner(MockObject<T> mockObject) {
        this.mockObject = mockObject;
    }


    public T returns(Object returnValue) {
        mockBehaviorBuilder.registerReturnValue(returnValue);
        return createOneTimeMatchingMockBehaviorProxy();
    }


    public T raises(Throwable exception) {
        mockBehaviorBuilder.registerThrownException(exception);
        return createOneTimeMatchingMockBehaviorProxy();
    }


    public T performs(Action action) {
        mockBehaviorBuilder.registerPerformedAction(action);
        return createOneTimeMatchingMockBehaviorProxy();
    }


    public T alwaysReturns(Object returnValue) {
        mockBehaviorBuilder.registerReturnValue(returnValue);
        return createAlwaysMatchingMockBehaviorProxy();
    }


    public T alwaysRaises(Throwable exception) {
        mockBehaviorBuilder.registerThrownException(exception);
        return createAlwaysMatchingMockBehaviorProxy();
    }


    public T alwaysPerforms(Action action) {
        mockBehaviorBuilder.registerPerformedAction(action);
        return createAlwaysMatchingMockBehaviorProxy();
    }


    protected T createOneTimeMatchingMockBehaviorProxy() {
        InvocationHandler invocationHandler = new OneTimeMatchingMockBehaviorInvocationHandler<T>(mockObject);
        return createMockObjectProxy(mockObject, invocationHandler);
    }


    protected T createAlwaysMatchingMockBehaviorProxy() {
        InvocationHandler invocationHandler = new AlwaysMatchingMockBehaviorInvocationHandler<T>(mockObject);
        return createMockObjectProxy(mockObject, invocationHandler);
    }
}
