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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitilsnew.core.reflect.OriginalFieldValue;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class InjectionServiceInjectIntoStaticTest {

    /* Tested object */
    private InjectionService injectionService = new InjectionService();

    private ObjectToInject objectToInject;


    @Before
    public void initialize() {
        objectToInject = new ObjectToInject("value");

        Target.field = "original value";
        Target.inner = new InnerClass();
        Target.inner.field = "original inner value";
    }


    @Test
    public void simpleProperty() {
        OriginalFieldValue result = injectionService.injectIntoStatic(Target.class, "field", objectToInject, false);

        assertEquals("value", Target.field);
        assertEquals("original value", result.getOriginalValue());
    }

    @Test
    public void compositeProperty() {
        OriginalFieldValue result = injectionService.injectIntoStatic(Target.class, "inner.field", objectToInject, false);

        assertEquals("value", Target.inner.field);
        assertEquals("original inner value", result.getOriginalValue());
    }

    @Test
    public void autoCreationOfInnerFields() {
        Target.inner = null;
        OriginalFieldValue result = injectionService.injectIntoStatic(Target.class, "inner.field", objectToInject, true);

        assertEquals("value", Target.inner.field);
        assertEquals(null, result.getOriginalValue());
        assertEquals("inner", result.getField().getName());
    }

    @Test
    public void nullValue() {
        Target.field = "value";
        objectToInject = new ObjectToInject(null);

        injectionService.injectIntoStatic(Target.class, "field", objectToInject, false);
        assertNull(Target.field);
    }

    @Test
    public void exceptionWhenFieldDoesNotExist() {
        try {
            injectionService.injectIntoStatic(Target.class, "xxx", objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertNotNull(e.getCause());
            assertEquals("Unable to inject into static property 'xxx' with target class org.unitils.inject.core.InjectionServiceInjectIntoStaticTest$Target. Reason:\n" +
                    "Unable to get field for property 'xxx'. Field with name 'xxx' does not exist on class org.unitils.inject.core.InjectionServiceInjectIntoStaticTest$Target or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenInnerObjectIsNull() {
        Target.inner = null;
        try {
            injectionService.injectIntoStatic(Target.class, "inner.field", objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertNotNull(e.getCause());
            assertEquals("Unable to inject into static property 'inner.field' with target class org.unitils.inject.core.InjectionServiceInjectIntoStaticTest$Target. Reason:\n" +
                    "Unable to set value for composite field with name 'inner.field'. Inner field with name 'inner' is null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullObjectToInject() {
        try {
            injectionService.injectIntoStatic(Target.class, "field", null, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static. Object to inject cannot be null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullTarget() {
        try {
            injectionService.injectIntoStatic(null, "field", objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static property 'field'. Target class cannot be null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullProperty() {
        try {
            injectionService.injectIntoStatic(Target.class, null, objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static. Property cannot be null or empty.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenEmptyProperty() {
        try {
            injectionService.injectIntoStatic(Target.class, "  ", objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static. Property cannot be null or empty.", e.getMessage());
        }
    }

    @Test
    public void valueCanBeRestored() {
        OriginalFieldValue result = injectionService.injectIntoStatic(Target.class, "field", objectToInject, false);
        result.restoreToOriginalValue();

        assertEquals("original value", Target.field);
    }


    public static class Target {

        private static String field;
        private static InnerClass inner;

    }

    public static class InnerClass {

        private String field;

    }
}
