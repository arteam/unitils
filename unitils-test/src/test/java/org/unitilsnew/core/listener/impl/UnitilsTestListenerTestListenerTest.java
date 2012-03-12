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

package org.unitilsnew.core.listener.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitilsnew.core.TestClass;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.listener.TestListener;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;
import static org.unitilsnew.core.TestPhase.EXECUTION;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTestListenerTestListenerTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTestListener unitilsTestListener;

    private Mock<TestListener> testListenerMock;


    @Before
    public void initialize() throws Exception {
        unitilsTestListener = new UnitilsTestListener(asList(testListenerMock.getMock()), null);

        testListenerMock.returns(EXECUTION).getTestPhase();
    }


    @Test
    public void testListener() throws Exception {
        Method testMethod = MyClass.class.getDeclaredMethod("testMethod");
        TestClass testClass = new TestClass(MyClass.class);
        Object testObject = new MyClass();
        TestInstance testInstance = new TestInstance(testClass, testObject, testMethod);
        NullPointerException exception = new NullPointerException();

        unitilsTestListener.beforeTestClass(MyClass.class);
        unitilsTestListener.beforeTestSetUp(testObject, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(exception);
        unitilsTestListener.afterTestTearDown();

        testListenerMock.assertInvokedInSequence().beforeTestClass(testClass);
        testListenerMock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListenerMock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListenerMock.assertInvokedInSequence().afterTestMethod(testInstance, exception);
        testListenerMock.assertInvokedInSequence().afterTestTearDown(testInstance);
    }


    public static class MyClass {

        public void testMethod() {
        }
    }
}
