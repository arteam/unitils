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

package org.unitils.inject.core;

import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.core.reflect.OriginalFieldValue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class InjectionByTypeServiceInjectIntoByTypeGenericTypeTest {

    /* Tested object */
    private InjectionByTypeService injectionByTypeService = new InjectionByTypeService();

    private Target target = new Target();
    private List<Type1> type1 = new ArrayList<Type1>();
    private List<Type2> type2 = new ArrayList<Type2>();
    private List<Type4> type4 = new ArrayList<Type4>();
    private List<Type5> type5 = new ArrayList<Type5>();
    private List<Type6> type6 = new ArrayList<Type6>();
    private List<Type8> type8 = new ArrayList<Type8>();


    @Test
    public void exactMatch() throws Exception {
        Type genericType = getClass().getDeclaredField("type1").getGenericType();
        ObjectToInject objectToInject = new ObjectToInject(type1, genericType);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, true);

        assertSame(type1, target.type1);
    }

    @Test
    public void assignableMatch() throws Exception {
        Type genericType = getClass().getDeclaredField("type6").getGenericType();
        ObjectToInject objectToInject = new ObjectToInject(type6, genericType);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, true);

        assertSame(type6, target.type3);
    }

    @Test
    public void mostSpecificAssignableMatchWins() throws Exception {
        Type genericType = getClass().getDeclaredField("type8").getGenericType();
        ObjectToInject objectToInject = new ObjectToInject(type8, genericType);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, true);

        assertSame(type8, target.type3);
    }

    @Test
    public void moreThanOneExactMatch() throws Exception {
        try {
            Type genericType = getClass().getDeclaredField("type2").getGenericType();
            ObjectToInject objectToInject = new ObjectToInject(type2, genericType);
            injectionByTypeService.injectIntoByType(target, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'java.util.List<org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeGenericTypeTest$Type2>'.\n" +
                    "More than one field with matching type found in class org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeGenericTypeTest$Target or one of its superclasses. Matching fields: [type2a, type2b].\n" +
                    "Specify the target field explicitly instead of injecting into by type.", e.getMessage());
        }
    }

    @Test
    public void moreThanOneAssignableMatch() throws Exception {
        try {
            Type genericType = getClass().getDeclaredField("type4").getGenericType();
            ObjectToInject objectToInject = new ObjectToInject(type5, genericType);
            injectionByTypeService.injectIntoByType(target, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'java.util.List<org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeGenericTypeTest$Type4>'.\n" +
                    "More than one field with matching type found in class org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeGenericTypeTest$Target or one of its superclasses. Matching fields: [type4a, type4b].\n" +
                    "Specify the target field explicitly instead of injecting into by type.", e.getMessage());
        }
    }

    @Test
    public void valueHasWrongType() throws Exception {
        try {
            Type genericType = getClass().getDeclaredField("type1").getGenericType();
            ObjectToInject objectToInject = new ObjectToInject("xxx", genericType);
            injectionByTypeService.injectIntoByType(target, objectToInject, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'java.util.List<org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeGenericTypeTest$Type1>'.\n" +
                    "Reason: Unable to set value for field with name 'type1'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: java.util.List. Value: xxx\n" +
                    "Reason: IllegalArgumentException: Can not set java.util.List field org.unitils.inject.core.InjectionByTypeServiceInjectIntoByTypeGenericTypeTest$Target.type1 to java.lang.String", e.getMessage());
        }
    }

    @Test
    public void valueCanBeRestored() throws Exception {
        List<Type1> originalType1 = new ArrayList<Type1>();
        target.type1 = originalType1;

        Type genericType = getClass().getDeclaredField("type1").getGenericType();
        ObjectToInject objectToInject = new ObjectToInject(type1, genericType);
        OriginalFieldValue result = injectionByTypeService.injectIntoByType(target, objectToInject, true);

        result.restoreToOriginalValue();
        assertSame(originalType1, target.type1);
    }


    public static class Target {

        private List<Type1> type1;
        private List<Type2> type2a;
        private List<Type2> type2b;

        private List<Type4> type4a;
        private List<Type4> type4b;

        private List<? extends Type3> type3;
        private List<?> type7;

    }

    public static class Type1 {
    }

    public static class Type2 {
    }

    public static class Type3 {
    }

    public static class Type4 extends Type1 {
    }

    public static class Type5 extends Type4 {
    }

    public static class Type6 extends Type3 {
    }

    public static class Type7 extends Type3 {
    }

    public static class Type8 extends Type7 {
    }

    public static class Type9 {
    }

}
