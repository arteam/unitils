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
package org.unitils.mock.mockbehavior;

import org.unitils.mock.core.MockFactory;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.util.StackTraceService;
import org.unitils.mock.mockbehavior.impl.DummyValueReturningMockBehavior;
import org.unitils.mock.mockbehavior.impl.ExceptionThrowingMockBehavior;
import org.unitils.mock.mockbehavior.impl.StubMockBehavior;
import org.unitils.mock.mockbehavior.impl.ValueReturningMockBehavior;

/**
 * @author Tim Ducheyne
 */
public class MockBehaviorFactory {

    protected StackTraceService stackTraceService;
    protected ProxyService proxyService;


    public MockBehaviorFactory(StackTraceService stackTraceService, ProxyService proxyService) {
        this.stackTraceService = stackTraceService;
        this.proxyService = proxyService;
    }


    public MockBehavior createValueReturningMockBehavior(Object returnValue) {
        return new ValueReturningMockBehavior(returnValue);
    }

    public MockBehavior createExceptionThrowingMockBehavior(Throwable exception) {
        return new ExceptionThrowingMockBehavior(exception, stackTraceService);
    }

    public MockBehavior createExceptionThrowingMockBehavior(Class<? extends Throwable> exceptionClass) {
        Throwable exception = proxyService.createInitializedOrUninitializedInstanceOfType(exceptionClass);
        exception.fillInStackTrace();
        return createExceptionThrowingMockBehavior(exception);
    }

    public MockBehavior createStubMockBehavior() {
        return new StubMockBehavior();
    }

    public MockBehavior createDummyValueReturningMockBehavior(MockFactory mockFactory) {
        return new DummyValueReturningMockBehavior(mockFactory);
    }
}
