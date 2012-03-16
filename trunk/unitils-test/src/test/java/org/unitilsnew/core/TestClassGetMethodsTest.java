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
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class TestClassGetMethodsTest {

    /* Tested object */
    private TestClass testClass;

    private Method method1a;
    private Method method2;
    private Method method1b;
    private Method method3;
    private Method method4;


    @Before
    public void initialize() throws Exception {
        method1a = SuperClass.class.getDeclaredMethod("method1");
        method2 = SuperClass.class.getDeclaredMethod("method2", String.class);
        method4 = SuperClass.class.getDeclaredMethod("method4");
        method1b = MyClass.class.getDeclaredMethod("method1");
        method3 = MyClass.class.getDeclaredMethod("method3", String.class, String.class);
    }


    @Test
    public void methods() {
        testClass = new TestClass(MyClass.class);

        List<Method> result = testClass.getMethods();
        assertEquals(asList(method3, method1b, method2, method4, method1a), result);
    }

    @Test
    public void emptyWhenNoFields() {
        testClass = new TestClass(NoMethodsClass.class);

        List<Method> result = testClass.getMethods();
        assertTrue(result.isEmpty());
    }

    @Test
    public void methodsAreCached() {
        testClass = new TestClass(MyClass.class);

        List<Method> result1 = testClass.getMethods();
        List<Method> result2 = testClass.getMethods();
        assertSame(result1, result2);
    }


    private static class SuperClass {

        protected String method1() {
            return null;
        }

        public void method2(String arg) {
        }

        private void method4() {
        }
    }

    private static class MyClass extends SuperClass {

        protected String method1() {
            return null;
        }

        public String method3(String a, String b) {
            return null;
        }
    }

    private static class NoMethodsClass {
    }
}
