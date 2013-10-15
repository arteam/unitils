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
package org.unitils.mock.core.matching;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;
import org.unitils.mock.core.util.StackTraceService;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class MatchingProxyInvocationHandlerAssertPreviousMatchingInvocationCompletedTest extends UnitilsJUnit4 {

    private MatchingProxyInvocationHandler matchingProxyInvocationHandler;

    private Mock<ArgumentMatcherRepository> argumentMatcherRepositoryMock;
    private Mock<ProxyService> proxyServiceMock;
    private Mock<StackTraceService> stackTraceServiceMock;
    @Dummy
    private MatchingInvocationHandler matchingInvocationHandler;
    @Dummy
    private Map proxy;
    private StackTraceElement[] stackTrace;
    private StackTraceElement[] stackTraceWithoutFirst;


    @Before
    public void initialize() {
        matchingProxyInvocationHandler = new MatchingProxyInvocationHandler(argumentMatcherRepositoryMock.getMock(), proxyServiceMock.getMock(), stackTraceServiceMock.getMock());

        StackTraceElement stackTraceElement1 = new StackTraceElement("class1", "method1", "file1", 111);
        StackTraceElement stackTraceElement2 = new StackTraceElement("class2", "method2", "file2", 222);
        stackTrace = new StackTraceElement[]{stackTraceElement1, stackTraceElement2};
        stackTraceWithoutFirst = new StackTraceElement[]{stackTraceElement2};
        stackTraceServiceMock.returns(stackTrace).getInvocationStackTrace(Mock.class);
        stackTraceServiceMock.returns(stackTraceWithoutFirst).getStackTraceStartingFrom(stackTrace, 1);

        proxyServiceMock.returns(proxy).createProxy("mockName", false, null, Map.class);
    }


    @Test
    public void okWhenNoInvocationStarted() {
        matchingProxyInvocationHandler.assertPreviousMatchingInvocationCompleted();
    }

    @Test
    public void okWhenInvocationStartedThatDoesNotRequireProxyInvocation() {
        matchingProxyInvocationHandler.startMatchingInvocation("mockName", false, matchingInvocationHandler);
        matchingProxyInvocationHandler.assertPreviousMatchingInvocationCompleted();
    }

    @Test
    public void exceptionWhenPreviousMatchingInvocationWasNotCompleted() {
        matchingProxyInvocationHandler.startMatchingInvocation("mockName", true, matchingInvocationHandler);
        try {
            matchingProxyInvocationHandler.assertPreviousMatchingInvocationCompleted();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid syntax: mockName.method1() must be followed by a method invocation on the returned proxy. E.g. mockName.method1().myMethod();", e.getMessage());
            assertReflectionEquals(stackTraceWithoutFirst, e.getStackTrace());
            // behavior is reset after exception
            matchingProxyInvocationHandler.assertPreviousMatchingInvocationCompleted();
        }
    }
}
