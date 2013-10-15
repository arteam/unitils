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

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.PartialMock;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PartialMockConstructionIntegrationTest extends UnitilsJUnit4 {

    private PartialMock<TestClass> mockObject;
    private PartialMock<TestClassNoDefaultConstructor> mockObjectNoDefaultConstructor;
    private PartialMock<TestInterface> mockObjectInterface;


    @Test
    public void defaultConstructorCalledAndFieldsInitialized() {
        TestClass result = mockObject.getMock();
        assertEquals(999, result.initializedValue);
        assertTrue(result.defaultConstructorCalled);
        assertFalse(result.otherConstructorCalled);
    }

    @Test
    public void fieldsNotInitializedWhenThereIsNoDefaultConstructor() {
        TestClassNoDefaultConstructor result = mockObjectNoDefaultConstructor.getMock();
        assertEquals(0, result.initializedValue);
        assertFalse(result.otherConstructorCalled);
    }

    @Test
    public void partialMockForInterfaceShouldSucceed() {
        TestInterface result = mockObjectInterface.getMock();
        assertNotNull(result);
    }


    public static abstract class TestClass {

        private boolean defaultConstructorCalled;
        private boolean otherConstructorCalled;
        private int initializedValue = 999;

        public TestClass() {
            this.defaultConstructorCalled = true;
        }

        public TestClass(String value) {
            this.otherConstructorCalled = true;
        }
    }

    public static abstract class TestClassNoDefaultConstructor {

        private boolean otherConstructorCalled;
        private int initializedValue = 999;

        public TestClassNoDefaultConstructor(String value) {
            this.otherConstructorCalled = true;
        }
    }

    public static interface TestInterface {
    }
}