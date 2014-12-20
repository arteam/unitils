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

package org.unitils.core.reflect;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class ClassWrapperGetMethodsTest {

    /* Tested object */
    private ClassWrapper classWrapper;

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
        classWrapper = new ClassWrapper(MyClass.class);

        List<Method> result = classWrapper.getMethods();
        assertLenientEquals(asList(method3, method1b, method2, method4, method1a), result);
    }

    @Test
    public void emptyWhenNoFields() {
        classWrapper = new ClassWrapper(NoMethodsClass.class);

        List<Method> result = classWrapper.getMethods();
        assertTrue(result.isEmpty());
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

        public static void staticMethod() {
        }
    }

    private static class NoMethodsClass {
    }
}
