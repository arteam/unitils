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
import org.unitils.mock.PartialMock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.core.TestClass;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.config.Configuration;
import org.unitilsnew.core.listener.TestListener;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTestListenerTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTestListener unitilsTestListener;

    private Mock<WrapperForFieldAnnotationListenerFactory> wrapperForFieldAnnotationListenerFactoryMock;
    private Mock<WrapperForTestAnnotationListenerFactory> wrapperForTestAnnotationListenerFactoryMock;
    private PartialMock<TestListener> testListener1Mock;
    private PartialMock<TestListener> testListener2Mock;
    @Dummy
    private Configuration configuration;

    private TestClass testClass;
    private TestInstance testInstance;
    private Object testObject;
    private Method testMethod;
    private Throwable testThrowable;


    @Before
    public void initialize() throws Exception {
        unitilsTestListener = new UnitilsTestListener(new ArrayList<TestListener>(), wrapperForFieldAnnotationListenerFactoryMock.getMock(), wrapperForTestAnnotationListenerFactoryMock.getMock());

        testObject = new MyClass();
        testMethod = MyClass.class.getDeclaredMethod("testMethod");
        testThrowable = new NullPointerException();

        testClass = new TestClass(MyClass.class);
        testInstance = new TestInstance(testClass, testObject, testMethod);
    }


    @Test
    public void testListeners() {
        unitilsTestListener = new UnitilsTestListener(asList(testListener1Mock.getMock(), testListener2Mock.getMock()), wrapperForFieldAnnotationListenerFactoryMock.getMock(), wrapperForTestAnnotationListenerFactoryMock.getMock());

        unitilsTestListener.beforeTestClass(MyClass.class);
        unitilsTestListener.beforeTestSetUp(testObject, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(testThrowable);
        unitilsTestListener.afterTestTearDown();

        testListener1Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener1Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener1Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener2Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener1Mock.assertInvokedInSequence().afterTestTearDown(testInstance);
        testListener2Mock.assertInvokedInSequence().afterTestTearDown(testInstance);
    }


    @Test
    public void fieldAnnotationTestListeners() throws Exception {
        wrapperForFieldAnnotationListenerFactoryMock.returns(asList(testListener1Mock.getMock(), testListener2Mock.getMock())).create(testInstance);

        unitilsTestListener.beforeTestClass(MyClass.class);
        unitilsTestListener.beforeTestSetUp(testObject, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(testThrowable);
        unitilsTestListener.afterTestTearDown();

        testListener1Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener1Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener1Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener2Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener1Mock.assertInvokedInSequence().afterTestTearDown(testInstance);
        testListener2Mock.assertInvokedInSequence().afterTestTearDown(testInstance);
    }

    @Test
    public void testAnnotationTestListeners() throws Exception {
        wrapperForTestAnnotationListenerFactoryMock.returns(asList(testListener1Mock.getMock(), testListener2Mock.getMock())).create(testInstance);

        unitilsTestListener.beforeTestClass(MyClass.class);
        unitilsTestListener.beforeTestSetUp(testObject, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(testThrowable);
        unitilsTestListener.afterTestTearDown();

        testListener1Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener1Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener1Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener2Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener1Mock.assertInvokedInSequence().afterTestTearDown(testInstance);
        testListener2Mock.assertInvokedInSequence().afterTestTearDown(testInstance);
    }

    @Test
    public void noListeners() throws Exception {
        unitilsTestListener.beforeTestClass(MyClass.class);
        unitilsTestListener.beforeTestSetUp(testObject, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(testThrowable);
        unitilsTestListener.afterTestTearDown();

        testListener1Mock.assertNotInvoked().beforeTestSetUp(null);
        testListener1Mock.assertNotInvoked().beforeTestMethod(null);
        testListener1Mock.assertNotInvoked().afterTestMethod(null, null);
        testListener1Mock.assertNotInvoked().afterTestTearDown(null);
    }


    public static class MyClass {

        public void testMethod() {
        }
    }
}
