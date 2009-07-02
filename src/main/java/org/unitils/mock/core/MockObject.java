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

import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.util.ObjectToInjectHolder;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.MatchStatement;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.impl.ExceptionThrowingMockBehavior;
import org.unitils.mock.mockbehavior.impl.ValueReturningMockBehavior;
import org.unitils.mock.proxy.ProxyInvocationHandler;
import static org.unitils.mock.proxy.ProxyUtils.createInstanceOfType;

/**
 * Implementation of a Mock and PartialMock.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockObject<T> implements Mock<T>, ObjectToInjectHolder<T> {

    /* The name of the mock (e.g. the name of the field) */
    protected String name;

    /* The class type that is mocked */
    protected Class<T> mockedType;

    protected MockProxy<T> mockProxy;

    /* Mock behaviors that are removed once they have been matched */
    protected BehaviorDefiner<T> oneTimeMatchingBehaviorDefiner;

    /* Mock behaviors that can be matched and re-used for several invocation */
    protected BehaviorDefiner<T> alwaysMatchingBehaviorDefiner;

    protected AssertInvokedVerifier<T> assertInvokedVerifier;

    protected AssertNotInvokedVerifier<T> assertNotInvokedVerifier;

    protected AssertInvokedInSequenceVerifier<T> assertInvokedInSequenceVerifier;

    /* The scenario that will record all observed invocations */
    protected Scenario scenario;

    /* The mock proxy instance */
    protected T mock;

    protected static SyntaxMonitor syntaxMonitor;


    /**
     * Creates a mock of the given type for the given scenario.
     *
     * @param name       The name of the mock, e.g. the field-name, not null
     * @param mockedType The mock type that will be proxied, not null
     * @param scenario   The scenario, not null
     */
    public MockObject(String name, Class<T> mockedType, Scenario scenario) {
        this.name = name;
        this.mockedType = mockedType;
        this.scenario = scenario;

        this.oneTimeMatchingBehaviorDefiner = createBehaviorDefiner(name);
        this.alwaysMatchingBehaviorDefiner = createBehaviorDefiner(name);

        this.assertInvokedVerifier = createAssertInvokedVerifier(name, scenario);
        this.assertNotInvokedVerifier = createAssertNotInvokedVerifier(name, scenario);
        this.assertInvokedInSequenceVerifier = createAssertInvokedInSequenceVerifier(name, scenario);

        if (syntaxMonitor == null) {
            syntaxMonitor = createSyntaxMonitor();
        }
        this.mockProxy = createMockProxy(oneTimeMatchingBehaviorDefiner, alwaysMatchingBehaviorDefiner, scenario, syntaxMonitor);
        this.mock = mockProxy.getProxyInstance(name, mockedType);
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
        return getMock();
    }


    /**
     * @return The type of the object to inject (i.e. the mocked type), not null.
     */
    public Class<T> getObjectToInjectType() {
        return mockedType;
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
        return mock;
    }


    /**
     * @return the type of the mock, not null
     */
    public Class<?> getMockedType() {
        return mockedType;
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
        ProxyInvocationHandler proxyInvocationHandler = alwaysMatchingBehaviorDefiner.createProxyInvocationHandler(new ValueReturningMockBehavior(returnValue));
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
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
        ProxyInvocationHandler proxyInvocationHandler = alwaysMatchingBehaviorDefiner.createProxyInvocationHandler(new ExceptionThrowingMockBehavior(exception));
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
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
        ProxyInvocationHandler proxyInvocationHandler = alwaysMatchingBehaviorDefiner.createProxyInvocationHandler(new ExceptionThrowingMockBehavior(exception));
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
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
        ProxyInvocationHandler proxyInvocationHandler = alwaysMatchingBehaviorDefiner.createProxyInvocationHandler(mockBehavior);
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
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
        ProxyInvocationHandler proxyInvocationHandler = oneTimeMatchingBehaviorDefiner.createProxyInvocationHandler(new ValueReturningMockBehavior(returnValue));
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
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
        ProxyInvocationHandler proxyInvocationHandler = oneTimeMatchingBehaviorDefiner.createProxyInvocationHandler(new ExceptionThrowingMockBehavior(exception));
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
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
        ProxyInvocationHandler proxyInvocationHandler = oneTimeMatchingBehaviorDefiner.createProxyInvocationHandler(new ExceptionThrowingMockBehavior(exception));
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
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
        ProxyInvocationHandler proxyInvocationHandler = oneTimeMatchingBehaviorDefiner.createProxyInvocationHandler(mockBehavior);
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
    }


    /**
     * Asserts that an invocation that matches the invocation following this call has been observed
     * on this mock object during this test.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T assertInvoked() {
        ProxyInvocationHandler proxyInvocationHandler = assertInvokedVerifier.createProxyInvocationHandler();
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
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
        ProxyInvocationHandler proxyInvocationHandler = assertInvokedInSequenceVerifier.createProxyInvocationHandler();
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
    }


    /**
     * Asserts that no invocation that matches the invocation following this call has been observed
     * on this mock object during this test.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T assertNotInvoked() {
        ProxyInvocationHandler proxyInvocationHandler = assertNotInvokedVerifier.createProxyInvocationHandler();
        return syntaxMonitor.getProxyInstance(name, mockedType, proxyInvocationHandler);
    }


    /**
     * Removes all behavior defined for this mock.
     * This will only remove the behavior, not the observed invocations for this mock.
     */
    @MatchStatement
    public void resetBehavior() {
        oneTimeMatchingBehaviorDefiner.reset();
        alwaysMatchingBehaviorDefiner.reset();
        syntaxMonitor.reset();
    }


    protected MockProxy<T> createMockProxy(BehaviorDefiner<T> oneTimeMatchingBehaviorDefiner, BehaviorDefiner<T> alwaysMatchingBehaviorDefiner, Scenario scenario, SyntaxMonitor syntaxMonitor) {
        return new MockProxy<T>(oneTimeMatchingBehaviorDefiner, alwaysMatchingBehaviorDefiner, scenario, syntaxMonitor);
    }


    protected BehaviorDefiner<T> createBehaviorDefiner(String name) {
        return new MockObjectBehaviorDefiner<T>(name);
    }


    protected AssertInvokedInSequenceVerifier<T> createAssertInvokedInSequenceVerifier(String mockName, Scenario scenario) {
        return new AssertInvokedInSequenceVerifier<T>(mockName, scenario);
    }


    protected AssertNotInvokedVerifier<T> createAssertNotInvokedVerifier(String mockName, Scenario scenario) {
        return new AssertNotInvokedVerifier<T>(mockName, scenario);
    }


    protected AssertInvokedVerifier<T> createAssertInvokedVerifier(String mockName, Scenario scenario) {
        return new AssertInvokedVerifier<T>(mockName, scenario);
    }


    protected SyntaxMonitor createSyntaxMonitor() {
        return new SyntaxMonitor();
    }


    protected class MockObjectBehaviorDefiner<T> extends BehaviorDefiner<T> {

        public MockObjectBehaviorDefiner(String mockName) {
            super(mockName);
        }

        @SuppressWarnings({"unchecked"})
        protected Mock<?> createInnerMock(String name, Class<?> mockedType) {
            try {
                if (Void.class.equals(mockedType)) {
                    return null;
                }
                return new MockObject(name, mockedType, scenario);
            } catch (Throwable t) {
                return null;
            }
        }
    }

}
