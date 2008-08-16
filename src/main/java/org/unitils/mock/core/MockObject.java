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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for handling the behavior of a mock object. This will register the required behavior of the mock during the
 * setting up of the mock and then executes the correct behavior during the actual execution.
 * <p/>
 * A behavior will be executed when it matches the performed method invocation. There are 2 types of behaviors:
 * behaviors that can be matched only once, they are removed once they have been used and behaviors that can
 * be used over and over again.
 * <p/>
 * If no behavior can be matched with an invocation, the default behavior is executed. If the mock object is created
 * as a partial mock, the actual method behavior of the mocked class will be invoked. If it is not a parital mock,
 * a default null result value is returned.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockObject<T> {

    /* The name of the mock (e.g. the name of the field) */
    private String name;

    /* The class type that is mocked */
    private Class<T> mockedClass;

    /* True if the actual method behavior should be invoked if no mock behavior is defined for the method */
    private boolean partialMock;

    /* Mock behaviors that are removed once they have been matched */
    private List<MockBehavior> oneTimeMatchingMockBehaviors = new ArrayList<MockBehavior>();

    /* Mock behaviors that can be matched and re-used for several invocation */
    private List<MockBehavior> alwaysMatchingMockBehaviors = new ArrayList<MockBehavior>();


    /**
     * Creates a mock for the given class type.
     *
     * @param name        The name of the mock (e.g. the name of the instance field)
     * @param mockedClass The class type that is mocked
     * @param partialMock True if this mock is a partial mock
     */
    public MockObject(String name, Class<T> mockedClass, boolean partialMock) {
        this.name = name;
        this.mockedClass = mockedClass;
        this.partialMock = partialMock;
    }


    /**
     * Registers the given mock behavior. This mock behavior can be matched only once.
     *
     * @param mockBehavior The mock behavior, not null
     */
    public void registerOneTimeMatchingMockBehavior(MockBehavior mockBehavior) {
        oneTimeMatchingMockBehaviors.add(mockBehavior);
    }


    /**
     * Registers the given mock behavior. This mock behavior can be matched multiple times.
     *
     * @param mockBehavior The mock behavior, not null
     */
    public void registerAlwaysMatchingMockBehavior(MockBehavior mockBehavior) {
        alwaysMatchingMockBehaviors.add(mockBehavior);
    }


    /**
     * Executes the behavior that matches the given invocation.
     *
     * @param invocation The method invocation, not null
     * @return The result value of the behavior
     */
    public Object executeMatchingBehavior(Invocation invocation) throws Throwable {
        // Check if there is a one-time matching behavior that hasn't been invoked yet
        MockBehavior oneTimeMatchingBehavior = getOneTimeMatchingMockBehavior(invocation);
        if (oneTimeMatchingBehavior != null) {
            return oneTimeMatchingBehavior.execute(invocation);
        }

        // Check if there is an always-matching behavior
        MockBehavior alwaysMatchingBehavior = getAlwaysMatchingMockBehavior(invocation);
        if (alwaysMatchingBehavior != null) {
            return alwaysMatchingBehavior.execute(invocation);
        }

        // There's no matching behavior, execute the default one
        if (partialMock) {
            return invocation.invokeOriginalBehavior();
        }
        return null;
    }


    /**
     * @return The name of the mock (e.g. the name of the field), not null
     */
    public String getName() {
        return name;
    }

    /**
     * @return The class type that is mocked, not null
     */
    public Class<T> getMockedClass() {
        return mockedClass;
    }


    /**
     * Finds a matching mock behavior that was registered as a one time matching mocking behavior.
     * If a matching behavior is found, it will be removed so that it is no longer available for matching.
     *
     * @param invocation The invocation to match to, not null
     * @return The matching mock behavior, null if none found
     */
    protected MockBehavior getOneTimeMatchingMockBehavior(Invocation invocation) {
        Iterator<MockBehavior> iterator = oneTimeMatchingMockBehaviors.iterator();
        while (iterator.hasNext()) {
            MockBehavior mockBehavior = iterator.next();
            if (mockBehavior.matches(invocation)) {
                iterator.remove();
                return mockBehavior;
            }
        }
        return null;
    }


    /**
     * Finds a matching mock behavior that was registered as an always matching mocking behavior.
     *
     * @param invocation The invocation to match to, not null
     * @return The matching mock behavior, null if none found
     */
    protected MockBehavior getAlwaysMatchingMockBehavior(Invocation invocation) {
        for (MockBehavior mockBehavior : alwaysMatchingMockBehaviors) {
            if (mockBehavior.matches(invocation)) {
                return mockBehavior;
            }
        }
        return null;
    }


    // todo remove
    public List<MockBehavior> getAlwaysMatchingMockBehaviors() {
        return alwaysMatchingMockBehaviors;
    }

    // todo remove
    public List<MockBehavior> getOneTimeMatchingMockBehaviors() {
        return oneTimeMatchingMockBehaviors;
    }


}
