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
import org.unitils.core.UnitilsException;
import org.unitilsnew.core.reflect.OriginalFieldValue;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class InjectionByTypeServiceInjectIntoByTypeTest {

    /* Tested object */
    private InjectionByTypeService injectionByTypeService = new InjectionByTypeService();

    private Target target = new Target();

    private Type1 type1 = new Type1();
    private Type2 type2 = new Type2();
    private Type3 type3 = new Type3();
    private Type5 type5 = new Type5();
    private Type7 type7 = new Type7();
    private Type8 type8 = new Type8();


    @Test
    public void exactMatch() {
        ObjectToInject objectToInject = new ObjectToInject(type1);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, true);

        assertSame(type1, target.type1);
        assertNull(target.type2a);
        assertNull(target.type2b);
        assertSame(target, result.getObject());
        assertNull(result.getOriginalValue());
        assertEquals("type1", result.getFieldWrapper().getName());
    }

    @Test
    public void assignableMatch() {
        ObjectToInject objectToInject = new ObjectToInject(type3);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, true);

        assertSame(type3, target.type1);
        assertNull(target.type2a);
        assertNull(target.type2b);
        assertSame(target, result.getObject());
        assertNull(result.getOriginalValue());
        assertEquals("type1", result.getFieldWrapper().getName());
    }

    @Test
    public void fieldOnSuperClass() {
        ObjectToInject objectToInject = new ObjectToInject(type8);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, true);

        assertSame(type8, ((Super) target).type8);
    }

    @Test
    public void mostSpecificAssignableMatchWins() {
        ObjectToInject objectToInject = new ObjectToInject(type7);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, true);

        assertSame(type7, target.type6);
    }

    @Test
    public void exceptionWhenMoreThanOneExactMatch() {
        try {
            ObjectToInject objectToInject = new ObjectToInject(type2);
            injectionByTypeService.injectIntoByType(target, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'class org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeTest$Type2'.\n" +
                    "More than one field with matching type found in class org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeTest$Target or one of its superclasses. Matching fields: [type2a, type2b].\n" +
                    "Specify the target field explicitly instead of injecting into by type.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenMoreThanOneAssignableMatch() {
        try {
            ObjectToInject objectToInject = new ObjectToInject(type5, Type4.class);
            injectionByTypeService.injectIntoByType(target, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'class org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeTest$Type4'.\n" +
                    "More than one field with matching type found in class org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeTest$Target or one of its superclasses. Matching fields: [type4a, type4b].\n" +
                    "Specify the target field explicitly instead of injecting into by type.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenValueHasWrongType() {
        try {
            ObjectToInject objectToInject = new ObjectToInject("xxx", Type1.class);
            injectionByTypeService.injectIntoByType(target, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'class org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeTest$Type1'.\n" +
                    "Reason: Unable to set value for field with name 'type1'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeTest$Type1. Value: xxx\n" +
                    "Reason: IllegalArgumentException: Can not set org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeTest$Type1 field org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeTest$Target.type1 to java.lang.String", e.getMessage());
        }
    }

    @Test
    public void ignoreWhenNoMatchFound() {
        ObjectToInject objectToInject = new ObjectToInject(null, StringBuilder.class);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, false);
        assertNull(result);
    }

    @Test
    public void exceptionWhenNoMatchFound() {
        try {
            ObjectToInject objectToInject = new ObjectToInject(null, StringBuilder.class);
            injectionByTypeService.injectIntoByType(target, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'class java.lang.StringBuilder'.\n" +
                    "No field of matching type exists on class org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeTest$Target or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void nullValue() {
        target.type1 = type1;
        ObjectToInject objectToInject = new ObjectToInject(null, Type1.class);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, true);

        assertNull(target.type1);
    }

    @Test
    public void exceptionWhenNullObjectToInject() {
        try {
            injectionByTypeService.injectIntoByType(target, null, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type. Object to inject cannot be null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullTargets() {
        try {
            ObjectToInject objectToInject = new ObjectToInject(type1);
            injectionByTypeService.injectIntoByType(null, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'class org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeTest$Type1'. Target cannot be null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullType() {
        try {
            ObjectToInject objectToInject = new ObjectToInject(null, null);
            injectionByTypeService.injectIntoByType(target, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type. Type cannot be null.", e.getMessage());
        }
    }

    @Test
    public void valueCanBeRestored() {
        Type1 originalType1 = new Type1();
        target.type1 = originalType1;

        ObjectToInject objectToInject = new ObjectToInject(type1);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, true);

        result.restoreToOriginalValue();
        assertSame(originalType1, target.type1);
    }


    public static class Super {

        private Type8 type8;

    }

    public static class Target extends Super {

        private static Type1 staticFieldIsIgnored;

        private Type1 type1;
        private Type2 type2a;
        private Type2 type2b;

        private Type4 type4a;
        private Type4 type4b;

        private Type6 type6;
    }

    public static class Type1 {
    }

    public static class Type2 {
    }

    public static class Type3 extends Type1 {
    }

    public static class Type4 extends Type1 {
    }

    public static class Type5 extends Type4 {
    }

    public static class Type6 extends Type1 {
    }

    public static class Type7 extends Type6 {
    }

    public static class Type8 {
    }

}
