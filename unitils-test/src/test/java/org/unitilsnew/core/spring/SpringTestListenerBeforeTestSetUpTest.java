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
package org.unitilsnew.core.spring;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.ClassWrapper;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class SpringTestListenerBeforeTestSetUpTest extends UnitilsJUnit4 {

    /* Tested object */
    private SpringTestListener springTestListener;

    private Mock<SpringTestManager> springTestManagerMock;

    private Object testObject1;
    private Object testObject2;
    private Method testMethod1;
    private Method testMethod2;
    private ClassWrapper classWrapper;
    private TestInstance testInstance1;
    private TestInstance testInstance2;
    private TestInstance testInstance3;

    @Before
    public void initialize() throws Exception {
        MyTestExecutionListener.prepareTestInstanceTestContext = null;
        MyTestExecutionListener.beforeTestMethodTestContext = null;
        MyTestExecutionListener.exceptionToThrowDuringPrepareTestInstance = null;
        MyTestExecutionListener.exceptionToThrowDuringBeforeTestMethod = null;

        testObject1 = new TestClass();
        testObject2 = new TestClass();
        testMethod1 = TestClass.class.getMethod("testMethod1");
        testMethod2 = TestClass.class.getMethod("testMethod2");
        classWrapper = new ClassWrapper(TestClass.class);
        testInstance1 = new TestInstance(classWrapper, testObject1, testMethod1);
        testInstance2 = new TestInstance(classWrapper, testObject1, testMethod2);
        testInstance3 = new TestInstance(classWrapper, testObject2, testMethod1);

        springTestListener = new SpringTestListener(springTestManagerMock.getMock());
    }


    @Test
    public void ignoredWhenNotASpringTest() throws Exception {
        springTestListener.beforeTestSetUp(testInstance1);

        assertNull(MyTestExecutionListener.prepareTestInstanceTestContext);
        assertNull(MyTestExecutionListener.beforeTestMethodTestContext);
    }

    @Test
    public void prepareTestInstance() throws Exception {
        springTestListener.beforeTestClass(classWrapper);

        springTestListener.beforeTestSetUp(testInstance1);

        assertSame(testObject1, MyTestExecutionListener.prepareTestInstanceTestContext.getTestInstance());
        assertEquals(TestClass.class, MyTestExecutionListener.prepareTestInstanceTestContext.getTestClass());
    }

    @Test
    public void prepareTestInstanceCalledForNewTestObject() throws Exception {
        springTestListener.beforeTestClass(classWrapper);

        springTestListener.beforeTestSetUp(testInstance1);
        springTestListener.beforeTestSetUp(testInstance3);

        assertSame(testObject2, MyTestExecutionListener.prepareTestInstanceTestContext.getTestInstance());
    }

    @Test
    public void prepareTestInstanceNotCalledForSameTestObject() throws Exception {
        springTestListener.beforeTestClass(classWrapper);

        springTestListener.beforeTestSetUp(testInstance1);
        MyTestExecutionListener.prepareTestInstanceTestContext = null;
        springTestListener.beforeTestSetUp(testInstance2);

        assertNull(MyTestExecutionListener.prepareTestInstanceTestContext);
    }

    @Test
    public void exceptionWhenPrepareTestInstanceFails() throws Exception {
        try {
            MyTestExecutionListener.exceptionToThrowDuringPrepareTestInstance = new RuntimeException("test");
            springTestListener.beforeTestClass(classWrapper);

            springTestListener.beforeTestSetUp(testInstance1);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Exception occurred during prepare test instance.\n" +
                    "Reason: RuntimeException: test", e.getMessage());
        }
    }

    @Test
    public void beforeTestMethod() throws Exception {
        springTestListener.beforeTestClass(classWrapper);

        springTestListener.beforeTestSetUp(testInstance1);

        assertSame(testObject1, MyTestExecutionListener.beforeTestMethodTestContext.getTestInstance());
        assertSame(testMethod1, MyTestExecutionListener.beforeTestMethodTestContext.getTestMethod());
        assertEquals(TestClass.class, MyTestExecutionListener.beforeTestMethodTestContext.getTestClass());
    }

    @Test
    public void beforeTestMethodCalledForSameTestObjectButDifferentTestMethod() throws Exception {
        springTestListener.beforeTestClass(classWrapper);

        springTestListener.beforeTestSetUp(testInstance1);
        springTestListener.beforeTestSetUp(testInstance2);

        assertSame(testMethod2, MyTestExecutionListener.beforeTestMethodTestContext.getTestMethod());
    }

    @Test
    public void exceptionWhenBeforeTestMethodFails() throws Exception {
        try {
            MyTestExecutionListener.exceptionToThrowDuringBeforeTestMethod = new RuntimeException("test");
            springTestListener.beforeTestClass(classWrapper);

            springTestListener.beforeTestSetUp(testInstance1);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Exception occurred during before test method.\n" +
                    "Reason: RuntimeException: test", e.getMessage());
        }
    }


    @TestExecutionListeners(MyTestExecutionListener.class)
    private static class TestClass {
        public void testMethod1() {
        }

        public void testMethod2() {
        }
    }


    private static class MyTestExecutionListener extends AbstractTestExecutionListener {

        public static TestContext beforeTestMethodTestContext;
        public static TestContext prepareTestInstanceTestContext;
        public static Exception exceptionToThrowDuringPrepareTestInstance;
        public static Exception exceptionToThrowDuringBeforeTestMethod;

        @Override
        public void prepareTestInstance(TestContext testContext) throws Exception {
            MyTestExecutionListener.prepareTestInstanceTestContext = testContext;
            if (exceptionToThrowDuringPrepareTestInstance != null) {
                throw exceptionToThrowDuringPrepareTestInstance;
            }
        }

        @Override
        public void beforeTestMethod(TestContext testContext) throws Exception {
            MyTestExecutionListener.beforeTestMethodTestContext = testContext;
            if (exceptionToThrowDuringBeforeTestMethod != null) {
                throw exceptionToThrowDuringBeforeTestMethod;
            }
        }
    }
}
