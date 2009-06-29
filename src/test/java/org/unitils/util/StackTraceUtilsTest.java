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
package org.unitils.util;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import static org.unitils.util.StackTraceUtils.getInvocationLineNr;
import static org.unitils.util.StackTraceUtils.getInvocationStackTrace;

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
        assertEquals(37, result);
    }


    @Test(expected = UnitilsException.class)
    public void lineNrNotFound() {
        getInvocationLineNr(String.class);
    }


    @Test
    public void invocationStackTrace() {
        StackTraceElement[] result = testClass.stackTraceTest();
        assertEquals(StackTraceUtilsTest.class.getName(), result[0].getClassName());
    }


    @Test(expected = UnitilsException.class)
    public void invocationStackTraceNotFound() {
        getInvocationStackTrace(String.class);
    }


    private interface TestInterface {

        public StackTraceElement[] stackTraceTest();

        public int lineNrTest();
    }

    private class TestClass implements TestInterface {

        public StackTraceElement[] stackTraceTest() {
            return getStackTrace();
        }

        public int lineNrTest() {
            return getLineNr();
        }
    }

    private StackTraceElement[] getStackTrace() {
        return getInvocationStackTrace(TestInterface.class);
    }

    private int getLineNr() {
        return getInvocationLineNr(TestInterface.class);
    }

}
