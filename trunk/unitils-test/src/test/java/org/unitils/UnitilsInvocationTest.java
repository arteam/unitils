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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.unitils.TracingTestListener.TestFramework;
import static org.unitils.TracingTestListener.TestFramework.JUNIT3;
import static org.unitils.TracingTestListener.TestFramework.JUNIT4;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.inject.util.InjectionUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Test for the main flow of the unitils test listeners for JUnit3 ({@link UnitilsJUnit3}),
 * JUnit4 (@link UnitilsJUnit4TestClassRunner}) and TestNG.
 * <p/>
 * Except for some minor differences, the flows for all these test frameworks
 * are expected to be the same.
 * <p/>
 * 3 tests are performed: TestClass1 and TestClass2 both with 2 test methods and EmptyTestClass
 * that does not contain any methods. TestClass1 also contains an ignored test (not for JUnit3).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see UnitilsJUnit3Test_TestClass1
 * @see UnitilsJUnit3Test_TestClass2
 * @see UnitilsJUnit3Test_EmptyTestClass
 * @see UnitilsJUnit4Test_TestClass1
 * @see UnitilsJUnit4Test_TestClass2
 */
@RunWith(Parameterized.class)
public class UnitilsInvocationTest extends UnitilsInvocationTestBase {

    Class<?> testClass1, testClass2;

    public UnitilsInvocationTest(TestFramework testFramework, TestExecutor testExecutor, Class<?> testClass1, Class<?> testClass2) {
        super(testFramework, testExecutor);
        this.testClass1 = testClass1;
        this.testClass2 = testClass2;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {JUNIT3, new JUnit3TestExecutor(), UnitilsJUnit3Test_TestClass1.class, UnitilsJUnit3Test_TestClass2.class},
                {JUNIT4, new JUnit4TestExecutor(), UnitilsJUnit4Test_TestClass1.class, UnitilsJUnit4Test_TestClass2.class},
                //{JUNIT3, new JUnit3TestExecutor(), SpringUnitilsJUnit38Test_TestClass1.class, SpringUnitilsJUnit38Test_TestClass2.class},
                //{JUNIT4, new JUnit4TestExecutor(), SpringUnitilsJUnit4Test_TestClass1.class, SpringUnitilsJUnit4Test_TestClass2.class},
        });
    }


    @Before
    public void resetJunit3() {
        InjectionUtils.injectIntoStatic(null, UnitilsJUnit3.class, "currentTestClass");
    }


    @Test
    public void testSuccessfulRun() throws Exception {
        testExecutor.runTests(testClass1, testClass2);
        assertInvocationOrder(testClass1, testClass2);
    }


    /**
     * JUnit 3 test class without any tests. Inner class to avoid a failing test.
     */
    protected static class UnitilsJUnit3Test_EmptyTestClass extends UnitilsJUnit3 {

        private static TracingTestListener tracingTestListener;

        public static void setTracingTestListener(TracingTestListener testListener) {
            tracingTestListener = testListener;
        }


        @Override
        protected Unitils getUnitils() {
            if (tracingTestListener != null) {
                return new Unitils() {

                    @Override
                    public TestListener getTestListener() {
                        return tracingTestListener;
                    }
                };
            }
            return super.getUnitils();
        }
    }

}
