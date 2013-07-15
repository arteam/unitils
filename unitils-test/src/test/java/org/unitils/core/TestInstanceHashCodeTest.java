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

package org.unitils.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.reflect.ClassWrapper;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceHashCodeTest {

    private ClassWrapper classWrapper;
    private Object testObject;
    private Method testMethod;


    @Before
    public void initialize() throws Exception {
        classWrapper = new ClassWrapper(MyClass.class);
        testObject = new MyClass();
        testMethod = MyClass.class.getDeclaredMethod("method");
    }


    @Test
    public void hashCodeForClass() {
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);
        int result = testInstance.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void sameHashCodeWhenEqual() {
        TestInstance testInstance1 = new TestInstance(classWrapper, testObject, testMethod);
        TestInstance testInstance2 = new TestInstance(classWrapper, testObject, testMethod);

        assertEquals(testInstance1.hashCode(), testInstance2.hashCode());
    }

    @Test
    public void nullTestClass() {
        TestInstance testInstance = new TestInstance(null, testObject, testMethod);
        int result = testInstance.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void nullTestObject() {
        TestInstance testInstance = new TestInstance(classWrapper, null, testMethod);
        int result = testInstance.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void nullTestMethod() {
        TestInstance testInstance = new TestInstance(classWrapper, testObject, null);
        int result = testInstance.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void nullTestClassTestObjectAndTestMethod() {
        TestInstance testInstance = new TestInstance(null, null, null);
        int result = testInstance.hashCode();

        assertEquals(0, result);
    }


    private static class MyClass {

        public void method() {
        }
    }
}
