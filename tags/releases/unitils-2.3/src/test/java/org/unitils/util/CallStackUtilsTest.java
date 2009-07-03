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

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class CallStackUtilsTest {

    @Test
    public void getInvocationStackTrace() {
        TestClass testClass = new TestClass();
        assertEquals(CallStackUtilsTest.class.getName(), testClass.getStackTrace()[0].getClassName());
    }
    
    public static class TestClass {
        
        public StackTraceElement[] getStackTrace() {
            return doGetStackTrace();
        }

        private StackTraceElement[] doGetStackTrace() {
            return CallStackUtils.getInvocationStackTrace(TestClass.class);
        }
    }
}