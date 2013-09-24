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
package org.unitils.mock.core.proxy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class StackTraceServiceGetInvocationStackTraceTest {

    private StackTraceService stackTraceService = new StackTraceService();

    private TestClass testClass = new TestClass();


    @Test
    public void getInvocationStackTrace() {
        StackTraceElement[] result = testClass.doGetInvocationStackTrace(TestClass.class);
        assertEquals(TestClass.class.getName(), result[0].getClassName());
    }

    @Test
    public void getInvocationStackTraceForInterface() {
        StackTraceElement[] result = testClass.doGetInvocationStackTrace(TestInterface.class);
        assertEquals(TestClass.class.getName(), result[0].getClassName());
    }

    @Test
    public void classNotIncluded() {
        StackTraceElement[] result = testClass.doGetInvocationStackTrace(TestClass.class, false);
        assertEquals(StackTraceServiceGetInvocationStackTraceTest.class.getName(), result[0].getClassName());
    }

    @Test
    public void nullWhenClassNotFoundInStackTrace() {
        StackTraceElement[] result = stackTraceService.getInvocationStackTrace(String.class);
        assertNull(result);
    }

    @Test
    public void ignoreInvalidStackTraceEntries() {
        stackTraceService = new StackTraceService() {
            @Override
            protected StackTraceElement[] getCurrentStackTrace() {
                return new StackTraceElement[]{new StackTraceElement("xxx", "xxx", "xxx", 5)};
            }
        };
        StackTraceElement[] result = stackTraceService.getInvocationStackTrace(String.class);
        assertNull(result);
    }


    private interface TestInterface {

        StackTraceElement[] doGetInvocationStackTrace(Class<?> invokedClass);

        StackTraceElement[] doGetInvocationStackTrace(Class<?> invokedClass, boolean included);
    }

    private class TestClass implements TestInterface {

        public StackTraceElement[] doGetInvocationStackTrace(Class<?> invokedClass) {
            return getStackTrace(invokedClass);
        }

        public StackTraceElement[] doGetInvocationStackTrace(Class<?> invokedClass, boolean included) {
            return getStackTrace(invokedClass, included);
        }
    }

    private StackTraceElement[] getStackTrace(Class<?> invokedClass) {
        return stackTraceService.getInvocationStackTrace(invokedClass);
    }

    private StackTraceElement[] getStackTrace(Class<?> invokedClass, boolean included) {
        return stackTraceService.getInvocationStackTrace(invokedClass, included);
    }
}
