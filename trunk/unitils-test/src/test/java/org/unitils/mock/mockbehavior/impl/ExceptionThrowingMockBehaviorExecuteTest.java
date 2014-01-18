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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.util.StackTraceService;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ExceptionThrowingMockBehaviorExecuteTest extends UnitilsJUnit4 {

    private ExceptionThrowingMockBehavior exceptionThrowingMockBehavior;

    private Mock<StackTraceService> stackTraceServiceMock;
    private NullPointerException exception;
    private ProxyInvocation proxyInvocation;
    private StackTraceElement[] stackTrace;


    @Before
    public void initialize() throws Exception {
        exception = new NullPointerException("exception");
        exceptionThrowingMockBehavior = new ExceptionThrowingMockBehavior(exception, stackTraceServiceMock.getMock());

        Method method = MyInterface.class.getMethod("method");
        proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);
        stackTrace = new StackTraceElement[]{new StackTraceElement("class", "method", "file", 333)};
    }


    @Test
    public void execute() throws Exception {
        stackTraceServiceMock.returns(stackTrace).getInvocationStackTrace(Mock.class, false);
        try {
            exceptionThrowingMockBehavior.execute(proxyInvocation);
            fail("NullPointerException expected");
        } catch (Throwable e) {
            assertSame(exception, e);
            assertReflectionEquals(stackTrace, e.getStackTrace());
        }
    }

    @Test
    public void originalStackTraceWhenNotCalledFromMock() throws Exception {
        stackTraceServiceMock.returns(null).getInvocationStackTrace(Mock.class, false);
        try {
            exceptionThrowingMockBehavior.execute(proxyInvocation);
            fail("NullPointerException expected");
        } catch (Throwable e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            assertEquals(ExceptionThrowingMockBehaviorExecuteTest.class.getName(), stackTrace[0].getClassName());
            assertEquals("initialize", stackTrace[0].getMethodName());
        }
    }


    private static interface MyInterface {

        void method();
    }
}

