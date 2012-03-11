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

package org.unitilsnew.core;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceTest {

    /* Tested object */
    private TestInstance testInstance;

    private TestClass testClass;
    private Object testObject;
    private Method testMethod;


    @Before
    public void initialize() throws Exception {
        testMethod = MyClass.class.getMethod("method");
        testClass = new TestClass(MyClass.class);
        testObject = new MyClass();
    }


    @Test
    public void getTestClass() {
        testInstance = new TestInstance(testClass, testObject, testMethod);

        TestClass result = testInstance.getTestClass();
        assertSame(testClass, result);
    }

    @Test
    public void getTestMethod() {
        testInstance = new TestInstance(testClass, testObject, testMethod);

        Method result = testInstance.getTestMethod();
        assertSame(testMethod, result);
    }

    @Test
    public void getTestObject() {
        testInstance = new TestInstance(testClass, testObject, testMethod);

        Object result = testInstance.getTestObject();
        assertSame(testObject, result);
    }

    @Test
    public void getName() {
        testInstance = new TestInstance(testClass, testObject, testMethod);

        String result = testInstance.getName();
        assertEquals("MyClass.method", result);
    }


    private static class MyClass {

        public void method() {
        }
    }
}
