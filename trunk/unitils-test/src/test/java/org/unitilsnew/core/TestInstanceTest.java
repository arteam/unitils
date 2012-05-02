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
import org.unitilsnew.core.reflect.ClassWrapper;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceTest {

    /* Tested object */
    private TestInstance testInstance;

    private ClassWrapper classWrapper;
    private Object testObject;
    private Method testMethod;


    @Before
    public void initialize() throws Exception {
        testMethod = MyClass.class.getMethod("method");
        classWrapper = new ClassWrapper(MyClass.class);
        testObject = new MyClass();
    }


    @Test
    public void getTestClass() {
        testInstance = new TestInstance(classWrapper, testObject, testMethod);

        ClassWrapper result = testInstance.getClassWrapper();
        assertSame(classWrapper, result);
    }

    @Test
    public void getTestMethod() {
        testInstance = new TestInstance(classWrapper, testObject, testMethod);

        Method result = testInstance.getTestMethod();
        assertSame(testMethod, result);
    }

    @Test
    public void getTestObject() {
        testInstance = new TestInstance(classWrapper, testObject, testMethod);

        Object result = testInstance.getTestObject();
        assertSame(testObject, result);
    }

    @Test
    public void getName() {
        testInstance = new TestInstance(classWrapper, testObject, testMethod);

        String result = testInstance.getName();
        assertEquals("org.unitilsnew.core.TestInstanceTest$MyClass.method", result);
    }


    private static class MyClass {

        public void method() {
        }
    }
}
