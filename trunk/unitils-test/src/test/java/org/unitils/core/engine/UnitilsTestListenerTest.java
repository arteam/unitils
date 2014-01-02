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

package org.unitils.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestInstance;
import org.unitils.core.TestListener;
import org.unitils.core.config.Configuration;
import org.unitils.core.reflect.ClassWrapper;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitils.mock.annotation.Dummy;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.unitils.core.TestPhase.EXECUTION;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTestListenerTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTestListener unitilsTestListener;

    private Mock<FieldAnnotationTestListenerFactory> wrapperForFieldAnnotationListenerFactoryMock;
    private Mock<TestAnnotationTestListenerFactory> wrapperForTestAnnotationListenerFactoryMock;
    private PartialMock<TestListener> testListener1Mock;
    private PartialMock<TestListener> testListener2Mock;
    @Dummy
    private Configuration configuration;

    private ClassWrapper classWrapper;
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

        classWrapper = new ClassWrapper(MyClass.class);
        testInstance = new TestInstance(classWrapper, testObject, testMethod);

        testListener1Mock.returns(EXECUTION).getTestPhase();
        testListener2Mock.returns(EXECUTION).getTestPhase();
    }


    @Test
    public void testListeners() {
        unitilsTestListener = new UnitilsTestListener(asList(testListener1Mock.getMock(), testListener2Mock.getMock()), wrapperForFieldAnnotationListenerFactoryMock.getMock(), wrapperForTestAnnotationListenerFactoryMock.getMock());

        unitilsTestListener.beforeTestClass(MyClass.class);
        unitilsTestListener.beforeTestSetUp(testObject, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(testThrowable);
        unitilsTestListener.afterTestTearDown();
        unitilsTestListener.afterTestClass();

        testListener1Mock.assertInvokedInSequence().beforeTestClass(classWrapper);
        testListener2Mock.assertInvokedInSequence().beforeTestClass(classWrapper);
        testListener1Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener1Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener1Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener2Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener1Mock.assertInvokedInSequence().afterTestTearDown(testInstance, testThrowable);
        testListener2Mock.assertInvokedInSequence().afterTestTearDown(testInstance, testThrowable);
        testListener1Mock.assertInvokedInSequence().afterTestClass(classWrapper);
        testListener2Mock.assertInvokedInSequence().afterTestClass(classWrapper);
    }


    @Test
    public void fieldAnnotationTestListeners() throws Exception {
        wrapperForFieldAnnotationListenerFactoryMock.returnsAll(testListener1Mock, testListener2Mock).create(testInstance);

        unitilsTestListener.beforeTestClass(MyClass.class);
        unitilsTestListener.beforeTestSetUp(testObject, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(testThrowable);
        unitilsTestListener.afterTestTearDown();
        unitilsTestListener.afterTestClass();

        testListener1Mock.assertNotInvoked().beforeTestClass(classWrapper);
        testListener2Mock.assertNotInvoked().beforeTestClass(classWrapper);
        testListener1Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener1Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener1Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener2Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener1Mock.assertInvokedInSequence().afterTestTearDown(testInstance, testThrowable);
        testListener2Mock.assertInvokedInSequence().afterTestTearDown(testInstance, testThrowable);
        testListener1Mock.assertNotInvoked().afterTestClass(classWrapper);
        testListener2Mock.assertNotInvoked().afterTestClass(classWrapper);
    }

    @Test
    public void testAnnotationTestListeners() throws Exception {
        wrapperForTestAnnotationListenerFactoryMock.returnsAll(testListener1Mock, testListener2Mock).create(testInstance);

        unitilsTestListener.beforeTestClass(MyClass.class);
        unitilsTestListener.beforeTestSetUp(testObject, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(testThrowable);
        unitilsTestListener.afterTestTearDown();
        unitilsTestListener.afterTestClass();

        testListener1Mock.assertNotInvoked().beforeTestClass(classWrapper);
        testListener2Mock.assertNotInvoked().beforeTestClass(classWrapper);
        testListener1Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestSetUp(testInstance);
        testListener1Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener2Mock.assertInvokedInSequence().beforeTestMethod(testInstance);
        testListener1Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener2Mock.assertInvokedInSequence().afterTestMethod(testInstance, testThrowable);
        testListener1Mock.assertInvokedInSequence().afterTestTearDown(testInstance, testThrowable);
        testListener2Mock.assertInvokedInSequence().afterTestTearDown(testInstance, testThrowable);
        testListener1Mock.assertNotInvoked().afterTestClass(classWrapper);
        testListener2Mock.assertNotInvoked().afterTestClass(classWrapper);
    }

    @Test
    public void noListeners() throws Exception {
        unitilsTestListener.beforeTestClass(MyClass.class);
        unitilsTestListener.beforeTestSetUp(testObject, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(testThrowable);
        unitilsTestListener.afterTestTearDown();
        unitilsTestListener.afterTestClass();

        testListener1Mock.assertNoMoreInvocations();
        testListener2Mock.assertNoMoreInvocations();
    }


    public static class MyClass {

        public void testMethod() {
        }
    }
}
