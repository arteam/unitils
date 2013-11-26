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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.util.StackTraceService;
import org.unitils.mock.mockbehavior.impl.ExceptionThrowingMockBehavior;

import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class MockBehaviorFactoryCreateExceptionThrowingMockBehaviorTest extends UnitilsJUnit4 {

    private MockBehaviorFactory mockBehaviorFactory;
    private Mock<ProxyService> proxyServiceMock;
    private Mock<IllegalArgumentException> exceptionMock;
    @Dummy
    private StackTraceService stackTraceService;


    @Before
    public void initialize() {
        mockBehaviorFactory = new MockBehaviorFactory(stackTraceService, proxyServiceMock.getMock());
    }


    @Test
    public void exceptionClass() {
        IllegalArgumentException expected = new IllegalArgumentException();
        proxyServiceMock.returns(exceptionMock).createInitializedOrUninitializedInstanceOfType(IllegalArgumentException.class);

        MockBehavior result = mockBehaviorFactory.createExceptionThrowingMockBehavior(IllegalArgumentException.class);
        assertTrue(result instanceof ExceptionThrowingMockBehavior);
        exceptionMock.assertInvoked().fillInStackTrace();
        assertPropertyReflectionEquals("exceptionToThrow", exceptionMock.getMock(), result);
        assertPropertyReflectionEquals("stackTraceService", stackTraceService, result);
    }

    @Test
    public void exceptionInstance() {
        IllegalArgumentException exception = new IllegalArgumentException();

        MockBehavior result = mockBehaviorFactory.createExceptionThrowingMockBehavior(exception);
        assertTrue(result instanceof ExceptionThrowingMockBehavior);
        assertPropertyReflectionEquals("exceptionToThrow", exception, result);
        assertPropertyReflectionEquals("stackTraceService", stackTraceService, result);
    }
}
