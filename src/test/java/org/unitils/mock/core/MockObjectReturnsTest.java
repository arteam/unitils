/*
 * Copyright 2006-2007,  Unitils.org
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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests the mock object functionality.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockObjectReturnsTest {

    /* Class under test */
    private MockObject<TestClass> mockObject;

    @Before
    public void setUp() {
        Scenario scenario = new Scenario(null);
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, false, scenario);
    }


    /**
     * Tests setting a return behavior for the mock. The behavior is an always matching behavior
     * so the method should keep returning that same value.
     */
    @Test
    public void returns() {
        mockObject.returns("aValue").testMethodString();

        String result1 = mockObject.getMock().testMethodString();
        String result2 = mockObject.getMock().testMethodString();
        assertLenientEquals("aValue", result1);
        assertLenientEquals("aValue", result2);
    }


    /**
     * Tests setting a once return behavior for the mock. The behavior should be executed only once, the second time
     * the default null value is returned.
     */
    @Test
    public void onceReturns() {
        mockObject.onceReturns("aValue").testMethodString();

        String result1 = mockObject.getMock().testMethodString();
        String result2 = mockObject.getMock().testMethodString();
        assertLenientEquals("aValue", result1);
        assertNull(result2);
    }


    /**
     * Tests the return behavior when no behavior was defined. The null value should be
     * returned as default object value.
     */
    @Test
    public void defaultBehaviorObject() {
        String result = mockObject.getMock().testMethodString();
        assertLenientEquals(null, result);
    }


    /**
     * Tests the return behavior when no behavior was defined. The 0 value should be
     * returned as default number value.
     */
    @Test
    public void defaultBehaviorNumber() {
        int result = mockObject.getMock().testMethodNumber();
        assertLenientEquals(0, result);
    }


    /**
     * Tests the return behavior when no behavior was defined. An empty list should be
     * returned as default list value.
     */
    @Test
    public void defaultBehaviorList() {
        List<String> result = mockObject.getMock().testMethodList();
        assertLenientEquals(0, result.size());
    }


    /**
     * Tests the return behavior when no behavior was defined. An empty set should be
     * returned as default set value.
     */
    @Test
    public void defaultBehaviorSet() {
        Set<String> result = mockObject.getMock().testMethodSet();
        assertLenientEquals(0, result.size());
    }


    /**
     * Tests the return behavior when no behavior was defined. An empty map should be
     * returned as default map value.
     */
    @Test
    public void defaultBehaviorMap() {
        Map<String, String> result = mockObject.getMock().testMethodMap();
        assertLenientEquals(0, result.size());
    }


    /**
     * When a mock instance is given, the mock proxy instance should be returned instead.
     */
    @Test
    public void returnsMock() {
        MockObject<Set> mockedSet = new MockObject<Set>("mock", Set.class, false, new Scenario());
        mockObject.returns(mockedSet).testMethodSet();

        Set<String> result = mockObject.getMock().testMethodSet();
        assertSame(mockedSet.getMock(), result);
    }


    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        public String testMethodString();

        public int testMethodNumber();

        public List<String> testMethodList();

        public Set<String> testMethodSet();

        public Map<String, String> testMethodMap();

    }

}