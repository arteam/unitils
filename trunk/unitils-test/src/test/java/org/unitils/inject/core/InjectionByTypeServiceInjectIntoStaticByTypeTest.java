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
public class InjectionByTypeServiceInjectIntoStaticByTypeTest {

    /* Tested object */
    private InjectionByTypeService injectionByTypeService = new InjectionByTypeService();

    private Target target;


    @Before
    public void initialize() {
        Target.type1 = null;
        Target.type2a = null;
        Target.type2b = null;
        Target.type4a = null;
        Target.type4b = null;
        Target.type6 = null;
    }

    @Test
    public void exactMatch() {
        Type1 type1 = new Type1();
        ObjectToInject objectToInject = new ObjectToInject(type1);
        OriginalFieldValue result = injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);

        assertSame(type1, Target.type1);
        assertNull(Target.type2a);
        assertNull(Target.type2b);
        assertNull(result.getObject());
        assertNull(result.getOriginalValue());
        assertEquals("type1", result.getFieldWrapper().getName());
    }

    @Test
    public void assignableMatch() {
        Type3 type3 = new Type3();
        ObjectToInject objectToInject = new ObjectToInject(type3);
        OriginalFieldValue result = injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);

        assertSame(type3, Target.type1);
        assertNull(Target.type2a);
        assertNull(Target.type2b);
        assertNull(result.getObject());
        assertNull(result.getOriginalValue());
        assertEquals("type1", result.getFieldWrapper().getName());
    }

    @Test
    public void fieldOnSuperClass() {
        Type8 type8 = new Type8();
        ObjectToInject objectToInject = new ObjectToInject(type8);

        OriginalFieldValue result = injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);
        assertSame(type8, Super.type8);
    }

    @Test
    public void mostSpecificAssignableMatchWins() {
        Type7 type7 = new Type7();
        ObjectToInject objectToInject = new ObjectToInject(type7);
        OriginalFieldValue result = injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);

        assertSame(type7, Target.type6);
    }

    @Test
    public void exceptionWhenMoreThanOneExactMatch() {
        try {
            Type2 type2 = new Type2();
            ObjectToInject objectToInject = new ObjectToInject(type2);
            injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static by type 'class org.unitils.inject.core.InjectionByTypeServiceInjectIntoStaticByTypeTest$Type2'.\n" +
                    "More than one static field with matching type found in class org.unitils.inject.core.InjectionByTypeServiceInjectIntoStaticByTypeTest$Target or one of its superclasses. Matching fields: [type2a, type2b].\n" +
                    "Specify the target field explicitly instead of injecting into by type.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenMoreThanOneAssignableMatch() {
        try {
            Type5 type5 = new Type5();
            ObjectToInject objectToInject = new ObjectToInject(type5, Type4.class);
            injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static by type 'class org.unitils.inject.core.InjectionByTypeServiceInjectIntoStaticByTypeTest$Type4'.\n" +
                    "More than one static field with matching type found in class org.unitils.inject.core.InjectionByTypeServiceInjectIntoStaticByTypeTest$Target or one of its superclasses. Matching fields: [type4a, type4b].\n" +
                    "Specify the target field explicitly instead of injecting into by type.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenValueHasWrongType() {
        try {
            ObjectToInject objectToInject = new ObjectToInject("xxx", Type1.class);
            injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static by type 'class org.unitils.inject.core.InjectionByTypeServiceInjectIntoStaticByTypeTest$Type1'.\n" +
                    "Reason: Unable to set value for field with name 'type1'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: org.unitils.inject.core.InjectionByTypeServiceInjectIntoStaticByTypeTest$Type1. Value: xxx\n" +
                    "Reason: IllegalArgumentException: Can not set static org.unitils.inject.core.InjectionByTypeServiceInjectIntoStaticByTypeTest$Type1 field org.unitils.inject.core.InjectionByTypeServiceInjectIntoStaticByTypeTest$Target.type1 to java.lang.String", e.getMessage());
        }
    }

    @Test
    public void ignoreWhenNoMatchFound() {
        ObjectToInject objectToInject = new ObjectToInject(null, StringBuilder.class);
        OriginalFieldValue result = injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, false);
        assertNull(result);
    }

    @Test
    public void exceptionWhenNoMatchFound() {
        try {
            ObjectToInject objectToInject = new ObjectToInject(null, StringBuilder.class);
            injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static by type 'class java.lang.StringBuilder'.\n" +
                    "No static field of matching type exists on class org.unitils.inject.core.InjectionByTypeServiceInjectIntoStaticByTypeTest$Target or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void nullValue() {
        ObjectToInject objectToInject = new ObjectToInject(null, Type1.class);
        OriginalFieldValue result = injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);

        assertNull(Target.type1);
    }

    @Test
    public void exceptionWhenNullObjectToInject() {
        try {
            injectionByTypeService.injectIntoStaticByType(Target.class, null, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static by type. Object to inject cannot be null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullTarget() {
        try {
            Type1 type1 = new Type1();
            ObjectToInject objectToInject = new ObjectToInject(type1);
            injectionByTypeService.injectIntoStaticByType(null, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static by type 'class org.unitils.inject.core.InjectionByTypeServiceInjectIntoStaticByTypeTest$Type1'. Target class cannot be null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullType() {
        try {
            ObjectToInject objectToInject = new ObjectToInject(null, null);
            injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static by type. Type cannot be null.", e.getMessage());
        }
    }

    @Test
    public void valueCanBeRestored() {
        Type1 originalType1 = new Type1();
        Target.type1 = originalType1;

        Type1 newType1 = new Type1();
        ObjectToInject objectToInject = new ObjectToInject(newType1);
        OriginalFieldValue result = injectionByTypeService.injectIntoStaticByType(Target.class, objectToInject, true);

        result.restoreToOriginalValue();
        assertSame(originalType1, Target.type1);
    }


    public static class Super {

        private static Type8 type8;

    }

    public static class Target extends Super {

        private Type1 regularFieldIsIgnored;

        private static Type1 type1;
        private static Type2 type2a;
        private static Type2 type2b;

        private static Type4 type4a;
        private static Type4 type4b;

        private static Type6 type6;

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
