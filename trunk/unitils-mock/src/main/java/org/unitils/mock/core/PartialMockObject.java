/*
 * Copyright Unitils.org
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

import org.unitils.mock.PartialMock;
import org.unitils.mock.annotation.MatchStatement;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.mockbehavior.impl.StubMockBehavior;

import static org.unitils.util.ReflectionUtils.copyFields;

/**
 * Implementation of a PartialMock.
 * For a partial mock, if a method is called that is not mocked, the original behavior will be called.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class PartialMockObject<T> extends MockObject<T> implements PartialMock<T> {

    /**
     * Creates a mock of the same type as the given mock prototype with un-capitalized type name + Mock as name, e.g. myServiceMock.
     * All instance fields of the given prototype will then be copied to the mock instance. This way you can have a
     * pre-initialized instance of the mock (e.g. when there is no default constructor).
     *
     * If the type mocked instance does not correspond to the declared type, a ClassCastException will occur when the mock
     * is used.
     *
     * @param mockPrototype The instance that will be wrapped with a proxy, use the raw type when mocking generic types, not null
     * @param testObject    The test object, not null
     */
    public PartialMockObject(Object mockPrototype, Object testObject) {
        this(null, mockPrototype, testObject);
    }

    /**
     * Creates a mock of the same type as the given mock prototype with the given name.
     * All instance fields of the given prototype will then be copied to the mock instance. This way you can have a
     * pre-initialized instance of the mock (e.g. when there is no default constructor).
     *
     * If the type mocked instance does not correspond to the declared type, a ClassCastException will occur when the mock
     * is used.
     *
     * If no name is given the un-capitalized type name + Mock is used, e.g. myServiceMock
     *
     * @param name           The name of the mock, e.g. the field-name, null for the default
     * @param mockedInstance The instance that will be wrapped with a proxy, use the raw type when mocking generic types, not null
     * @param testObject     The test object, not null
     */
    public PartialMockObject(String name, Object mockedInstance, Object testObject) {
        super(name, mockedInstance.getClass(), testObject);
        copyFields(mockedInstance, getMock());
    }


    /**
     * Creates a mock of the given type with un-capitalized type name + Mock as name, e.g. myServiceMock.
     *
     * There is no .class literal for generic types. Therefore you need to pass the raw type when mocking generic types.
     * E.g. Mock&lt;List&lt;String&gt;&gt; myMock = new MockObject("myMock", List.class, this);
     *
     * If the mocked type does not correspond to the declared type, a ClassCastException will occur when the mock
     * is used.
     *
     * @param mockedType The mock type that will be proxied, use the raw type when mocking generic types, not null
     * @param testObject The test object, not null
     */
    public PartialMockObject(Class<?> mockedType, Object testObject) {
        this(null, mockedType, testObject);
    }

    /**
     * Creates a mock of the given type for the given scenario.
     *
     * There is no .class literal for generic types. Therefore you need to pass the raw type when mocking generic types.
     * E.g. Mock&lt;List&lt;String&gt;&gt; myMock = new MockObject("myMock", List.class, this);
     *
     * If the mocked type does not correspond to the declared type, a ClassCastException will occur when the mock
     * is used.
     *
     * If no name is given the un-capitalized type name + Mock is used, e.g. myServiceMock
     *
     * @param name       The name of the mock, e.g. the field-name, null for the default
     * @param mockedType The mock type that will be proxied, use the raw type when mocking generic types, not null
     * @param testObject The test object, not null
     */
    public PartialMockObject(String name, Class<?> mockedType, Object testObject) {
        super(name, mockedType, testObject);
    }


    @MatchStatement
    public T stub() {
        MatchingInvocationHandler matchingInvocationHandler = createAlwaysMatchingBehaviorDefiningMatchingInvocationHandler(new StubMockBehavior());
        return startMatchingInvocation(matchingInvocationHandler);
    }


    @Override
    protected MockProxy<T> createMockProxy() {
        return new PartialMockProxy<T>(name, mockedType, oneTimeMatchingBehaviorDefiningInvocations, alwaysMatchingBehaviorDefiningInvocations, getCurrentScenario(), getMatchingInvocationBuilder());
    }

}