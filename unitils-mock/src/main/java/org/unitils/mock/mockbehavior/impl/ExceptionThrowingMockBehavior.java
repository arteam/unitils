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

import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.util.StackTraceService;
import org.unitils.mock.mockbehavior.ValidatableMockBehavior;

import java.util.Arrays;


/**
 * Mock behavior that throws a given exception.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ExceptionThrowingMockBehavior implements ValidatableMockBehavior {

    /* The exception to throw */
    protected Throwable exceptionToThrow;
    protected StackTraceService stackTraceService;


    /**
     * Creates the throwing behavior for the given exception.
     *
     * @param exceptionToThrow The exception, not null
     */
    public ExceptionThrowingMockBehavior(Throwable exceptionToThrow, StackTraceService stackTraceService) {
        this.exceptionToThrow = exceptionToThrow;
        this.stackTraceService = stackTraceService;
    }


    /**
     * Checks whether the mock behavior can be executed for the given invocation.
     * An exception is raised if the method is a void method or has a non-assignable return type.
     *
     * @param proxyInvocation The proxy method invocation, not null
     */
    public void assertCanExecute(ProxyInvocation proxyInvocation) throws UnitilsException {
        if (exceptionToThrow instanceof RuntimeException || exceptionToThrow instanceof Error) {
            return;
        }
        Class<?>[] exceptionTypes = proxyInvocation.getMethod().getExceptionTypes();
        for (Class<?> exceptionType : exceptionTypes) {
            if (exceptionType.isAssignableFrom(exceptionToThrow.getClass())) {
                return;
            }
        }
        throw new UnitilsException("Trying to make a method throw an exception that it doesn't declare. Exception type: " + exceptionToThrow.getClass() +
                (exceptionTypes.length > 0 ? ", declared exceptions: " + Arrays.toString(exceptionTypes) : ", no declared exceptions"));
    }

    /**
     * Executes the mock behavior.
     *
     * @param proxyInvocation The proxy method invocation, not null
     * @return Nothing
     */
    public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
        StackTraceElement[] invocationStackTraceWithoutMock = stackTraceService.getInvocationStackTrace(Mock.class, false);
        if (invocationStackTraceWithoutMock != null) {
            exceptionToThrow.setStackTrace(invocationStackTraceWithoutMock);
        }
        throw exceptionToThrow;
    }
}
