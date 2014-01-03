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
package org.unitils.core.util;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.*;
import static org.unitils.core.util.ReflectionUtils.createInstanceOfType;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionUtilsCreateInstanceOfTypeTest extends UnitilsJUnit4 {


    @Test
    public void publicConstructor() {
        TestConstructor result = createInstanceOfType(TestConstructor.class.getName(), false);
        assertNotNull(result);
    }

    @Test
    public void privateConstructor_byPassing() {
        TestPrivateConstructor result = createInstanceOfType(TestPrivateConstructor.class.getName(), true);
        assertNotNull(result);
    }

    @Test
    public void privateConstructor_noByPassed() {
        try {
            createInstanceOfType(TestPrivateConstructor.class.getName(), false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Error while trying to create object of class org.unitils.core.util.ReflectionUtilsCreateInstanceOfTypeTest$TestPrivateConstructor\n" +
                    "Reason: IllegalAccessException: Class org.unitils.core.util.ReflectionUtils can not access a member of class org.unitils.core.util.ReflectionUtilsCreateInstanceOfTypeTest$TestPrivateConstructor with modifiers \"private\"", e.getMessage());
        }
    }

    @Test
    public void noConstructor() {
        TestNoConstructor result = createInstanceOfType(TestNoConstructor.class.getName(), false);
        assertNotNull(result);
    }

    @Test
    public void argumentConstructor() {
        TestConstructor result = createInstanceOfType(TestConstructor.class, false, new Class[]{String.class}, new Object[]{"value"});
        assertEquals("value", result.value);
    }

    @Test
    public void noConstructorNonStaticInnerClass() {
        try {
            createInstanceOfType(TestNoConstructorInnerClass.class.getName(), false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Creation of an instance of a non-static innerclass is not possible using reflection. The type TestNoConstructorInnerClass is only known in the context of an instance of the enclosing class ReflectionUtilsCreateInstanceOfTypeTest. Declare the innerclass as static to make construction possible.", e.getMessage());
        }
    }

    @Test
    public void classNotFound() {
        try {
            createInstanceOfType("xxx", false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create instance of type xxx\n" +
                    "Reason: ClassNotFoundException: xxx", e.getMessage());
        }
    }

    @Test
    public void exceptionInConstructor() {
        try {
            createInstanceOfType(ExceptionConstructor.class, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Error while trying to create object of class org.unitils.core.util.ReflectionUtilsCreateInstanceOfTypeTest$ExceptionConstructor\n" +
                    "Reason: NullPointerException: expected", e.getMessage());
        }
    }

    @Test
    public void constructionForCoverage() {
        new ReflectionUtils();
    }


    public static class TestConstructor {

        public String value;

        public TestConstructor() {
        }

        public TestConstructor(String value) {
            this.value = value;
        }

        private TestConstructor(int intValue) {
        }
    }

    public static class TestPrivateConstructor {

        private TestPrivateConstructor() {
        }
    }

    public static class TestNoConstructor {
    }

    public class TestNoConstructorInnerClass {
    }

    public static class ExceptionConstructor {

        public ExceptionConstructor() {
            throw new NullPointerException("expected");
        }
    }
}