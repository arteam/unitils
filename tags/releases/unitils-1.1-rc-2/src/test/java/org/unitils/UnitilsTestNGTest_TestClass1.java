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
package org.unitils;

import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;

import org.testng.annotations.Test;

/**
 * TestNG test class containing 2 active and 1 ignored test method. This test test-class is used
 * in the {@link UnitilsInvocationTest} and {@link UnitilsInvocationExceptionTest} tests.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsTestNGTest_TestClass1 extends UnitilsTestNGTestBase {


    @Test
    public void test1() {
        registerTestInvocation(TEST_METHOD, "test1");
    }


    @Test
    public void test2() {
        registerTestInvocation(TEST_METHOD, "test2");
    }


    @Test(enabled = false)
    public void test3() {
        registerTestInvocation(TEST_METHOD, "test3");
    }
    

}
