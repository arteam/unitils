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
        if (!declaredTypeWrapper.hasRawType(Mock.class)) {
            return type;
        }
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

    public T getMock() {
        return proxy;
    }

    @MatchStatement
    public T returns(Object returnValue) {
        MockBehavior mockBehavior = mockBehaviorFactory.createValueReturningMockBehavior(returnValue);
        return startBehaviorMatchingInvocation(mockBehavior, false);
    }

    public T returnsAll(Object... returnValues) {
        MockBehavior mockBehavior = mockBehaviorFactory.createCollectionReturningMockBehavior(returnValues);
        return startBehaviorMatchingInvocation(mockBehavior, false);
    }

    @MatchStatement
    public T raises(Throwable exception) {
        MockBehavior mockBehavior = mockBehaviorFactory.createExceptionThrowingMockBehavior(exception);
        return startBehaviorMatchingInvocation(mockBehavior, false);
    }

    @MatchStatement
    public T raises(Class<? extends Throwable> exceptionClass) {
        MockBehavior mockBehavior = mockBehaviorFactory.createExceptionThrowingMockBehavior(exceptionClass);
        return startBehaviorMatchingInvocation(mockBehavior, false);
    }

    @MatchStatement
    public T performs(MockBehavior mockBehavior) {
        return startBehaviorMatchingInvocation(mockBehavior, false);
    }

    @MatchStatement
    public T onceReturns(Object returnValue) {
        MockBehavior mockBehavior = mockBehaviorFactory.createValueReturningMockBehavior(returnValue);
        return startBehaviorMatchingInvocation(mockBehavior, true);
    }

    public T onceReturnsAll(Object... returnValues) {
        MockBehavior mockBehavior = mockBehaviorFactory.createCollectionReturningMockBehavior(returnValues);
        return startBehaviorMatchingInvocation(mockBehavior, true);
    }

    @MatchStatement
    public T onceRaises(Throwable exception) {
        MockBehavior mockBehavior = mockBehaviorFactory.createExceptionThrowingMockBehavior(exception);
        return startBehaviorMatchingInvocation(mockBehavior, true);
    }

    @MatchStatement
    public T onceRaises(Class<? extends Throwable> exceptionClass) {
        MockBehavior mockBehavior = mockBehaviorFactory.createExceptionThrowingMockBehavior(exceptionClass);
        return startBehaviorMatchingInvocation(mockBehavior, true);
    }

    @MatchStatement
    public T oncePerforms(MockBehavior mockBehavior) {
        return startBehaviorMatchingInvocation(mockBehavior, true);
    }

    @MatchStatement
    public T assertInvoked() {
        MatchingInvocationHandler matchingInvocationHandler = matchingInvocationHandlerFactory.createAssertInvokedVerifyingMatchingInvocationHandler();
        return startAssertMatchingInvocation(matchingInvocationHandler);
    }

    @MatchStatement
    public T assertInvokedInSequence() {
        MatchingInvocationHandler matchingInvocationHandler = matchingInvocationHandlerFactory.createAssertInvokedInSequenceVerifyingMatchingInvocationHandler();
        return startAssertMatchingInvocation(matchingInvocationHandler);
    }

    @MatchStatement
    public T assertNotInvoked() {
        MatchingInvocationHandler matchingInvocationHandler = matchingInvocationHandlerFactory.createAssertNotInvokedVerifyingMatchingInvocationHandler();
        return startAssertMatchingInvocation(matchingInvocationHandler);
    }

    public void resetBehavior() {
        behaviorDefiningInvocations.reset();
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
