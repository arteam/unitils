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
import org.unitils.mock.core.proxy.Argument;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.util.CloneService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatcherRepositoryRegisterArgumentMatcherTest extends UnitilsJUnit4 {

    private ArgumentMatcherRepository argumentMatcherRepository;

    private Mock<ArgumentMatcherPositionFinder> argumentMatcherPositionFinderMock;
    private Mock<CloneService> cloneServiceMock;
    @Dummy
    private ArgumentMatcher argumentMatcher;
    private Method method;


    @Before
    public void initialize() throws Exception {
        argumentMatcherRepository = new ArgumentMatcherRepository(argumentMatcherPositionFinderMock.getMock(), cloneServiceMock.getMock());

        method = MyInterface.class.getMethod("method");

        cloneServiceMock.returns("cloned arg1").createDeepClone("arg1");
        cloneServiceMock.returns("cloned arg3").createDeepClone("arg3");
    }


    @Test
    public void registerArgumentMatcher() {
        ProxyInvocation proxyInvocation = createProxyInvocation(method, 333, "arg1");
        argumentMatcherPositionFinderMock.returns(asList(0)).getArgumentMatcherIndexes(proxyInvocation, 111, 333, 1);
        argumentMatcherRepository.startMatchingInvocation(111);

        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
        List<ArgumentMatcher<?>> result = argumentMatcherRepository.finishMatchingInvocation(proxyInvocation);
        assertEquals(asList(argumentMatcher), result);
    }

    @Test
    public void argumentsWithoutArgumentMatcherAreWrappedInDefaultArgumentMatcher() {
        ProxyInvocation proxyInvocation = createProxyInvocation(method, 333, "arg1", "arg2", "arg3");
        argumentMatcherPositionFinderMock.returns(asList(1)).getArgumentMatcherIndexes(proxyInvocation, 111, 333, 1);
        argumentMatcherRepository.startMatchingInvocation(111);

        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
        List<ArgumentMatcher<?>> result = argumentMatcherRepository.finishMatchingInvocation(proxyInvocation);
        assertReflectionEquals(asList(new DefaultArgumentMatcher<String>("arg1", "cloned arg1"), argumentMatcher, new DefaultArgumentMatcher<String>("arg3", "cloned arg3")), result);
    }

    @Test
    public void exceptionWhenNoMatchingInvocationWasStarted() {
        try {
            argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to register argument matcher. Argument matchers can only be used when defining behavior for a mock (e.g. returns) or when doing an assert on a mock. Argument matcher: " + argumentMatcher.getClass(), e.getMessage());
        }
    }

    @Test
    public void ignoreWhenNullArgumentMatcher() {
        argumentMatcherRepository.registerArgumentMatcher(null);
    }


    private ProxyInvocation createProxyInvocation(Method method, int lineNr, String... argumentValues) {
        StackTraceElement element = new StackTraceElement(MyInterface.class.getName(), "method1", "file", lineNr);
        StackTraceElement[] stackTrace = new StackTraceElement[]{element};
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        for (String argumentValue : argumentValues) {
            arguments.add(new Argument<String>(argumentValue, argumentValue, String.class));
        }
        return new ProxyInvocation("mockName", null, method, arguments, stackTrace);
    }

    private static interface MyInterface {

        void method();
    }
}
