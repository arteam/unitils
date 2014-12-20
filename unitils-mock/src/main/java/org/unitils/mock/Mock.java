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
package org.unitils.mock;

import org.unitils.mock.annotation.MatchStatement;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.mockbehavior.MockBehavior;

/**
 * Declares the contract for a controller object that enables defining the behavior of methods of a mock object,
 * or for performing assert statements that verify that certain calls were effectively made. A method is also defined
 * that provides access to the actual mock object.
 * <p/>
 * If Unitils encounters a field declared as {@link Mock}, a {@link MockObject} is automatically instantiated and
 * assigned to the declared field.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface Mock<T> {

    /**
     * Gets the mock proxy instance. This is the instance that can be used to perform the test.
     * You could for example inject it in the tested object. It will then perform the defined behavior and record
     * all observed method invocations so that assertions can be performed afterwards.
     *
     * @return The proxy instance, not null
     */
    T getMock();

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
    T returns(Object returnValue);

    /*
    * Defines behavior for this mock so that it will return the given values as a list/array/set when the invocation following
    * this call matches the observed behavior. E.g.
    * Suppose method1 haa a List return type. Then setting
     * <p/>
     * mock.returnsAll("value1", "value2).method1();
     * <p/>
     * will return a list list containing ("aValue", "value2") when method1 is called. Array types and Sets are also supported.
     * <p/>
     * If no values were specified, an empty list/array/set will be returned.
     * <p/>
     * Note that this behavior is executed each time a match is found. So the same list/set/array will be returned
     * each time method1() is called. If you only want to return the value once, use the {@link #onceReturnsAll} method.
     *
     * @param returnValues The value to return, optional
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T returnsAll(Object... returnValues);

    /**
     * Defines behavior for this mock so that it will return a dummy value when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.returnsDummy().method1();
     * <p/>
     * will return a dummy return value when method1 is called.
     * <p/>
     * Note that this behavior is executed each time a match is found. So the dummy instance will be returned
     * each time method1() is called. If you only want to return the value once, use the {@link #onceReturnsDummy} method.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T returnsDummy();

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
    T raises(Throwable exception);

    /**
     * Defines behavior for this mock so that it raises an instance of the given exception class when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.raises(new MyException()).method1();
     * <p/>
     * will throw an instance of the given exception class when method1 is called.
     * <p/>
     * Note that this behavior is executed each time a match is found. So the exception will be raised
     * each time method1() is called. If you only want to raise the exception once, use the {@link #onceRaises} method.
     *
     * @param exceptionClass The class of the exception to raise, not null
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T raises(Class<? extends Throwable> exceptionClass);

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
    T performs(MockBehavior mockBehavior);

    /**
     * Defines behavior for this mock so that it will return the given value when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.onceReturns("aValue").method1();
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
    T onceReturns(Object returnValue);

    /**
     * Defines behavior for this mock so that it will return the given values as a list/array/set when the invocation following
     * this call matches the observed behavior. E.g.
     * Suppose method1 haa a List return type. Then setting
     * <p/>
     * mock.onceReturnsAll("value1", "value2).method1();
     * <p/>
     * will return a list list containing ("aValue", "value2") when method1 is called. Array types and Sets are also supported.
     * <p/>
     * If no values were specified, an empty list/array/set will be returned.
     * <p/>
     * Note that this behavior is executed only once. If method1() is invoked a second time, a different
     * behavior definition will be used (if defined) or a default value will be returned. If you want this
     * definition to be able to be matched multiple times, use the method {@link #returnsAll} instead.
     *
     * @param returnValues The value to return, none for empty
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T onceReturnsAll(Object... returnValues);

    /**
     * Defines behavior for this mock so that it will return a dummy of the return type when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.onceReturnsDummy().method1();
     * <p/>
     * will return a dummy return value when method1 is called.
     * <p/>
     * Note that this behavior is executed only once. If method1() is invoked a second time, a different
     * behavior definition will be used (if defined) or a default value will be returned. If you want this
     * definition to be able to be matched multiple times, use the method {@link #returnsDummy} instead.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T onceReturnsDummy();

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
     * @param exception The exception to raise, not null
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T onceRaises(Throwable exception);

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
     * @param exceptionClass The class of the exception to raise, not null
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T onceRaises(Class<? extends Throwable> exceptionClass);

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
    T oncePerforms(MockBehavior mockBehavior);

    /**
     * Asserts that an invocation that matches the invocation following this call has been observed
     * on this mock object during this test.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T assertInvoked();

    /**
     * Asserts that an invocation that matches the invocation following this call has been observed the given amount of
     * times on this mock object during this test.
     * An exception will be raised when times is zero or negative
     *
     * @param times The nr of times, should be > 0
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T assertInvoked(int times);

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
    T assertInvokedInSequence();

    /**
     * Asserts that no invocation that matches the invocation following this call has been observed
     * on this mock object during this test.
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T assertNotInvoked();

    /**
     * Asserts that no more invocations have been observed on this mock object during this test.
     */
    @MatchStatement
    void assertNoMoreInvocations();

    /**
     * Removes all behavior defined for this mock.
     * This will only remove the behavior, not the observed invocations for this mock.
     */
    void resetBehavior();
}
