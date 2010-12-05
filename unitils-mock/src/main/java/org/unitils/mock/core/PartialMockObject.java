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
     * @param name        The name of the mock, e.g. the field-name, null for the default
     * @param mockedClass The mock type that will be proxied, use the raw type when mocking generic types, not null
     * @param testObject  The test object, not null
     */
    public PartialMockObject(String name, Class<?> mockedClass, Object testObject) {
        super(name, mockedClass, testObject);
    }


    @Override
    protected MockProxy<T> createMockProxy() {
        return new PartialMockProxy<T>(name, mockedType, oneTimeMatchingBehaviorDefiningInvocations, alwaysMatchingBehaviorDefiningInvocations, getCurrentScenario(), getMatchingInvocationBuilder());
    }

}