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

import org.unitils.mock.PartialMock;

/**
 * Implementation of a Mock and PartialMock.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class PartialMockObject<T> extends MockObject<T> implements PartialMock<T> {


    /**
     * Creates a mock of the given type for the given scenario.
     *
     * @param name        The name of the mock, e.g. the field-name, not null
     * @param mockedClass The mock type that will be proxied, not null
     * @param scenario    The scenario, not null
     */
    public PartialMockObject(String name, Class<T> mockedClass, Scenario scenario) {
        super(name, mockedClass, scenario);
    }


    @Override
    protected MockProxy<T> createMockProxy() {
        return new PartialMockProxy<T>(oneTimeMatchingBehaviorDefiningInvocations, alwaysMatchingBehaviorDefiningInvocations, scenario, matchingInvocationBuilder);
    }

}