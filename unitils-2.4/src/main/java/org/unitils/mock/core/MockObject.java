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

import static org.unitils.core.util.CloneUtil.createDeepClone;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.util.ObjectToInjectHolder;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitils.mock.annotation.MatchStatement;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import static org.unitils.mock.argumentmatcher.ArgumentMatcherPositionFinder.getArgumentMatcherIndexes;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.argumentmatcher.impl.DefaultArgumentMatcher;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;
import org.unitils.mock.mockbehavior.impl.ExceptionThrowingMockBehavior;
import org.unitils.mock.mockbehavior.impl.OriginalBehaviorInvokingMockBehavior;
import org.unitils.mock.mockbehavior.impl.ValueReturningMockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;
import static org.unitils.mock.proxy.ProxyUtil.createInstanceOfType;
import static org.unitils.mock.proxy.ProxyUtil.createProxy;
import org.unitils.util.CallStackUtils;
import static org.unitils.util.MethodUtils.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of a Mock and PartialMock.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockObject<T> implements Mock<T>, PartialMock<T>, ObjectToInjectHolder<T> {

    /* The name of the mock (e.g. the name of the field) */
    protected String name;

    /* The class type that is mocked */
    protected Class<T> mockedClass;

    /* True if the actual method behavior should be invoked if no mock behavior is defined for the method */
    protected boolean partialMock;

    /* Mock behaviors that are removed once they have been matched */
    protected List<BehaviorDefiningInvocation> oneTimeMatchingMockBehaviors = new ArrayList<BehaviorDefiningInvocation>();

    /* Mock behaviors that can be matched and re-used for several invocation */
    protected List<BehaviorDefiningInvocation> alwaysMatchingMockBehaviors = new ArrayList<BehaviorDefiningInvocation>();

    /* The scenario that will record all observed invocations */
    protected Scenario scenario;

    /* The mock proxy instance */
    protected T instance;

    /* The hash code that is returned when the hashCode method is called */
    protected Integer mockObjectHashCode = new Object().hashCode();


    /**
     * Creates a mock of the given type for the given scenario.
     *
     * @param name        The name of the mock, e.g. the field-name, not null
     * @param mockedClass The mock type that will be proxied, not null
     * @param partialMock True for creating a partial mock
     * @param scenario    The scenario, not null
     */
    public MockObject(String name, Class<T> mockedClass, boolean partialMock, Scenario scenario) {
        this.name = name;
        this.mockedClass = mockedClass;
        this.partialMock = partialMock;
        this.scenario = scenario;
        this.instance = createInstance();
    }


    //
    // Implementation of the ObjectToInjectHolder interface. Implementing this interface makes sure that the 
    // proxy instance is injected instead of this object (which doesn't directly implement the mocked interface)
    //

    /**
     * Returns the mock proxy instance. This is the object that must be injected if the field that it holds is
     * annotated with {@link InjectInto} or one of it's equivalents.
     *
     * @return The mock proxy instance, not null
     */
    public T getObjectToInject() {
        return instance;
    }


    /**
     * @return The type of the object to inject (i.e. the mocked type), not null.
     */
    public Class<T> getObjectToInjectType() {
        return mockedClass;
    }

    //
    // Implementation of the Mock and PartialMock interfaces
    //

    /**
     * Returns the mock proxy instance. This is the instance that can be used to perform the test.
     * You could for example inject it in the tested object. It will then perform the defined behavior and record
     * all observed method invocations so that assertions can be performed afterwards.
     *
     * @return The proxy instance, not null
     */
    public T getMock() {
        return instance;
    }


    public Class<?> getMockedClass() {
        return mockedClass;
    }


    /**
     * Defines behavior for this mock so that it will return the given value when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.returns("aValue").method1();
     * <p/>
     * will return "aValue" when method1 is called.
     * <p/>
     * Note that this behavior is executed each time a match is found. So "aValue" will be returned
     * each time method1() is called. If you only want to return the value once, use the {@link #onceReturns} method.
     *
     * @param returnValue The value to return
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T returns(Object returnValue) {
        AlwaysMatchingMockBehaviorInvocationHandler proxyInvocationHandler = new AlwaysMatchingMockBehaviorInvocationHandler(new ValueReturningMockBehavior(returnValue));
        return startBehaviorDefinition(proxyInvocationHandler, "returns");
    }


    /**
     * Defines behavior for this mock so that it raises the given exception when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.raises(new MyException()).method1();
     * <p/>
     * will throw the given exception when method1 is called.
     * <p/>
     * Note that this behavior is executed each time a match is found. So the exception will be raised
     * each time method1() is called. If you only want to raise the exception once, use the {@link #onceRaises} method.
     *
     * @param exception The exception to raise, not null
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T raises(Throwable exception) {
        AlwaysMatchingMockBehaviorInvocationHandler proxyInvocationHandler = new AlwaysMatchingMockBehaviorInvocationHandler(new ExceptionThrowingMockBehavior(exception));
        return startBehaviorDefinition(proxyInvocationHandler, "raises");
    }


    /**
     * Defines behavior for this mock so that it raises the given exception when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.raises(MyException.class).method1();
     * <p/>
     * will throw an instance of the given exception classwhen method1 is called.
     * <p/>
     * Note that this behavior is executed each time a match is found. So the exception will be raised
     * each time method1() is called. If you only want to raise the exception once, use the {@link #onceRaises} method.
     *
     * @param exceptionClass The type of exception to raise, not null
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T raises(Class<? extends Throwable> exceptionClass) {
        Throwable exception = createInstanceOfType(exceptionClass);
        exception.setStackTrace(getInvokedAt());
        AlwaysMatchingMockBehaviorInvocationHandler proxyInvocationHandler = new AlwaysMatchingMockBehaviorInvocationHandler(new ExceptionThrowingMockBehavior(exception));
        return startBehaviorDefinition(proxyInvocationHandler, "raises");
    }


    /**
     * Defines behavior for this mock so that will be performed when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.performs(new MyMockBehavior()).method1();
     * <p/>
     * will execute the given mock behavior when method1 is called.
     * <p/>
     * Note that this behavior is executed each time a match is found. So the behavior will be executed
     * each time method1() is called. If you only want to execute the behavior once, use the {@link #oncePerforms} method.
     *
     * @param mockBehavior The behavior to perform, not null
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T performs(MockBehavior mockBehavior) {
        AlwaysMatchingMockBehaviorInvocationHandler proxyInvocationHandler = new AlwaysMatchingMockBehaviorInvocationHandler(mockBehavior);
        return startBehaviorDefinition(proxyInvocationHandler, "performs");
    }


    /**
     * Defines behavior for this mock so that it will return the given value when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.returns("aValue").method1();
     * <p/>
     * will return "aValue" when method1 is called.
     * <p/>
     * Note that this behavior is executed only once. If method1() is invoked a second time, a different
     * behavior definition will be used (if defined) or a default value will be returned. If you want this
     * definition to be able to be matched multiple times, use the method {@link #returns} instead.
     *
     * @param returnValue The value to return
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T onceReturns(Object returnValue) {
        OneTimeMatchingMockBehaviorInvocationHandler proxyInvocationHandler = new OneTimeMatchingMockBehaviorInvocationHandler(new ValueReturningMockBehavior(returnValue));
        return startBehaviorDefinition(proxyInvocationHandler, "onceReturns");
    }


    /**
     * Defines behavior for this mock so that it raises the given exception when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.raises(new MyException()).method1();
     * <p/>
     * will throw the given exception when method1 is called.
     * <p/>
     * Note that this behavior is executed only once. If method1() is invoked a second time, a different
     * behavior definition will be used (if defined) or a default value will be returned. If you want this
     * definition to be able to be matched multiple times, use the method {@link #raises} instead.
     *
     * @param exception The exception to raise, not null
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T onceRaises(Throwable exception) {
        exception.setStackTrace(getInvokedAt());
        OneTimeMatchingMockBehaviorInvocationHandler proxyInvocationHandler = new OneTimeMatchingMockBehaviorInvocationHandler(new ExceptionThrowingMockBehavior(exception));
        return startBehaviorDefinition(proxyInvocationHandler, "onceRaises");
    }


    /**
     * Defines behavior for this mock so that it raises an instance of the given exception class when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.raises(new MyException()).method1();
     * <p/>
     * will throw an instance of the given exception class when method1 is called.
     * <p/>
     * Note that this behavior is executed only once. If method1() is invoked a second time, a different
     * behavior definition will be used (if defined) or a default value will be returned. If you want this
     * definition to be able to be matched multiple times, use the method {@link #raises} instead.
     *
     * @param exceptionClass The type of exception to raise, not null
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T onceRaises(Class<? extends Throwable> exceptionClass) {
        Throwable exception = createInstanceOfType(exceptionClass);
        OneTimeMatchingMockBehaviorInvocationHandler proxyInvocationHandler = new OneTimeMatchingMockBehaviorInvocationHandler(new ExceptionThrowingMockBehavior(exception));
        return startBehaviorDefinition(proxyInvocationHandler, "onceRaises");
    }


    /**
     * Defines behavior for this mock so that will be performed when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.performs(new MyMockBehavior()).method1();
     * <p/>
     * will execute the given mock behavior when method1 is called.
     * <p/>
     * Note that this behavior is executed only once. If method1() is invoked a second time, a different
     * behavior definition will be used (if defined) or a default value will be returned. If you want this
     * definition to be able to be matched multiple times, use the method {@link #performs} instead.
     *
     * @param mockBehavior The behavior to perform, not null
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T oncePerforms(MockBehavior mockBehavior) {
        OneTimeMatchingMockBehaviorInvocationHandler proxyInvocationHandler = new OneTimeMatchingMockBehaviorInvocationHandler(mockBehavior);
        return startBehaviorDefinition(proxyInvocationHandler, "oncePerforms");
    }


    /**
     * Asserts that an invocation that matches the invocation following this call has been observed
     * on this mock object during this test.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T assertInvoked() {
        AssertInvokedInvocationHandler proxyInvocationHandler = new AssertInvokedInvocationHandler(getInvokedAt());
        return startAssertion(proxyInvocationHandler, "assertInvoked");
    }


    /**
     * Asserts that an invocation that matches the invocation following this call has been observed
     * on this mock object during this test.
     * <p/>
     * If this method is used multiple times during the current test, the sequence of the observed method
     * calls has to be the same as the sequence of the calls to this method.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T assertInvokedInSequence() {
        AssertInvokedInOrderInvocationHandler proxyInvocationHandler = new AssertInvokedInOrderInvocationHandler(getInvokedAt());
        return startAssertion(proxyInvocationHandler, "assertInvokedInOrder");
    }


    /**
     * Asserts that no invocation that matches the invocation following this call has been observed
     * on this mock object during this test.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T assertNotInvoked() {
        AssertNotInvokedInvocationHandler proxyInvocationHandler = new AssertNotInvokedInvocationHandler(getInvokedAt());
        return startAssertion(proxyInvocationHandler, "assertNotInvoked");
    }


    /**
     * Removes all behavior defined for this mock.
     * This will only remove the behavior, not the observed invocations for this mock.
     */
    @MatchStatement
    public void resetBehavior() {
        oneTimeMatchingMockBehaviors.clear();
        alwaysMatchingMockBehaviors.clear();
    }


    protected T startBehaviorDefinition(ProxyInvocationHandler proxyInvocationHandler, String behaviorDefinitionMethodName) {
        StackTraceElement[] invokedAt = getInvokedAt();
        ArgumentMatcherRepository.getInstance().registerStartOfMatchingInvocation(invokedAt[0].getLineNumber());
        getSyntaxMonitor().startDefinition(name, behaviorDefinitionMethodName, proxyInvocationHandler, invokedAt);
        return createProxy(mockedClass, proxyInvocationHandler);
    }


    protected T startAssertion(ProxyInvocationHandler proxyInvocationHandler, String assertMethodName) {
        StackTraceElement[] invokedAt = getInvokedAt();
        ArgumentMatcherRepository.getInstance().registerStartOfMatchingInvocation(invokedAt[0].getLineNumber());
        getSyntaxMonitor().startDefinition(name, assertMethodName, proxyInvocationHandler, invokedAt);
        return createProxy(mockedClass, proxyInvocationHandler);
    }

    //
    // Core implementation
    //

    protected Object handleMockObjectInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        getSyntaxMonitor().assertNotExpectingInvocation();

        if (isEqualsMethod(proxyInvocation.getMethod())) {
            Object other = proxyInvocation.getArguments().get(0);
            return proxyInvocation.getProxy() == other;
        } else if (isHashCodeMethod(proxyInvocation.getMethod())) {
            return mockObjectHashCode;
        } else if (isCloneMethod(proxyInvocation.getMethod())) {
            return proxyInvocation.getProxy();
        }

        BehaviorDefiningInvocation behaviorDefiningInvocation = getMatchingBehaviorDefiningInvocation(proxyInvocation);
        MockBehavior mockBehavior = getMockBehavior(proxyInvocation, behaviorDefiningInvocation);

        ObservedInvocation mockInvocation = createObservedInvocation(proxyInvocation, behaviorDefiningInvocation, mockBehavior);
        scenario.addObservedMockInvocation(mockInvocation);

        Object result = null;
        if (mockBehavior != null) {
            result = mockBehavior.execute(proxyInvocation);
        }

        mockInvocation.setResultAtInvocationTime(createDeepClone(result));
        return result;
    }


    protected void handleOneTimeMatchingMockBehaviorInvocation(ProxyInvocation proxyInvocation, MockBehavior mockBehavior) {
        getSyntaxMonitor().endDefinition();
        BehaviorDefiningInvocation behaviorDefiningInvocation = createBehaviorDefiningInvocation(proxyInvocation, mockBehavior);
        oneTimeMatchingMockBehaviors.add(behaviorDefiningInvocation);
    }


    protected void handleAlwaysMatchingMockBehaviorInvocation(ProxyInvocation proxyInvocation, MockBehavior mockBehavior) {
        getSyntaxMonitor().endDefinition();
        BehaviorDefiningInvocation behaviorDefiningInvocation = createBehaviorDefiningInvocation(proxyInvocation, mockBehavior);
        alwaysMatchingMockBehaviors.add(behaviorDefiningInvocation);
    }


    protected void handleAssertInvokedInvocation(ProxyInvocation proxyInvocation, StackTraceElement[] assertedAt) {
        getSyntaxMonitor().endDefinition();
        BehaviorDefiningInvocation behaviorDefiningInvocation = createBehaviorDefiningInvocation(proxyInvocation, null);
        scenario.assertInvoked(behaviorDefiningInvocation, assertedAt);
    }


    protected void handleAssertInvokedInOrderInvocation(ProxyInvocation proxyInvocation, StackTraceElement[] assertedAt) {
        getSyntaxMonitor().endDefinition();
        BehaviorDefiningInvocation behaviorDefiningInvocation = createBehaviorDefiningInvocation(proxyInvocation, null);
        scenario.assertInvokedInOrder(behaviorDefiningInvocation, assertedAt);
    }


    protected void handleAssertNotInvokedInvocation(ProxyInvocation proxyInvocation, StackTraceElement[] assertedAt) {
        getSyntaxMonitor().endDefinition();
        BehaviorDefiningInvocation behaviorDefiningInvocation = createBehaviorDefiningInvocation(proxyInvocation, null);
        scenario.assertNotInvoked(behaviorDefiningInvocation, assertedAt);
    }


    protected BehaviorDefiningInvocation getMatchingBehaviorDefiningInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        // Check if there is a one-time matching behavior that hasn't been invoked yet
        for (BehaviorDefiningInvocation behaviorDefiningInvocation : oneTimeMatchingMockBehaviors) {
            if (behaviorDefiningInvocation.isUsed()) {
                continue;
            }
            if (behaviorDefiningInvocation.matches(proxyInvocation)) {
                behaviorDefiningInvocation.markAsUsed();
                return behaviorDefiningInvocation;
            }
        }

        // Check if there is an always-matching behavior
        for (BehaviorDefiningInvocation behaviorDefiningInvocation : alwaysMatchingMockBehaviors) {
            if (behaviorDefiningInvocation.matches(proxyInvocation)) {
                behaviorDefiningInvocation.markAsUsed();
                return behaviorDefiningInvocation;
            }
        }
        return null;
    }

    protected MockBehavior getMockBehavior(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation) {
        if (behaviorDefiningInvocation != null) {
            return behaviorDefiningInvocation.getMockBehavior();
        }
        // There's no matching behavior, execute the default one
        if (partialMock) {
            return new OriginalBehaviorInvokingMockBehavior();
        }
        if (proxyInvocation.getMethod().getReturnType() == Void.TYPE) {
            return null;
        }
        return new DefaultValueReturningMockBehavior();
    }

    //
    // Utility methods
    //

    /*protected StackTraceElement getAssertedAt() {
        StackTraceElement[] currentStackTrace = Thread.currentThread().getStackTrace();
        return currentStackTrace[4];
    }*/

    protected StackTraceElement[] getInvokedAt() {
        return CallStackUtils.getInvocationStackTrace(MockObject.class);
    }


    //
    // Factory methods
    //
    @SuppressWarnings("unchecked")
    protected T createInstance() {
        Class<?> superClass;
        Class<?>[] interfaces;
        if (mockedClass.isInterface()) {
            superClass = Object.class;
            interfaces = new Class<?>[]{mockedClass, Cloneable.class};
        } else {
            superClass = mockedClass;
            interfaces = new Class<?>[]{Cloneable.class};
        }
        return (T) createProxy(superClass, interfaces, new MockObjectInvocationHandler());
    }


    protected BehaviorDefiningInvocation createBehaviorDefiningInvocation(ProxyInvocation proxyInvocation, MockBehavior mockBehavior) {
        List<ArgumentMatcher> argumentMatchers = createArgumentMatchers(proxyInvocation);

        Method method = proxyInvocation.getMethod();
        List<Object> arguments = proxyInvocation.getArguments();
        List<Object> argumentsAtInvocationTime = createDeepClone(arguments);
        StackTraceElement invokedAt = proxyInvocation.getInvokedAt();

        return new BehaviorDefiningInvocation(proxyInvocation.getProxy(), name, method, arguments, argumentsAtInvocationTime, invokedAt, argumentMatchers, mockBehavior);
    }


    protected ObservedInvocation createObservedInvocation(ProxyInvocation proxyInvocation, BehaviorDefiningInvocation behaviorDefiningInvocation, MockBehavior mockBehavior) {
        Method method = proxyInvocation.getMethod();
        List<Object> arguments = proxyInvocation.getArguments();
        List<Object> argumentsAtInvocationTime = createDeepClone(arguments);
        StackTraceElement invokedAt = proxyInvocation.getInvokedAt();

        return new ObservedInvocation(proxyInvocation.getProxy(), name, method, arguments, argumentsAtInvocationTime, invokedAt, behaviorDefiningInvocation, mockBehavior);
    }


    protected List<ArgumentMatcher> createArgumentMatchers(ProxyInvocation proxyInvocation) {
        List<ArgumentMatcher> result = new ArrayList<ArgumentMatcher>();

        int lineNr = proxyInvocation.getInvokedAt().getLineNumber();
        int matchInvocationStartLineNr = Math.min(lineNr, ArgumentMatcherRepository.getInstance().getMatchInvocationStartLineNr());
        int matchInvocationEndLineNr = Math.max(lineNr, ArgumentMatcherRepository.getInstance().getMatchInvocationEndLineNr());
        List<Integer> argumentMatcherIndexes = getArgumentMatcherIndexes(proxyInvocation, matchInvocationStartLineNr, matchInvocationEndLineNr);

        int argumentIndex = 0;
        Iterator<ArgumentMatcher> argumentMatcherIterator = ArgumentMatcherRepository.getInstance().getArgumentMatchers().iterator();
        for (Object argument : proxyInvocation.getArguments()) {
            if (argumentMatcherIndexes.contains(argumentIndex++)) {
                result.add(argumentMatcherIterator.next());
            } else {
                result.add(new DefaultArgumentMatcher(argument));
            }
        }
        ArgumentMatcherRepository.getInstance().registerEndOfMatchingInvocation();
        return result;
    }


    protected SyntaxMonitor getSyntaxMonitor() {
        return scenario.getSyntaxMonitor();
    }

    //
    // Proxy invocation handlers
    //

    /**
     * Handles a method invocation of the proxy that is returned after a returns, raises... The handling is
     * delegated to the {@link MockObject#handleAlwaysMatchingMockBehaviorInvocation} method.
     */
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


    /**
     * Handles a method invocation of the proxy that is returned after an onceReturns, onceRaises... The handling is
     * delegated to the {@link MockObject#handleOneTimeMatchingMockBehaviorInvocation} method.
     */
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


    /**
     * Handles a method invocation of the mock proxy during a test. The handling is delegated
     * to the {@link MockObject#handleMockObjectInvocation}  method.
     */
    protected class MockObjectInvocationHandler implements ProxyInvocationHandler {

        public Object handleInvocation(ProxyInvocation invocation) throws Throwable {
            return handleMockObjectInvocation(invocation);
        }
    }


    /**
     * Handles a method invocation of the proxy that is returned after an assertInvoked. The handling is delegated
     * to {@link Scenario#assertInvoked}.
     */
    protected class AssertInvokedInvocationHandler implements ProxyInvocationHandler {

        private StackTraceElement[] assertedAt;

        public AssertInvokedInvocationHandler(StackTraceElement[] assertedAt) {
            this.assertedAt = assertedAt;
        }

        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            handleAssertInvokedInvocation(proxyInvocation, assertedAt);
            return null;
        }
    }


    /**
     * Handles a method invocation of the proxy that is returned after an assertInvokedInOrder. The handling is delegated
     * to {@link Scenario#assertInvokedInOrder}.
     */
    protected class AssertInvokedInOrderInvocationHandler implements ProxyInvocationHandler {

        private StackTraceElement[] assertedAt;

        public AssertInvokedInOrderInvocationHandler(StackTraceElement[] assertedAt) {
            this.assertedAt = assertedAt;
        }


        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            handleAssertInvokedInOrderInvocation(proxyInvocation, assertedAt);
            return null;
        }
    }


    /**
     * Handles a method invocation of the proxy that is returned after an assertNotInvoked. The handling is delegated
     * to {@link Scenario#assertNotInvoked}.
     */
    protected class AssertNotInvokedInvocationHandler implements ProxyInvocationHandler {

        private StackTraceElement[] assertedAt;

        public AssertNotInvokedInvocationHandler(StackTraceElement[] assertedAt) {
            this.assertedAt = assertedAt;
        }

        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            handleAssertNotInvokedInvocation(proxyInvocation, assertedAt);
            return null;
        }
    }


}
