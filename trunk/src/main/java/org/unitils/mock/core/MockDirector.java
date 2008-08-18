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
package org.unitils.mock.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.mock.MockBehaviorDefiner;
import org.unitils.mock.annotation.ArgumentMatcher;
import org.unitils.mock.argumentmatcher.impl.*;
import org.unitils.mock.report.ScenarioReport;
import org.unitils.mock.report.impl.DefaultScenarioReport;
import org.unitils.mock.core.InvocationMatcherBuilder;
import org.unitils.mock.util.MethodFormatUtil;
import static org.unitils.mock.util.ProxyUtil.createMockObjectProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockDirector {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(MockDirector.class);

    private Scenario scenario;

    private InvocationMatcherBuilder invocationMatcherBuilder;


    public MockDirector(Scenario scenario, InvocationMatcherBuilder invocationMatcherBuilder) {
        this.scenario = scenario;
        this.invocationMatcherBuilder = invocationMatcherBuilder;
    }


    @SuppressWarnings("unchecked")
    public <T> MockBehaviorDefiner<T> mock(T mock) {
        if (!MockObjectProxy.class.isAssignableFrom(mock.getClass())) {
            throw new UnitilsException(mock + " is not a mock object");
        }
        MockObject<T> mockObject = ((MockObjectProxy<T>) mock).$_$_getMockObject();
        return new MockBehaviorDefiner<T>(mockObject, invocationMatcherBuilder);
    }


    public <T> T assertInvoked(T mock) {
        MockObject<T> mockObject = getMockObjectFromProxy(mock);
        return createMockObjectProxy(mockObject, new AssertInvokedInvocationHandler());
    }


    public <T> T assertNotInvoked(T mock) {
        MockObject<T> mockObject = getMockObjectFromProxy(mock);
        return createMockObjectProxy(mockObject, new AssertNotInvokedInvocationHandler());
    }


    public void assertNoMoreInvocations() {
        List<Invocation> unverifiedInvocations = scenario.getUnverifiedInvocations();
        if (!unverifiedInvocations.isEmpty()) {
            Invocation invocation = unverifiedInvocations.get(0);
            throw new AssertionError(getNoMoreInvocationsErrorMessage(invocation));
        }
    }


    /**
     * Creates a mock object of the given type, associated to the given {@link org.unitils.mock.core.Scenario}.
     *
     * @param name     A name for the mock, not null
     * @param mockType The type of the mock, not null
     * @return A mock for the given class or interface, not null
     */
    public <T> T createMock(String name, Class<T> mockType) {
        return createMock(name, mockType, false);
    }


    /**
     * Creates a mock object of the given type, associated to the given {@link org.unitils.mock.core.Scenario}.
     *
     * @param name     A name for the mock, not null
     * @param mockType The type of the mock, not null
     * @return A mock for the given class or interface, not null
     */
    public <T> T createPartialMock(String name, Class<T> mockType) {
        return createMock(name, mockType, true);
    }


    protected <T> T createMock(String name, Class<T> mockType, boolean partialMock) {
        MockObject<T> mockObject = new MockObject<T>(name, mockType, partialMock);
        return createMockObjectProxy(mockObject, new MockObjectInvocationHandler<T>(mockObject));
    }


    @ArgumentMatcher
    @SuppressWarnings({"UnusedDeclaration"})
    public <T> T notNull(Class<T> argumentClass) {
        invocationMatcherBuilder.registerArgumentMatcher(new NotNullArgumentMatcher());
        return null;
    }


    @ArgumentMatcher
    @SuppressWarnings({"UnusedDeclaration"})
    public <T> T isNull(Class<T> argumentClass) {
        invocationMatcherBuilder.registerArgumentMatcher(new NullArgumentMatcher());
        return null;
    }


    @ArgumentMatcher
    public <T> T same(T sameAs) {
        invocationMatcherBuilder.registerArgumentMatcher(new SameArgumentMatcher(sameAs));
        return null;
    }


    @ArgumentMatcher
    public <T> T eq(T equalTo) {
        invocationMatcherBuilder.registerArgumentMatcher(new EqualsArgumentMatcher(equalTo));
        return null;
    }


    @ArgumentMatcher
    public <T> T refEq(T equalTo) {
        invocationMatcherBuilder.registerArgumentMatcher(new RefEqArgumentMatcher(equalTo));
        return null;
    }


    @ArgumentMatcher
    public <T> T lenEq(T equalTo) {
        invocationMatcherBuilder.registerArgumentMatcher(new LenEqArgumentMatcher(equalTo));
        return null;
    }


    @SuppressWarnings("unchecked")
    protected <T> MockObject<T> getMockObjectFromProxy(T proxy) {
        if (!MockObjectProxy.class.isAssignableFrom(proxy.getClass())) {
            throw new UnitilsException(proxy + " is not a mock object");
        }
        return ((MockObjectProxy<T>) proxy).$_$_getMockObject();
    }


    public void logExecutionScenario(Object testObject) {
        ScenarioReport scenarioReport = new DefaultScenarioReport();
        logger.info(scenarioReport.createReport("", testObject, scenario));
    }


    public Scenario getScenario() {
        return scenario;
    }


    protected String getAssertNotInvokedErrorMessage(Invocation invocation, InvocationMatcher invocationMatcher) {
        StringBuilder message = new StringBuilder();
        Method method = invocationMatcher.getMethod();
        message.append("Prohibited invocation of ");
        message.append(MethodFormatUtil.getCompleteRepresentation(method));
        message.append(" at ");
        message.append(invocation.getInvokedAt());
        return message.toString();
    }


    protected String getAssertInvokedErrorMessage(Invocation matchedInvocation, InvocationMatcher invocationMatcher) {
        StringBuilder message = new StringBuilder();
        Method method = invocationMatcher.getMethod();
        message.append("Expected invocation of ");
        message.append(MethodFormatUtil.getCompleteRepresentation(method));
        message.append(", but ");
        if (matchedInvocation != null) {
            message.append("it was called with different or non-matching arguments.");
        } else {
            message.append("the invocation didn't occur.");
        }
        return message.toString();
    }


    protected String getNoMoreInvocationsErrorMessage(Invocation invocation) {
        StringBuilder message = new StringBuilder();
        Method method = invocation.getMethod();
        message.append("No more invocations expected, but ");
        message.append(MethodFormatUtil.getCompleteRepresentation(method));
        message.append(" was called from ");
        message.append(invocation.getInvokedAt());
        return message.toString();
    }


    protected void assertInvoked(Invocation invocation) {
        InvocationMatcher invocationMatcher = invocationMatcherBuilder.createInvocationMatcher(invocation);
        Invocation observedInvocation = scenario.verifyMatchingInvocation(invocationMatcher);
        if (observedInvocation == null) {
            throw new AssertionError(getAssertInvokedErrorMessage(invocation, invocationMatcher));
        }
    }


    protected void assertNotInvoked(Invocation invocation) {
        InvocationMatcher invocationMatcher = invocationMatcherBuilder.createInvocationMatcher(invocation);
        Invocation observedInvocation = scenario.verifyMatchingInvocation(invocationMatcher);
        if (observedInvocation != null) {
            throw new AssertionError(getAssertNotInvokedErrorMessage(observedInvocation, invocationMatcher));
        }
    }


    protected class MockObjectInvocationHandler<T> implements InvocationHandler {

        private MockObject<T> mockObject;

        public MockObjectInvocationHandler(MockObject<T> mockObject) {
            this.mockObject = mockObject;

        }

        public Object handleInvocation(Invocation invocation) throws Throwable {
            scenario.addObservedInvocation(invocation);
            return mockObject.executeMatchingBehavior(invocation);
        }
    }


    protected class AssertInvokedInvocationHandler implements InvocationHandler {

        public Object handleInvocation(Invocation invocation) throws Throwable {
            assertInvoked(invocation);
            return null;
        }
    }


    protected class AssertNotInvokedInvocationHandler implements InvocationHandler {

        public Object handleInvocation(Invocation invocation) throws Throwable {
            assertNotInvoked(invocation);
            return null;
        }
    }
}