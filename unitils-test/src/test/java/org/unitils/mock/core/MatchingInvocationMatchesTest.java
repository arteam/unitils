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
package org.unitils.mock.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.core.proxy.Argument;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.*;

/**
 * @author Tim Ducheyne
 */
public class MatchingInvocationMatchesTest extends UnitilsJUnit4 {

    private MatchingInvocation matchingInvocation;

    private Mock<ArgumentMatcher<String>> argumentMatcherMock1;
    private Mock<ArgumentMatcher<String>> argumentMatcherMock2;
    @Dummy
    private Argument<String> argument1;
    @Dummy
    private Argument<String> argument2;
    @Dummy
    private Argument<String> argument3;

    private Method testMethod;
    private Method otherTestMethod;


    @Before
    public void initialize() throws Exception {
        testMethod = MyInterface.class.getMethod("testMethod");
        otherTestMethod = MyInterface.class.getMethod("otherTestMethod");

        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        ProxyInvocation proxyInvocation = new ProxyInvocation("1", null, null, testMethod, arguments, null);
        List<ArgumentMatcher<?>> argumentMatchers = new ArrayList<ArgumentMatcher<?>>();
        argumentMatchers.add(argumentMatcherMock1.getMock());
        argumentMatchers.add(argumentMatcherMock2.getMock());
        matchingInvocation = new MatchingInvocation(proxyInvocation, argumentMatchers);
    }


    @Test
    public void sumOfMatchingScoresWhenMatch() throws Exception {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(argument1);
        arguments.add(argument2);
        argumentMatcherMock1.returns(SAME).matches(argument1);
        argumentMatcherMock2.returns(MATCH).matches(argument2);
        ProxyInvocation proxyInvocation = createProxyInvocation("1", testMethod, arguments);

        int result = matchingInvocation.matches(proxyInvocation);
        assertEquals(3, result);
    }

    @Test
    public void minusOneWhenDifferentProxy() {
        ProxyInvocation proxyInvocation = createProxyInvocation("xxx", testMethod);

        int result = matchingInvocation.matches(proxyInvocation);
        assertEquals(-1, result);
    }

    @Test
    public void minusOneWhenDifferentMethod() {
        ProxyInvocation proxyInvocation = createProxyInvocation("1", otherTestMethod);

        int result = matchingInvocation.matches(proxyInvocation);
        assertEquals(-1, result);
    }

    @Test
    public void minusOneWhenArgumentMatcherDoesNotMatch() {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(argument1);
        arguments.add(argument2);
        argumentMatcherMock1.returns(SAME).matches(argument1);
        argumentMatcherMock2.returns(NO_MATCH).matches(argument2);
        ProxyInvocation proxyInvocation = createProxyInvocation("1", testMethod, arguments);

        int result = matchingInvocation.matches(proxyInvocation);
        assertEquals(-1, result);
    }


    private ProxyInvocation createProxyInvocation(String id, Method method) {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        return createProxyInvocation(id, method, arguments);
    }

    private ProxyInvocation createProxyInvocation(String id, Method method, List<Argument<?>> arguments) {
        return new ProxyInvocation(id, null, null, method, arguments, null);
    }


    private static interface MyInterface {

        void testMethod();

        void otherTestMethod();
    }
}
