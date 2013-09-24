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

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class StackTraceServiceGetInvocationLineNrTest {

    private StackTraceService stackTraceService = new StackTraceService();

    private TestClass testClass = new TestClass();


    @Test
    public void getInvocationLineNr() {
        int result = testClass.doGetInvocationLineNr(TestInterface.class);
        assertEquals(35, result);  // should be the line above
    }

    @Test
    public void minusOneIfClassNotFoundInStackTrace() {
        int result = stackTraceService.getInvocationLineNr(String.class);
        assertEquals(-1, result);
    }


    private interface TestInterface {

        int doGetInvocationLineNr(Class<?> invokedClass);
    }

    private class TestClass implements TestInterface {

        public int doGetInvocationLineNr(Class<?> invokedClass) {
            return getInvocationLineNr(invokedClass);
        }
    }

    private int getInvocationLineNr(Class<?> invokedClass) {
        return stackTraceService.getInvocationLineNr(invokedClass);
    }

}
