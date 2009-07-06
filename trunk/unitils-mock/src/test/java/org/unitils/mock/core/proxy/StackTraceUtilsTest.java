/*
 * Copyright 2006-2007,  Unitils.org
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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import static org.unitils.mock.core.proxy.StackTraceUtils.getInvocationLineNr;
import static org.unitils.mock.core.proxy.StackTraceUtils.getInvocationStackTrace;

/**
 * Tests the stack trace utilities.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class StackTraceUtilsTest {

    private TestClass testClass = new TestClass();


    @Test
    public void lineNr() {
        int result = testClass.lineNrTest();
        assertEquals(39, result);
    }


    @Test
    public void lineNrNotFound() {
        int result = getInvocationLineNr(String.class);
        assertEquals(-1, result);
    }


    @Test
    public void invocationStackTrace_interfaceIncluded() {
        StackTraceElement[] result = testClass.stackTraceTest(true);
        assertEquals(TestClass.class.getName(), result[0].getClassName());
    }


    @Test
    public void invocationStackTrace_interfaceNotIncuded() {
        StackTraceElement[] result = testClass.stackTraceTest(false);
        assertEquals(StackTraceUtilsTest.class.getName(), result[0].getClassName());
    }


    @Test
    public void invocationStackTraceNotFound() {
        StackTraceElement[] result = getInvocationStackTrace(String.class);
        assertNull(result);
    }


    private interface TestInterface {

        public StackTraceElement[] stackTraceTest(boolean included);

        public int lineNrTest();
    }

    private class TestClass implements TestInterface {

        public StackTraceElement[] stackTraceTest(boolean included) {
            return getStackTrace(included);
        }

        public int lineNrTest() {
            return getLineNr();
        }
    }

    private StackTraceElement[] getStackTrace(boolean included) {
        return getInvocationStackTrace(TestInterface.class, included);
    }

    private int getLineNr() {
        return getInvocationLineNr(TestInterface.class);
    }

}
