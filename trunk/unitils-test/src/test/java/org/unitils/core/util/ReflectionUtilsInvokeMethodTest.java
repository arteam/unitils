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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Method;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionUtilsInvokeMethodTest extends UnitilsJUnit4 {

    private TestClass testClass;
    private Method method1;
    private Method method2;
    private Method method3;

    @Before
    public void initialize() throws Exception {
        method1 = TestClass.class.getDeclaredMethod("method1", String.class, int.class);
        method2 = TestClass.class.getDeclaredMethod("method2");
        method3 = TestClass.class.getDeclaredMethod("method3");
        testClass = new TestClass();
    }


    @Test
    public void invokeMethod() {
        String result = ReflectionUtils.invokeMethod(testClass, method1, "value", 1);
        assertEquals("value1", result);
    }

    @Test
    public void voidMethod() {
        String result = ReflectionUtils.invokeMethod(testClass, method3);
        assertTrue(testClass.invoked);
    }

    @Test
    public void exceptionWhenMethodNotFound() {
        try {
            ReflectionUtils.invokeMethod(new Properties(), method1);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Error while invoking method public java.lang.String org.unitils.core.util.ReflectionUtilsInvokeMethodTest$TestClass.method1(java.lang.String,int)\n" +
                    "Reason: IllegalArgumentException: object is not an instance of declaring class", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenWrongType() {
        try {
            Properties result = ReflectionUtils.invokeMethod(testClass, method1, "value", 1);
            fail("ClassCastException expected");
        } catch (ClassCastException e) {
            assertEquals("java.lang.String cannot be cast to java.util.Properties", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenExceptionDuringMethod() {
        try {
            Properties result = ReflectionUtils.invokeMethod(testClass, method2);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Error while invoking method public java.lang.String org.unitils.core.util.ReflectionUtilsInvokeMethodTest$TestClass.method2()\n" +
                    "Reason: NullPointerException: expected", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenWrongArguments() {
        try {
            Properties result = ReflectionUtils.invokeMethod(testClass, method1, "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Error while invoking method public java.lang.String org.unitils.core.util.ReflectionUtilsInvokeMethodTest$TestClass.method1(java.lang.String,int)\n" +
                    "Reason: IllegalArgumentException: wrong number of arguments", e.getMessage());
        }
    }


    public static class TestClass {

        public boolean invoked;

        public String method1(String arg1, int arg2) {
            return arg1 + arg2;
        }

        public String method2() {
            throw new NullPointerException("expected");
        }

        public void method3() {
            invoked = true;
        }
    }
}