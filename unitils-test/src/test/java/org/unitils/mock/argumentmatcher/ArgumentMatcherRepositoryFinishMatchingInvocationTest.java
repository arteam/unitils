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
package org.unitils.mock.argumentmatcher;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.argumentmatcher.impl.DefaultArgumentMatcher;
import org.unitils.mock.core.proxy.CloneService;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatcherRepositoryFinishMatchingInvocationTest extends UnitilsJUnit4 {

    private ArgumentMatcherRepository argumentMatcherRepository;

    private Mock<ArgumentMatcherPositionFinder> argumentMatcherPositionFinderMock;
    private Mock<CloneService> cloneServiceMock;
    @Dummy
    private ArgumentMatcher argumentMatcher1;
    @Dummy
    private ArgumentMatcher argumentMatcher2;
    private Method method1;
    private Method method2;

    @Before
    public void initialize() throws Exception {
        argumentMatcherRepository = new ArgumentMatcherRepository(argumentMatcherPositionFinderMock.getMock(), cloneServiceMock.getMock());

        method1 = MyInterface.class.getMethod("method1");
        method2 = MyInterface.class.getMethod("method2");

        cloneServiceMock.returns("cloned arg1").createDeepClone("arg1");
        cloneServiceMock.returns("cloned arg3").createDeepClone("arg3");
    }


    @Test
    public void registerEndOfMatchingInvocation() {
        ProxyInvocation proxyInvocation = createProxyInvocation(method1, 333, "arg1", "arg2");
        argumentMatcherPositionFinderMock.returns(asList(0, 1)).getArgumentMatcherIndexes(proxyInvocation, 111, 333, 1);

        argumentMatcherRepository.startMatchingInvocation(111);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher1);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher2);

        List<ArgumentMatcher> result = argumentMatcherRepository.finishMatchingInvocation(proxyInvocation);
        assertEquals(asList(argumentMatcher1, argumentMatcher2), result);
    }

    @Test
    public void argumentsWithoutArgumentMatcherAreWrappedInDefaultArgumentMatcher() {
        ProxyInvocation proxyInvocation = createProxyInvocation(method1, 333, "arg1", "arg2", "arg3");
        argumentMatcherPositionFinderMock.returns(asList(1)).getArgumentMatcherIndexes(proxyInvocation, 111, 333, 1);

        argumentMatcherRepository.startMatchingInvocation(111);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher1);

        List<ArgumentMatcher> result = argumentMatcherRepository.finishMatchingInvocation(proxyInvocation);
        assertReflectionEquals(asList(new DefaultArgumentMatcher("arg1", "cloned arg1"), argumentMatcher1, new DefaultArgumentMatcher("arg3", "cloned arg3")), result);
    }

    @Test
    public void argumentMatchersAreResetAfterFinish() {
        ProxyInvocation proxyInvocation1 = createProxyInvocation(method1, 333, "arg1");
        ProxyInvocation proxyInvocation2 = createProxyInvocation(method1, 333, "arg1", "arg2");

        argumentMatcherRepository.startMatchingInvocation(111);
        List<ArgumentMatcher> result1 = argumentMatcherRepository.finishMatchingInvocation(proxyInvocation1);
        argumentMatcherRepository.startMatchingInvocation(222);
        List<ArgumentMatcher> result2 = argumentMatcherRepository.finishMatchingInvocation(proxyInvocation2);
        assertEquals(1, result1.size());
        assertEquals(2, result2.size());
    }

    @Test
    public void increaseIndexWhenTwoInvocationsOfSameMethodOnSameLine() {
        ProxyInvocation proxyInvocation1 = createProxyInvocation(method1, 333, "a");
        ProxyInvocation proxyInvocation2 = createProxyInvocation(method1, 333, "b");
        ProxyInvocation proxyInvocation3 = createProxyInvocation(method2, 333, "c");
        ProxyInvocation proxyInvocation4 = createProxyInvocation(method1, 333, "d");

        argumentMatcherRepository.startMatchingInvocation(111);
        argumentMatcherRepository.finishMatchingInvocation(proxyInvocation1);

        argumentMatcherRepository.startMatchingInvocation(111);
        argumentMatcherRepository.finishMatchingInvocation(proxyInvocation2);

        argumentMatcherRepository.startMatchingInvocation(111);
        argumentMatcherRepository.finishMatchingInvocation(proxyInvocation3);

        argumentMatcherRepository.startMatchingInvocation(111);
        argumentMatcherRepository.finishMatchingInvocation(proxyInvocation4);

        argumentMatcherPositionFinderMock.assertInvoked().getArgumentMatcherIndexes(proxyInvocation1, 111, 333, 1);
        argumentMatcherPositionFinderMock.assertInvoked().getArgumentMatcherIndexes(proxyInvocation2, 111, 333, 2);
        argumentMatcherPositionFinderMock.assertInvoked().getArgumentMatcherIndexes(proxyInvocation3, 111, 333, 1);
        argumentMatcherPositionFinderMock.assertInvoked().getArgumentMatcherIndexes(proxyInvocation4, 111, 333, 3);
    }


    @Test
    public void doNotIncreaseIndexWhenTwoInvocationsOfSameMethodOnDifferentLine() {
        ProxyInvocation proxyInvocation1 = createProxyInvocation(method1, 333, "a");
        ProxyInvocation proxyInvocation2 = createProxyInvocation(method1, 444, "b");
        ProxyInvocation proxyInvocation3 = createProxyInvocation(method1, 555, "c");

        argumentMatcherRepository.startMatchingInvocation(111);
        argumentMatcherRepository.finishMatchingInvocation(proxyInvocation1);

        argumentMatcherRepository.startMatchingInvocation(111);
        argumentMatcherRepository.finishMatchingInvocation(proxyInvocation2);

        argumentMatcherRepository.startMatchingInvocation(111);
        argumentMatcherRepository.finishMatchingInvocation(proxyInvocation3);

        argumentMatcherPositionFinderMock.assertInvoked().getArgumentMatcherIndexes(proxyInvocation1, 111, 333, 1);
        argumentMatcherPositionFinderMock.assertInvoked().getArgumentMatcherIndexes(proxyInvocation2, 111, 444, 1);
        argumentMatcherPositionFinderMock.assertInvoked().getArgumentMatcherIndexes(proxyInvocation3, 111, 555, 1);
    }

    @Test
    public void exceptionWhenNoStartOfMatchingInvocation() {
        ProxyInvocation proxyInvocation = createProxyInvocation(method1, 333, "arg1", "arg2");
        try {
            argumentMatcherRepository.finishMatchingInvocation(proxyInvocation);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to finish matching invocation: the matching invocation was not started first. Proxy method: method1", e.getMessage());
            assertReflectionEquals(proxyInvocation.getInvokedAtTrace(), e.getStackTrace());
        }
    }


    private ProxyInvocation createProxyInvocation(Method method, int lineNr, String... arguments) {
        StackTraceElement element = new StackTraceElement(MyInterface.class.getName(), "method1", "file", lineNr);
        StackTraceElement[] stackTrace = new StackTraceElement[]{element};
        return new ProxyInvocation("mockName", null, method, asList(arguments), asList(arguments), stackTrace);
    }


    private static interface MyInterface {

        void method1();

        void method2();
    }
}
