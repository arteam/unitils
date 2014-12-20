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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceEqualsTest {

    private ClassWrapper classWrapper1;
    private ClassWrapper classWrapper2;
    private Object testObject1;
    private Object testObject2;
    private Method testMethod1;
    private Method testMethod2;


    @Before
    public void initialize() throws Exception {
        classWrapper1 = new ClassWrapper(MyClass1.class);
        classWrapper2 = new ClassWrapper(MyClass2.class);
        testObject1 = new MyClass1();
        testObject2 = new MyClass2();
        testMethod1 = MyClass1.class.getDeclaredMethod("method1");
        testMethod2 = MyClass2.class.getDeclaredMethod("method2");
    }


    @Test
    public void equal() {
        TestInstance testInstance1 = new TestInstance(classWrapper1, testObject1, testMethod1);
        TestInstance testInstance2 = new TestInstance(classWrapper1, testObject1, testMethod1);

        assertTrue(testInstance1.equals(testInstance2));
        assertTrue(testInstance2.equals(testInstance1));
    }

    @Test
    public void same() {
        TestInstance testInstance = new TestInstance(classWrapper1, testObject1, testMethod1);

        assertTrue(testInstance.equals(testInstance));
    }

    @Test
    public void notEqualTestClass() {
        TestInstance testInstance1 = new TestInstance(classWrapper1, testObject1, testMethod1);
        TestInstance testInstance2 = new TestInstance(classWrapper2, testObject1, testMethod1);

        assertFalse(testInstance1.equals(testInstance2));
        assertFalse(testInstance2.equals(testInstance1));
    }

    @Test
    public void notEqualTestObject() {
        TestInstance testInstance1 = new TestInstance(classWrapper1, testObject1, testMethod1);
        TestInstance testInstance2 = new TestInstance(classWrapper1, testObject2, testMethod1);

        assertFalse(testInstance1.equals(testInstance2));
        assertFalse(testInstance2.equals(testInstance1));
    }

    @Test
    public void notEqualTestMethod() {
        TestInstance testInstance1 = new TestInstance(classWrapper1, testObject1, testMethod1);
        TestInstance testInstance2 = new TestInstance(classWrapper1, testObject1, testMethod2);

        assertFalse(testInstance1.equals(testInstance2));
        assertFalse(testInstance2.equals(testInstance1));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void notEqualToNull() {
        TestInstance testInstance = new TestInstance(classWrapper1, testObject1, testMethod1);

        assertFalse(testInstance.equals(null));
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public void notEqualToOtherType() {
        TestInstance testInstance = new TestInstance(classWrapper1, testObject1, testMethod1);

        assertFalse(testInstance.equals("xxx"));
    }

    @Test
    public void nullTestClasses() {
        TestInstance testInstance1 = new TestInstance(null, testObject1, testMethod1);
        TestInstance testInstance2 = new TestInstance(null, testObject1, testMethod1);

        assertTrue(testInstance1.equals(testInstance2));
    }

    @Test
    public void nullTestObjects() {
        TestInstance testInstance1 = new TestInstance(classWrapper1, null, testMethod1);
        TestInstance testInstance2 = new TestInstance(classWrapper1, null, testMethod1);

        assertTrue(testInstance1.equals(testInstance2));
    }

    @Test
    public void nullTestMethods() {
        TestInstance testInstance1 = new TestInstance(classWrapper1, testObject1, null);
        TestInstance testInstance2 = new TestInstance(classWrapper1, testObject1, null);

        assertTrue(testInstance1.equals(testInstance2));
    }

    @Test
    public void nullTestClassesTestObjectsAndTestMethods() {
        TestInstance testInstance1 = new TestInstance(null, null, null);
        TestInstance testInstance2 = new TestInstance(null, null, null);

        assertTrue(testInstance1.equals(testInstance2));
    }


    private static class MyClass1 {

        public void method1() {
        }
    }

    private static class MyClass2 {

        public void method2() {
        }
    }
}
