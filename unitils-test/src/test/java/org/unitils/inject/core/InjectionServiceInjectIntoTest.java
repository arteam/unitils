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
public class InjectionServiceInjectIntoTest {

    /* Tested object */
    private InjectionService injectionService = new InjectionService();

    private TargetClass target;
    private ObjectToInject objectToInject;


    @Before
    public void initialize() {
        target = new TargetClass();
        objectToInject = new ObjectToInject("value");

        target.field = "original value";
        target.inner = new InnerClass();
        target.inner.field = "original inner value";
    }


    @Test
    public void simpleProperty() {
        OriginalFieldValue result = injectionService.injectInto(target, "field", objectToInject, false);

        assertEquals("value", target.field);
        assertEquals("original value", result.getOriginalValue());
    }

    @Test
    public void compositeProperty() {
        OriginalFieldValue result = injectionService.injectInto(target, "inner.field", objectToInject, false);

        assertEquals("value", target.inner.field);
        assertEquals("original inner value", result.getOriginalValue());
    }

    @Test
    public void autoCreationOfInnerFields() {
        target.inner = null;
        OriginalFieldValue result = injectionService.injectInto(target, "inner.field", objectToInject, true);

        assertEquals("value", target.inner.field);
        assertEquals(null, result.getOriginalValue());
        assertEquals("inner", result.getField().getName());
    }

    @Test
    public void nullValue() {
        target.field = "value";
        objectToInject = new ObjectToInject(null);

        injectionService.injectInto(target, "field", objectToInject, false);
        assertNull(target.field);
    }

    @Test
    public void exceptionWhenFieldDoesNotExist() {
        try {
            injectionService.injectInto(target, "xxx", objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertNotNull(e.getCause());
            assertEquals("Unable to inject into property 'xxx' with target of type org.unitils.inject.core.InjectionServiceInjectIntoTest$TargetClass.\n" +
                    "Reason: Unable to get field for property 'xxx'. Field with name 'xxx' does not exist on class org.unitils.inject.core.InjectionServiceInjectIntoTest$TargetClass or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenInnerObjectIsNull() {
        try {
            target.inner = null;

            injectionService.injectInto(target, "inner.field", objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertNotNull(e.getCause());
            assertEquals("Unable to inject into property 'inner.field' with target of type org.unitils.inject.core.InjectionServiceInjectIntoTest$TargetClass.\n" +
                    "Reason: Unable to set value for composite field with name 'inner.field'. Inner field with name 'inner' is null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullObjectToInject() {
        try {
            injectionService.injectInto(target, "field", null, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into. Object to inject cannot be null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullTarget() {
        try {
            injectionService.injectInto(null, "field", objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into property 'field'. Target cannot be null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullProperty() {
        try {
            injectionService.injectInto(target, null, objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into. Property cannot be null or empty.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenEmptyProperty() {
        try {
            injectionService.injectInto(target, "  ", objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into. Property cannot be null or empty.", e.getMessage());
        }
    }

    @Test
    public void valueCanBeRestored() {
        OriginalFieldValue result = injectionService.injectInto(target, "field", objectToInject, false);
        result.restoreToOriginalValue();

        assertEquals("original value", target.field);
    }


    public static class TargetClass {

        private String field;
        private InnerClass inner;

    }

    public static class InnerClass {

        private String field;

    }
}
