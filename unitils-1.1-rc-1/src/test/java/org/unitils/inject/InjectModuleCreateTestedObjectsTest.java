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
package org.unitils.inject;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;

import java.util.AbstractList;
import java.util.EnumMap;
import java.util.List;

/**
 * Test for the automatic creation of the tested object, if null
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModuleCreateTestedObjectsTest extends UnitilsJUnit4 {

    private InjectModule injectModule = new InjectModule();

    private TestObject testObject;
    private TestObject_TestedObjectWithInterfaceType testObject_TestedObjectWithInterfaceType;
    private TestObject_TestedObjectWithAbstractClassType testObject_TestedObjectWithAbstractClassType;
    private TestObject_TestedObjectWithTypeThatHasNoDefaultConstructor testObject_TestedObjectWithTypeThatHasNoDefaultConstructor;


    @Before
    public void setUp() throws Exception {
        testObject = new TestObject();
        testObject_TestedObjectWithInterfaceType = new TestObject_TestedObjectWithInterfaceType();
        testObject_TestedObjectWithAbstractClassType = new TestObject_TestedObjectWithAbstractClassType();
        testObject_TestedObjectWithTypeThatHasNoDefaultConstructor = new TestObject_TestedObjectWithTypeThatHasNoDefaultConstructor();
    }


    @Test
    public void testNotCreatedWhenNotNull() {
        testObject.setTestedObject("test");
        injectModule.createTestedObjectsIfNull(testObject);

        assertEquals("test", testObject.getTestedObject());
    }


    @Test
    public void testCreatedWhenNull() {
        assertNull(testObject.getTestedObject());
        injectModule.createTestedObjectsIfNull(testObject);

        assertNotNull(testObject.getTestedObject());
    }


    @Test
    public void testNotCreatedWhenInterfaceType() {

        injectModule.createTestedObjectsIfNull(testObject_TestedObjectWithInterfaceType);
        assertNull(testObject_TestedObjectWithInterfaceType.getTestedObject());
    }


    @Test
    public void testNotCreatedWhenAbstractClassType() {

        injectModule.createTestedObjectsIfNull(testObject_TestedObjectWithAbstractClassType);
        assertNull(testObject_TestedObjectWithAbstractClassType.getTestedObject());
    }


    @Test
    public void testNotCreatedWhenTypeThatHasNoDefaultConstructor() {

        injectModule.createTestedObjectsIfNull(testObject_TestedObjectWithTypeThatHasNoDefaultConstructor);
        assertNull(testObject_TestedObjectWithTypeThatHasNoDefaultConstructor.getTestedObject());
    }


    private class TestObject {

        @TestedObject
        private String testedObject;

        public String getTestedObject() {
            return testedObject;
        }

        public void setTestedObject(String testedObject) {
            this.testedObject = testedObject;
        }
    }


    private class TestObject_TestedObjectWithInterfaceType {

        @TestedObject
        private List<?> testedObject;

        public List<?> getTestedObject() {
            return testedObject;
        }

    }


    private class TestObject_TestedObjectWithAbstractClassType {

        @TestedObject
        private AbstractList<?> testedObject;

        public List<?> getTestedObject() {
            return testedObject;
        }

    }


    private class TestObject_TestedObjectWithTypeThatHasNoDefaultConstructor {

        @TestedObject
        private EnumMap<?, ?> testedObject;

        public EnumMap<?, ?> getTestedObject() {
            return testedObject;
        }

    }
}