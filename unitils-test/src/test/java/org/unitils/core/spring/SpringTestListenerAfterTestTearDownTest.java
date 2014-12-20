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
package org.unitils.core.spring;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestInstance;
import org.unitils.core.UnitilsException;
import org.unitils.core.reflect.ClassWrapper;
import org.unitils.mock.Mock;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class SpringTestListenerAfterTestTearDownTest extends UnitilsJUnit4 {

    /* Tested object */
    private SpringTestListener springTestListener;

    private Mock<SpringTestManager> springTestManagerMock;

    private Object testObject;
    private Method testMethod;
    private ClassWrapper classWrapper;
    private TestInstance testInstance;
    private Throwable testThrowable;

    @Before
    public void initialize() throws Exception {
        MyTestExecutionListener.testContext = null;
        MyTestExecutionListener.exceptionToThrow = null;

        testObject = new TestClass();
        testMethod = TestClass.class.getMethod("testMethod");
        classWrapper = new ClassWrapper(TestClass.class);
        testInstance = new TestInstance(classWrapper, testObject, testMethod);
        testThrowable = new RuntimeException("test");

        springTestListener = new SpringTestListener(springTestManagerMock.getMock());
    }


    @Test
    public void ignoredWhenNotASpringTest() throws Exception {
        springTestListener.afterTestTearDown(testInstance, testThrowable);

        assertNull(MyTestExecutionListener.testContext);
    }

    @Test
    public void afterTestMethod() throws Exception {
        springTestListener.beforeTestClass(classWrapper);

        springTestListener.afterTestTearDown(testInstance, testThrowable);

        assertSame(testObject, MyTestExecutionListener.testContext.getTestInstance());
        assertSame(testMethod, MyTestExecutionListener.testContext.getTestMethod());
        assertEquals(TestClass.class, MyTestExecutionListener.testContext.getTestClass());
        assertSame(testThrowable, MyTestExecutionListener.testContext.getTestException());
    }

    @Test
    public void exceptionWhenAfterTestMethodFails() throws Exception {
        try {
            MyTestExecutionListener.exceptionToThrow = new RuntimeException("test");
            springTestListener.beforeTestClass(classWrapper);

            springTestListener.afterTestTearDown(testInstance, null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Exception occurred during after test method.\n" +
                    "Reason: RuntimeException: test", e.getMessage());
        }
    }


    @TestExecutionListeners(MyTestExecutionListener.class)
    private static class TestClass {
        public void testMethod() {
        }
    }


    private static class MyTestExecutionListener extends AbstractTestExecutionListener {

        public static TestContext testContext;
        public static Exception exceptionToThrow;

        @Override
        public void afterTestMethod(TestContext testContext) throws Exception {
            MyTestExecutionListener.testContext = testContext;
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
        }
    }
}
