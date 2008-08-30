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

import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import static org.unitils.mock.argumentmatcher.ArgumentMatcherPositionFinder.getArgumentMatcherIndexes;
import static org.unitils.mock.argumentmatcher.ArgumentMatcherRepository.*;
import org.unitils.mock.argumentmatcher.impl.LenEqArgumentMatcher;
import org.unitils.mock.invocation.BehaviorDefiningInvocation;
import org.unitils.mock.invocation.ObservedInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;
import org.unitils.mock.mockbehavior.impl.ExceptionThrowingMockBehavior;
import org.unitils.mock.mockbehavior.impl.OriginalBehaviorInvokingMockBehavior;
import org.unitils.mock.mockbehavior.impl.ValueReturningMockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;
import static org.unitils.mock.proxy.ProxyUtil.createProxy;
import org.unitils.core.util.ObjectFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockObject<T> implements Mock<T>, PartialMock<T> {


    /* The name of the mock (e.g. the name of the field) */
    private String name;

    /* The class type that is mocked */
    private Class<T> mockedClass;

    /* True if the actual method behavior should be invoked if no mock behavior is defined for the method */
    private boolean partialMock;

    /* Mock behaviors that are removed once they have been matched */
    private List<BehaviorDefiningInvocation> oneTimeMatchingMockBehaviors = new ArrayList<BehaviorDefiningInvocation>();

    /* Mock behaviors that can be matched and re-used for several invocation */
    private List<BehaviorDefiningInvocation> alwaysMatchingMockBehaviors = new ArrayList<BehaviorDefiningInvocation>();


    private Scenario scenario;

    private T instance;


    public MockObject(String name, Class<T> mockedClass, boolean partialMock, Scenario scenario) {
        this.name = name;
        this.mockedClass = mockedClass;
        this.partialMock = partialMock;
        this.scenario = scenario;
        this.instance = createInstance();
    }


    public T getInstance() {
        return instance;
    }


    public T returns(Object returnValue) {
        MockBehavior mockBehavior = new ValueReturningMockBehavior(returnValue);
        return createMockObjectProxy(new AlwaysMatchingMockBehaviorInvocationHandler(mockBehavior));
    }


    public T raises(Throwable exception) {
        MockBehavior mockBehavior = new ExceptionThrowingMockBehavior(exception);
        return createMockObjectProxy(new AlwaysMatchingMockBehaviorInvocationHandler(mockBehavior));
    }


    public T performs(MockBehavior mockBehavior) {
        return createMockObjectProxy(new AlwaysMatchingMockBehaviorInvocationHandler(mockBehavior));
    }


    public T onceReturns(Object returnValue) {
        MockBehavior mockBehavior = new ValueReturningMockBehavior(returnValue);
        return createMockObjectProxy(new OneTimeMatchingMockBehaviorInvocationHandler(mockBehavior));
    }


    public T onceRaises(Throwable exception) {
        MockBehavior mockBehavior = new ExceptionThrowingMockBehavior(exception);
        return createMockObjectProxy(new OneTimeMatchingMockBehaviorInvocationHandler(mockBehavior));
    }


    public T oncePerforms(MockBehavior mockBehavior) {
        return createMockObjectProxy(new OneTimeMatchingMockBehaviorInvocationHandler(mockBehavior));
    }


    public T assertInvoked() {
        return createMockObjectProxy(new AssertInvokedInvocationHandler());
    }


    public T assertNotInvoked() {
        return createMockObjectProxy(new AssertNotInvokedInvocationHandler());
    }


    protected Object handleMockObjectInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        BehaviorDefiningInvocation behaviorDefiningInvocation = getMatchingBehaviorDefiningInvocation(proxyInvocation);
        MockBehavior mockBehavior = getMockBehavior(proxyInvocation, behaviorDefiningInvocation);

        Object result = null;
        if (mockBehavior != null) {
            result = mockBehavior.execute(proxyInvocation);
        }

        ObservedInvocation mockInvocation = createObservedInvocation(proxyInvocation, result, behaviorDefiningInvocation, mockBehavior);
        scenario.addObservedMockInvocation(mockInvocation);
        return result;
    }


    protected T createInstance() {
        return createMockObjectProxy(new MockObjectInvocationHandler());
    }

    protected ObjectFormatter createObjectFormatter() {
        return new ObjectFormatter(10);
    }


    protected T createMockObjectProxy(ProxyInvocationHandler invocationHandler) {
        return createProxy(mockedClass, invocationHandler);
    }


    protected BehaviorDefiningInvocation getMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        // Check if there is a one-time matching behavior that hasn't been invoked yet
        Iterator<BehaviorDefiningInvocation> iterator = oneTimeMatchingMockBehaviors.iterator();
        while (iterator.hasNext()) {
            BehaviorDefiningInvocation behaviorDefiningInvocation = iterator.next();
            if (behaviorDefiningInvocation.matches(proxyInvocation)) {
                iterator.remove();
                return behaviorDefiningInvocation;
            }
        }

        // Check if there is an always-matching behavior
        for (BehaviorDefiningInvocation behaviorDefiningInvocation : alwaysMatchingMockBehaviors) {
            if (behaviorDefiningInvocation.matches(proxyInvocation)) {
                return behaviorDefiningInvocation;
            }
        }

        return null;
    }


    protected void handleOneTimeMatchingMockBehaviorInvocation(ProxyInvocation proxyInvocation, MockBehavior mockBehavior) {
        BehaviorDefiningInvocation behaviorDefiningInvocation = createBehaviorDefiningInvocation(proxyInvocation, mockBehavior);
        oneTimeMatchingMockBehaviors.add(behaviorDefiningInvocation);
    }


    protected void handleAlwaysMatchingMockBehaviorInvocation(ProxyInvocation proxyInvocation, MockBehavior mockBehavior) {
        BehaviorDefiningInvocation behaviorDefiningInvocation = createBehaviorDefiningInvocation(proxyInvocation, mockBehavior);
        alwaysMatchingMockBehaviors.add(behaviorDefiningInvocation);
    }


    protected MockBehavior getMockBehavior(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        if (proxyInvocation.getMethod().getReturnType() == Void.TYPE) {
            return null;
        }
        if (behaviorDefiningInvocation != null) {
            return behaviorDefiningInvocation.getMockBehavior();
        }
        // There's no matching behavior, execute the default one
        if (partialMock) {
            return new OriginalBehaviorInvokingMockBehavior();
        }
        return new DefaultValueReturningMockBehavior();
    }

    protected BehaviorDefiningInvocation createBehaviorDefiningInvocation(ProxyInvocation proxyInvocation, MockBehavior mockBehavior) {
        List<ArgumentMatcher> argumentMatchers = createArgumentMatchers(proxyInvocation);
        return new BehaviorDefiningInvocation(name, proxyInvocation, argumentMatchers, mockBehavior);
    }


    protected ObservedInvocation createObservedInvocation(ProxyInvocation proxyInvocation, Object result, BehaviorDefiningInvocation behaviorDefiningInvocation, MockBehavior mockBehavior) {
        ObjectFormatter objectFormatter = createObjectFormatter();
        List<String> argumentsAsStrings = new ArrayList<String>();
        for (Object argument : proxyInvocation.getArguments()) {
            argumentsAsStrings.add(objectFormatter.format(argument));
        }
        String resultAsString = objectFormatter.format(result);
        return new ObservedInvocation(name, proxyInvocation, argumentsAsStrings, resultAsString, behaviorDefiningInvocation, mockBehavior);
    }


    protected List<ArgumentMatcher> createArgumentMatchers(ProxyInvocation proxyInvocation) {
        List<ArgumentMatcher> result = new ArrayList<ArgumentMatcher>();

        List<Integer> argumentMatcherIndexes = getArgumentMatcherIndexes(proxyInvocation);

        int argumentIndex = 0;
        Iterator<ArgumentMatcher> argumentMatcherIterator = getArgumentMatchers().iterator();
        for (Object argument : proxyInvocation.getArguments()) {
            if (argumentMatcherIndexes.contains(argumentIndex++)) {
                result.add(argumentMatcherIterator.next());
            } else {
                result.add(new LenEqArgumentMatcher(argument));
            }
        }
        resetArgumentMatchers();

        return result;
    }


    protected class AlwaysMatchingMockBehaviorInvocationHandler implements ProxyInvocationHandler {

        private MockBehavior mockBehavior;


        public AlwaysMatchingMockBehaviorInvocationHandler(MockBehavior mockBehavior) {
            this.mockBehavior = mockBehavior;
        }

        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            handleAlwaysMatchingMockBehaviorInvocation(invocation, mockBehavior);
            return null;
        }
    }

    protected class OneTimeMatchingMockBehaviorInvocationHandler implements ProxyInvocationHandler {

        private MockBehavior mockBehavior;

        public OneTimeMatchingMockBehaviorInvocationHandler(MockBehavior mockBehavior) {
            this.mockBehavior = mockBehavior;
        }

        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            handleOneTimeMatchingMockBehaviorInvocation(invocation, mockBehavior);
            return null;
        }
    }


    protected class MockObjectInvocationHandler implements ProxyInvocationHandler {

        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            return handleMockObjectInvocation(invocation);
        }
    }


    protected class AssertInvokedInvocationHandler implements ProxyInvocationHandler {

        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            BehaviorDefiningInvocation behaviorDefiningInvocation = createBehaviorDefiningInvocation(proxyInvocation, null);
            scenario.assertInvoked(behaviorDefiningInvocation);
            return null;
        }
    }


    protected class AssertNotInvokedInvocationHandler implements ProxyInvocationHandler {

        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            BehaviorDefiningInvocation behaviorDefiningInvocation = createBehaviorDefiningInvocation(proxyInvocation, null);
            scenario.assertNotInvoked(behaviorDefiningInvocation);
            return null;
        }
    }


}
