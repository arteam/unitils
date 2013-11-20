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
package org.unitils.mock.core.proxy.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.core.MatchingInvocation;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.util.StackTraceService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class MatchingProxyInvocationHandleInvocationTest extends UnitilsJUnit4 {

    private MatchingProxyInvocationHandler matchingProxyInvocationHandler;

    private Mock<ArgumentMatcherRepository> argumentMatcherRepositoryMock;
    private Mock<MatchingInvocationHandler> matchingInvocationHandlerMock;
    private Mock<StackTraceService> stackTraceServiceMock;
    @Dummy
    private ArgumentMatcher<?> argumentMatcher;

    private StackTraceElement[] stackTrace;
    private ProxyInvocation proxyInvocation;


    @Before
    public void initialize() {
        matchingProxyInvocationHandler = new MatchingProxyInvocationHandler(argumentMatcherRepositoryMock.getMock(), stackTraceServiceMock.getMock());


        StackTraceElement stackTraceElement1 = new StackTraceElement("class1", "method1", "file1", 111);
        StackTraceElement stackTraceElement2 = new StackTraceElement("class2", "method2", "file2", 222);
        stackTrace = new StackTraceElement[]{stackTraceElement1, stackTraceElement2};
        proxyInvocation = new ProxyInvocation(null, null, null, null, stackTrace);
        stackTraceServiceMock.returns(stackTrace).getInvocationStackTrace(Mock.class);
    }


    @Test
    public void handleInvocation() {
        List<ArgumentMatcher<?>> argumentMatchers = new ArrayList<ArgumentMatcher<?>>();
        argumentMatchers.add(argumentMatcher);
        argumentMatcherRepositoryMock.returns(argumentMatchers).finishMatchingInvocation(proxyInvocation);
        matchingInvocationHandlerMock.returns("result").handleInvocation(new MatchingInvocation(proxyInvocation, argumentMatchers));
        matchingProxyInvocationHandler.startMatchingInvocation("mock", true, matchingInvocationHandlerMock.getMock());

        Object result = matchingProxyInvocationHandler.handleInvocation(proxyInvocation);
        assertEquals("result", result);
        matchingProxyInvocationHandler.assertPreviousMatchingInvocationCompleted();
    }

    @Test
    public void exceptionWhenNoMatchingInvocationWasStarted() {
        try {
            matchingProxyInvocationHandler.handleInvocation(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unexpected matching proxy invocation. Expected following syntax 'mock'.'matching method'.'method'. E.g. myMock.returns().myMethod();", e.getMessage());
            assertReflectionEquals(stackTrace, e.getStackTrace());
        }
    }

    @Test
    public void stillResetWhenExceptionOccurs() {
        argumentMatcherRepositoryMock.raises(new NullPointerException("expected")).finishMatchingInvocation(proxyInvocation);
        matchingProxyInvocationHandler.startMatchingInvocation("mock", true, matchingInvocationHandlerMock.getMock());
        try {
            matchingProxyInvocationHandler.handleInvocation(proxyInvocation);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            matchingProxyInvocationHandler.assertPreviousMatchingInvocationCompleted();
        }
    }
}
