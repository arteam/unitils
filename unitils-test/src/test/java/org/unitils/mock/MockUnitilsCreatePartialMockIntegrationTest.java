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

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class MockUnitilsCreatePartialMockIntegrationTest {


    @Test
    public void createPartialMock() {
        Mock<TestClass> result = MockUnitils.createPartialMock("mockName", TestClass.class, this);
        assertEquals("mockName", result.toString());
        assertEquals(asList("original value"), result.getMock().method());
    }

    @Test
    public void settingBehavior() {
        Mock<TestClass> result = MockUnitils.createPartialMock("mockName", TestClass.class, this);
        result.returnsAll("new value").method();

        assertEquals("mockName", result.toString());
        assertEquals(asList("new value"), result.getMock().method());
    }

    @Test
    public void createPartialMockWithDefaultName() {
        Mock<TestClass> result = MockUnitils.createPartialMock(TestClass.class, this);
        assertEquals("testClass", result.toString());
    }

    @Test
    public void mockNotInitializedWhenNoDefaultConstructor() {
        PartialMock<NoDefaultConstructorTestClass> result = MockUnitils.createPartialMock("mockName", NoDefaultConstructorTestClass.class, this);
        assertNull(result.getMock().value);
    }

    @Test
    public void createPartialMockPrototype() {
        NoDefaultConstructorTestClass prototype = new NoDefaultConstructorTestClass("test");
        PartialMock<NoDefaultConstructorTestClass> result = MockUnitils.createPartialMock("mockName", prototype, this);
        assertEquals("test", result.getMock().value);
        assertEquals("mockName", result.toString());
    }

    @Test
    public void createPartialMockPrototypeWithDefaultName() {
        NoDefaultConstructorTestClass prototype = new NoDefaultConstructorTestClass("test");
        PartialMock<NoDefaultConstructorTestClass> result = MockUnitils.createPartialMock(prototype, this);
        assertEquals("test", result.getMock().value);
        assertEquals("noDefaultConstructorTestClass", result.toString());
    }


    public static class TestClass {

        public List<String> method() {
            return asList("original value");
        }
    }

    public static class NoDefaultConstructorTestClass {

        private String value = "original";

        public NoDefaultConstructorTestClass(String value) {
            this.value = value;
        }
    }
}