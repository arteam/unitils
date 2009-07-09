/*
 * Copyright 2008,  Unitils.org
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

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.unitils.TracingTestListener.TestFramework;
import static org.unitils.TracingTestListener.TestFramework.TESTNG;

import java.util.Arrays;
import java.util.Collection;

/**
 * Test for the flows in case an exception occurs in one of the listener or test methods for JUnit3 ({@link org.unitils.UnitilsJUnit3}),
 * JUnit4 (@link UnitilsJUnit4TestClassRunner}) and TestNG ({@link UnitilsTestNG}).
 * <p/>
 * Except for some minor differences, the flows for all these test frameworks
 * are expected to be the same (see assertInvocationOrder* methods).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see org.unitils.UnitilsJUnit3Test_TestClass1
 * @see org.unitils.UnitilsJUnit4Test_TestClass1
 * @see UnitilsTestNGTest_TestClass1
 */
@RunWith(Parameterized.class)
public class TestNGUnitilsInvocationExceptionTest extends UnitilsInvocationExceptionTest {


    public TestNGUnitilsInvocationExceptionTest(TestFramework testFramework, TestExecutor testExecutor, Class<?> testClass) {
        super(testFramework, testExecutor, testClass);
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {TESTNG, new TestNGTestExecutor(), UnitilsTestNGTest_TestClass1.class},
                //{TESTNG, new TestNGTestExecutor(), SpringUnitilsTestNGTest_TestClass1.class},
        });
    }

}