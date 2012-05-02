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
package org.unitils.inject;

import org.junit.Test;
import org.unitils.inject.annotation.TestedObject;
import org.unitilsnew.UnitilsJUnit4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TestedObjectsIntegrationTest extends UnitilsJUnit4 {

    @TestedObject
    private TestObject testObject;

    @TestedObject
    private TestObject existingTestObject = new TestObject("existing");

    @TestedObject
    private TestInterface testInterface;

    @TestedObject
    private TestAbstractClass testAbstractClass;

    @TestedObject
    private TestObjectWithoutDefaultConstructor testObjectWithoutDefaultConstructor;


    @Test
    public void autoCreationOfTestedObjects() {
        // test object created
        assertNotNull(testObject);
        // existing test object not overwritten
        assertNotNull(existingTestObject);
        assertNotNull(existingTestObject.value = "existing");
        // test object for interface not created
        assertNull(testInterface);
        // test object for abstract class not created
        assertNull(testAbstractClass);
        // test object not created when there is no default constructor
        assertNull(testObjectWithoutDefaultConstructor);
    }


    private static class TestObject {

        private String value;

        private TestObject() {
        }

        private TestObject(String value) {
            this.value = value;
        }
    }

    private static interface TestInterface {
    }

    private static abstract class TestAbstractClass {
    }

    private static class TestObjectWithoutDefaultConstructor {

        private TestObjectWithoutDefaultConstructor(String arg) {
        }
    }
}