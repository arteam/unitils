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

import org.unitils.core.reflect.TypeWrapper;
import org.unitils.core.util.ObjectToFormat;
import org.unitils.core.util.ObjectToInjectHolder;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.MatchStatement;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.core.matching.MatchingInvocationHandlerFactory;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.MockBehaviorFactory;

import java.lang.reflect.Type;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class MockObject<T> implements Mock<T>, ObjectToInjectHolder<T>, ObjectToFormat {

    /* The proxy instance */
    protected String name;
    protected Class<T> type;
    protected T proxy;
    protected T matchingProxy;
    /* True if this mock object was created to support mock chaining */
    protected boolean chained;
    protected BehaviorDefiningInvocations behaviorDefiningInvocations;
    protected MockBehaviorFactory mockBehaviorFactory;
    protected MatchingProxyInvocationHandler matchingProxyInvocationHandler;
    protected MatchingInvocationHandlerFactory matchingInvocationHandlerFactory;


    /**
     * Creates a mock of the given type.
     * <p/>
     * There is no .class literal for generic types. Therefore you need to pass the raw type when mocking generic types.
     * E.g. Mock&lt;List&lt;String&gt;&gt; myMock = new MockObject("myMock", List.class, this);
     * <p/>
     * If the mocked type does not correspond to the declared type, a ClassCastException will occur when the mock
     * is used.
     * <p/>
     * If no name is given the un-capitalized type name + Mock is used, e.g. myServiceMock
     */
    public MockObject(String name, Class<T> type, T proxy, T matchingProxy, boolean chained, BehaviorDefiningInvocations behaviorDefiningInvocations, MatchingProxyInvocationHandler matchingProxyInvocationHandler, MockBehaviorFactory mockBehaviorFactory, MatchingInvocationHandlerFactory matchingInvocationHandlerFactory) {
        this.name = name;
        this.type = type;
        this.proxy = proxy;
        this.matchingProxy = matchingProxy;
        this.chained = chained;
        this.behaviorDefiningInvocations = behaviorDefiningInvocations;
        this.matchingProxyInvocationHandler = matchingProxyInvocationHandler;
        this.mockBehaviorFactory = mockBehaviorFactory;
        this.matchingInvocationHandlerFactory = matchingInvocationHandlerFactory;
    }


    /**
     * Returns the mock proxy instance. This is the object that must be injected if the field that it holds is
     * annotated with @InjectInto or one of it's equivalents.
     *
     * @return The mock proxy instance, not null
     */
    public T getObjectToInject() {
        return getMock();
    }

    /**
     * @param declaredType The declared type (e.g. type of field) of this mock object, null if not known
     * @return The type of the object to inject (i.e. the mocked type), not null.
     */
    public Type getObjectToInjectType(Type declaredType) {
        if (declaredType == null) {
            return type;
        }
        TypeWrapper declaredTypeWrapper = new TypeWrapper(declaredType);
        return declaredTypeWrapper.getSingleGenericType();
    }

    /**
     * @return The name of the mock, not null
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * @return The name to display when formatted by the {@link org.unitils.core.util.ObjectFormatter}
     */
    public String $formatObject() {
        return "Mock<" + name + ">";
    }

    /**
     * Returns the mock proxy instance. This is the instance that can be used to perform the test.
     * You could for example inject it in the tested object. It will then perform the defined behavior and record
     * all observed method invocations so that assertions can be performed afterwards.
     *
     * @return The proxy instance, not null
     */
    public T getMock() {
        return proxy;
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
        MockBehavior mockBehavior = mockBehaviorFactory.createValueReturningMockBehavior(returnValue);
        return startBehaviorMatchingInvocation(mockBehavior, false);
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
        MockBehavior mockBehavior = mockBehaviorFactory.createExceptionThrowingMockBehavior(exception);
        return startBehaviorMatchingInvocation(mockBehavior, false);
    }

    /**
     * Defines behavior for this mock so that it raises the given exception when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.raises(MyException.class).method1();
     * <p/>
     * will throw an instance of the given exception class when method1 is called.
     * <p/>
     * Note that this behavior is executed each time a match is found. So the exception will be raised
     * each time method1() is called. If you only want to raise the exception once, use the {@link #onceRaises} method.
     *
     * @param exceptionClass The type of exception to raise, not null
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T raises(Class<? extends Throwable> exceptionClass) {
        MockBehavior mockBehavior = mockBehaviorFactory.createExceptionThrowingMockBehavior(exceptionClass);
        return startBehaviorMatchingInvocation(mockBehavior, false);
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
        return startBehaviorMatchingInvocation(mockBehavior, false);
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
        MockBehavior mockBehavior = mockBehaviorFactory.createValueReturningMockBehavior(returnValue);
        return startBehaviorMatchingInvocation(mockBehavior, true);
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
        MockBehavior mockBehavior = mockBehaviorFactory.createExceptionThrowingMockBehavior(exception);
        return startBehaviorMatchingInvocation(mockBehavior, true);
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
        MockBehavior mockBehavior = mockBehaviorFactory.createExceptionThrowingMockBehavior(exceptionClass);
        return startBehaviorMatchingInvocation(mockBehavior, true);
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
        return startBehaviorMatchingInvocation(mockBehavior, true);
    }

    /**
     * Asserts that an invocation that matches the invocation following this call has been observed
     * on this mock object during this test.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T assertInvoked() {
        MatchingInvocationHandler matchingInvocationHandler = matchingInvocationHandlerFactory.createAssertInvokedVerifyingMatchingInvocationHandler();
        return startAssertMatchingInvocation(matchingInvocationHandler);
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
        MatchingInvocationHandler matchingInvocationHandler = matchingInvocationHandlerFactory.createAssertInvokedInSequenceVerifyingMatchingInvocationHandler();
        return startAssertMatchingInvocation(matchingInvocationHandler);
    }

    /**
     * Asserts that no invocation that matches the invocation following this call has been observed
     * on this mock object during this test.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    public T assertNotInvoked() {
        MatchingInvocationHandler matchingInvocationHandler = matchingInvocationHandlerFactory.createAssertNotInvokedVerifyingMatchingInvocationHandler();
        return startAssertMatchingInvocation(matchingInvocationHandler);
    }

    /**
     * Removes all behavior defined for this mock.
     * This will only remove the behavior, not the observed invocations for this mock.
     */
    public void resetBehavior() {
        behaviorDefiningInvocations.clear();
    }


    protected T startBehaviorMatchingInvocation(MockBehavior mockBehavior, boolean oneTimeMatch) {
        MatchingInvocationHandler matchingInvocationHandler = matchingInvocationHandlerFactory.createBehaviorDefiningMatchingInvocationHandler(mockBehavior, oneTimeMatch, behaviorDefiningInvocations);
        matchingProxyInvocationHandler.startMatchingInvocation(name, !chained, matchingInvocationHandler);
        return matchingProxy;
    }

    protected T startAssertMatchingInvocation(MatchingInvocationHandler matchingInvocationHandler) {
        matchingProxyInvocationHandler.startMatchingInvocation(name, !chained, matchingInvocationHandler);
        return matchingProxy;
    }
}
