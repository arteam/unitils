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
import org.unitils.inject.annotation.TestedObject;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.ClassWrapper;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class TargetServiceGetTargetsForInjectionTest {

    /* Tested object */
    private TargetService targetService = new TargetService();

    private TestInstance testInstance;


    @Before
    public void initialize() {
        testInstance = new TestInstance(new ClassWrapper(MyClass.class), new MyClass(), null);
    }


    @Test
    public void targetNamesSpecified() {
        List<?> result = targetService.getTargetsForInjection(asList("field3", "field2"), testInstance);
        assertLenientEquals(asList("value3", "value2"), result);
    }

    @Test
    public void testedObjectsReturnedWhenNullTargetNames() {
        List<?> result = targetService.getTargetsForInjection(null, testInstance);
        assertLenientEquals(asList("value1", "value2"), result);
    }

    @Test
    public void testedObjectsReturnedWhenEmptyTargetNames() {
        List<?> result = targetService.getTargetsForInjection(Collections.<String>emptyList(), testInstance);
        assertLenientEquals(asList("value1", "value2"), result);
    }

    @Test
    public void staticFieldUsingTargetName() {
        testInstance = new TestInstance(new ClassWrapper(StaticFieldClass.class), new StaticFieldClass(), null);

        List<?> result = targetService.getTargetsForInjection(asList("field2"), testInstance);
        assertLenientEquals(asList("value2"), result);
    }

    @Test
    public void staticFieldUsingTestedObjectAreNotSupported() {
        try {
            testInstance = new TestInstance(new ClassWrapper(StaticFieldClass.class), new StaticFieldClass(), null);
            targetService.getTargetsForInjection(null, testInstance);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No targets for injection found.\n" +
                    "The targets should either be specified explicitly using the target property or by annotating the target fields using the @TestedObject annotation.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoTargetNamesAndNoTestedObjects() {
        try {
            testInstance = new TestInstance(new ClassWrapper(NoTestedObjectClass.class), new NoTestedObjectClass(), null);

            targetService.getTargetsForInjection(Collections.<String>emptyList(), testInstance);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No targets for injection found.\n" +
                    "The targets should either be specified explicitly using the target property or by annotating the target fields using the @TestedObject annotation.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenTargetsCannotBeFound() {
        try {
            targetService.getTargetsForInjection(asList("xxx"), testInstance);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get targets for injection.\n" +
                    "Reason: Unable to get field with name 'xxx'. No such field exists on class org.unitils.inject.core.TargetServiceGetTargetsForInjectionTest$MyClass or one of its superclasses.", e.getMessage());
        }
    }


    private static class MyClass {

        @TestedObject
        private String field1 = "value1";
        @TestedObject
        private String field2 = "value2";
        private String field3 = "value3";
    }

    private static class StaticFieldClass {

        @TestedObject
        private static String field1 = "value1";
        private static String field2 = "value2";
    }

    private static class NoTestedObjectClass {

        private String field1 = "value1";
    }
}
