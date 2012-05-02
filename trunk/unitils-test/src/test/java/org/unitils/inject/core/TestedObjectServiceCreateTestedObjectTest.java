/*
 * Copyright 2012,  Unitils.org
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

package org.unitils.inject.core;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestedObjectServiceCreateTestedObjectTest {

    /* Tested object */
    private TestedObjectService testedObjectService = new TestedObjectService();


    @Test
    public void createTestedObject() {
        Object result = testedObjectService.createTestedObject(MyClass.class);
        assertTrue(result instanceof MyClass);
    }

    @Test
    public void nullWhenTypeIsInterface() {
        Object result = testedObjectService.createTestedObject(MyInterface.class);
        assertNull(result);
    }

    @Test
    public void nullWhenTypeIsEnum() {
        Object result = testedObjectService.createTestedObject(MyEnum.class);
        assertNull(result);
    }

    @Test
    public void nullWhenTypeIsAbstract() {
        Object result = testedObjectService.createTestedObject(MyAbstractClass.class);
        assertNull(result);
    }

    @Test
    public void nullWhenThereIsNoDefaultConstructor() {
        Object result = testedObjectService.createTestedObject(NoDefaultConstructorClass.class);
        assertNull(result);
    }


    private static class MyClass {
    }

    private static interface MyInterface {
    }

    private static abstract class MyAbstractClass {
    }

    private static class NoDefaultConstructorClass {

        private NoDefaultConstructorClass(String arg) {
        }
    }

    private static @interface MyEnum {
    }
}
