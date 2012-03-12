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
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.core.Annotations;
import org.unitilsnew.core.Context;
import org.unitilsnew.core.TestClass;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.annotation.TestAnnotation;
import org.unitilsnew.core.config.Configuration;
import org.unitilsnew.core.listener.TestAnnotationListener;
import org.unitilsnew.core.listener.TestListener;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.unitilsnew.core.TestPhase.EXECUTION;

/**
 * todo add test for annotation that is not annotated
 *
 * @author Tim Ducheyne
 */
public class UnitilsTestListenerTestAnnotationTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTestListener unitilsTestListener;

    private Mock<Context> contextMock;
    private Mock<TestTestAnnotationHandler> testAnnotationHandlerMock;
    @Dummy
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        unitilsTestListener = new UnitilsTestListener(new ArrayList<TestListener>(), contextMock.getMock());

        testAnnotationHandlerMock.returns(EXECUTION).getTestPhase();
        contextMock.returns(testAnnotationHandlerMock).getInstanceOfType(TestTestAnnotationHandler.class);
        contextMock.returns(configuration).getConfiguration();
    }

    @Test
    public void methodAnnotation() throws Exception {
        Method testMethod = AnnotationOnField.class.getDeclaredMethod("testMethod");

        TestClass testClass = new TestClass(AnnotationOnField.class);
        TestInstance testInstance = new TestInstance(testClass, null, testMethod);
        TestTestAnnotation methodAnnotation = testMethod.getAnnotation(TestTestAnnotation.class);
        Annotations<TestTestAnnotation> annotations = new Annotations<TestTestAnnotation>(methodAnnotation, new ArrayList<TestTestAnnotation>(), configuration);

        unitilsTestListener.beforeTestClass(AnnotationOnField.class);
        unitilsTestListener.beforeTestSetUp(null, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(null);
        unitilsTestListener.afterTestTearDown();

        testAnnotationHandlerMock.assertInvokedInSequence().beforeTestSetUp(testInstance, annotations);
        testAnnotationHandlerMock.assertInvokedInSequence().beforeTestMethod(testInstance, annotations);
        testAnnotationHandlerMock.assertInvokedInSequence().afterTestMethod(testInstance, annotations, null);
        testAnnotationHandlerMock.assertInvokedInSequence().afterTestTearDown(testInstance, annotations);
    }

    @Test
    public void classAnnotation() throws Exception {
        Method testMethod = AnnotationOnClass.class.getDeclaredMethod("testMethod");

        TestClass testClass = new TestClass(AnnotationOnClass.class);
        TestInstance testInstance = new TestInstance(testClass, null, testMethod);
        TestTestAnnotation classAnnotation = AnnotationOnClass.class.getAnnotation(TestTestAnnotation.class);
        Annotations<TestTestAnnotation> annotations = new Annotations<TestTestAnnotation>(null, asList(classAnnotation), configuration);

        unitilsTestListener.beforeTestClass(AnnotationOnClass.class);
        unitilsTestListener.beforeTestSetUp(null, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(null);
        unitilsTestListener.afterTestTearDown();

        testAnnotationHandlerMock.assertInvokedInSequence().beforeTestSetUp(testInstance, annotations);
        testAnnotationHandlerMock.assertInvokedInSequence().beforeTestMethod(testInstance, annotations);
        testAnnotationHandlerMock.assertInvokedInSequence().afterTestMethod(testInstance, annotations, null);
        testAnnotationHandlerMock.assertInvokedInSequence().afterTestTearDown(testInstance, annotations);
    }

    @Test
    public void classAndMethodAnnotation() throws Exception {
        Method testMethod = AnnotationOnClassAndMethod.class.getDeclaredMethod("testMethod");

        TestClass testClass = new TestClass(AnnotationOnClassAndMethod.class);
        TestInstance testInstance = new TestInstance(testClass, null, testMethod);
        TestTestAnnotation methodAnnotation = testMethod.getAnnotation(TestTestAnnotation.class);
        TestTestAnnotation classAnnotation = AnnotationOnClassAndMethod.class.getAnnotation(TestTestAnnotation.class);
        Annotations<TestTestAnnotation> annotations = new Annotations<TestTestAnnotation>(methodAnnotation, asList(classAnnotation), configuration);

        unitilsTestListener.beforeTestClass(AnnotationOnClassAndMethod.class);
        unitilsTestListener.beforeTestSetUp(null, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(null);
        unitilsTestListener.afterTestTearDown();

        testAnnotationHandlerMock.assertInvokedInSequence().beforeTestSetUp(testInstance, annotations);
        testAnnotationHandlerMock.assertInvokedInSequence().beforeTestMethod(testInstance, annotations);
        testAnnotationHandlerMock.assertInvokedInSequence().afterTestMethod(testInstance, annotations, null);
        testAnnotationHandlerMock.assertInvokedInSequence().afterTestTearDown(testInstance, annotations);
    }

    @Test
    public void noAnnotation() throws Exception {
        Method testMethod = NoAnnotation.class.getDeclaredMethod("testMethod");

        TestClass testClass = new TestClass(NoAnnotation.class);
        TestInstance testInstance = new TestInstance(testClass, null, testMethod);

        unitilsTestListener.beforeTestClass(NoAnnotation.class);
        unitilsTestListener.beforeTestSetUp(null, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(null);
        unitilsTestListener.afterTestTearDown();

        testAnnotationHandlerMock.assertNotInvoked().beforeTestSetUp(null, null);
        testAnnotationHandlerMock.assertNotInvoked().beforeTestMethod(null, null);
        testAnnotationHandlerMock.assertNotInvoked().afterTestMethod(null, null, null);
        testAnnotationHandlerMock.assertNotInvoked().afterTestTearDown(null, null);
    }


    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    @TestAnnotation(TestTestAnnotationHandler.class)
    public static @interface TestTestAnnotation {
    }

    public static class TestTestAnnotationHandler extends TestAnnotationListener<TestTestAnnotation> {
    }

    public static class AnnotationOnField {

        @TestTestAnnotation
        public void testMethod() {
        }
    }

    public static class NoAnnotation {

        public void testMethod() {
        }
    }

    @TestTestAnnotation
    public static class AnnotationOnClass {

        public void testMethod() {
        }
    }

    @TestTestAnnotation
    public static class AnnotationOnClassAndMethod {

        @TestTestAnnotation
        public void testMethod() {
        }
    }

}
